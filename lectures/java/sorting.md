# 정렬

## 정렬 기능 추상화

정렬 알고리즘마다 구현 방식은 다르지만, 입력받은 `List`를 정렬해 새로운 `List`로 반환한다는 공통 기능을 갖는다.

```java
public interface Sortable {

    <T> List<T> sort(List<T> list, Comparator<T> comparator);

    default <T extends Comparable<T>> List<T> sort(List<T> list) {
        return sort(list, Comparable::compareTo);
    }
}
```

`Comparator`를 전달하면 원하는 기준으로 정렬할 수 있다.

요소가 `Comparable`을 구현한 경우에는 기본 비교 기준을 사용할 수 있다.

원본을 유지하려면 입력받은 `List`를 복사한 뒤 복사본을 정렬한다.

```java
List<T> copy = new ArrayList<>(list);
```

이때 복사되는 것은 객체 자체가 아니라 객체의 참조값이다.

## Bubble Sort

인접하거나 비교 대상이 되는 두 값을 확인하고, 순서가 잘못되어 있으면 위치를 교환한다.

이 과정을 반복하면서 큰 값이 뒤쪽으로 이동한다.

* 구현이 단순하다.
* 평균 및 최악 시간복잡도는 `O(n²)`이다.
* 데이터가 많을수록 비효율적이다.

## Insertion Sort

정렬된 구간에 새로운 값을 알맞은 위치로 삽입한다.

현재 값을 앞쪽 값들과 비교하면서 들어갈 위치를 찾는다.

* 일부 데이터가 이미 정렬되어 있을 때 효율적이다.
* 평균 및 최악 시간복잡도는 `O(n²)`이다.
* 작은 데이터 집합을 정렬할 때 활용할 수 있다.

## Selection Sort

정렬되지 않은 구간에서 가장 작은 값을 찾은 뒤 현재 위치와 교환한다.

```text
최솟값 탐색 → 현재 위치에 배치 → 다음 구간 반복
```

* 구현이 단순하다.
* 입력 상태와 관계없이 비교 횟수가 많다.
* 시간복잡도는 `O(n²)`이다.

## Quick Sort

기준값인 `pivot`을 정하고, 값을 두 그룹으로 분할한다.

```text
pivot보다 작은 값 | pivot | pivot보다 크거나 같은 값
```

분할된 각 그룹에 같은 과정을 재귀적으로 반복한 뒤 결과를 합친다.

* 평균 시간복잡도는 `O(n log n)`이다.
* 분할이 한쪽으로 치우치면 최악 시간복잡도는 `O(n²)`이다.
* `pivot` 선택에 따라 분할 결과와 성능이 달라진다.

원본 데이터의 배치를 알 수 없다면 특정 위치의 값을 `pivot`으로 선택해 사용한다.

## Merge Sort

데이터를 더 이상 나눌 수 없을 때까지 절반으로 분할한다.

분할된 데이터를 정렬하면서 다시 병합한다.

```text
분할 → 부분 정렬 → 병합
```

병합할 때는 두 부분 리스트의 앞쪽 값을 비교해 더 작은 값을 결과에 추가한다.

* 시간복잡도는 항상 `O(n log n)`이다.
* 정렬 과정에서 추가 공간이 필요하다.
* 분할 정복과 재귀를 사용한다.

## 정렬 알고리즘 비교

| 알고리즘           |     평균 시간복잡도 |     최악 시간복잡도 | 핵심 방식         |
| -------------- | -----------: | -----------: | ------------- |
| Bubble Sort    |      `O(n²)` |      `O(n²)` | 값을 비교해 위치 교환  |
| Insertion Sort |      `O(n²)` |      `O(n²)` | 정렬된 구간에 값 삽입  |
| Selection Sort |      `O(n²)` |      `O(n²)` | 최솟값을 찾아 배치    |
| Quick Sort     | `O(n log n)` |      `O(n²)` | `pivot` 기준 분할 |
| Merge Sort     | `O(n log n)` | `O(n log n)` | 분할 후 병합       |

## Tim Sort

Tim Sort는 Insertion Sort와 Merge Sort를 결합한 정렬 알고리즘이다.

데이터를 작은 구간으로 나누고 각 구간을 정렬한 뒤 병합한다.

이미 정렬된 데이터의 특성을 활용할 수 있어 실제 라이브러리의 객체 정렬에도 사용된다.

## 테스트

각 정렬 구현체가 동일한 정렬 결과를 반환하는지 테스트한다.

```java
@Test
void bubbleSort() {
    List<Integer> list = List.of(5, 4, 3, 2, 1);

    List<Integer> result = new BubbleSort().sort(list);

    assertEquals(List.of(1, 2, 3, 4, 5), result);
}
```

다른 정렬 알고리즘도 같은 입력과 예상 결과를 기준으로 검증한다.

## 기억할 판단 기준

* 간단한 정렬 원리를 이해할 때는 Bubble Sort, Insertion Sort, Selection Sort를 확인한다.
* 평균적으로 빠른 정렬이 필요하면 Quick Sort를 고려한다.
* 최악의 경우에도 `O(n log n)`을 유지해야 하면 Merge Sort를 고려한다.
* Quick Sort는 `pivot`에 따라 성능이 달라질 수 있다.
* Merge Sort는 안정적인 성능을 제공하지만 추가 공간이 필요하다.
* 정렬 기준을 외부에서 전달하려면 `Comparator`를 사용한다.
* 원본을 유지해야 하면 복사본을 만든 뒤 정렬한다.
