# HashMap 직접 만들기

## 1. 목표

Java의 `HashMap`과 유사한 자료구조를 직접 구현하며 해시 테이블의 동작 원리를 이해한다.

배열, 해시 함수, 충돌 처리 방식인 체이닝을 직접 구현하면서 저장과 조회가 평균적으로 빠른 이유와 저장 순서가 보장되지 않는 이유를 확인한다.

이 과제에서는 제네릭을 사용하지 않고 키는 `String`, 값은 `Integer`로 고정한다.

---

## 2. 구현 기능

다음 기능을 제공하는 `MyHashMap` 클래스를 구현한다.

* `put(String key, Integer value)` → 키와 값 저장
* `get(String key)` → 키에 해당하는 값 조회
* `remove(String key)` → 키에 해당하는 데이터 삭제
* `size()` → 저장된 데이터 개수 반환
* `containsKey(String key)` → 키 존재 여부 확인

사용 예시는 다음과 같다.

```java
MyHashMap map = new MyHashMap();
map.put("apple", 1);
map.put("banana", 2);
System.out.println(map.get("apple"));    // 1
System.out.println(map.get("cherry"));   // null
map.remove("apple");
System.out.println(map.get("apple"));    // null
```

---

## 3. 학습 목표

| 개념             | 학습 내용                           |
| -------------- | ------------------------------- |
| 해시 함수          | 키를 배열 인덱스로 변환                   |
| 버킷 배열          | 데이터를 저장하는 배열 구조                 |
| 충돌과 체이닝        | 같은 인덱스에 여러 키가 배정될 때 연결 리스트로 처리  |
| 평균 `O(1)` 접근   | 계산한 인덱스로 바로 접근하는 저장 및 조회 구조     |
| 순서가 보장되지 않는 이유 | 저장 위치가 삽입 순서가 아니라 해시값으로 결정되기 때문 |

---

## 4. 핵심 개념

### 4.1 해시 테이블

해시 테이블은 배열과 해시 함수를 결합한 자료구조이다.

문자열 키는 배열의 인덱스로 바로 사용할 수 없으므로 `hashCode()`를 통해 숫자로 변환한다. 변환된 값을 배열 크기로 나눈 나머지를 실제 저장 인덱스로 사용한다.

```text
"apple" --(hashCode)--> 93029210 --(% 16)--> 10번 칸
```

키로부터 인덱스를 바로 계산할 수 있으므로 평균적으로 저장과 조회를 빠르게 수행할 수 있다.

단, 충돌이 많이 발생하면 같은 버킷의 연결 리스트를 탐색해야 하므로 항상 `O(1)`이 보장되는 것은 아니다.

### 4.2 충돌과 체이닝

서로 다른 키가 같은 인덱스로 계산되는 상황을 충돌이라고 한다.

한 버킷에 하나의 데이터만 저장하면 기존 값이 덮어써질 수 있으므로 각 버킷에 연결 리스트를 두어 여러 노드를 연결한다. 이러한 충돌 처리 방식을 체이닝이라고 한다.

```text
인덱스 10 → ["apple":1] → ["grape":7] → null
```

조회할 때는 먼저 인덱스를 계산한 뒤 해당 버킷의 연결 리스트를 순회하며 키가 일치하는 노드를 찾는다.

### 4.3 저장 순서가 보장되지 않는 이유

데이터의 저장 위치는 삽입 순서가 아니라 키의 해시값으로 결정된다.

따라서 먼저 저장한 데이터가 앞에 위치한다는 보장이 없으며, 해시 테이블의 내부 구조나 크기가 변경되면 저장 위치도 달라질 수 있다.

---

## 5. 파일 구조

| 파일               | 역할                                             |
| ---------------- | ---------------------------------------------- |
| `MyHashMap.java` | 버킷 배열, 해시 함수, `put`, `get`, `remove`를 구현하는 클래스 |
| `Main.java`      | 구현한 `MyHashMap`의 기능을 확인하는 실행 클래스               |

`Node`는 `MyHashMap`의 내부 클래스로 구현한다.

---

## 6. 단계별 구현

각 단계는 목표, 구현 내용, 힌트, 확인 방법 순서로 진행한다.

힌트는 먼저 직접 구현한 뒤 막히는 경우에 확인한다.

### Step 1. 노드와 기본 구조 만들기

#### 목표

키와 값을 저장하는 노드와 버킷 배열을 가진 `MyHashMap`의 기본 구조를 만든다.

#### 구현 내용

1. `Node` 내부 클래스를 만든다.
2. `Node`에 `key`, `value`, `next` 필드를 선언한다.
3. `MyHashMap`에 `buckets`, `capacity`, `size` 필드를 선언한다.
4. 생성자에서 버킷 배열을 생성한다.

<details>
<summary>힌트 보기</summary>

```java
public class MyHashMap {

    // 데이터 한 칸 (연결 리스트의 노드)
    static class Node {
        String key;
        Integer value;
        Node next;          // 충돌 시 다음 노드로 연결
        Node(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node[] buckets;
    private int capacity = 16;
    private int size = 0;

    public MyHashMap() {
        buckets = new Node[capacity];   // 제네릭이 없어 형변환·경고도 없이 깔끔!
    }
}
```

</details>

#### 확인

컴파일 오류가 발생하지 않으면 기본 구조가 완성된 것이다.

---

### Step 2. 해시 함수 구현

#### 목표

문자열 키를 버킷 배열의 인덱스로 변환하는 메서드를 구현한다.

#### 구현 내용

1. `key.hashCode()`로 키의 해시값을 구한다.
2. 음수 인덱스가 나오지 않도록 처리한다.
3. `capacity`로 나눈 나머지를 반환한다.

반환되는 인덱스 범위는 `0`부터 `capacity - 1`까지이다.

<details>
<summary>힌트 보기</summary>

```java
private int getIndex(String key) {
    return Math.abs(key.hashCode()) % capacity;
}
```

`hashCode()`는 음수일 수 있어서 `Math.abs`로 양수로 만든 뒤 나머지를 구합니다.

</details>

#### 확인

동일한 키를 여러 번 전달했을 때 항상 같은 인덱스가 나오는지 확인한다.

---

### Step 3. `put` 구현

#### 목표

키와 값을 저장하고 동일한 키가 이미 존재하면 기존 값을 갱신한다.

#### 구현 내용

1. `getIndex(key)`로 저장할 버킷의 인덱스를 구한다.
2. 해당 버킷의 연결 리스트를 순회한다.
3. 동일한 키가 있으면 값만 변경하고 종료한다.
4. 동일한 키가 없으면 새 노드를 생성한다.
5. 새 노드를 연결 리스트의 맨 앞에 연결한다.
6. `size`를 1 증가시킨다.

<details>
<summary>힌트 보기</summary>

```java
public void put(String key, Integer value) {
    int idx = getIndex(key);
    Node head = buckets[idx];

    // 같은 키가 이미 있나? → 값만 갱신
    for (Node n = head; n != null; n = n.next) {
        if (n.key.equals(key)) {
            n.value = value;
            return;
        }
    }
    // 없으면 새 노드를 맨 앞에 추가 (체이닝)
    Node node = new Node(key, value);
    node.next = head;
    buckets[idx] = node;
    size++;
}
```

키 비교는 항상 `equals`로! (해시값이 같아도 키가 진짜 같은지 확인해야 함)

</details>

#### 확인

* 같은 키로 `put`을 두 번 호출했을 때 값이 갱신되는지 확인한다.
* 서로 다른 키를 저장했을 때 두 데이터가 모두 유지되는지 확인한다.
* 같은 키를 갱신할 때 `size`가 증가하지 않는지 확인한다.

---

### Step 4. `get` 구현

#### 목표

키에 해당하는 값을 반환하고 키가 존재하지 않으면 `null`을 반환한다.

#### 구현 내용

1. `getIndex(key)`로 버킷 인덱스를 구한다.
2. 해당 버킷의 연결 리스트를 순회한다.
3. 키가 일치하는 노드를 찾으면 해당 노드의 값을 반환한다.
4. 끝까지 찾지 못하면 `null`을 반환한다.

<details>
<summary>힌트 보기</summary>

```java
public Integer get(String key) {
    int idx = getIndex(key);
    for (Node n = buckets[idx]; n != null; n = n.next) {
        if (n.key.equals(key)) {
            return n.value;
        }
    }
    return null;   // 끝까지 못 찾으면 null
}
```

</details>

#### 확인

* 저장한 키를 조회했을 때 해당 값이 반환되는지 확인한다.
* 저장하지 않은 키를 조회했을 때 `null`이 반환되는지 확인한다.
* 충돌한 키도 각각 올바른 값을 반환하는지 확인한다.

---

### Step 5. 충돌 처리와 보조 메서드 구현

#### 목표

체이닝이 정상적으로 동작하는지 확인하고 `size()`와 `containsKey()`를 구현한다.

#### 구현 내용

1. `size()`에서 현재 저장된 데이터 개수를 반환한다.
2. `containsKey(key)`에서 해당 키의 존재 여부를 직접 탐색한다.

값으로 `null`을 허용하는 구조에서는 `get(key) != null`만으로 키 존재 여부를 정확히 판단할 수 없으므로 연결 리스트를 직접 탐색한다.

<details>
<summary>힌트 보기</summary>

```java
public int size() {
    return size;
}

public boolean containsKey(String key) {
    int idx = getIndex(key);
    for (Node n = buckets[idx]; n != null; n = n.next) {
        if (n.key.equals(key)) return true;
    }
    return false;
}
```

</details>

#### 확인

여러 키를 저장하여 같은 버킷에 두 개 이상의 노드가 연결되도록 한다.

충돌이 발생해도 각 키를 `get`으로 정확히 조회할 수 있으면 체이닝이 정상적으로 동작하는 것이다.

---

### Step 6. `remove` 구현

#### 목표

키에 해당하는 노드를 연결 리스트에서 제거한다.

#### 구현 내용

1. 키에 해당하는 버킷의 인덱스를 구한다.
2. 현재 노드와 이전 노드를 함께 관리하며 연결 리스트를 순회한다.
3. 삭제할 노드가 첫 번째 노드라면 버킷의 시작 노드를 다음 노드로 변경한다.
4. 중간이나 마지막 노드라면 이전 노드가 다음 노드를 가리키도록 연결한다.
5. `size`를 1 감소시킨다.
6. 삭제한 값을 반환한다.
7. 키를 찾지 못하면 `null`을 반환한다.

<details>
<summary>힌트 보기</summary>

```java
public Integer remove(String key) {
    int idx = getIndex(key);
    Node n = buckets[idx];
    Node prev = null;

    while (n != null) {
        if (n.key.equals(key)) {
            if (prev == null) buckets[idx] = n.next;  // 첫 노드 삭제
            else              prev.next = n.next;     // 중간/끝 노드 삭제
            size--;
            return n.value;
        }
        prev = n;
        n = n.next;
    }
    return null;
}
```

</details>

#### 확인

* 삭제한 키를 `get`으로 조회했을 때 `null`이 반환되는지 확인한다.
* 삭제 후 `size`가 1 감소하는지 확인한다.
* 존재하지 않는 키를 삭제했을 때 `size`가 변하지 않는지 확인한다.
* 충돌로 연결된 첫 번째, 중간, 마지막 노드가 각각 정상적으로 삭제되는지 확인한다.

---

### Step 7. 최종 점검

다음 항목을 모두 확인한다.

* [ ] 동일한 키로 `put`을 호출하면 기존 값이 갱신되는가?
* [ ] 동일한 키를 갱신할 때 중복 노드가 생성되지 않는가?
* [ ] 충돌이 발생해도 `get`이 정확한 값을 반환하는가?
* [ ] 존재하지 않는 키를 `get`하거나 `remove`하면 `null`을 반환하는가?
* [ ] 데이터 추가와 삭제에 따라 `size`가 정확히 변경되는가?
* [ ] 데이터를 삭제한 뒤에도 같은 버킷의 다른 노드를 정상적으로 조회할 수 있는가?

모든 항목을 통과하면 기본 `MyHashMap` 구현이 완료된 것이다.

---

## 7. 학습 체크

* [ ] 해시 함수로 키를 배열 인덱스로 변환했다.
* [ ] 버킷 배열에 키와 값을 저장했다.
* [ ] 충돌을 연결 리스트 기반 체이닝으로 처리했다.
* [ ] `put`, `get`, `remove`의 동작 흐름을 이해했다.
* [ ] 동일한 키를 저장하면 값이 갱신되는 이유를 이해했다.
* [ ] 해시 테이블의 평균 조회 성능이 빠른 이유를 이해했다.
* [ ] 저장 순서가 보장되지 않는 이유를 설명할 수 있다.

---

## 8. 최종 완성 체크리스트

* [ ] `MyHashMap.java`의 `Node` 내부 클래스 구현
* [ ] 버킷 배열과 해시 함수 구현
* [ ] `put` 구현
* [ ] `get` 구현
* [ ] `remove` 구현
* [ ] `size` 구현
* [ ] `containsKey` 구현
* [ ] 충돌 발생 시 체이닝 동작 확인
* [ ] `Main.java`에서 다양한 시나리오 테스트

---

## 9. 선택 도전 과제

1. **리사이즈**: 데이터 개수가 배열 용량의 75%를 넘으면 배열 크기를 2배로 늘리고 모든 노드를 새 인덱스로 다시 배치
2. **로드 팩터**: 리사이즈 기준인 75%를 상수로 분리해 관리
3. **값 타입 변경**: 값 타입을 `Integer`에서 `String`으로 변경해 단어와 뜻을 저장하는 사전 구현
4. **키와 값 목록 조회**: 모든 키 또는 값을 반환하는 `keySet()`과 `values()` 구현
5. **음수 해시 안전 처리**: `Math.abs(Integer.MIN_VALUE)` 문제를 방지하도록 `(key.hashCode() & 0x7fffffff) % capacity` 적용
6. **제네릭 적용**: 제네릭 학습 후 `String`과 `Integer` 고정을 `<K, V>`로 변경해 다양한 키와 값 타입 지원
