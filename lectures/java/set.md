# Set

## 구현체별 특징과 시간복잡도

| 구현체             |    `add()` | `contains()` | 특징               |
| --------------- | ---------: | -----------: | ---------------- |
| `HashSet`       |  평균 `O(1)` |    평균 `O(1)` | 순서를 보장하지 않는다     |
| `LinkedHashSet` |  평균 `O(1)` |    평균 `O(1)` | 삽입 순서를 유지한다      |
| `TreeSet`       | `O(log n)` |   `O(log n)` | 요소를 정렬된 상태로 유지한다 |

`HashSet`은 해시 구조를 사용하므로 추가와 탐색이 평균적으로 빠르다.

다만 순회할 때는 저장된 요소 수뿐 아니라 내부 버킷의 크기도 영향을 줄 수 있다.

## 집합 연산

### 합집합

`addAll()`은 다른 집합의 요소를 모두 추가한다.

```java
Set<String> union = new HashSet<>(set1);
union.addAll(set2);
```

### 차집합

`removeAll()`은 다른 집합에 포함된 요소를 제거한다.

```java
Set<String> difference = new HashSet<>(set1);
difference.removeAll(set2);
```

### 교집합

`retainAll()`은 두 집합에 공통으로 포함된 요소만 남긴다.

```java
Set<String> intersection = new HashSet<>(set1);
intersection.retainAll(set2);
```

집합 연산 메서드는 호출한 `Set`의 내용을 변경한다.

원본을 유지해야 하면 새로운 `Set`에 복사한 뒤 연산한다.

## List의 중복 요소 제거

`List`를 `Set`으로 변환하면 중복 요소를 제거할 수 있다.

```java
List<String> list =
        Arrays.asList("A", "B", "C", "A", "B", "C");

Set<String> set = new HashSet<>(list);
List<String> result = new ArrayList<>(set);
```

`HashSet`은 순서를 보장하지 않는다.

기존 순서를 유지하려면 `LinkedHashSet`을 사용한다.

```java
List<String> result =
        new ArrayList<>(new LinkedHashSet<>(list));
```

Stream의 `distinct()`를 사용할 수도 있다.

```java
List<String> result = list.stream()
        .distinct()
        .toList();
```

## List로 집합 연산 구현

`List`에서도 `contains()`를 이용해 집합 연산과 비슷한 처리를 할 수 있다.

```java
<T> List<T> union(List<T> list1, List<T> list2) {
    List<T> result = new ArrayList<>(list1);

    for (T value : list2) {
        if (!result.contains(value)) {
            result.add(value);
        }
    }

    return result;
}
```

다만 `List.contains()`는 일반적으로 `O(n)`이므로 반복문 안에서 계속 호출하면 처리 시간이 증가할 수 있다.

중복 검사와 포함 여부 확인이 중심이라면 `HashSet`을 사용하는 것이 더 효율적이다.

## 기억할 판단 기준

* 중복을 허용하지 않으려면 `Set`을 사용한다.
* 빠른 추가와 탐색이 필요하면 `HashSet`을 사용한다.
* 삽입 순서를 유지해야 하면 `LinkedHashSet`을 사용한다.
* 정렬된 상태가 필요하면 `TreeSet`을 사용한다.
* 합집합은 `addAll()`, 차집합은 `removeAll()`, 교집합은 `retainAll()`을 사용한다.
* `List`의 중복 제거에는 `Set` 변환이나 `distinct()`를 사용할 수 있다.
* 원본을 유지해야 하면 복사본에서 집합 연산을 수행한다.
