# 달팽이 경주 만들기 (멀티스레드)

> 달팽이 여러 마리가 각각 하나의 스레드가 되어 동시에 결승선을 향해 달리는 콘솔 게임을 구현한다. `Thread`, `run()`, `start()`, `sleep()`, `volatile`, `synchronized`를 활용해 멀티스레드의 기본 흐름을 학습한다.

---

## 1. 무엇을 만드나요?

달팽이 여러 마리가 동시에 출발해 결승선까지 달리는 콘솔 경주 게임을 구현한다.

각 달팽이는 독립된 스레드로 동작한다.

달팽이는 무작위로 조금씩 전진하고, 화면에는 진행 상황이 막대(`=`)로 표시된다.

가장 먼저 결승선에 도착한 달팽이가 우승한다.

```text
달팽이1: ===>
달팽이2: ==>
달팽이3: =====>
달팽이1: ======>
달팽이3: =========>
달팽이2: =====>
...
달팽이3: =============================>
*** 우승: 달팽이3 ***
```

출력이 서로 섞여 나오는 것은 정상이다.

여러 스레드가 동시에 실행되고 있기 때문에 각 달팽이의 출력 순서가 매번 달라질 수 있다.

---

## 2. 요구사항 정리 (기능 명세)

| 번호 | 기능      | 설명                                  |
| -- | ------- | ----------------------------------- |
| 1  | 달팽이 스레드 | 달팽이 한 마리를 하나의 독립된 `Thread`로 구현한다.   |
| 2  | 전진      | 각 달팽이는 무작위로 1~3칸씩 전진한다.             |
| 3  | 진행 표시   | 전진할 때마다 자신의 위치를 막대로 출력한다.           |
| 4  | 동시 출발   | 달팽이 여러 마리를 `start()`로 동시에 출발시킨다.    |
| 5  | 우승 판정   | 가장 먼저 결승선에 도착한 달팽이를 우승자로 한 번만 선언한다. |

---

## 3. 핵심 개념

### 1. 달팽이 하나는 스레드 하나이다

움직이는 달팽이 객체는 `Thread`를 상속한다.

`run()` 메서드 안에서 전진, 출력, 잠깐 멈춤을 반복한다.

```java
public void run() {
    while (position < FINISH) {
        position += rand.nextInt(3) + 1;
        printProgress();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
        }
    }
}
```

### 2. start()로 동시에 출발한다

여러 달팽이를 만들고 각각 `start()`를 호출하면 각 스레드가 동시에 실행된다.

`run()`을 직접 호출하면 스레드가 새로 시작되지 않고 일반 메서드처럼 순서대로 실행된다.

따라서 멀티스레드 경주를 구현하려면 반드시 `start()`를 사용해야 한다.

### 3. 우승자는 한 번만 선언한다

여러 스레드가 거의 동시에 결승선에 도착할 수 있다.

이때 우승자가 여러 번 출력되지 않도록 공유 상태를 안전하게 관리해야 한다.

* 여러 스레드가 함께 읽는 값은 `volatile`로 관리한다.
* 우승 선언처럼 한 번만 실행되어야 하는 코드는 `synchronized`로 보호한다.

---

## 4. 파일 구조

| 파일           | 역할                                           |
| ------------ | -------------------------------------------- |
| `Snail.java` | `Thread`를 상속한다. 달팽이 한 마리의 전진과 진행 출력 로직을 가진다. |
| `Race.java`  | 경주 종료 여부와 우승 선언 상태를 관리한다.                    |
| `Main.java`  | 달팽이 여러 마리를 생성하고 동시에 출발시킨다.                   |

---

## 5. Step by Step

각 Step에는 목표, 할 일, 힌트, 확인 방법이 있다.

한 Step씩 구현한 뒤 실행 결과를 확인하면서 다음 Step으로 넘어간다.

---

### Step 1. 달팽이 한 마리 움직이기 (`Snail.java`)

**목표**: 달팽이 한 마리가 결승선까지 무작위로 전진하도록 만든다.

**할 일**

1. `Snail` 클래스가 `Thread`를 상속하도록 작성한다.
2. 필드로 `name`, `position`, `FINISH`, `Random`을 선언한다.
3. `position`의 시작값은 0으로 둔다.
4. `run()`에서 `position < FINISH`인 동안 1~3칸씩 전진한다.
5. 전진할 때마다 현재 위치를 출력한다.
6. `Thread.sleep()`으로 잠깐 멈추며 속도를 조절한다.
7. 결승선에 도착하면 도착 메시지를 출력한다.

**힌트**

```java
import java.util.Random;

public class Snail extends Thread {
    private String name;
    private int position = 0;
    private final int FINISH = 30;
    private Random rand = new Random();

    public Snail(String name) {
        this.name = name;
    }

    public void run() {
        while (position < FINISH) {
            position += rand.nextInt(3) + 1;

            System.out.println(name + " 위치: " + position);

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }
        }

        System.out.println(name + " 결승선 도착!");
    }
}
```

```java
Snail s = new Snail("달팽이1");
s.start();
```

**확인**: 달팽이 위치가 0에서 결승선까지 증가하고, 마지막에 도착 메시지가 출력되면 성공이다.

---

### Step 2. 진행 막대로 출력하기 (`Snail.java`)

**목표**: 숫자 위치 대신 막대(`=`)로 진행 상황을 출력한다.

**할 일**

1. 위치만큼 `=`를 붙인다.
2. 막대 끝에 `>`를 붙인다.
3. 진행 상황을 출력하는 `printProgress()` 메서드를 만든다.
4. `run()` 안의 위치 출력 코드를 `printProgress()` 호출로 교체한다.

**힌트**

```java
private void printProgress() {
    StringBuilder bar = new StringBuilder();

    for (int i = 0; i < position; i++) {
        bar.append("=");
    }

    bar.append(">");

    System.out.println(name + ": " + bar);
}
```

**확인**: `달팽이1: =====>`처럼 막대가 점점 길어지면 성공이다.

---

### Step 3. 달팽이 여러 마리 동시 출발하기 (`Main.java`)

**목표**: 달팽이 3마리를 만들어 동시에 출발시킨다.

**할 일**

1. `Snail` 객체를 3개 생성한다.
2. 각 객체의 `start()`를 호출한다.
3. 출력이 섞여 나오면서 동시에 진행되는지 확인한다.

**힌트**

```java
public class Main {
    public static void main(String[] args) {
        Snail s1 = new Snail("달팽이1");
        Snail s2 = new Snail("달팽이2");
        Snail s3 = new Snail("달팽이3");

        s1.start();
        s2.start();
        s3.start();
    }
}
```

**확인**: 세 달팽이의 막대가 서로 섞여 출력되고, 동시에 길어지면 성공이다.

`start()` 대신 `run()`을 직접 호출하면 한 달팽이가 끝난 뒤 다음 달팽이가 실행된다.

이 차이를 통해 `start()`와 `run()`의 차이를 확인할 수 있다.

---

### Step 4. 우승자 가리기 (`Race.java`)

**목표**: 가장 먼저 도착한 달팽이를 우승자로 한 번만 선언하고, 나머지 달팽이는 멈추도록 만든다.

**할 일**

1. `Race` 클래스에 경주 종료 여부를 나타내는 공유 값을 만든다.
2. 경주 종료 여부를 확인하는 `isOver()` 메서드를 만든다.
3. 우승자를 선언하는 `finish()` 메서드를 만든다.
4. 우승 선언이 한 번만 실행되도록 `finish()`에 `synchronized`를 사용한다.
5. `Snail` 생성자에서 `Race` 객체를 전달받아 필드에 저장한다.
6. `run()`의 반복 조건에 `!race.isOver()`를 추가한다.
7. 결승선에 도착한 경우 `race.finish(name)`을 호출한다.

**힌트**

```java
public class Race {
    private volatile boolean over = false;

    public boolean isOver() {
        return over;
    }

    public synchronized void finish(String name) {
        if (!over) {
            over = true;
            System.out.println("\n*** 우승: " + name + " ***");
        }
    }
}
```

```java
private Race race;

public Snail(String name, Race race) {
    this.name = name;
    this.race = race;
}

public void run() {
    while (position < FINISH && !race.isOver()) {
        position += rand.nextInt(3) + 1;
        printProgress();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
        }
    }

    if (position >= FINISH) {
        race.finish(name);
    }
}
```

```java
Race race = new Race();

new Snail("달팽이1", race).start();
new Snail("달팽이2", race).start();
new Snail("달팽이3", race).start();
```

**확인**: 한 마리가 도착하면 우승 메시지가 한 번만 출력되고, 나머지 달팽이가 곧 멈추면 성공이다.

---

### Step 5. 마무리 다듬기

**목표**: 멀티스레드 동작과 우승 판정을 점검하고 프로그램을 완성한다.

**점검 항목**

* [ ] 달팽이마다 속도가 조금씩 달라 매번 결과가 바뀌는지 확인한다.
* [ ] 우승 선언이 한 번만 출력되는지 확인한다.
* [ ] 우승이 정해진 뒤 나머지 달팽이가 멈추는지 확인한다.
* [ ] 막대가 결승선을 넘어 너무 길어지지 않는지 확인한다.

막대가 결승선을 넘어 너무 길어지는 경우, 결승선을 넘은 위치를 결승선 값으로 맞출 수 있다.

```java
if (position > FINISH) {
    position = FINISH;
}
```

여기까지 통과하면 달팽이 경주 게임이 완성된다.

---

## 6. 멀티스레드 학습 체크

* [ ] `Thread`를 상속한다.
* [ ] `run()`을 오버라이드한다.
* [ ] `start()`를 호출해 스레드를 시작한다.
* [ ] `Thread.sleep()`으로 실행 속도를 조절한다.
* [ ] 여러 스레드가 동시에 실행되는 것을 출력으로 확인한다.
* [ ] `start()`와 `run()`의 차이를 확인한다.
* [ ] 공유 값을 `volatile`로 관리한다.
* [ ] 한 번만 실행되어야 하는 코드를 `synchronized`로 보호한다.

---

## 7. 최종 완성 체크리스트

* [ ] `Snail.java`에서 `Thread`를 상속하고 전진과 진행 막대 출력을 구현한다.
* [ ] `Race.java`에서 공유 깃발과 우승 선언을 구현한다.
* [ ] `Main.java`에서 달팽이 여러 마리를 동시에 `start()`한다.
* [ ] 실행할 때마다 우승자가 달라질 수 있다.
* [ ] 우승은 한 번만 선언된다.
* [ ] 우승이 선언되면 나머지 달팽이는 정지한다.

---

## 8. 선택 도전 과제

1. **마리 수 선택**: 시작 시 달팽이 몇 마리로 경주할지 입력받아 설정
2. **베팅**: 출발 전에 우승할 달팽이를 예측하고 결과에 따라 메시지 출력
3. **순위 매기기**: 우승뿐 아니라 도착 순서대로 전체 순위 출력
4. **화면 정리**: 매 틱마다 화면을 지우고 전체 트랙을 다시 출력
5. **트랙 옵션**: 결승선 거리나 `sleep` 시간을 난이도에 따라 선택
