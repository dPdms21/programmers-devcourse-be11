# Map

## 구현체별 특징과 시간복잡도

| 구현체                 | `get()`    | `containsKey()` | 특징                        |
| ------------------- | ---------- | --------------- | ------------------------- |
| `HashMap`           | 평균 `O(1)`  | 평균 `O(1)`       | 입력 순서를 보장하지 않는다           |
| `LinkedHashMap`     | 평균 `O(1)`  | 평균 `O(1)`       | 입력 순서를 유지한다               |
| `EnumMap`           | `O(1)`     | `O(1)`          | `enum`을 `key`로 사용할 때 적합하다 |
| `TreeMap`           | `O(log n)` | `O(log n)`      | `key`를 기준으로 정렬한다          |
| `ConcurrentHashMap` | 평균 `O(1)`  | 평균 `O(1)`       | 멀티스레드 환경에서 사용한다           |

`HashMap`은 일반적으로 저장과 조회에 평균 `O(1)`이 걸린다.

서로 다른 `key`의 해시값이 같은 위치에 배정되면 해시 충돌이 발생한다. 충돌된 데이터를 순차적으로 탐색하면 최악의 경우 `O(n)`이 걸릴 수 있으며, 트리 구조로 관리되면 `O(log n)`에 탐색할 수 있다.

## 값 저장과 조회

`Map`은 `key`와 `value`를 한 쌍으로 저장한다. `key`는 중복될 수 없으며, 같은 `key`로 다시 저장하면 기존 값이 변경된다.

```java
Map<String, Integer> map = new HashMap<>();

map.put("A", 1);
map.put("B", 2);
map.put("A", 3);
```

위 코드에서 `"A"`의 최종 값은 `3`이다.

```java
int value = map.get("A");
```

존재하지 않는 `key`를 `get()`으로 조회하면 `null`이 반환될 수 있다.

```java
boolean exists = map.containsKey("A");
```

`containsKey()`는 특정 `key`의 존재 여부를 확인한다.

```java
map.remove("A");
```

`remove()`는 해당 `key`와 연결된 값을 삭제한다.

```java
int size = map.size();
```

`size()`는 저장된 `key-value` 쌍의 개수를 반환한다.

## 값의 개수 누적

`getOrDefault()`는 `key`가 존재하면 기존 값을 반환하고, 존재하지 않으면 지정한 기본값을 반환한다.

```java
Map<String, Integer> map = new HashMap<>();

for (String value : values) {
    map.put(value, map.getOrDefault(value, 0) + 1);
}
```

동작 흐름은 다음과 같다.

```text
key가 존재함
→ 기존 값을 가져옴
→ 1 증가
→ 다시 저장

key가 존재하지 않음
→ 기본값 0을 사용
→ 1 증가
→ 새로 저장
```

문자열이나 숫자의 등장 횟수를 계산할 때 자주 사용하는 방식이다.

## Map 순회

### `entrySet()`

`key`와 `value`가 모두 필요할 때 사용한다.

```java
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    String key = entry.getKey();
    int value = entry.getValue();

    System.out.println(key + " : " + value);
}
```

`Map.Entry`는 하나의 `key-value` 쌍을 나타낸다.

### `keySet()`

모든 `key`를 순회할 때 사용한다.

```java
for (String key : map.keySet()) {
    System.out.println(key);
}
```

`key`를 이용해 값도 조회할 수 있다.

```java
for (String key : map.keySet()) {
    System.out.println(key + " : " + map.get(key));
}
```

`key`와 `value`가 모두 필요한 경우에는 값을 다시 조회하지 않아도 되는 `entrySet()`이 더 직접적이다.

### `values()`

모든 `value`만 순회할 때 사용한다.

```java
for (int value : map.values()) {
    System.out.println(value);
}
```

```text
key와 value가 모두 필요함
→ entrySet()

key만 필요함
→ keySet()

value만 필요함
→ values()
```

## Iterator 사용

`entrySet()`과 `keySet()`은 `Set`을 반환하므로 `Iterator`로 순회할 수 있다.

```java
Iterator<Map.Entry<String, Integer>> iterator =
        map.entrySet().iterator();

while (iterator.hasNext()) {
    Map.Entry<String, Integer> entry = iterator.next();

    System.out.println(
            entry.getKey() + " : " + entry.getValue()
    );
}
```

Java 10 이상에서는 `var`를 사용해 지역 변수의 타입을 추론할 수 있다.

```java
var iterator = map.entrySet().iterator();

while (iterator.hasNext()) {
    var entry = iterator.next();
    System.out.println(entry.getKey() + " : " + entry.getValue());
}
```

`var`는 초기값을 통해 타입을 추론하며 지역 변수에만 사용할 수 있다.

## 생성과 동시에 초기화할 때의 주의점

double brace initialization을 사용하면 객체 생성과 값 저장을 한 번에 작성할 수 있다.

```java
Map<String, Integer> map = new HashMap<>() {{
    put("A", 1);
    put("B", 2);
    put("C", 3);
}};
```

코드가 짧아질 수 있지만 익명 하위 클래스가 생성되고, 외부 객체에 대한 숨겨진 참조가 남을 수 있다.

일반적으로는 객체를 생성한 뒤 `put()`으로 값을 추가하는 방식이 더 명확하다.

```java
Map<String, Integer> map = new HashMap<>();

map.put("A", 1);
map.put("B", 2);
map.put("C", 3);
```

## 기억할 판단 기준

* 빠른 저장과 조회가 필요하면 `HashMap`을 사용한다.
* 입력 순서를 유지해야 하면 `LinkedHashMap`을 사용한다.
* `key` 정렬이 필요하면 `TreeMap`을 사용한다.
* `enum`을 `key`로 사용하면 `EnumMap`을 고려한다.
* 값의 등장 횟수는 `getOrDefault(key, 0) + 1`로 누적할 수 있다.
* `key`와 `value`가 모두 필요하면 `entrySet()`을 사용한다.
* `key`만 필요하면 `keySet()`을 사용한다.
* `value`만 필요하면 `values()`를 사용한다.
* double brace initialization은 익명 클래스가 생성되므로 사용에 주의한다.
