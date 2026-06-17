# 세마포어를 활용한 던전 동시 입장 제한

`Semaphore`를 사용해 정원이 정해진 던전을 구현한다.

여러 모험가 스레드가 하나의 던전 입장을 시도하며, 동시에 지정된 인원까지만 입장할 수 있도록 제어한다.

---

## 1. 먼저 알아둘 점

세마포어는 제한된 개수의 허가증을 관리하며 여러 스레드가 공유 자원에 동시에 접근할 수 있는 수를 제한한다.

던전 정원이 2명이라면 세마포어의 허가증도 2개로 설정한다. 모험가가 던전에 입장할 때 허가증 하나를 사용하고, 퇴장할 때 다시 반납한다.

스레드 실행 순서는 운영체제와 스케줄러가 결정하므로 실행할 때마다 달라질 수 있다.

따라서 모험가의 정확한 입장 순서보다 동시에 던전 안에 존재하는 모험가 수가 정원을 초과하지 않는지 확인하는 것이 중요하다.

---

## 2. 구현 내용

정원이 2명인 던전에 모험가 5명이 입장을 시도하도록 구현한다.

던전의 빈자리가 없으면 나머지 모험가 스레드는 허가증이 반환될 때까지 기다린다.

```text
전사 던전 입장 대기...
마법사 던전 입장 대기...
[입장] 전사 (남은 자리: 1/2)
[입장] 마법사 (남은 자리: 0/2)
궁수 던전 입장 대기...
[클리어] 전사 → 320 골드 획득
[퇴장] 전사
[입장] 궁수 (남은 자리: 0/2)
```

구현할 주요 기능은 다음과 같다.

* 던전 최대 입장 인원 설정
* 모험가별 스레드 생성
* 던전 입장 전 대기
* 입장 시 허가증 획득
* 무작위 시간 동안 던전 탐험
* 무작위 골드 획득
* 퇴장 시 허가증 반환
* 모든 모험가가 하나의 던전 공유

---

## 3. 학습 목표

| 개념                   | 학습 내용                    |
| -------------------- | ------------------------ |
| `Semaphore`          | 동시에 접근할 수 있는 스레드 수 제한    |
| `acquire()`          | 허가증을 획득하고 던전에 입장         |
| `release()`          | 허가증을 반환하고 던전에서 퇴장        |
| `availablePermits()` | 현재 남아 있는 허가증 수 확인        |
| `try-finally`        | 예외 발생 여부와 관계없이 허가증 반환    |
| 공유 자원                | 여러 모험가 스레드가 하나의 던전 객체 공유 |

---

## 4. 핵심 개념

### 4.1 세마포어와 동시 접근 제한

세마포어는 지정된 개수의 허가증을 관리한다.

```java
Semaphore slots = new Semaphore(2);
```

위 코드는 동시에 최대 2개의 스레드만 허가증을 획득할 수 있다는 의미다.

```java
slots.acquire();

try {
    // 공유 자원 사용
} finally {
    slots.release();
}
```

* `acquire()` → 허가증 하나를 획득한다.
* 남은 허가증이 없으면 스레드는 대기한다.
* `release()` → 사용한 허가증을 반환한다.
* 허가증이 반환되면 대기 중인 다른 스레드가 획득할 수 있다.

---

### 4.2 뮤텍스와 세마포어의 차이

뮤텍스는 한 번에 하나의 스레드만 공유 자원에 접근하도록 제한한다.

세마포어는 허가증 개수를 설정해 여러 스레드가 동시에 접근할 수 있도록 한다.

| 구분   |     동시 접근 수 |
| ---- | ----------: |
| 뮤텍스  |           1 |
| 세마포어 | 설정한 허가증 수만큼 |

던전 정원을 2명이나 3명으로 제한하려면 세마포어를 사용해야 한다.

`Semaphore(1)`은 동시에 하나의 스레드만 접근할 수 있으므로 뮤텍스와 유사한 방식으로 동작한다.

---

### 4.3 `release()`를 `finally`에서 호출하는 이유

허가증을 획득한 뒤 작업 도중 예외가 발생해도 반드시 허가증을 반환해야 한다.

```java
slots.acquire();

try {
    // 던전 탐험
} finally {
    slots.release();
}
```

`release()`를 일반 실행 코드에만 작성하면 예외가 발생했을 때 실행되지 않을 수 있다.

허가증이 반환되지 않으면 사용할 수 있는 허가증 수가 감소하고, 이후 스레드가 계속 대기하는 문제가 발생할 수 있다.

---

### 4.4 공정한 세마포어

기본 세마포어는 대기 중인 스레드가 반드시 요청 순서대로 허가증을 획득한다고 보장하지 않는다.

```java
new Semaphore(2);
```

두 번째 인자로 `true`를 전달하면 먼저 기다린 스레드가 먼저 허가증을 획득하는 공정 모드를 사용할 수 있다.

```java
new Semaphore(2, true);
```

공정 모드는 요청 순서를 관리하는 비용이 추가될 수 있다.

---

## 5. 파일 구조

| 파일                | 역할                        |
| ----------------- | ------------------------- |
| `Dungeon.java`    | 세마포어를 이용해 던전 정원과 입장 기능 관리 |
| `Adventurer.java` | 던전 입장을 시도하는 모험가 스레드       |
| `Main.java`       | 하나의 던전과 여러 모험가를 생성해 실행    |

필요한 클래스는 다음과 같다.

```java
java.util.concurrent.Semaphore
```

---

## 6. 단계별 구현

### Step 1. `Dungeon` 클래스 생성

#### 목표

던전 정원만큼의 허가증을 가진 세마포어를 생성한다.

#### 구현 내용

1. `Semaphore` 타입의 `slots` 필드를 선언한다.
2. 최대 입장 인원을 저장하는 `capacity` 필드를 선언한다.
3. 생성자에서 정원을 전달받는다.
4. 전달받은 정원으로 세마포어를 생성한다.

<details>
<summary>힌트 보기</summary>

```java
import java.util.concurrent.Semaphore;

class Dungeon {
    private final Semaphore slots;
    private final int capacity;

    public Dungeon(int capacity) {
        this.capacity = capacity;
        this.slots = new Semaphore(capacity);
    }
}
```

</details>

#### 확인

* 생성자에서 전달받은 정원으로 `Semaphore`가 생성되는지 확인한다.
* `slots`와 `capacity`가 외부에서 변경되지 않도록 `final`로 선언했는지 확인한다.

---

### Step 2. 던전 입장 기능 구현

#### 목표

모험가가 허가증을 획득해 던전에 입장하고, 탐험을 마치면 허가증을 반환하도록 한다.

#### 구현 내용

1. 모험가의 입장 대기 메시지를 출력한다.
2. `acquire()`로 허가증을 획득한다.
3. 입장 후 남은 허가증 수를 출력한다.
4. 무작위 시간 동안 스레드를 정지해 탐험 시간을 표현한다.
5. 무작위 골드를 생성한다.
6. `finally`에서 퇴장 메시지를 출력한다.
7. `release()`로 허가증을 반환한다.

<details>
<summary>힌트 보기</summary>

```java
public void enter(String name) throws InterruptedException {
    System.out.println(name + " 던전 입장 대기...");

    slots.acquire();

    try {
        System.out.println("[입장] " + name
                + " (남은 자리: "
                + slots.availablePermits()
                + "/" + capacity + ")");

        Thread.sleep((int) (Math.random() * 2000) + 1000);

        int gold = (int) (Math.random() * 400) + 100;

        System.out.println(
                "[클리어] " + name
                + " → " + gold + " 골드 획득"
        );
    } finally {
        System.out.println("[퇴장] " + name);
        slots.release();
    }
}
```

</details>

#### 확인

* 허가증이 없으면 `acquire()`에서 스레드가 대기하는지 확인한다.
* 입장 후 `availablePermits()`가 감소하는지 확인한다.
* 탐험을 마친 뒤 `release()`가 호출되는지 확인한다.
* 예외가 발생해도 허가증이 반환되도록 `finally`를 사용하는지 확인한다.

---

### Step 3. `Adventurer` 스레드 구현

#### 목표

각 모험가가 별도의 스레드에서 던전 입장을 시도하도록 구현한다.

#### 구현 내용

1. `Thread`를 상속하는 `Adventurer` 클래스를 작성한다.
2. 모든 모험가가 공유할 `Dungeon` 객체를 필드로 저장한다.
3. 모험가 이름을 필드로 저장한다.
4. `run()`에서 `dungeon.enter()`를 호출한다.
5. `InterruptedException`을 처리한다.

<details>
<summary>힌트 보기</summary>

```java
class Adventurer extends Thread {
    private final Dungeon dungeon;
    private final String name;

    public Adventurer(Dungeon dungeon, String name) {
        this.dungeon = dungeon;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            dungeon.enter(name);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(name + "의 던전 입장이 중단되었습니다.");
        }
    }
}
```

</details>

#### 확인

* 각 모험가가 별도의 스레드로 실행되는지 확인한다.
* 모든 모험가가 동일한 `Dungeon` 객체를 전달받는지 확인한다.
* 인터럽트가 발생하면 인터럽트 상태를 복원하는지 확인한다.

---

### Step 4. 던전과 모험가 실행

#### 목표

정원이 2명인 던전 하나에 모험가 5명이 동시에 입장을 시도하도록 한다.

#### 구현 내용

1. 정원이 2명인 `Dungeon` 객체를 하나 생성한다.
2. 모험가 이름 배열을 생성한다.
3. 각 이름으로 `Adventurer` 객체를 생성한다.
4. 각 모험가 스레드의 `start()`를 호출한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {
    public static void main(String[] args) {
        Dungeon dungeon = new Dungeon(2);

        String[] names = {
                "전사",
                "마법사",
                "궁수",
                "도적",
                "성기사"
        };

        for (String name : names) {
            new Adventurer(dungeon, name).start();
        }
    }
}
```

</details>

#### 확인

* 던전 객체를 하나만 생성했는지 확인한다.
* 모든 모험가가 같은 던전 객체를 공유하는지 확인한다.
* 모험가 5명이 모두 `start()`로 실행되는지 확인한다.
* 동시에 입장한 모험가 수가 2명을 초과하지 않는지 확인한다.
* 한 모험가가 퇴장하면 대기 중인 모험가가 입장하는지 확인한다.

모험가마다 별도의 던전을 생성하면 세마포어도 각각 생성되므로 동시 입장 제한이 적용되지 않는다.

---

## 7. 최종 점검

* [ ] `Semaphore`에 던전 정원과 같은 수의 허가증을 설정했다.
* [ ] 모험가 입장 전에 `acquire()`를 호출했다.
* [ ] 던전 내부에 동시에 최대 2명만 존재한다.
* [ ] 빈자리가 없으면 모험가 스레드가 대기한다.
* [ ] 모험가가 퇴장하면 대기 중인 다른 모험가가 입장한다.
* [ ] 허가증 반환을 `finally`에서 처리했다.
* [ ] 모든 모험가가 하나의 던전 객체를 공유한다.
* [ ] 스레드 실행 순서가 매번 달라질 수 있음을 확인했다.

---

## 8. 학습 체크

* [ ] `Semaphore(N)`이 동시에 최대 N개의 스레드 접근을 허용한다는 것을 이해했다.
* [ ] `acquire()`와 `release()`의 역할을 이해했다.
* [ ] 허가증이 없으면 스레드가 대기한다는 것을 확인했다.
* [ ] `availablePermits()`로 남은 허가증 수를 확인했다.
* [ ] `release()`를 `finally`에서 호출해야 하는 이유를 이해했다.
* [ ] 뮤텍스와 세마포어의 차이를 이해했다.
* [ ] 여러 스레드가 하나의 공유 객체를 사용하도록 구현했다.

---

## 9. 선택 도전 과제

1. **던전 정원 변경**: 정원을 1명 또는 3명으로 변경해 동시에 입장하는 모험가 수를 비교한다.
2. **골드 순위 구현**: 각 모험가가 획득한 골드를 저장하고 모든 스레드 종료 후 순위를 출력한다.
3. **몬스터 이벤트 추가**: 일정 확률로 보스 몬스터가 등장하도록 구현하고 탐험 시간과 보상을 변경한다.
4. **반복 입장 구현**: 각 모험가가 던전에 여러 번 입장하도록 반복문을 추가한다.
5. **공정 모드 적용**: `new Semaphore(2, true)`를 적용해 대기 순서에 따른 입장 흐름을 확인한다.
6. **시간 제한 입장 구현**: `tryAcquire()`를 사용해 지정된 시간 안에 허가증을 획득하지 못하면 입장을 포기하도록 구현한다.
