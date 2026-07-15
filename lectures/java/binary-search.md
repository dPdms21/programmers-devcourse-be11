# Binary Search

## 이진 탐색

이진 탐색은 정렬된 데이터에서 찾는 값을 빠르게 찾는 탐색 방법이다.

데이터의 가운데 값을 기준으로 찾는 값이 왼쪽에 있는지, 오른쪽에 있는지를 판단하면서 탐색 범위를 절반씩 줄인다.

```text
1 2 3 4 5 6 7 8 9 10
```

예를 들어 `4`를 찾는다면 가운데 값을 확인한 뒤, 필요한 범위만 남기고 나머지 범위는 버린다.

## 구현 흐름

이진 탐색은 최소 범위와 최대 범위를 정한 뒤 가운데 인덱스를 계산하면서 진행한다.

```java
int min = 0;
int max = data.size();
```

가운데 인덱스는 현재 탐색 범위 안에서 구해야 한다.

```java
int mid = min + (max - min) / 2;
```

가운데 값과 찾는 값을 비교한다.

```java
T value = data.get(mid);
int compare = value.compareTo(target);
```

비교 결과에 따라 탐색 범위를 줄인다.

```java
if (compare == 0) {
    return value;
}

if (compare < 0) {
    min = mid + 1;
} else {
    max = mid;
}
```

## 제네릭과 Comparable

이진 탐색은 값의 대소 비교가 필요하다.

따라서 어떤 타입이든 받을 수 있게 제네릭을 사용하되, 비교 가능한 타입으로 제한해야 한다.

```java
static <T extends Comparable<T>> T binarySearch(List<T> data, T target)
```

`Comparable`을 제한 조건으로 두면 `compareTo()`를 사용할 수 있다.

## 전체 코드

```java
static <T extends Comparable<T>> T binarySearch(List<T> data, T target) {
    int min = 0;
    int max = data.size();

    while (min < max) {
        int mid = min + (max - min) / 2;
        T value = data.get(mid);

        int compare = value.compareTo(target);

        if (compare == 0) {
            return value;
        }

        if (compare < 0) {
            min = mid + 1;
        } else {
            max = mid;
        }
    }

    return null;
}
```

찾는 값이 있으면 해당 값을 반환하고, 없으면 `null`을 반환한다.

## 중간 인덱스 계산 주의점

가운데 인덱스를 다음처럼 계산하면 현재 탐색 범위가 반영되지 않는다.

```java
int mid = (max - min) / 2;
```

예를 들어 탐색 범위가 `3 ~ 5`인 경우 가운데 인덱스는 `4`가 되어야 한다.

하지만 위 방식은 `1`을 반환하므로 잘못된 위치를 확인하게 된다.

현재 범위의 시작점인 `min`을 더해야 한다.

```java
int mid = min + (max - min) / 2;
```

## 유닛 테스트

구현한 함수는 정상 동작과 실패 상황을 함께 검증해야 한다.

```java
@Test
@DisplayName("찾는 값이 있으면 해당 값을 반환한다")
void binarySearch_ok() {
    List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    Integer found = binarySearch(list, 4);

    assertEquals(4, found);
}
```

```java
@Test
@DisplayName("찾는 값이 없으면 null을 반환한다")
void binarySearch_notFound() {
    List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    Integer found = binarySearch(list, 40);

    assertNull(found);
}
```

## 기억할 판단 기준

* 이진 탐색은 정렬된 데이터에서만 사용할 수 있다.
* 탐색 범위를 절반씩 줄이므로 시간복잡도는 `O(log n)`이다.
* 대소 비교가 필요하므로 제네릭 타입에는 `Comparable` 제한을 둔다.
* 중간 인덱스는 `min + (max - min) / 2`로 계산한다.
* 찾는 값이 없을 때의 결과도 테스트해야 한다.
* 구현 후에는 정상 케이스와 실패 케이스를 모두 유닛 테스트로 확인한다.
