# TreeMap 직접 만들기

## 1. 목표

Java의 `TreeMap`과 유사한 자료구조를 직접 구현하며 이진 검색 트리의 동작 원리를 이해한다.

이진 검색 트리를 직접 구현하면서 키가 자동으로 정렬되는 이유와 탐색, 삽입, 삭제가 평균적으로 빠른 이유를 확인한다.

이 과제에서는 제네릭을 사용하지 않고 키는 `String`, 값은 `Integer`로 고정한다.

실제 Java의 `TreeMap`은 레드-블랙 트리를 사용하지만, 이 과제에서는 그 기반이 되는 이진 검색 트리까지 구현한다.

---

## 2. 먼저 알아둘 점

실제 `TreeMap`은 스스로 균형을 맞추는 레드-블랙 트리를 사용한다.

일반적인 이진 검색 트리는 데이터가 균형 있게 저장되면 탐색, 삽입, 삭제를 평균 `O(log n)`에 수행할 수 있다.

그러나 정렬된 데이터를 순서대로 삽입하면 트리가 한쪽으로 치우칠 수 있다. 이 경우 트리 구조가 연결 리스트와 비슷해져 성능이 `O(n)`까지 낮아질 수 있다.

이 과제에서는 다음 내용을 중심으로 구현한다.

* 이진 검색 트리의 저장 구조
* `compareTo`를 이용한 키 비교
* 중위 순회를 이용한 정렬 출력
* 이진 검색 트리의 조회와 삭제
* 트리 균형이 필요한 이유

레드-블랙 트리의 회전과 재색칠은 기본 구현 범위에 포함하지 않는다.

---

## 3. 구현 기능

키와 값을 저장하고 키의 정렬 순서대로 데이터를 조회할 수 있는 `MyTreeMap` 클래스를 구현한다.

* `put(String key, Integer value)` → 키와 값 저장
* `get(String key)` → 키에 해당하는 값 조회
* `remove(String key)` → 키에 해당하는 데이터 삭제
* `size()` → 저장된 데이터 개수 반환
* `containsKey(String key)` → 키 존재 여부 확인
* `printSorted()` → 키의 오름차순으로 전체 데이터 출력
* `firstKey()` → 가장 작은 키 반환
* `lastKey()` → 가장 큰 키 반환

사용 예시는 다음과 같다.

```java
MyTreeMap map = new MyTreeMap();
map.put("banana", 2);
map.put("apple", 1);
map.put("cherry", 3);

map.printSorted();          // [apple=1] [banana=2] [cherry=3]
System.out.println(map.get("banana"));   // 2
System.out.println(map.firstKey());      // apple
System.out.println(map.lastKey());       // cherry
```

데이터를 삽입한 순서와 관계없이 키의 정렬 순서대로 출력되는 것이 `HashMap`과의 주요 차이이다.

---

## 4. 학습 목표

| 개념               | 학습 내용                              |
| ---------------- | ---------------------------------- |
| 이진 검색 트리         | 왼쪽 키는 현재 키보다 작고 오른쪽 키는 현재 키보다 큰 구조 |
| 키 비교             | `compareTo`를 사용해 왼쪽과 오른쪽 이동 방향 결정  |
| 중위 순회            | 왼쪽, 현재 노드, 오른쪽 순서로 방문해 정렬 결과 생성    |
| 평균 `O(log n)` 탐색 | 균형 잡힌 트리에서 높이만큼 노드를 이동             |
| 트리 균형            | 한쪽으로 치우친 트리의 성능이 `O(n)`으로 낮아지는 이유  |
| 노드 삭제            | 자식이 없는 경우, 하나인 경우, 둘인 경우를 구분해 처리   |

---

## 5. 핵심 개념

### 5.1 이진 검색 트리

이진 검색 트리는 다음 규칙에 따라 키를 저장한다.

* 현재 노드보다 작은 키는 왼쪽에 저장한다.
* 현재 노드보다 큰 키는 오른쪽에 저장한다.
* 현재 노드와 같은 키가 들어오면 새로운 노드를 만들지 않고 값을 갱신한다.

```text
            banana
           /      \
       apple      cherry
```

키를 조회할 때 현재 키와 비교하여 왼쪽과 오른쪽 중 한 방향만 선택한다.

트리가 균형 있게 구성되어 있다면 탐색 범위를 계속 줄일 수 있으므로 평균적으로 빠르게 조회할 수 있다.

---

### 5.2 `compareTo`를 이용한 키 비교

문자열 키는 `compareTo`를 사용해 크기를 비교한다.

```java
int cmp = key.compareTo(node.key);
```

반환값에 따라 이동 방향을 결정한다.

* `cmp < 0` → 현재 키보다 작으므로 왼쪽 이동
* `cmp > 0` → 현재 키보다 크므로 오른쪽 이동
* `cmp == 0` → 같은 키이므로 값 갱신 또는 조회 완료

---

### 5.3 중위 순회와 정렬

이진 검색 트리를 왼쪽, 현재 노드, 오른쪽 순서로 방문하면 키가 오름차순으로 출력된다.

```text
왼쪽 → 현재 노드 → 오른쪽
```

예시 트리를 중위 순회하면 다음 순서로 출력된다.

```text
apple → banana → cherry
```

이진 검색 트리의 왼쪽에는 작은 키, 오른쪽에는 큰 키가 저장되므로 중위 순회 결과가 정렬된 형태로 나타난다.

---

### 5.4 평균 `O(log n)`과 트리 균형

균형 잡힌 이진 검색 트리의 높이는 데이터 개수에 비해 완만하게 증가한다.

따라서 탐색, 삽입, 삭제는 평균적으로 트리 높이만큼만 이동하면 된다.

그러나 정렬된 키를 순서대로 삽입하면 트리가 한쪽 방향으로만 자랄 수 있다.

```text
a
 \
  b
   \
    c
```

이 구조에서는 원하는 키를 찾기 위해 모든 노드를 차례대로 확인할 수 있으므로 시간 복잡도가 `O(n)`까지 낮아진다.

실제 `TreeMap`은 레드-블랙 트리를 사용해 트리의 균형을 유지한다.

---

## 6. 파일 구조

| 파일               | 역할                                                |
| ---------------- | ------------------------------------------------- |
| `MyTreeMap.java` | 노드, 루트, `put`, `get`, `remove`, 정렬 출력 등을 구현하는 클래스 |
| `Main.java`      | 구현한 `MyTreeMap`의 기능을 확인하는 실행 클래스                  |

`Node`는 `MyTreeMap`의 내부 클래스로 구현한다.

---

## 7. 단계별 구현

각 단계는 목표, 구현 내용, 힌트, 확인 방법 순서로 진행한다.

힌트는 먼저 직접 구현한 뒤 막히는 경우에 확인한다.

---

### Step 1. 노드와 기본 구조 만들기

#### 목표

키와 값, 왼쪽과 오른쪽 자식을 저장하는 노드와 트리의 시작점인 루트를 구현한다.

#### 구현 내용

1. `Node` 내부 클래스를 만든다.
2. `Node`에 `key`, `value`, `left`, `right` 필드를 선언한다.
3. 생성자에서 전달받은 키와 값을 저장한다.
4. `MyTreeMap`에 `root`와 `size` 필드를 선언한다.

<details>
<summary>힌트 보기</summary>

```java
public class MyTreeMap {

    static class Node {
        String key;
        Integer value;
        Node left;      // 나보다 작은 키들
        Node right;     // 나보다 큰 키들
        Node(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;   // 트리의 시작점
    private int size = 0;
}
```

</details>

#### 확인

컴파일 오류가 발생하지 않으면 기본 구조가 완성된 것이다.

---

### Step 2. `put` 구현

#### 목표

이진 검색 트리의 규칙에 따라 키와 값을 저장한다.

동일한 키가 이미 존재하면 새 노드를 추가하지 않고 값만 갱신한다.

#### 구현 내용

1. 루트부터 탐색을 시작한다.
2. `key.compareTo(node.key)`로 키를 비교한다.
3. 키가 작으면 왼쪽 서브트리로 이동한다.
4. 키가 크면 오른쪽 서브트리로 이동한다.
5. 빈 위치를 찾으면 새 노드를 생성하고 `size`를 증가시킨다.
6. 같은 키를 찾으면 값만 변경한다.

<details>
<summary>힌트 보기</summary>

재귀로 짜면 깔끔해요. "이 자리에 넣고, 바뀐 서브트리를 돌려준다"는 패턴입니다.

```java
public void put(String key, Integer value) {
    root = putNode(root, key, value);
}

private Node putNode(Node node, String key, Integer value) {
    if (node == null) {                 // 빈 자리 → 새 노드
        size++;
        return new Node(key, value);
    }
    int cmp = key.compareTo(node.key);
    if (cmp < 0)      node.left  = putNode(node.left, key, value);   // 작으면 왼쪽
    else if (cmp > 0) node.right = putNode(node.right, key, value);  // 크면 오른쪽
    else              node.value = value;                           // 같으면 값 갱신
    return node;
}
```

키 비교는 `compareTo`로! (음수면 작다, 0이면 같다, 양수면 크다)

</details>

#### 확인

* 여러 키를 순서와 관계없이 삽입한다.
* `printSorted()`에서 키가 정렬되어 출력되는지 확인한다.
* 같은 키로 `put`을 다시 호출하면 값만 갱신되는지 확인한다.
* 같은 키를 갱신할 때 `size`가 증가하지 않는지 확인한다.

---

### Step 3. `get` 구현

#### 목표

키를 이용해 값을 조회하고 키가 존재하지 않으면 `null`을 반환한다.

#### 구현 내용

1. 루트부터 탐색을 시작한다.
2. 현재 노드의 키와 조회할 키를 `compareTo`로 비교한다.
3. 조회할 키가 작으면 왼쪽으로 이동한다.
4. 조회할 키가 크면 오른쪽으로 이동한다.
5. 같은 키를 찾으면 값을 반환한다.
6. 끝까지 찾지 못하면 `null`을 반환한다.

<details>
<summary>힌트 보기</summary>

```java
public Integer get(String key) {
    Node n = root;
    while (n != null) {
        int cmp = key.compareTo(n.key);
        if (cmp < 0)      n = n.left;    // 작으면 왼쪽으로
        else if (cmp > 0) n = n.right;   // 크면 오른쪽으로
        else              return n.value; // 찾음!
    }
    return null;   // 끝까지 못 찾음
}
```

</details>

#### 확인

* 저장한 키를 조회했을 때 해당 값이 반환되는지 확인한다.
* 저장하지 않은 키를 조회했을 때 `null`이 반환되는지 확인한다.
* 왼쪽과 오른쪽 서브트리에 저장된 키를 각각 조회한다.

---

### Step 4. 중위 순회로 정렬 출력 구현

#### 목표

키와 값을 키의 오름차순으로 출력한다.

#### 구현 내용

1. 현재 노드가 `null`이면 재귀 호출을 종료한다.
2. 왼쪽 서브트리를 순회한다.
3. 현재 노드의 키와 값을 출력한다.
4. 오른쪽 서브트리를 순회한다.

<details>
<summary>힌트 보기</summary>

```java
public void printSorted() {
    inOrder(root);
    System.out.println();
}

private void inOrder(Node node) {
    if (node == null) return;
    inOrder(node.left);                                         // 1) 왼쪽 먼저
    System.out.print("[" + node.key + "=" + node.value + "] "); // 2) 자신
    inOrder(node.right);                                        // 3) 오른쪽
}
```

이 "왼 → 자신 → 오른" 순서가 바로 정렬을 만들어내는 마법이에요.

</details>

#### 확인

다음 순서로 키를 삽입한다.

```text
banana, apple, cherry
```

삽입 순서와 관계없이 다음과 같이 출력되는지 확인한다.

```text
[apple=1] [banana=2] [cherry=3]
```

---

### Step 5. 보조 메서드 구현

#### 목표

저장된 데이터 개수, 키 존재 여부, 가장 작은 키와 가장 큰 키를 구한다.

#### 구현 내용

1. `size()`에서 현재 데이터 개수를 반환한다.
2. `containsKey(key)`에서 이진 검색 트리 방식으로 키를 탐색한다.
3. `firstKey()`에서 가장 왼쪽 노드의 키를 반환한다.
4. `lastKey()`에서 가장 오른쪽 노드의 키를 반환한다.
5. 트리가 비어 있으면 `firstKey()`와 `lastKey()`는 `null`을 반환한다.

<details>
<summary>힌트 보기</summary>

```java
public int size() { return size; }

public boolean containsKey(String key) {
    Node n = root;
    while (n != null) {
        int cmp = key.compareTo(n.key);
        if (cmp < 0)      n = n.left;
        else if (cmp > 0) n = n.right;
        else              return true;
    }
    return false;
}

public String firstKey() {           // 가장 작은 키 = 맨 왼쪽
    if (root == null) return null;
    Node n = root;
    while (n.left != null) n = n.left;
    return n.key;
}

public String lastKey() {            // 가장 큰 키 = 맨 오른쪽
    if (root == null) return null;
    Node n = root;
    while (n.right != null) n = n.right;
    return n.key;
}
```

BST에선 왼쪽으로만 끝까지 가면 최소, 오른쪽으로만 끝까지 가면 최대예요.

</details>

#### 확인

* `size()`가 저장된 키 개수와 같은지 확인한다.
* `containsKey()`가 존재하는 키와 존재하지 않는 키를 구분하는지 확인한다.
* `firstKey()`가 정렬 출력의 첫 번째 키와 같은지 확인한다.
* `lastKey()`가 정렬 출력의 마지막 키와 같은지 확인한다.

---

### Step 6. `remove` 구현

#### 목표

키에 해당하는 노드를 삭제하면서 이진 검색 트리의 규칙을 유지한다.

삭제할 노드는 자식 수에 따라 세 가지 경우로 나뉜다.

1. **자식이 없는 노드**: 해당 노드를 제거한다.
2. **자식이 하나인 노드**: 자식 노드를 삭제한 노드의 위치로 연결한다.
3. **자식이 둘인 노드**: 오른쪽 서브트리에서 가장 작은 노드를 찾아 현재 노드의 위치를 대체한다.

#### 구현 내용

1. 삭제할 키가 존재하는지 확인한다.
2. 키가 존재하지 않으면 `null`을 반환한다.
3. `compareTo`를 사용해 삭제할 노드를 찾는다.
4. 노드의 자식 수에 따라 삭제 방식을 결정한다.
5. 자식이 둘이면 오른쪽 서브트리의 최소 노드를 후계자로 사용한다.
6. 삭제 완료 후 `size`를 1 감소시킨다.
7. 삭제한 기존 값을 반환한다.

<details>
<summary>힌트 보기</summary>

먼저 키가 있는지 확인하고 개수를 줄인 뒤, 재귀로 노드를 떼어냅니다.

```java
public Integer remove(String key) {
    Integer old = get(key);
    if (old == null) return null;   // 없으면 아무것도 안 함
    root = removeNode(root, key);
    size--;
    return old;
}

private Node removeNode(Node node, String key) {
    if (node == null) return null;
    int cmp = key.compareTo(node.key);
    if (cmp < 0)      node.left  = removeNode(node.left, key);
    else if (cmp > 0) node.right = removeNode(node.right, key);
    else {
        // 찾음! 경우를 나눈다
        if (node.left == null)  return node.right;  // 경우1·2 (왼쪽 없음)
        if (node.right == null) return node.left;   // 경우2 (오른쪽 없음)

        // 경우3: 자식 둘 → 오른쪽의 최소(후계자)로 대체
        Node succ = node.right;
        while (succ.left != null) succ = succ.left;
        node.key = succ.key;
        node.value = succ.value;
        node.right = removeNode(node.right, succ.key); // 후계자 제거
    }
    return node;
}
```

왜 "오른쪽의 최소"일까요? 그 값이 **나보다는 크지만 오른쪽 중에선 가장 작아서**, 내 자리에 와도 왼<부모<오른 규칙이 깨지지 않기 때문이에요.

</details>

#### 확인

다음 세 가지 경우를 각각 테스트한다.

* 자식이 없는 잎 노드 삭제
* 자식이 하나인 노드 삭제
* 자식이 둘인 노드 삭제

각 삭제 이후에도 `printSorted()`의 출력 순서가 올바르게 유지되는지 확인한다.

또한 다음 항목을 확인한다.

* 삭제한 키를 조회하면 `null`이 반환되는가?
* 삭제 후 `size`가 1 감소하는가?
* 존재하지 않는 키를 삭제하면 `size`가 변하지 않는가?
* 다른 키의 조회 결과에는 영향을 주지 않는가?

---

### Step 7. 최종 점검

다음 항목을 모두 확인한다.

* [ ] 삽입 순서와 관계없이 `printSorted()`가 정렬된 결과를 출력하는가?
* [ ] 동일한 키로 `put`을 호출하면 기존 값이 갱신되는가?
* [ ] 동일한 키를 갱신할 때 `size`가 증가하지 않는가?
* [ ] 존재하지 않는 키를 `get`하거나 `remove`하면 `null`을 반환하는가?
* [ ] 자식이 없는 노드를 정상적으로 삭제할 수 있는가?
* [ ] 자식이 하나인 노드를 정상적으로 삭제할 수 있는가?
* [ ] 자식이 둘인 노드를 정상적으로 삭제할 수 있는가?
* [ ] `firstKey()`와 `lastKey()`가 최소 키와 최대 키를 반환하는가?
* [ ] 삭제 후에도 이진 검색 트리의 정렬 규칙이 유지되는가?

모든 항목을 통과하면 기본 `MyTreeMap` 구현이 완료된 것이다.

---

## 8. 학습 체크

* [ ] 이진 검색 트리의 왼쪽, 부모, 오른쪽 관계를 구현했다.
* [ ] `compareTo`를 사용해 키를 비교했다.
* [ ] 비교 결과에 따라 왼쪽 또는 오른쪽으로 탐색했다.
* [ ] 중위 순회로 키를 정렬된 순서로 출력했다.
* [ ] 균형 잡힌 트리의 탐색이 평균적으로 빠른 이유를 이해했다.
* [ ] 트리가 한쪽으로 치우치면 성능이 `O(n)`으로 낮아지는 이유를 이해했다.
* [ ] 노드 삭제의 세 가지 경우를 구분할 수 있다.
* [ ] 실제 `TreeMap`에서 균형 트리가 필요한 이유를 설명할 수 있다.

---

## 9. 최종 완성 체크리스트

* [ ] `MyTreeMap.java`의 `Node` 내부 클래스 구현
* [ ] 루트와 `size` 필드 구현
* [ ] `put` 구현
* [ ] `get` 구현
* [ ] `remove` 구현
* [ ] `size` 구현
* [ ] `containsKey` 구현
* [ ] `printSorted` 구현
* [ ] `firstKey` 구현
* [ ] `lastKey` 구현
* [ ] 세 가지 노드 삭제 상황 확인
* [ ] `Main.java`에서 삽입, 조회, 정렬, 삭제 기능 테스트

---

## 10. 선택 도전 과제

1. **불균형 확인**: `a`, `b`, `c`, `d`, `e`를 순서대로 삽입해 트리가 한쪽으로 치우치는 과정 확인
2. **범위 검색**: 특정 키 미만 또는 일정 범위의 키를 조회하는 `headMap`과 `subMap` 기능 구현
3. **가장 가까운 키 조회**: 주어진 키 이상 또는 이하에서 가장 가까운 키를 찾는 `ceilingKey`와 `floorKey` 구현
4. **HashMap과 비교**: 같은 데이터를 `MyHashMap`과 `MyTreeMap`에 저장하고 출력 순서와 탐색 구조 비교
5. **제네릭 적용**: 제네릭 학습 후 키와 값 타입을 `<K, V>`로 변경하고 키가 `Comparable`을 구현하도록 제한
6. **레드-블랙 트리 적용**: 회전과 재색칠을 구현해 트리가 한쪽으로 치우치지 않도록 균형 유지
