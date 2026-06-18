# 기아 현상과 notifyAll()

여러 스레드가 하나의 자원을 기다리는 상황에서 기아 현상이 발생하는 원인을 확인한다.

먼저 `notify()`를 사용해 특정 스레드가 자원을 계속 얻지 못할 수 있는 상황을 관찰하고, `notifyAll()`로 변경해 모든 대기 스레드에 실행 기회를 제공한다.

---

## 1. 먼저 알아둘 점

이 과제는 `synchronized`, `wait()`, `notify()`의 기본 동작을 이해하고 있다는 전제로 진행한다.

스레드 실행 순서는 운영체제와 스케줄러가 결정하므로 실행할 때마다 결과가 달라질 수 있다. 따라서 특정 스레드가 항상 같은 방식으로 기아 상태에 빠지는 것은 아니다.

`notify()`와 `notifyAll()`을 사용했을 때 자원을 획득하는 스레드의 분포와 실행 흐름이 어떻게 달라지는지 확인하는 것이 목적이다.

---

## 2. 구현 내용

여러 개의 일꾼 스레드가 하나의 공유 자원을 기다리도록 한다.

공급자 스레드는 일정 시간마다 자원을 하나씩 공급하고, 대기 중인 스레드에 자원이 준비되었음을 알린다.

```text
worker1 is waiting for resource...
worker2 is waiting for resource...
worker3 is waiting for resource...

Resource is now available!
worker2 got the resource!
```

먼저 `notify()`를 사용해 대기 중인 스레드 하나만 깨운다.

이후 `notifyAll()`로 변경해 모든 대기 스레드를 깨우고, 자원을 획득하기 위해 다시 경쟁하도록 한다.

---

## 3. 학습 목표

| 개념            | 학습 내용                         |
| ------------- | ----------------------------- |
| 기아 현상         | 특정 스레드가 오랜 시간 자원을 획득하지 못하는 상태 |
| `notify()`    | 대기 중인 스레드 하나를 임의로 깨움          |
| `notifyAll()` | 대기 중인 모든 스레드를 깨움              |
| `wait()`      | 자원이 없을 때 락을 반납하고 대기           |
| 조건 재확인        | 깨어난 뒤 `while`로 자원 상태 재확인      |
| 공유 자원         | 여러 스레드가 하나의 자원 상태를 함께 사용      |

---

## 4. 핵심 개념

### 4.1 기아 현상

기아 현상은 여러 스레드가 동일한 자원을 요청하는 상황에서 특정 스레드가 계속 우선순위에서 밀려 자원을 획득하지 못하는 상태다.

교착 상태와 달리 일부 스레드는 계속 실행된다. 그러나 특정 스레드는 실행 기회를 충분히 얻지 못할 수 있다.

---

### 4.2 `notify()`의 한계

`notify()`는 해당 객체에서 대기 중인 스레드 가운데 하나만 깨운다.

```java
notify();
```

어떤 스레드가 선택되는지는 보장되지 않는다.

따라서 대기 스레드가 여러 개인 경우 특정 스레드가 반복해서 선택되고 다른 스레드는 오랜 시간 선택되지 않을 가능성이 있다.

다만 `notify()`를 사용한다고 해서 반드시 기아 현상이 발생하는 것은 아니다. 스레드 스케줄링 결과에 따라 실행마다 다른 결과가 나타날 수 있다.

---

### 4.3 `notifyAll()`을 이용한 기아 완화

`notifyAll()`은 해당 객체에서 대기 중인 모든 스레드를 깨운다.

```java
notifyAll();
```

깨어난 모든 스레드는 객체의 락을 얻기 위해 경쟁한다.

락은 한 번에 하나의 스레드만 획득할 수 있으므로 실제 자원을 사용하는 스레드는 하나지만, 모든 스레드가 조건을 다시 확인할 기회를 얻는다.

이 방식은 `notify()`보다 특정 스레드가 계속 대기할 가능성을 줄일 수 있다.

---

### 4.4 `notifyAll()`도 공정성을 보장하지 않는다

`notifyAll()`은 모든 스레드를 깨우지만 락 획득 순서는 운영체제와 스케줄러가 결정한다.

따라서 다음 순서를 보장하지 않는다.

* 먼저 기다린 스레드가 먼저 실행됨
* 모든 스레드가 같은 횟수만큼 자원을 획득함
* 일정한 순서로 자원이 분배됨

완전한 공정성이 필요하면 요청 순서를 직접 관리하거나 공정 모드의 `ReentrantLock`과 `Condition` 등을 검토할 수 있다.

---

### 4.5 조건은 `while`로 확인한다

`notifyAll()`로 모든 스레드가 깨어나도 실제 자원은 하나뿐이다.

한 스레드가 자원을 소비하면 나머지 스레드는 다시 대기해야 한다.

```java
while (!isAvailable) {
    wait();
}
```

`if`를 사용하면 조건이 만족되지 않은 상태에서도 다음 코드가 실행될 수 있으므로 `while`로 조건을 반복해서 확인한다.

---

## 5. 파일 구조

| 파일                    | 역할                        |
| --------------------- | ------------------------- |
| `SharedResource.java` | 자원 상태와 대기 및 공급 기능 관리      |
| `WorkerThread.java`   | 자원을 반복해서 요청하는 일꾼 스레드      |
| `Main.java`           | 공유 자원, 일꾼 스레드, 공급자 스레드 생성 |

---

## 6. 단계별 구현

### Step 1. 공유 자원 생성

#### 목표

여러 일꾼 스레드가 공유하는 자원 객체와 자원 상태를 정의한다.

#### 구현 내용

1. `SharedResource` 클래스를 작성한다.
2. 자원 사용 가능 여부를 나타내는 `isAvailable` 필드를 선언한다.
3. 초기 상태는 자원이 없는 상태로 설정한다.

<details>
<summary>힌트 보기</summary>

```java
class SharedResource {
    private boolean isAvailable = false;
}
```

</details>

#### 확인

* 자원 상태가 하나의 공유 객체에서 관리되는지 확인한다.
* 초기값이 `false`인지 확인한다.

---

### Step 2. 자원 대기 기능 구현

#### 목표

자원이 없으면 스레드를 대기시키고, 자원이 준비되면 해당 자원을 소비하도록 한다.

#### 구현 내용

1. `waitForResource()`를 `synchronized` 메서드로 선언한다.
2. 자원이 없으면 `wait()`을 호출한다.
3. 대기에서 깨어난 뒤 조건을 다시 확인한다.
4. 자원을 획득한 스레드 이름을 출력한다.
5. 자원을 소비한 뒤 상태를 `false`로 변경한다.

<details>
<summary>힌트 보기</summary>

```java
public synchronized void waitForResource(
        String threadName
) {
    while (!isAvailable) {
        try {
            System.out.println(
                    threadName
                    + " is waiting for resource..."
            );

            wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    System.out.println(
            threadName + " got the resource!"
    );

    isAvailable = false;
}
```

</details>

#### 확인

* 자원이 없으면 스레드가 대기하는지 확인한다.
* `wait()`이 `while` 내부에서 호출되는지 확인한다.
* 자원을 획득한 뒤 `isAvailable`이 다시 `false`가 되는지 확인한다.
* 인터럽트 발생 시 인터럽트 상태를 복원하는지 확인한다.

---

### Step 3. `notify()`를 이용한 자원 공급

#### 목표

자원을 공급하고 대기 중인 스레드 하나를 깨운다.

#### 구현 내용

1. `makeResourceAvailable()`을 `synchronized` 메서드로 선언한다.
2. 자원 상태를 `true`로 변경한다.
3. 자원이 공급되었다는 메시지를 출력한다.
4. `notify()`로 스레드 하나를 깨운다.

<details>
<summary>힌트 보기</summary>

```java
public synchronized void makeResourceAvailable() {
    isAvailable = true;

    System.out.println(
            "Resource is now available!"
    );

    notify();
}
```

</details>

#### 확인

* 자원 공급 시 `isAvailable`이 `true`가 되는지 확인한다.
* 대기 중인 스레드 가운데 하나만 깨우는지 확인한다.
* 여러 번 실행하며 특정 스레드가 자원을 적게 획득하는 경우가 있는지 관찰한다.

---

### Step 4. 일꾼 스레드 구현

#### 목표

각 일꾼 스레드가 반복해서 공유 자원을 요청하도록 한다.

<details>
<summary>힌트 보기</summary>

```java
class WorkerThread extends Thread {
    private final SharedResource resource;
    private final String workerName;

    public WorkerThread(
            SharedResource resource,
            String workerName
    ) {
        this.resource = resource;
        this.workerName = workerName;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            resource.waitForResource(workerName);

            if (isInterrupted()) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                interrupt();
                break;
            }
        }
    }
}
```

</details>

#### 확인

* 모든 일꾼이 같은 `SharedResource` 객체를 사용하는지 확인한다.
* 자원을 획득한 뒤 일정 시간 작업하는지 확인한다.
* 인터럽트 발생 시 반복을 종료하는지 확인한다.

---

### Step 5. `notify()` 버전 실행

#### 목표

일꾼 세 명과 자원 공급자 스레드를 실행해 `notify()`의 동작을 확인한다.

#### 구현 내용

1. `SharedResource` 객체를 하나 생성한다.
2. 일꾼 스레드 세 개에 같은 객체를 전달한다.
3. 각 일꾼 스레드를 실행한다.
4. 공급자 스레드가 2초마다 자원을 공급하도록 한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {
    public static void main(String[] args) {
        SharedResource resource =
                new SharedResource();

        new WorkerThread(
                resource,
                "worker1"
        ).start();

        new WorkerThread(
                resource,
                "worker2"
        ).start();

        new WorkerThread(
                resource,
                "worker3"
        ).start();

        new Thread(() -> {
            while (!Thread.currentThread()
                    .isInterrupted()) {

                try {
                    Thread.sleep(2000);

                    resource
                            .makeResourceAvailable();
                } catch (InterruptedException e) {
                    Thread.currentThread()
                            .interrupt();
                    break;
                }
            }
        }).start();
    }
}
```

</details>

#### 확인

* 세 일꾼이 모두 하나의 공유 자원을 사용하는지 확인한다.
* 자원이 공급될 때마다 일꾼 한 명이 자원을 획득하는지 확인한다.
* 자원을 획득하는 일꾼이 고르게 분포하지 않는 경우가 있는지 관찰한다.
* 실행 결과가 매번 달라질 수 있음을 확인한다.

---

### Step 6. `notifyAll()`로 변경

#### 목표

대기 중인 모든 스레드를 깨워 자원을 획득할 기회를 제공한다.

#### 구현 내용

`makeResourceAvailable()`의 `notify()`를 `notifyAll()`로 변경한다.

<details>
<summary>힌트 보기</summary>

```java
public synchronized void makeResourceAvailable() {
    isAvailable = true;

    System.out.println(
            "Resource is now available!"
    );

    notifyAll();
}
```

</details>

#### 확인

* 자원이 공급될 때 모든 대기 스레드가 깨어나는지 확인한다.
* 깨어난 스레드가 `while`에서 자원 상태를 다시 확인하는지 확인한다.
* 한 스레드가 자원을 소비하면 나머지 스레드가 다시 대기하는지 확인한다.
* `notify()` 버전과 자원 획득 분포를 비교한다.

---

## 7. 최종 점검

* [ ] 여러 일꾼 스레드가 하나의 공유 자원을 사용한다.
* [ ] 자원이 없으면 `wait()`으로 대기한다.
* [ ] 조건 검사를 `while`로 처리한다.
* [ ] 자원을 획득하면 상태를 다시 `false`로 변경한다.
* [ ] `notify()`는 대기 스레드 하나를 깨운다.
* [ ] `notifyAll()`은 대기 스레드 전체를 깨운다.
* [ ] `notify()` 사용 시 특정 스레드가 오래 대기할 수 있음을 확인했다.
* [ ] `notifyAll()`이 기아 가능성을 줄일 수 있음을 확인했다.
* [ ] `notifyAll()`도 완전한 공정성을 보장하지 않는다는 점을 이해했다.
* [ ] `wait()`, `notify()`, `notifyAll()`을 동기화된 영역에서 호출했다.

---

## 8. 학습 체크

* [ ] 기아 현상의 의미를 설명할 수 있다.
* [ ] 교착 상태와 기아 현상의 차이를 구분할 수 있다.
* [ ] `notify()`에서 어떤 스레드가 깨어날지 보장되지 않는다는 점을 이해했다.
* [ ] `notifyAll()`이 모든 대기 스레드에 조건 확인 기회를 제공한다는 점을 이해했다.
* [ ] `notifyAll()` 사용 시에도 `while` 조건 검사가 필요한 이유를 이해했다.
* [ ] 스레드 실행 결과가 비결정적이라는 점을 이해했다.
* [ ] 완전한 공정성이 필요한 경우 다른 동기화 방식이 필요할 수 있음을 이해했다.

---

## 9. 선택 도전 과제

1. **자원 개수 관리**: `boolean` 대신 `int count`를 사용해 여러 개의 자원을 저장하고 소비한다.
2. **요청 순서 관리**: 대기 스레드의 요청 순서를 직접 저장해 먼저 요청한 스레드가 먼저 자원을 얻도록 구현한다.
3. **획득 횟수 통계**: 각 일꾼이 자원을 획득한 횟수를 기록해 `notify()`와 `notifyAll()` 결과를 비교한다.
4. **공정 락 비교**: 공정 모드의 `ReentrantLock`과 `Condition`을 사용해 실행 결과를 비교한다.
5. **일꾼 수 증가**: 일꾼 수를 늘려 자원 획득 편차가 어떻게 달라지는지 확인한다.
