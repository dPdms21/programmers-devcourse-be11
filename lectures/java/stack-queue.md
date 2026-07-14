# Stack / Queue

## Stack

`Stack`은 나중에 들어간 데이터가 먼저 나오는 자료구조다.

이를 `LIFO(Last In First Out)` 구조라고 한다.

```java
Stack<Integer> stack = new Stack<>();

stack.push(1);
stack.push(2);

System.out.println(stack.pop()); // 2
```

주요 연산은 다음과 같다.

| 메서드         | 설명             |  시간복잡도 |
| ----------- | -------------- | -----: |
| `push()`    | 값을 넣는다         | `O(1)` |
| `pop()`     | 마지막에 넣은 값을 꺼낸다 | `O(1)` |
| `peek()`    | 마지막 값을 확인한다    | `O(1)` |
| `isEmpty()` | 비어 있는지 확인한다    | `O(1)` |

## Stack의 활용

### 함수 호출

함수가 호출되면 현재 실행 상태가 Stack에 저장된다.

호출된 함수가 종료되면 Stack에서 이전 실행 상태를 꺼내 다시 돌아간다.

```java
void func1() {
    func2();
}

void func2() {
    func3();
}

void func3() {
    // 실행
}
```

실행 흐름은 다음과 같다.

```text
func1 호출
→ func2 호출
→ func3 호출
→ func3 종료
→ func2 종료
→ func1 종료
```

함수 호출이 끝나지 않은 상태에서 계속 새로운 함수가 호출되면 Stack의 저장 한계를 넘을 수 있다.

이때 발생하는 오류가 `StackOverflowError`다.

### Undo

되돌리기 기능도 Stack 구조로 이해할 수 있다.

사용자의 작업을 순서대로 Stack에 저장해 두고, 되돌리기를 실행할 때마다 가장 최근 작업을 꺼내 취소한다.

```text
작업 A → 작업 B → 작업 C
Undo 실행 → 작업 C 취소
```

### 화면 이동

앱에서 화면을 이동한 뒤 뒤로가기를 누르면 이전 화면으로 돌아간다.

이때 화면 이동 기록을 Stack처럼 관리할 수 있다.

```text
목록 화면 → 상세 화면 → 댓글 화면
뒤로가기 → 상세 화면
뒤로가기 → 목록 화면
```

Android에서는 `Activity Stack`, iOS에서는 `Navigation Stack`이라는 개념으로 다룬다.

### 후위 표기식 계산

후위 표기식은 연산자를 피연산자 뒤에 적는 방식이다.

```text
중위 표기식: 2 + 2
후위 표기식: 2 2 +
```

후위 표기식 계산은 Stack을 사용한다.

1. 숫자는 Stack에 넣는다.
2. 연산자를 만나면 Stack에서 숫자 2개를 꺼낸다.
3. 계산 결과를 다시 Stack에 넣는다.
4. 모든 항목을 처리한 뒤 Stack에 남은 값이 결과가 된다.

```text
2 2 / 2 +

2 push
2 push
/ 연산 → 2 / 2 = 1 push
2 push
+ 연산 → 1 + 2 = 3 push

결과: 3
```

## Queue

`Queue`는 먼저 들어간 데이터가 먼저 나오는 자료구조다.

이를 `FIFO(First In First Out)` 구조라고 한다.

```java
Queue<Integer> queue = new LinkedList<>();

queue.offer(1);
queue.offer(2);

System.out.println(queue.poll()); // 1
```

주요 연산은 다음과 같다.

| 메서드         | 설명              |
| ----------- | --------------- |
| `offer()`   | 값을 넣는다          |
| `poll()`    | 가장 먼저 넣은 값을 꺼낸다 |
| `peek()`    | 가장 앞의 값을 확인한다   |
| `isEmpty()` | 비어 있는지 확인한다     |

일반적인 `Queue` 구현체에서 `offer()`, `poll()`, `peek()`은 보통 `O(1)`로 동작한다.

다만 `PriorityQueue`는 우선순위를 유지해야 하므로 삽입과 삭제가 `O(log n)`이다.

## Deque

`Deque`는 `double-ended queue`의 줄임말이다.

양쪽 끝에서 삽입과 삭제가 가능한 자료구조다.

발음은 보통 `덱`으로 읽는다.

```java
Deque<Integer> deque = new ArrayDeque<>();

deque.offerFirst(1);
deque.offerLast(2);

System.out.println(deque.pollFirst()); // 1
System.out.println(deque.pollLast());  // 2
```

`Deque`는 Stack처럼 사용할 수도 있고 Queue처럼 사용할 수도 있다.

## Queue의 활용

### Event Queue

키보드 입력, 마우스 클릭, 화면 이벤트는 발생한 순서대로 처리되어야 한다.

컴퓨터가 이벤트를 즉시 처리하지 못하는 경우 이벤트를 Queue에 저장해 두고 순서대로 처리한다.

```text
클릭 이벤트 → 키보드 입력 이벤트 → 마우스 이동 이벤트
```

이처럼 Queue는 이벤트를 잠시 저장하는 버퍼 역할을 할 수 있다.

### Job Scheduler

작업을 순서대로 처리해야 할 때도 Queue를 사용할 수 있다.

프린터 출력 대기열, 파일 다운로드 대기열처럼 먼저 요청한 작업을 먼저 처리하는 구조에 적합하다.

```text
문서 A 출력 요청
→ 문서 B 출력 요청
→ 문서 C 출력 요청

처리 순서: A → B → C
```

## 기억할 판단 기준

* 나중에 넣은 값을 먼저 꺼내야 하면 `Stack`을 사용한다.
* 먼저 넣은 값을 먼저 꺼내야 하면 `Queue`를 사용한다.
* 양쪽 끝에서 삽입과 삭제가 필요하면 `Deque`를 사용한다.
* 함수 호출, 되돌리기, 화면 뒤로가기는 `Stack` 구조로 이해할 수 있다.
* 이벤트 처리, 작업 대기열, 다운로드 순서 관리는 `Queue` 구조로 이해할 수 있다.
* `PriorityQueue`는 일반 Queue와 달리 우선순위를 기준으로 값을 꺼낸다.
