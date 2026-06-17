# 스레드 실행 제어 익히기

`sleep()`, `interrupt()`, `yield()`, `join()`을 직접 실행하며 스레드의 상태 변화와 실행 제어 방식을 확인한다.

각 단계는 하나의 실험으로 구성한다. 실행 결과의 정확한 순서보다 각 메서드가 스레드의 실행에 어떤 영향을 주는지 확인하는 것이 중요하다.

---

## 1. 먼저 알아둘 점

스레드 실행 순서는 운영체제와 스케줄러가 결정하므로 비결정적이다.

같은 코드를 실행해도 매번 출력 순서가 달라질 수 있다. 따라서 출력 순서를 고정된 결과로 판단하지 않고 각 메서드의 동작과 상태 변화를 중심으로 확인한다.

---

## 2. 구현 내용

다음 스레드 제어 메서드를 단계별로 실험한다.

```text
실험 1: sleep()     → 현재 스레드를 일정 시간 정지
실험 2: interrupt() → 실행 중인 스레드에 중단 요청
실험 3: interrupt() → 일시 정지 중인 스레드 깨우기
실험 4: yield()     → 다른 스레드에 실행 기회 양보
실험 5: join()      → 다른 스레드가 종료될 때까지 대기
```

---

## 3. 학습 목표

| 개념            | 학습 내용                                                                  |
| ------------- | ---------------------------------------------------------------------- |
| 스레드 상태        | `NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, `TERMINATED` |
| `sleep()`     | 현재 실행 중인 스레드를 일정 시간 정지                                                 |
| `interrupt()` | 스레드에 작업 중단을 요청하거나 일시 정지 상태 해제                                          |
| `yield()`     | 다른 스레드에 실행 기회를 양보                                                      |
| `join()`      | 대상 스레드가 종료될 때까지 현재 스레드 대기                                              |

---

## 4. 핵심 개념

### 4.1 스레드 상태

```text
NEW
  │ start()
  ▼
RUNNABLE
  │
  ├─ BLOCKED
  ├─ WAITING
  ├─ TIMED_WAITING
  │
  ▼
TERMINATED
```

* `NEW` → 스레드 객체는 생성됐지만 `start()`를 호출하지 않은 상태
* `RUNNABLE` → 실행 중이거나 CPU 실행 순서를 기다리는 상태
* `BLOCKED` → 동기화 락을 얻기 위해 기다리는 상태
* `WAITING` → 다른 스레드의 동작이 끝나기를 시간 제한 없이 기다리는 상태
* `TIMED_WAITING` → 지정된 시간 동안 기다리는 상태
* `TERMINATED` → `run()` 실행이 끝난 상태

---

### 4.2 실행 제어 메서드

| 메서드           | 동작                   | 특징                |
| ------------- | -------------------- | ----------------- |
| `sleep(ms)`   | 현재 스레드를 지정된 시간 동안 정지 | `static` 메서드      |
| `interrupt()` | 대상 스레드에 중단 요청        | 강제 종료가 아님         |
| `yield()`     | 다른 스레드에 실행 기회 양보     | 실제 양보 여부는 보장되지 않음 |
| `join()`      | 대상 스레드가 끝날 때까지 대기    | 호출 중인 스레드가 대기     |

`stop()`, `suspend()`, `resume()`은 교착 상태나 데이터 불일치를 발생시킬 수 있으므로 사용하지 않는다.

---

### 4.3 인터럽트 관련 메서드

* `interrupt()` → 대상 스레드의 인터럽트 상태를 설정한다.
* `isInterrupted()` → 대상 스레드의 인터럽트 상태를 확인하며 상태를 유지한다.
* `interrupted()` → 현재 스레드의 인터럽트 상태를 확인한 뒤 초기화한다.

`interrupt()`는 스레드를 즉시 종료하는 메서드가 아니다. 스레드가 인터럽트 상태를 직접 확인하거나 일시 정지 메서드에서 예외를 처리해야 한다.

---

## 5. 파일 구조

| 구성                 | 역할                    |
| ------------------ | --------------------- |
| 여러 `Thread` 하위 클래스 | 각 실행 제어 메서드를 실험하는 스레드 |
| `Main.java`        | 각 실험 메서드를 호출하는 실행 클래스 |

한 파일에 여러 실험용 클래스를 작성하거나 실험별 클래스로 분리할 수 있다.

`main()`에서는 한 번에 하나의 실험만 실행해 결과를 확인한다.

---

## 6. 단계별 구현

### Step 1. 두 개의 스레드 동시 실행

#### 목표

`Thread`를 상속하고 `run()`을 재정의한 뒤 `start()`로 실행한다.

#### 구현 내용

1. `-`를 300번 출력하는 스레드를 작성한다.
2. `|`를 300번 출력하는 스레드를 작성한다.
3. 두 스레드를 각각 `start()`로 실행한다.

<details>
<summary>힌트 보기</summary>

```java
class PrintDash extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 300; i++) {
            System.out.print("-");
        }
    }
}

class PrintBar extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 300; i++) {
            System.out.print("|");
        }
    }
}
```

```java
new PrintDash().start();
new PrintBar().start();
```

</details>

#### 확인

`-`와 `|`가 섞여 출력되는지 확인한다.

`run()`을 직접 호출하면 새로운 스레드가 생성되지 않고 현재 스레드에서 순차적으로 실행된다.

---

### Step 2. `sleep()`으로 현재 스레드 정지

#### 목표

`sleep()`이 현재 실행 중인 스레드에 작동하는 `static` 메서드임을 확인한다.

#### 구현 내용

1. 스레드가 문자를 출력하도록 한다.
2. 출력 후 `Thread.sleep(2000)`으로 2초간 정지한다.
3. 정지 후 종료 문구를 출력한다.
4. `main()`에서 `t1.sleep(2000)` 형태도 실행해 차이를 확인한다.

<details>
<summary>힌트 보기</summary>

```java
class SleepThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 300; i++) {
            System.out.print("-");
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("종료");
    }
}
```

</details>

#### 확인

`main()`에서 `t1.sleep(2000)`을 호출해도 `t1`이 아니라 해당 코드를 실행하는 `main` 스레드가 정지하는지 확인한다.

`sleep()`은 인스턴스가 아니라 현재 실행 중인 스레드에 적용되므로 `Thread.sleep()` 형태로 호출한다.

---

### Step 3. `interrupt()`로 실행 중단 요청

#### 목표

`interrupt()`가 강제 종료가 아니라 중단 요청임을 확인한다.

#### 구현 내용

1. 카운트다운을 반복하는 스레드를 작성한다.
2. 반복 조건에서 `isInterrupted()`를 확인한다.
3. `main()`에서 사용자 입력을 기다린다.
4. 입력이 들어오면 대상 스레드의 `interrupt()`를 호출한다.

<details>
<summary>힌트 보기</summary>

```java
class CountThread extends Thread {
    @Override
    public void run() {
        int count = 10;

        while (count != 0 && !isInterrupted()) {
            System.out.println(count--);

            for (long i = 0; i < 2_500_000_000L; i++) {
            }
        }

        System.out.println("카운트가 종료되었습니다.");
    }
}
```

```java
CountThread thread = new CountThread();
thread.start();

new Scanner(System.in).nextLine();
thread.interrupt();
```

</details>

#### 확인

* `interrupt()` 호출 전에는 반복이 계속되는지 확인한다.
* `interrupt()` 호출 후 `isInterrupted()`가 `true`가 되는지 확인한다.
* 반복 조건에서 인터럽트 상태를 확인하지 않으면 작업이 계속되는지 확인한다.

---

### Step 4. 일시 정지 중인 스레드에 `interrupt()` 호출

#### 목표

`sleep()` 상태인 스레드에 `interrupt()`를 호출하면 `InterruptedException`이 발생하는 것을 확인한다.

#### 구현 내용

1. 카운트다운 스레드에서 반복마다 `Thread.sleep(2000)`을 호출한다.
2. `main()`에서 대상 스레드에 `interrupt()`를 호출한다.
3. `InterruptedException`이 발생하면 반복을 종료한다.

<details>
<summary>힌트 보기</summary>

```java
class CountSleepThread extends Thread {
    @Override
    public void run() {
        int count = 10;

        while (count != 0 && !isInterrupted()) {
            System.out.println(count--);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("일시 정지 상태가 해제되었습니다.");
                break;
            }
        }

        System.out.println("카운트가 종료되었습니다.");
    }
}
```

</details>

#### 확인

`sleep()` 중인 스레드에 `interrupt()`를 호출했을 때 대기 시간이 끝나기 전에 예외가 발생하는지 확인한다.

`InterruptedException`이 발생하면 인터럽트 상태가 초기화된다는 점도 확인한다.

---

### Step 5. `yield()`로 실행 기회 양보

#### 목표

`yield()`가 다른 스레드에 실행 기회를 양보하도록 스케줄러에 요청하는 메서드임을 확인한다.

#### 구현 내용

1. 이름을 가진 두 개의 스레드를 작성한다.
2. 각 스레드가 반복 횟수를 출력하도록 한다.
3. 반복마다 `Thread.yield()`를 호출한다.
4. 두 스레드를 동시에 실행한다.

<details>
<summary>힌트 보기</summary>

```java
class YieldThread extends Thread {
    private final String threadName;

    public YieldThread(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(threadName + " 실행 중. 반복: " + i);
            Thread.yield();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
```

</details>

#### 확인

두 스레드의 출력 순서가 매번 달라지는지 확인한다.

`yield()`를 호출하더라도 반드시 다른 스레드가 실행되는 것은 아니다.

---

### Step 6. `join()`으로 스레드 종료 대기

#### 목표

`join()`을 호출한 스레드가 대상 스레드의 종료를 기다리는 것을 확인한다.

#### 구현 내용

1. 여러 번 문자를 출력하는 스레드 두 개를 생성한다.
2. 두 스레드를 `start()`로 실행한다.
3. `main` 스레드에서 각각 `join()`을 호출한다.
4. 두 스레드 종료 후 소요 시간을 출력한다.

<details>
<summary>힌트 보기</summary>

```java
ManyPrintThread thread1 = new ManyPrintThread('-');
ManyPrintThread thread2 = new ManyPrintThread('|');

long startTime = System.currentTimeMillis();

thread1.start();
thread2.start();

try {
    thread1.join();
    thread2.join();
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}

long endTime = System.currentTimeMillis();
System.out.println("소요 시간: " + (endTime - startTime) + "ms");
```

</details>

#### 확인

* `join()`이 없을 때 `main` 스레드의 출력이 먼저 실행되는지 확인한다.
* `join()`이 있을 때 두 스레드가 종료된 후 다음 코드가 실행되는지 확인한다.
* `join()`을 호출하면 대상 스레드가 아니라 호출한 스레드가 기다린다는 점을 확인한다.

---

## 7. 최종 점검

* [ ] `start()`가 스레드를 `NEW`에서 `RUNNABLE` 상태로 변경하는 것을 이해했다.
* [ ] `run()`과 `start()`의 차이를 확인했다.
* [ ] `sleep()`이 현재 실행 중인 스레드에 작동하는 것을 확인했다.
* [ ] `interrupt()`가 강제 종료가 아닌 중단 요청임을 확인했다.
* [ ] 일시 정지 중 `interrupt()`를 호출하면 `InterruptedException`이 발생하는 것을 확인했다.
* [ ] `yield()`의 실행 결과가 보장되지 않는다는 점을 확인했다.
* [ ] `join()`을 호출한 스레드가 대상 스레드의 종료를 기다리는 것을 확인했다.
* [ ] 스레드 실행 순서가 비결정적이라는 점을 이해했다.

---

## 8. 선택 도전 과제

1. **스레드 상태 출력**: `getState()`를 사용해 `NEW`, `RUNNABLE`, `TIMED_WAITING`, `TERMINATED` 상태를 출력한다.
2. **인터럽트 상태 비교**: `isInterrupted()`와 `interrupted()`의 상태 유지 여부를 비교한다.
3. **시간 제한 join**: `join(1000)`을 사용해 최대 1초 동안만 대상 스레드를 기다린다.
4. **데몬 스레드**: `setDaemon(true)`를 적용해 일반 스레드 종료 시 함께 종료되는지 확인한다.
5. **인터럽트 상태 복원**: `InterruptedException` 처리 후 `Thread.currentThread().interrupt()`를 호출한다.
6. **Runnable 적용**: `Thread` 상속 대신 `Runnable`을 구현해 같은 기능을 작성한다.
