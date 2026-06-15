# 노드로 LinkedList 직접 만들기

## 1. 목표

노드가 `prev`와 `next`로 앞뒤 노드의 주소를 저장하며 논리적으로 연결되는 구조를 직접 구현한다.

연결 리스트는 삽입과 삭제 시 데이터 전체를 이동하지 않고 연결만 바꾸면 되므로 빠르다. 반면 인덱스 접근은 원하는 위치까지 노드를 하나씩 따라가야 하므로 느리다.

이 과제에서는 노드 기반 리스트의 내부 동작을 직접 구현하며 `LinkedList`의 기본 원리를 확인한다.

---

## 2. 준비물

`MyLinkedListTest.java` 스켈레톤 코드를 IDE에 추가한다.

`printLinks()`는 완성된 상태로 제공되므로 직접 구현하지 않는다. 각 단계를 구현한 뒤 실행하여 노드 연결 상태를 확인한다.

---

## 3. 핵심 개념

* 노드 한 칸은 `[prev][data][next]` 구조로 이루어진다.
* `prev`는 이전 노드의 주소를 저장한다.
* `next`는 다음 노드의 주소를 저장한다.
* `head`는 첫 번째 노드를 가리킨다.
* `tail`은 마지막 노드를 가리킨다.
* 삽입과 삭제는 노드 간 연결만 바꾸면 되므로 데이터 이동이 없다.
* 인덱스 접근은 `head`부터 `next`를 따라가야 하므로 느리다.

---

## 4. 단계별 구현

### Step 1. `Node` 클래스 구현

노드 한 칸을 표현하는 `Node` 클래스를 이해한다.

구현 조건은 다음과 같다.

1. 생성자에서 전달받은 `data`를 현재 노드의 `data` 필드에 저장한다.
2. `prev`와 `next`는 처음에는 `null` 상태로 둔다.

```java
this.data = data;
```

---

### Step 2. 필드 이해

다음 세 필드의 역할을 확인한다.

```java
private Node head;
private Node tail;
private int size;
```

`head`는 첫 번째 노드를 가리킨다.

`tail`은 마지막 노드를 가리킨다.

`size`는 현재 저장된 노드 개수를 의미한다.

---

### Step 3. `addLast(String data)` 구현

리스트의 마지막 위치에 새 노드를 추가한다.

구현 조건은 다음과 같다.

1. 새 `Node` 객체를 생성한다.
2. 리스트가 비어 있으면 `head`와 `tail`이 모두 새 노드를 가리키게 한다.
3. 리스트가 비어 있지 않으면 기존 `tail` 뒤에 새 노드를 연결한다.
4. `tail`을 새 노드로 변경한다.
5. `size`를 1 증가시킨다.

확인 기준은 다음과 같다.

```text
"가", "나", "다"를 addLast 한 뒤 printLinks() 결과가 다음과 같으면 성공

[null <- 가 -> 나] [가 <- 나 -> 다] [나 <- 다 -> null]
```

---

### Step 4. `printLinks()` 이해

`printLinks()`는 연결 상태를 확인하기 위한 출력 메서드이다.

직접 구현하지 않고, 제공된 코드를 읽고 이해한다.

이 메서드는 `head`부터 시작해 `next`를 따라가며 각 노드의 이전 값, 현재 값, 다음 값을 출력한다.

```text
[prev <- data -> next]
```

---

### Step 5. `addFirst(String data)` 구현

리스트의 맨 앞에 새 노드를 추가한다.

구현 조건은 다음과 같다.

1. 새 `Node` 객체를 생성한다.
2. 리스트가 비어 있으면 `head`와 `tail`이 모두 새 노드를 가리키게 한다.
3. 리스트가 비어 있지 않으면 새 노드를 기존 `head` 앞에 연결한다.
4. `head`를 새 노드로 변경한다.
5. `size`를 1 증가시킨다.

확인 기준은 다음과 같다.

```text
addFirst("앞")을 호출하면 맨 앞에 다음 노드가 추가된다.

[null <- 앞 -> 가]
```

기존 노드의 데이터는 이동하지 않고 연결만 변경된다.

---

### Step 6. `nodeAt(int index)`와 `get(int index)` 구현

인덱스 위치에 있는 노드를 찾고, 해당 노드의 데이터를 반환한다.

`nodeAt(int index)` 구현 조건은 다음과 같다.

1. `head`부터 탐색을 시작한다.
2. `next`를 따라 `index`번 이동한다.
3. 도착한 노드를 반환한다.

`get(int index)` 구현 조건은 다음과 같다.

1. `nodeAt(index)`로 노드를 찾는다.
2. 찾은 노드의 `data`를 반환한다.

생각해볼 점은 다음과 같다.

```text
1000번째 노드를 꺼내려면 몇 번 이동해야 하는가?
```

---

### Step 7. `insert(int index, String data)` 구현

원하는 인덱스 위치에 새 노드를 삽입한다.

구현 조건은 다음과 같다.

1. `index == 0`이면 `addFirst(data)`로 처리한다.
2. `index == size`이면 `addLast(data)`로 처리한다.
3. 그 외의 경우 `nodeAt(index)`로 현재 해당 위치에 있는 노드를 찾는다.
4. 새 노드의 `prev`와 `next`를 설정한다.
5. 앞뒤 노드가 새 노드를 가리키도록 연결을 변경한다.
6. `size`를 1 증가시킨다.

연결 변경 흐름은 다음과 같다.

```java
Node next = nodeAt(index);
Node prev = next.prev;

node.prev = prev;
node.next = next;

prev.next = node;
next.prev = node;
```

확인 기준은 다음과 같다.

```text
insert(2, "끼움")을 호출하면 2번 위치에 "끼움" 노드가 삽입된다.
```

이때 데이터 전체를 이동하지 않고 양옆 연결만 변경된다.

---

## 5. 선택 도전 과제

1. **중간 삭제**: `remove(int index)`를 구현해 원하는 위치의 노드 삭제
2. **인덱스 검증**: 잘못된 인덱스가 들어오면 예외 처리
3. **역방향 출력**: `tail`부터 `prev`를 따라가며 거꾸로 출력하는 메서드 추가
4. **전체 삭제**: 모든 노드를 제거하는 `clear()` 메서드 추가

---

## 6. 제출 및 확인

`MyLinkedListTest.java`를 실행했을 때 다음과 같이 출력되면 기본 기능 구현이 완료된 것이다.

```text
addLast 후: [null <- 가 -> 나] [가 <- 나 -> 다] [나 <- 다 -> null]
addFirst 후: [null <- 앞 -> 가] [앞 <- 가 -> 나] [가 <- 나 -> 다] [나 <- 다 -> null]
get(2) = 나
insert 후: [null <- 앞 -> 가] [앞 <- 가 -> 끼움] [가 <- 끼움 -> 나] [끼움 <- 나 -> 다] [나 <- 다 -> null]
```

---

## 7. 스켈레톤 코드

```java
public class MyLinkedListTest {  
    public static void main(String[] args) {  
        MyLinkedList list = new MyLinkedList();  
  
        // --- Step 3 + 4 확인 ---  
        list.addLast("가");  
        list.addLast("나");  
        list.addLast("다");  
        System.out.print("addLast 후: ");  
        list.printLinks();  
        // 기대: [null <- 가 -> 나] [가 <- 나 -> 다] [나 <- 다 -> null]  
  
        // --- Step 5 확인 ---        list.addFirst("앞");  
        System.out.print("addFirst 후: ");  
        list.printLinks();  
        // 기대: [null <- 앞 -> 가] [앞 <- 가 -> 나] [가 <- 나 -> 다] [나 <- 다 -> null]  
  
        // --- Step 6 확인 ---        System.out.println("get(2) = " + list.get(2));   // 기대: 나  
  
        // --- Step 7 확인 ---        list.insert(2, "끼움");  
        System.out.print("insert 후: ");  
        list.printLinks();  
        // 기대: [null <- 앞 -> 가] [앞 <- 가 -> 끼움] [가 <- 끼움 -> 나] [끼움 <- 나 -> 다] [나 <- 다 -> null]  
    }  
}  
  
  
class MyLinkedList {  
  
    // [Step 1] 노드 한 칸: 데이터 + 앞/뒤 노드의 주소  
    static class Node {  
        String data;  
        Node prev;   // 앞 노드  
        Node next;   // 뒤 노드  
        Node(String data) {  
            // TODO: this.data 를 설정하세요.  
        }  
    }  
  
    // [Step 2] 필드 (작성돼 있음)  
    private Node head;   // 첫 노드  
    private Node tail;   // 마지막 노드  
    private int size;  
  
    // [Step 3] 맨 뒤에 추가  
    void addLast(String data) {  
        Node node = new Node(data);  
        // TODO: head == null 이면 head = tail = node;  
        //       아니면 node.prev = tail;  tail.next = node;  tail = node;  
        //       마지막에 size++  
    }  
  
    // [Step 4] 연결 상태 출력 (제공됨 — 읽고 이해만 하세요)  
    void printLinks() {  
        Node cur = head;  
        while (cur != null) {  
            String p = (cur.prev == null) ? "null" : cur.prev.data;  
            String n = (cur.next == null) ? "null" : cur.next.data;  
            System.out.print("[" + p + " <- " + cur.data + " -> " + n + "] ");  
            cur = cur.next;  
        }  
        System.out.println();  
    }  
  
    // [Step 5] 맨 앞에 추가  
    void addFirst(String data) {  
        Node node = new Node(data);  
        // TODO: head == null 이면 head = tail = node;  
        //       아니면 node.next = head;  head.prev = node;  head = node;  
        //       마지막에 size++  
    }  
  
    // [Step 6] index번째 노드 찾기  
    private Node nodeAt(int index) {  
        // TODO: head 부터 시작해서 next 로 index 번 이동한 노드를 반환하세요.  
        return null;  
    }  
  
    String get(int index) {  
        // TODO: nodeAt(index).data 를 반환하세요.  
        return null;  
    }  
  
    // [Step 7] index 위치에 삽입 (양옆 연결만 바꾸기)  ★핵심★  
    void insert(int index, String data) {  
        // TODO: index == 0 이면 addFirst, index == size 이면 addLast 로 처리.  
        //       그 외:  
        //         Node next = nodeAt(index);  Node prev = next.prev;  
        //         Node node = new Node(data);  
        //         node.prev = prev;  node.next = next;  
        //         prev.next = node;  next.prev = node;  
        //         size++  
    }  
  
    // [도전] index 위치 노드 삭제  
    void remove(int index) {  
        // TODO (도전): 삭제할 노드의 prev 와 next 를 서로 연결하고 size--  
        //              (맨 앞/맨 뒤 삭제 시 head/tail 갱신 주의)  
    }  
  
    int size() { return size; }  
}
```
