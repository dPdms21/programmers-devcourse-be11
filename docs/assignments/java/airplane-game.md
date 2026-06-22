# 비행기 게임 1945 만들기 (Swing + 멀티스레드)

> Java Swing으로 종스크롤 슈팅 게임을 구현하며 멀티스레드를 학습한다.
>
> 이 과제에서는 다음 구조를 중심으로 구현한다.
>
> ## 움직이는 객체 하나당 하나의 스레드를 사용한다
>
> 각 총알과 적기를 별도의 스레드로 실행하며, 여러 스레드가 동시에 동작할 때 발생하는 동시성과 동기화 문제를 확인한다.
>
> 아래 Step을 순서대로 진행하면 마지막 Step에서 게임을 완성할 수 있다.

---

## 1. 무엇을 만드나요?

* 키보드로 플레이어 비행기를 좌우 또는 상하좌우로 이동한다.
* 스페이스바를 누르면 위로 이동하는 총알을 발사한다.
* 화면 위쪽에서 적기가 일정한 간격으로 생성되어 아래로 이동한다.
* 총알이 적기에 명중하면 두 객체가 사라지고 점수가 1 증가한다.
* 적기가 플레이어 비행기와 충돌하면 게임이 종료된다.

```text
 ┌─────────────────┐
 │      ▼   ▼       │  ← 각각의 스레드로 내려오는 적기
 │   ▼             │
 │        ▲        │  ← 각각의 스레드로 올라가는 총알
 │      ▲          │
 │                 │
 │        ■        │  ← 키 입력으로 이동하는 플레이어
 └─────────────────┘
        SCORE: 12
```

---

## 2. 요구사항 정리

| 기능         | 설명                               | 관련 스레드 개념                         |
| ---------- | -------------------------------- | --------------------------------- |
| 비행기 이동     | 방향키로 플레이어 비행기를 이동한다              | 키 이벤트를 EDT에서 처리                   |
| 총알 발사      | 스페이스바로 총알을 생성하고 위로 이동시킨다         | 총알마다 `Thread` 하나 사용               |
| 적기 등장      | 적기를 일정 간격으로 생성하고 아래로 이동시킨다       | 적기마다 `Thread` 하나와 생성기 `Thread` 사용 |
| 충돌 처리      | 총알과 적기가 겹치면 두 객체를 제거하고 점수를 증가시킨다 | 공유 리스트 접근 동기화                     |
| 점수 및 게임 종료 | 점수를 표시하고 플레이어 충돌 시 게임을 종료한다      | 스레드의 안전한 종료                       |

게임 기능을 구현하는 과정에서 각 스레드 개념이 어떻게 사용되는지 확인하는 것이 주요 학습 목표다.

---

## 3. 핵심 개념

### 3.1 객체별 스레드와 `run()` 반복 구조

움직이는 객체는 `Thread`를 상속하고 `run()` 안에서 위치 변경, 화면 갱신 요청, 대기를 반복한다.

```java
public void run() {
    while (alive) {
        y -= speed;
        panel.repaint();

        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

`Thread.sleep()`의 대기 시간이 짧을수록 객체가 더 빠르게 움직인다.

---

### 3.2 Swing 화면 그리기와 `paintComponent()`

`JPanel`을 상속하고 `paintComponent(Graphics g)`를 오버라이드해 플레이어, 총알, 적기를 그린다.

`paintComponent()`를 직접 호출하지 않고 `repaint()`를 호출해 Swing에 다시 그리기를 요청한다.

```java
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // 플레이어, 총알, 적기 그리기
}
```

---

### 3.3 공유 리스트와 동기화

총알 목록인 `List<Bullet>`과 적기 목록인 `List<Enemy>`은 여러 스레드가 함께 사용하는 공유 자원이다.

* 총알과 적기 스레드는 객체를 목록에서 제거한다.
* 적기 생성기와 키 이벤트는 객체를 목록에 추가한다.
* Swing EDT는 목록을 순회하며 객체를 그린다.
* 충돌 검사는 목록을 순회하며 상태를 변경한다.

한 스레드가 목록을 순회하는 동안 다른 스레드가 내용을 수정하면 `ConcurrentModificationException`이 발생할 수 있다.

공유 목록을 순회하거나 수정하는 부분은 같은 객체를 기준으로 `synchronized` 처리한다.

```java
synchronized (bullets) {
    for (Bullet bullet : bullets) {
        // 총알 그리기
    }
}
```

`repaint()`는 화면을 즉시 그리는 메서드가 아니라 EDT에 다시 그리기를 요청하는 메서드다.

여러 스레드에서 호출할 수 있지만, `repaint()` 전에 접근하는 공유 데이터는 별도로 동기화해야 한다.

---

### 3.4 스레드의 안전한 종료

`Thread.stop()`은 공유 데이터가 일관되지 않은 상태에서 스레드를 강제로 종료할 수 있으므로 사용하지 않는다.

대신 `alive`나 `playing`과 같은 상태값을 사용해 반복문이 자연스럽게 종료되도록 한다.

```java
private volatile boolean alive = true;

@Override
public void run() {
    while (alive) {
        // 반복 작업
    }
}

public void stopMoving() {
    alive = false;
}
```

여러 스레드가 상태값을 확인한다면 `volatile`을 사용해 변경된 값의 가시성을 보장할 수 있다.

---

## 4. 파일 구조

| 파일                  | 역할                                         |
| ------------------- | ------------------------------------------ |
| `Main.java`         | `main()` 메서드에서 게임 창을 생성한다                  |
| `GamePanel.java`    | 화면 그리기, 객체 목록 관리, 키 입력, 충돌 처리, 점수 관리를 담당한다 |
| `Player.java`       | 플레이어 비행기의 위치와 이동을 관리한다                     |
| `Bullet.java`       | `Thread`를 상속하고 총알을 위로 이동시킨다                |
| `Enemy.java`        | `Thread`를 상속하고 적기를 아래로 이동시킨다               |
| `EnemySpawner.java` | `Thread`를 상속하고 적기를 일정한 간격으로 생성한다           |

선택적으로 `MovableObject`와 같은 공통 인터페이스를 작성해 `Bullet`과 `Enemy`의 공통 동작을 정의할 수 있다.

---

## 5. Step by Step

각 Step은 목표, 할 일, 힌트, 확인 방법으로 구성한다.

한 Step을 구현할 때마다 프로그램을 실행해 결과를 확인한다.

---

### Step 1. 게임 창 띄우기 (`Main.java`, `GamePanel.java`)

**목표**: 일정한 크기의 게임 창을 생성한다.

**할 일**

* `GamePanel`이 `JPanel`을 상속하도록 한다.
* 게임 패널의 선호 크기를 설정한다.
* `paintComponent()`에서 배경을 그린다.
* `Main`에서 `JFrame`을 생성하고 `GamePanel`을 추가한다.

**힌트**

```java
// GamePanel.java
public class GamePanel extends JPanel {

    public GamePanel() {
        setPreferredSize(
                new Dimension(400, 600)
        );

        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
```

```java
// Main.java
JFrame frame = new JFrame("1945");

frame.add(new GamePanel());
frame.pack();

frame.setDefaultCloseOperation(
        JFrame.EXIT_ON_CLOSE
);

frame.setVisible(true);
```

**확인**

설정한 크기의 검은색 창이 표시되는지 확인한다.

---

### Step 2. 플레이어 비행기 그리기 (`Player.java`)

**목표**: 화면 아래쪽에 플레이어 비행기를 표시한다.

**할 일**

* `Player`에 `x`, `y`, `width`, `height` 필드를 작성한다.
* 초기 위치를 화면 아래쪽 가운데로 설정한다.
* `GamePanel.paintComponent()`에서 플레이어를 사각형 또는 이미지로 그린다.

**힌트**

```java
g.setColor(Color.GREEN);

g.fillRect(
        player.x,
        player.y,
        player.width,
        player.height
);
```

**확인**

창 아래쪽에 플레이어 비행기가 표시되는지 확인한다.

---

### Step 3. 키보드로 플레이어 이동하기 (`GamePanel.java`)

**목표**: 방향키로 플레이어 비행기를 이동시킨다.

**할 일**

* `GamePanel`에 `KeyListener`를 등록한다.
* `setFocusable(true)`를 호출한다.
* 좌우 방향키 입력에 따라 `player.x`를 변경한다.
* 필요하면 상하 방향키로 `player.y`도 변경한다.
* 이동 후 `repaint()`를 호출한다.
* 플레이어가 화면 밖으로 나가지 않도록 이동 범위를 제한한다.

**힌트**

```java
setFocusable(true);

addKeyListener(new KeyAdapter() {

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            player.x -= 15;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            player.x += 15;
        }

        repaint();
    }
});
```

**확인**

방향키를 누를 때 플레이어가 해당 방향으로 이동하는지 확인한다.

키를 누르는 동안 연속으로 부드럽게 이동하도록 하려면 플레이어 이동을 별도의 스레드나 게임 루프로 처리할 수 있다.

기본 구현에서는 키 입력마다 일정 거리만 이동해도 된다.

---

### Step 4. 총알 발사하기 (`Bullet.java`)

**목표**: 스페이스바를 누르면 총알이 생성되고 위로 이동하도록 한다.

각 총알은 독립적인 스레드로 실행한다.

**할 일**

1. `Bullet`이 `Thread`를 상속하도록 한다.
2. `x`, `y`, `alive`, `GamePanel` 필드를 작성한다.
3. `run()`에서 위로 이동하고 `repaint()`를 호출한다.
4. 화면 위쪽을 벗어나면 반복문을 종료한다.
5. `GamePanel`에 `List<Bullet>`을 작성한다.
6. 총알 추가와 제거 시 공유 목록을 동기화한다.
7. 스페이스바 입력 시 총알을 생성하고 `start()`를 호출한다.
8. `paintComponent()`에서 모든 총알을 그린다.

**힌트**

```java
// Bullet.java
public class Bullet extends Thread {

    int x;
    int y;

    boolean alive = true;

    private final GamePanel panel;

    public Bullet(
            int x,
            int y,
            GamePanel panel
    ) {
        this.x = x;
        this.y = y;
        this.panel = panel;
    }

    @Override
    public void run() {
        while (alive && panel.isPlaying()) {
            y -= 10;

            if (y < 0) {
                alive = false;
            }

            panel.repaint();

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        panel.removeBullet(this);
    }
}
```

```java
// GamePanel.java
private final List<Bullet> bullets =
        new ArrayList<>();

public void addBullet(Bullet bullet) {
    synchronized (bullets) {
        bullets.add(bullet);
    }

    bullet.start();
}

public void removeBullet(Bullet bullet) {
    synchronized (bullets) {
        bullets.remove(bullet);
    }
}
```

```java
// paintComponent() 내부
synchronized (bullets) {
    for (Bullet bullet : bullets) {
        g.fillRect(
                bullet.x,
                bullet.y,
                5,
                12
        );
    }
}
```

```java
// keyPressed() 내부
if (e.getKeyCode() == KeyEvent.VK_SPACE) {
    Bullet bullet = new Bullet(
            player.x,
            player.y,
            this
    );

    addBullet(bullet);
}
```

**확인**

스페이스바를 누를 때마다 새로운 총알이 생성되어 위로 이동하는지 확인한다.

여러 총알을 연속으로 발사했을 때 각각 독립적으로 움직이는지도 확인한다.

---

### Step 5. 적기 생성하기 (`Enemy.java`, `EnemySpawner.java`)

**목표**: 화면 위쪽에서 적기가 일정한 간격으로 생성되어 아래로 이동하도록 한다.

각 적기와 적기 생성기는 독립적인 스레드로 실행한다.

**할 일**

1. `Enemy`가 `Thread`를 상속하도록 한다.
2. `run()`에서 `y` 값을 증가시켜 적기를 아래로 이동시킨다.
3. 화면 아래쪽을 벗어나면 적기 스레드를 종료한다.
4. `EnemySpawner`가 `Thread`를 상속하도록 한다.
5. 게임이 실행 중인 동안 일정한 간격으로 적기를 생성한다.
6. `GamePanel`에 `List<Enemy>`를 작성한다.
7. 적기 추가와 제거 시 목록을 동기화한다.
8. `paintComponent()`에서 적기를 그린다.
9. 게임 시작 시 `EnemySpawner.start()`를 호출한다.

**힌트**

```java
@Override
public void run() {
    while (panel.isPlaying()) {
        int x =
                (int) (
                        Math.random()
                        * (panel.getWidth() - 20)
                );

        Enemy enemy =
                new Enemy(x, 0, panel);

        panel.addEnemy(enemy);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
}
```

**확인**

적기가 화면 위쪽에서 일정한 간격으로 생성되고 아래로 이동하는지 확인한다.

여러 적기가 동시에 서로 독립적으로 움직이는지도 확인한다.

---

### Step 6. 충돌 처리하기 (`GamePanel.java`)

**목표**: 총알이 적기에 명중하면 두 객체가 사라지도록 한다.

공유 목록을 동시에 순회하고 변경할 때 발생하는 경쟁 상태를 확인하고 동기화로 해결한다.

**할 일**

1. 총알과 적기를 각각 `Rectangle`로 변환한다.
2. `Rectangle.intersects()`로 충돌을 검사한다.
3. 충돌하면 총알과 적기의 `alive`를 `false`로 변경한다.
4. 충돌한 객체를 목록에서 제거한다.
5. 총알 목록과 적기 목록을 순회할 때 동기화한다.
6. 필요하면 목록의 복사본을 생성한 뒤 충돌 검사를 수행한다.

**힌트**

```java
Rectangle bulletRect =
        new Rectangle(
                bullet.x,
                bullet.y,
                5,
                12
        );

Rectangle enemyRect =
        new Rectangle(
                enemy.x,
                enemy.y,
                20,
                20
        );

if (bulletRect.intersects(enemyRect)) {
    bullet.alive = false;
    enemy.alive = false;
}
```

목록을 직접 순회하는 동안 다른 스레드가 내용을 변경할 수 있으므로 복사본을 사용할 수 있다.

```java
List<Bullet> snapshot;

synchronized (bullets) {
    snapshot =
            new ArrayList<>(bullets);
}

for (Bullet bullet : snapshot) {
    // 충돌 검사
}
```

**확인**

총알이 적기에 닿았을 때 두 객체가 모두 사라지는지 확인한다.

동기화를 제거했을 때 발생할 수 있는 `ConcurrentModificationException`과 실행 결과의 차이도 확인한다.

---

### Step 7. 점수와 게임 종료 처리하기 (`GamePanel.java`)

**목표**: 적기 격추 시 점수를 증가시키고, 적기가 플레이어와 충돌하면 게임을 종료한다.

**할 일**

1. `score` 필드를 작성한다.
2. 총알과 적기가 충돌하면 점수를 1 증가시킨다.
3. `paintComponent()`에서 현재 점수를 표시한다.
4. 적기와 플레이어의 충돌을 검사한다.
5. 충돌하면 `playing`을 `false`로 변경한다.
6. 각 스레드가 `playing` 값을 확인하고 반복문을 종료하도록 한다.
7. 게임 종료 문구와 최종 점수를 화면에 표시한다.

**힌트**

```java
g.setColor(Color.WHITE);

g.drawString(
        "SCORE: " + score,
        10,
        20
);
```

```java
private volatile boolean playing = true;

public boolean isPlaying() {
    return playing;
}
```

`volatile`은 한 스레드가 변경한 `playing` 값을 다른 스레드가 확인할 수 있도록 가시성을 보장한다.

**확인**

* 적기를 격추할 때 점수가 증가하는지 확인한다.
* 적기가 플레이어와 충돌하면 게임이 종료되는지 확인한다.
* 게임 종료 후 총알, 적기, 생성기 스레드가 반복을 종료하는지 확인한다.

---

### Step 8. 마무리 점검

다음 항목을 확인한다.

* [ ] 게임 종료 시 총알, 적기, 생성기 스레드가 정상적으로 종료된다.
* [ ] 공유 목록을 순회하거나 변경하는 부분에 동기화가 적용되어 있다.
* [ ] 화면 밖으로 나간 총알과 적기가 목록에서 제거된다.
* [ ] 여러 총알과 적기가 동시에 동작해도 예외가 발생하지 않는다.
* [ ] `paintComponent()`에서 `super.paintComponent(g)`를 호출한다.
* [ ] 화면이 과도하게 깜빡이지 않는다.
* [ ] 스레드가 불필요하게 계속 생성되거나 남아 있지 않는다.

위 항목을 모두 확인하면 멀티스레드를 활용한 비행기 게임 구현이 완료된다.

---

## 6. 멀티스레드 학습 체크

* [ ] `Thread`를 상속하고 `run()`을 오버라이드했다.
* [ ] `start()`를 호출해 새로운 스레드에서 `run()`을 실행했다.
* [ ] `Thread.sleep()`으로 객체의 이동 속도를 조절했다.
* [ ] 여러 총알과 적기 스레드가 동시에 동작하는 것을 확인했다.
* [ ] 공유 목록 접근을 `synchronized`로 동기화했다.
* [ ] `ConcurrentModificationException`이 발생하는 원인을 확인했다.
* [ ] 상태값을 사용해 스레드를 안전하게 종료했다.
* [ ] `volatile`을 사용한 가시성 보장을 확인했다.
* [ ] Swing EDT와 `repaint()`의 관계를 확인했다.

---

## 7. 최종 완성 체크리스트

* [ ] `Main.java` — 게임 창 생성
* [ ] `GamePanel.java` — 화면 그리기, 공유 목록 관리, 키 입력, 충돌 처리, 점수 관리
* [ ] `Player.java` — 플레이어 위치와 이동 관리
* [ ] `Bullet.java` — 총알 스레드와 위쪽 이동 구현
* [ ] `Enemy.java` — 적기 스레드와 아래쪽 이동 구현
* [ ] `EnemySpawner.java` — 적기 주기 생성 구현
* [ ] 플레이어 이동과 총알 발사가 동작한다.
* [ ] 적기가 일정한 간격으로 생성된다.
* [ ] 총알과 적기의 충돌이 처리된다.
* [ ] 격추 점수가 표시된다.
* [ ] 플레이어 충돌 시 게임이 종료된다.
* [ ] 게임 종료 후 관련 스레드가 정상적으로 종료된다.

---

## 8. 선택 도전 과제

1. **생명 기능**: 플레이어에게 생명 3개를 부여하고 충돌할 때마다 하나씩 감소시킨다.
2. **적기 총알**: 적기도 아래쪽으로 총알을 발사하도록 구현한다.
3. **이미지 적용**: 사각형 대신 `.png` 이미지를 불러와 플레이어, 총알, 적기를 그린다.
4. **공통 인터페이스**: `MovableObject` 인터페이스를 작성하고 `Bullet`과 `Enemy`가 구현하도록 한다.
5. **스레드 풀**: 객체마다 새로운 스레드를 생성하는 방식과 `ExecutorService`를 사용하는 방식을 비교한다.
6. **게임 기능 확장**: 보스 적기, 스테이지, 아이템 등의 기능을 추가한다.

---

## 9. 구현 시 참고할 점

객체마다 스레드를 생성하는 구조는 멀티스레드의 동작과 동기화 문제를 확인하기에 적합하다.

다만 총알과 적기가 많아지면 객체 수만큼 스레드가 생성되므로 자원 사용량이 증가할 수 있다.

실제 게임에서는 하나의 게임 루프에서 여러 객체의 상태를 갱신하거나, 스레드 풀과 작업 큐를 사용하는 방식도 고려할 수 있다.

공유 목록의 동기화를 제거했을 때 발생하는 문제를 확인한 뒤 다시 동기화를 적용하면 `synchronized`가 필요한 이유를 구체적으로 확인할 수 있다.
