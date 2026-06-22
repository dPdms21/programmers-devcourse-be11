# List

## 구현체별 특징과 시간복잡도

| 작업                  | `ArrayList` | `LinkedList` |
| ------------------- | ----------- | ------------ |
| 인덱스 조회 `get(index)` | `O(1)`      | `O(n)`       |
| 끝에 추가               | 평균 `O(1)`   | `O(1)`       |
| 중간 삽입·삭제            | `O(n)`      | 위치 탐색 `O(n)` |
| 값 탐색 `contains()`   | `O(n)`      | `O(n)`       |

`ArrayList`의 끝 삽입은 일반적으로 `O(1)`이지만, 내부 배열의 크기를 확장하고 복사하는 시점에는 `O(n)`이 발생한다.

`LinkedList`는 삭제할 노드를 이미 알고 있다면 연결 변경이 `O(1)`이지만, 인덱스나 값을 기준으로 노드를 찾아야 하면 탐색에 `O(n)`이 필요하다.

## 간단한 List 생성

```java
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
```

배열을 이용해서도 생성할 수 있다.

```java
Integer[] array = {1, 2, 3, 4, 5};
List<Integer> list = Arrays.asList(array);
```

기본형 배열인 `int[]`는 `List<Integer>`로 바로 변환되지 않는다.

```java
int[] array = {1, 2, 3, 4, 5};

// List<Integer> list = Arrays.asList(array); // 원하는 결과가 아님
```

## `Arrays.asList()`의 주의점

`Arrays.asList()`로 만든 리스트는 배열을 기반으로 하는 고정 크기 리스트다.

```java
List<Integer> list = Arrays.asList(1, 2, 3);

list.set(0, 10);  // 가능
// list.add(4);   // 불가능
// list.remove(0); // 불가능
```

추가와 삭제가 필요한 경우 새로운 리스트로 복사한다.

```java
List<Integer> list =
        new ArrayList<>(Arrays.asList(1, 2, 3));
```

## List를 배열로 변환

참조형 배열은 `toArray()`를 사용할 수 있다.

```java
List<String> list = Arrays.asList("A", "B", "C");

String[] array = list.toArray(String[]::new);
```

`List<Integer>`를 `int[]`로 변환하려면 Stream을 사용할 수 있다.

```java
List<Integer> list = Arrays.asList(1, 2, 3);

int[] array = list.stream()
        .mapToInt(Integer::intValue)
        .toArray();
```

* `stream()`: 요소를 순차적으로 처리하는 흐름을 만든다
* `mapToInt()`: 각 요소를 기본형 `int`로 변환한다
* `Integer::intValue`: `Integer`의 `intValue()`를 호출하는 메소드 참조다
* `toArray()`: 처리 결과를 `int[]`로 만든다

## 반복 중 요소 삭제

인덱스를 증가시키면서 요소를 삭제하면, 삭제할 때마다 뒤의 요소가 앞으로 이동해 일부 요소가 건너뛰어질 수 있다.

```java
List<Integer> list =
        new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

for (int i = 0; i < list.size(); i++) {
    list.remove(i);
}
```

조건에 맞는 값을 삭제할 때는 `removeIf()`를 사용하는 것이 간단하다.

```java
list.removeIf(value -> value % 2 == 0);
```

반복자를 직접 사용할 수도 있다.

```java
Iterator<Integer> iterator = list.iterator();

while (iterator.hasNext()) {
    int value = iterator.next();

    if (value % 2 == 0) {
        iterator.remove();
    }
}
```

## 변경할 수 없는 List

Java는 객체 자체가 아니라 **참조값을 값으로 전달**한다. 메소드에서 전달받은 리스트를 수정하면 호출한 쪽의 원본 객체에도 변경이 반영될 수 있다.

```java
void update(List<Integer> list) {
    list.set(0, 100);
}
```

수정할 수 없는 뷰를 전달하려면 `Collections.unmodifiableList()`를 사용할 수 있다.

```java
List<Integer> readOnly =
        Collections.unmodifiableList(list);
```

이 리스트를 통해 `add()`, `remove()`, `set()`을 호출하면 예외가 발생한다.

단, 이는 원본 리스트를 감싸는 읽기 전용 뷰이므로 원본 리스트가 변경되면 그 결과가 읽기 전용 뷰에도 반영된다. 독립된 불변 리스트가 필요하면 `List.copyOf()`를 사용할 수 있다.

```java
List<Integer> immutable = List.copyOf(list);
```

## 기억할 판단 기준

* 인덱스 조회가 많으면 `ArrayList`가 유리하다.
* `Arrays.asList()`는 고정 크기 리스트다.
* 기본형 배열은 Wrapper 타입 리스트로 바로 변환되지 않는다.
* `List<Integer>`를 `int[]`로 바꿀 때는 `mapToInt()`를 사용할 수 있다.
* 반복 중 삭제는 `removeIf()`나 `Iterator.remove()`를 사용한다.
* `final`은 참조 재할당을 막을 뿐 객체 내부 변경까지 막지는 않는다.
* `Collections.unmodifiableList()`는 읽기 전용 뷰이며, `List.copyOf()`는 별도의 수정 불가능한 리스트를 만든다.
