# wait()와 notify()를 활용한 스레드 협업

`synchronized`, `wait()`, `notify()`를 사용해 두 스레드가 실행 순서를 주고받는 구조를 구현한다.

질문 스레드와 답변 스레드가 하나의 `Chat` 객체를 공유하며 질문과 답변을 번갈아 출력하도록 한다.

---

## 1. 먼저 알아둘 점

`wait()`와 `notify()`는 해당 객체의 모니터 락을 획득한 상태에서만 호출할 수 있다.

따라서 반드시 `synchronized` 메서드 또는 `synchronized` 블록 내부에서 사용해야 한다.

이 과제에서는 `flag` 값으로 질문 차례와 답변 차례를 구분한다. 스레드 실행 순서는 일반적으로 비결정적이지만, 조건과 `wait()`·`notify()`를 사용해 다음 순서를 유지한다.

```text
질문 → 답변 → 질문 → 답변
```

---

## 2. 구현 내용

질문 스레드와 답변 스레드가 하나의 `Chat` 객체를 공유한다.

각 스레드는 자신의 차례가 아니면 대기하고, 차례가 되면 메시지를 출력한 뒤 상대 스레드에 실행 순서를 넘긴다.

```text
Question : Hi
Answer : Hello
Question : How are you?
Answer : I'm fine, thank you!
Question : What are you doing?
Answer : I'm coding in Java
```

구현할 주요 기능은 다음과 같다.

* 질문과 답변 차례를 나타내는 `flag` 관리
* `synchronized`를 이용한 공유 객체 접근 제어
* `wait()`을 이용한 락 반납과 대기
* `notify()`를 이용한 대기 스레드 깨우기
* `while`을 이용한 조건 재확인
* 하나의 `Chat` 객체 공유

---

## 3. 학습 목표

| 개념             | 학습 내용                                  |
| -------------- | -------------------------------------- |
| `wait()`       | 현재 스레드를 대기 상태로 전환하고 객체의 락 반납           |
| `notify()`     | 같은 객체에서 대기 중인 스레드 하나를 깨움               |
| `synchronized` | 공유 객체 접근과 `wait()`·`notify()` 호출 조건 보장 |
| 조건 대기          | 자신의 차례가 아닐 때 대기                        |
| `while`        | 스레드가 깨어난 뒤 조건 재확인                      |
| 공유 객체          | 두 스레드가 하나의 상태를 함께 사용                   |

---

## 4. 핵심 개념

### 4.1 `wait()`과 `notify()`가 필요한 이유

`synchronized`는 한 번에 하나의 스레드만 공유 객체에 접근하도록 제한한다.

하지만 현재 스레드가 조건을 만족하지 못한 상태에서 락을 계속 보유하면, 조건을 변경해야 하는 다른 스레드도 공유 객체에 접근할 수 없다.

이때 `wait()`을 호출하면 현재 스레드는 대기 상태로 전환되며 객체의 락을 반납한다.

다른 스레드는 반환된 락을 획득해 작업을 수행하고, 조건이 변경되면 `notify()`를 호출해 대기 중인 스레드를 깨울 수 있다.

---

### 4.2 `wait()`과 `sleep()` 비교

| 구분        | `sleep()`         | `wait()`                        |
| --------- | ----------------- | ------------------------------- |
| 현재 스레드 정지 | 가능                | 가능                              |
| 락 반납      | 반납하지 않음           | 반납함                             |
| 호출 위치     | 일반 코드에서도 가능       | `synchronized` 내부에서만 가능         |
| 실행 재개     | 지정된 시간 경과 또는 인터럽트 | `notify()`, `notifyAll()`, 인터럽트 |
| 소속        | `Thread` 클래스      | `Object` 클래스                    |

`sleep()` 중에는 현재 스레드가 락을 유지한다.

`wait()`을 호출하면 락을 반납하므로 다른 스레드가 같은 공유 객체에 접근할 수 있다.

---

### 4.3 `synchronized` 내부에서 호출해야 하는 이유

`wait()`과 `notify()`는 현재 스레드가 해당 객체의 모니터 락을 가진 상태에서만 호출할 수 있다.

```java
public synchronized void question(String message) {
    wait();
    notify();
}
```

동기화되지 않은 상태에서 호출하면 `IllegalMonitorStateException`이 발생한다.

---

### 4.4 `while`로 조건을 확인하는 이유

대기 중인 스레드가 깨어났더라도 실행 조건이 반드시 만족된 것은 아니다.

다음과 같은 상황이 발생할 수 있다.

* 이유 없이 대기 상태가 해제되는 허위 각성
* 여러 스레드가 동시에 깨어나 락을 얻기 위해 경쟁
* 다른 스레드가 먼저 락을 획득해 조건을 다시 변경

따라서 `if`가 아니라 `while`을 사용해 깨어난 뒤에도 조건을 다시 확인해야 한다.

```java
while (조건) {
    wait();
}
```

---

### 4.5 `notify()`와 `notifyAll()`

* `notify()` → 해당 객체에서 대기 중인 스레드 하나를 깨운다.
* `notifyAll()` → 해당 객체에서 대기 중인 모든 스레드를 깨운다.

`notifyAll()`로 여러 스레드가 깨어나더라도 객체의 락은 한 번에 하나의 스레드만 획득할 수 있다.

대기 스레드가 여러 종류이거나 어떤 스레드가 조건을 만족하는지 확실하지 않은 경우에는 `notifyAll()`이 더 안전할 수 있다.

---

## 5. 파일 구조

| 파일                    | 역할                                           |
| --------------------- | -------------------------------------------- |
| `Chat.java`           | `flag`, `question()`, `answer()`를 관리하는 공유 객체 |
| `QuestionThread.java` | 질문 메시지를 출력하는 스레드                             |
| `AnswerThread.java`   | 답변 메시지를 출력하는 스레드                             |
| `Main.java`           | 하나의 `Chat` 객체를 생성해 두 스레드에 전달                 |

---

## 6. 단계별 구현

### Step 1. `Chat` 공유 객체 생성

#### 목표

질문 스레드와 답변 스레드가 공유하는 객체와 실행 차례를 나타내는 필드를 작성한다.

#### 구현 내용

1. `Chat` 클래스를 작성한다.
2. 질문과 답변 차례를 구분하는 `flag`를 선언한다.
3. `false`는 질문 차례, `true`는 답변 차례로 정한다.

<details>
<summary>힌트 보기</summary>

```java
class Chat {
    private boolean flag = false;
}
```

</details>

#### 확인

* `flag`의 초기값이 질문 차례를 나타내는지 확인한다.
* 두 스레드가 동일한 `Chat` 객체를 공유할 수 있는 구조인지 확인한다.

---

### Step 2. 질문 메서드 구현

#### 목표

질문 차례가 아니면 대기하고, 질문을 출력한 뒤 답변 스레드에 차례를 넘긴다.

#### 구현 내용

1. `question()`을 `synchronized` 메서드로 선언한다.
2. `flag`가 `true`이면 답변 차례이므로 대기한다.
3. `wait()`을 호출해 락을 반납하고 대기한다.
4. 질문을 출력한다.
5. `flag`를 `true`로 변경한다.
6. `notify()`를 호출해 답변 스레드를 깨운다.

<details>
<summary>힌트 보기</summary>

```java
public synchronized void question(String message) {
    while (flag) {
        try {
            wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    System.out.println("Question : " + message);

    flag = true;
    notify();
}
```

</details>

#### 확인

* 답변 차례에는 질문 스레드가 대기하는지 확인한다.
* `wait()`이 `while` 조건 내부에 있는지 확인한다.
* 질문 출력 후 `flag`가 답변 차례로 변경되는지 확인한다.
* 인터럽트 발생 시 인터럽트 상태를 복원하는지 확인한다.

---

### Step 3. 답변 메서드 구현

#### 목표

답변 차례가 아니면 대기하고, 답변을 출력한 뒤 질문 스레드에 차례를 넘긴다.

#### 구현 내용

1. `answer()`를 `synchronized` 메서드로 선언한다.
2. `flag`가 `false`이면 질문 차례이므로 대기한다.
3. 답변을 출력한다.
4. `flag`를 `false`로 변경한다.
5. `notify()`를 호출해 질문 스레드를 깨운다.

<details>
<summary>힌트 보기</summary>

```java
public synchronized void answer(String message) {
    while (!flag) {
        try {
            wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    System.out.println("Answer : " + message);

    flag = false;
    notify();
}
```

</details>

#### 확인

* 질문 차례에는 답변 스레드가 대기하는지 확인한다.
* 질문 메서드와 반대 조건을 사용하는지 확인한다.
* 답변 출력 후 `flag`가 질문 차례로 변경되는지 확인한다.

---

### Step 4. 질문 스레드 구현

#### 목표

질문 배열을 순회하며 `Chat` 객체의 `question()`을 호출한다.

<details>
<summary>힌트 보기</summary>

```java
class QuestionThread extends Thread {
    private final Chat chat;

    private final String[] questions = {
            "Hi",
            "How are you?",
            "What are you doing?"
    };

    public QuestionThread(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void run() {
        for (String question : questions) {
            chat.question(question);

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

* 생성자로 `Chat` 객체를 전달받는지 확인한다.
* 질문마다 `question()`을 호출하는지 확인한다.
* 인터럽트 발생 시 반복을 종료하는지 확인한다.

---

### Step 5. 답변 스레드 구현

#### 목표

답변 배열을 순회하며 `Chat` 객체의 `answer()`를 호출한다.

<details>
<summary>힌트 보기</summary>

```java
class AnswerThread extends Thread {
    private final Chat chat;

    private final String[] answers = {
            "Hello",
            "I'm fine, thank you!",
            "I'm coding in Java"
    };

    public AnswerThread(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void run() {
        for (String answer : answers) {
            chat.answer(answer);

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

* 질문 스레드와 같은 `Chat` 객체를 전달받는지 확인한다.
* 답변마다 `answer()`를 호출하는지 확인한다.

---

### Step 6. 두 스레드 실행

#### 목표

하나의 `Chat` 객체를 질문 스레드와 답변 스레드가 공유하도록 한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {
    public static void main(String[] args) {
        Chat chat = new Chat();

        QuestionThread questionThread =
                new QuestionThread(chat);

        AnswerThread answerThread =
                new AnswerThread(chat);

        questionThread.start();
        answerThread.start();
    }
}
```

</details>

#### 확인

* `Chat` 객체를 하나만 생성했는지 확인한다.
* 두 스레드에 같은 객체를 전달했는지 확인한다.
* 두 스레드를 `start()`로 실행했는지 확인한다.
* 질문과 답변이 번갈아 출력되는지 확인한다.

각 스레드에 서로 다른 `Chat` 객체를 전달하면 `flag`와 모니터 락을 공유하지 않으므로 실행 순서를 조정할 수 없다.

---

## 7. 최종 점검

* [ ] 질문과 답변이 번갈아 출력된다.
* [ ] `question()`과 `answer()`를 `synchronized`로 선언했다.
* [ ] `wait()`과 `notify()`를 동기화된 영역에서 호출했다.
* [ ] 자신의 차례가 아니면 `wait()`을 호출한다.
* [ ] 조건 확인에 `if`가 아니라 `while`을 사용했다.
* [ ] 작업 후 상대 스레드가 실행되도록 `flag`를 변경했다.
* [ ] `notify()`를 호출해 대기 중인 스레드를 깨웠다.
* [ ] 질문 스레드와 답변 스레드가 하나의 `Chat` 객체를 공유한다.
* [ ] 인터럽트 발생 시 인터럽트 상태를 복원한다.

---

## 8. 학습 체크

* [ ] `wait()`이 객체의 락을 반납한다는 것을 이해했다.
* [ ] `sleep()`과 `wait()`의 락 처리 차이를 설명할 수 있다.
* [ ] `wait()`과 `notify()`를 `synchronized` 내부에서 호출해야 하는 이유를 이해했다.
* [ ] 깨어난 뒤 조건을 다시 확인해야 하는 이유를 설명할 수 있다.
* [ ] `notify()`와 `notifyAll()`의 차이를 이해했다.
* [ ] `flag`를 사용해 두 스레드의 실행 순서를 조정했다.
* [ ] 여러 스레드가 하나의 공유 객체를 사용해야 하는 이유를 이해했다.

---

## 9. 선택 도전 과제

1. **조건문 비교**: `while`을 `if`로 변경하고 여러 스레드가 실행될 때 발생할 수 있는 문제를 확인한다.
2. **`wait()`과 `sleep()` 비교**: 대기 코드에서 `wait()` 대신 `Thread.sleep()`을 사용해 락 반납 여부의 차이를 확인한다.
3. **`notifyAll()` 적용**: 질문 스레드와 답변 스레드를 여러 개 생성하고 `notifyAll()`을 적용한다.
4. **생산자-소비자 구현**: 생산자는 데이터를 생성하고 소비자는 데이터가 없을 때 대기하는 구조를 구현한다.
5. **시간 제한 대기**: `wait(2000)`을 사용해 최대 2초 동안만 대기하도록 구현한다.
