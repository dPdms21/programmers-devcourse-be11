# 자바 스트림 익히기

> 스트림(Stream)은 컬렉션의 데이터를 선언적인 파이프라인 형태로 처리하는 기능이다.
>
> `for`문과 `if`문으로 작성한 로직을 `stream()`, `filter()`, `map()`, `collect()`를 사용하는 구조로 변경하며 스트림의 동작을 확인한다.
>
> 상품 목록을 필터링하고, 변환하고, 새로운 컬렉션으로 모으며 통계를 계산하는 과정을 단계별로 구현한다.
>
> 아래 Step을 순서대로 진행하면 스트림의 기본 구조와 주요 연산을 익힐 수 있다.

---

## 0. 먼저 알아둘 점

스트림은 컬렉션의 데이터를 순차적으로 처리하기 위한 기능이다.

`filter()`, `map()`과 같은 연산에 람다를 전달해 처리할 동작을 정의한다.

스트림 파이프라인은 다음 세 단계로 구성된다.

1. 스트림 생성
2. 중간 연산
3. 종료 연산

```java
products.stream()
        .filter(product ->
                product.getPrice() >= 1000
        )
        .map(Product::getName)
        .toList();
```

* `stream()`: 컬렉션에서 스트림을 생성한다.
* `filter()`, `map()`: 데이터를 가공하는 중간 연산이다.
* `toList()`: 처리 결과를 리스트로 만드는 종료 연산이다.

중간 연산은 스트림을 반환하므로 여러 연산을 연결할 수 있다.

종료 연산이 호출되어야 스트림의 연산이 실제로 실행된다.

합계와 평균처럼 숫자 연산이 필요한 경우 `mapToInt()`를 사용해 `IntStream`으로 변환한 뒤 `sum()`, `average()` 등을 사용한다.

이 과제에서는 Java에서 제공하는 `List<Product>`, `Stream<Product>` 등의 제네릭 타입을 사용하며 직접 제네릭 타입을 정의하지 않는다.

---

## 1. 무엇을 만드나요?

상품 목록을 스트림으로 필터링하고 변환하며, 주문의 중첩된 상품 목록을 평탄화하고 통계를 계산하는 프로그램을 완성한다.

최종 실행 결과는 다음과 같다.

```text
===== 1. 스트림 만들고 전체 출력 =====
연필 (500원)
공책 (1200원)
지우개 (300원)
필통 (3000원)
볼펜 (800원)

===== 2. filter: 1000원 이상 =====
공책 (1200원)
필통 (3000원)

===== 3. map: 이름만 추출 =====
연필
공책
지우개
필통
볼펜

===== 4. map과 flatMap =====
map     : [[연필, 공책], [필통, 볼펜, 공책]]
flatMap : [연필, 공책, 필통, 볼펜, 공책]

===== 5. filter + map + collect =====
[공책, 필통]

===== 6. 통계 =====
1000원 이상 개수: 2
전체 가격 합계: 5800
전체 가격 평균: 1160.0
가격 오름차순: [지우개, 연필, 볼펜, 공책, 필통]
```

핵심은 다음 파이프라인이다.

```java
products.stream()
        .filter(product ->
                product.getPrice() >= 1000
        )
        .map(Product::getName)
        .toList();
```

조건에 맞는 상품을 남기고, 상품을 이름으로 변환한 뒤, 결과를 리스트로 수집한다.

또한 `map()`과 `flatMap()`의 차이를 통해 중첩된 데이터와 평탄화된 데이터의 구조를 비교한다.

---

## 2. 학습 목표

| 개념                                          | 학습 위치                              |
| ------------------------------------------- | ---------------------------------- |
| 스트림 생성과 `forEach()`                         | Step 1 (`Main.java`)               |
| `filter()`를 이용한 조건 처리                       | Step 2 (`Main.java`)               |
| `map()`을 이용한 데이터 변환                         | Step 3 (`Main.java`)               |
| `map()`과 `flatMap()`의 차이                    | Step 4 (`Order.java`, `Main.java`) |
| `filter()`, `map()`, `collect()` 파이프라인      | Step 5 (`Main.java`)               |
| `count()`, `sum()`, `average()`, `sorted()` | Step 6 (`Main.java`)               |

---

## 3. 핵심 개념

### 3.1 스트림 파이프라인

스트림은 생성, 중간 연산, 종료 연산의 흐름으로 구성된다.

```java
products.stream()
        .filter(product ->
                product.getPrice() >= 1000
        )
        .map(Product::getName)
        .collect(Collectors.toList());
```

#### 스트림 생성

```java
products.stream()
```

컬렉션의 요소를 순차적으로 처리할 수 있는 스트림을 생성한다.

#### 중간 연산

```java
.filter(...)
.map(...)
.sorted(...)
```

중간 연산은 데이터를 필터링하거나 변환하고 새로운 스트림을 반환한다.

중간 연산만 선언하면 실제 처리는 시작되지 않는다.

#### 종료 연산

```java
.forEach(...)
.collect(...)
.count()
.sum()
```

종료 연산은 스트림을 실행하고 최종 결과를 반환한다.

하나의 스트림은 종료 연산을 호출한 뒤 다시 사용할 수 없다.

---

### 3.2 반복문과 스트림 비교

다음 코드는 가격이 1,000원 이상인 상품의 이름을 리스트로 저장한다.

#### 반복문 사용

```java
List<String> result =
        new ArrayList<>();

for (Product product : products) {
    if (product.getPrice() >= 1000) {
        result.add(
                product.getName()
        );
    }
}
```

#### 스트림 사용

```java
List<String> result =
        products.stream()
                .filter(product ->
                        product.getPrice() >= 1000
                )
                .map(Product::getName)
                .collect(
                        Collectors.toList()
                );
```

반복문은 요소를 순회하고 조건을 검사하며 결과를 추가하는 과정을 직접 작성한다.

스트림은 필터링, 변환, 수집과 같이 수행할 작업을 연산 단위로 표현한다.

---

### 3.3 `filter()`

`filter()`는 조건이 `true`인 요소만 다음 연산으로 전달한다.

```java
products.stream()
        .filter(product ->
                product.getPrice() >= 1000
        )
```

`filter()`는 `boolean` 값을 반환하는 조건식을 전달받는다.

```text
연필 500원
→ false
→ 제외

공책 1200원
→ true
→ 통과
```

반복문의 `if`문과 비슷한 역할을 한다.

---

### 3.4 `map()`

`map()`은 스트림의 각 요소를 다른 값으로 변환한다.

```java
products.stream()
        .map(Product::getName)
```

변환 전과 변환 후의 스트림 타입은 달라질 수 있다.

```text
Stream<Product>
→ map(Product::getName)
→ Stream<String>
```

`map()`은 각 입력 요소를 하나의 출력 요소로 바꾸는 일대일 변환이다.

---

### 3.5 `map()`과 `flatMap()`

주문 객체가 상품 이름 목록을 가지고 있다고 가정한다.

```text
주문 1 → [연필, 공책]
주문 2 → [필통, 볼펜, 공책]
```

`map()`으로 상품 목록을 추출하면 리스트가 중첩된다.

```java
List<List<String>> result =
        orders.stream()
                .map(Order::getItems)
                .collect(
                        Collectors.toList()
                );
```

```text
[[연필, 공책], [필통, 볼펜, 공책]]
```

스트림 타입은 다음과 같다.

```text
Stream<Order>
→ Stream<List<String>>
```

`flatMap()`은 각 리스트를 스트림으로 변환한 뒤 하나의 스트림으로 연결한다.

```java
List<String> result =
        orders.stream()
                .flatMap(order ->
                        order.getItems()
                                .stream()
                )
                .collect(
                        Collectors.toList()
                );
```

```text
[연필, 공책, 필통, 볼펜, 공책]
```

스트림 타입은 다음과 같다.

```text
Stream<Order>
→ Stream<String>
```

`map()`은 변환 결과의 구조를 유지하고, `flatMap()`은 중첩된 구조를 한 단계 평탄화한다.

---

### 3.6 `collect()`와 `toList()`

`collect()`는 스트림의 요소를 리스트, 집합, 맵 등의 자료구조로 수집하는 종료 연산이다.

```java
List<String> names =
        products.stream()
                .map(Product::getName)
                .collect(
                        Collectors.toList()
                );
```

Java 16 이상에서는 다음과 같이 `toList()`를 사용할 수 있다.

```java
List<String> names =
        products.stream()
                .map(Product::getName)
                .toList();
```

두 방식 모두 스트림의 결과를 리스트로 만든다.

다만 `Stream.toList()`로 생성한 리스트는 수정할 수 없는 리스트이므로 이후 요소를 추가하거나 삭제해야 한다면 `Collectors.toList()` 등을 사용하는 것이 적절하다.

---

### 3.7 숫자 스트림

일반적인 `Stream<Product>`에서는 바로 `sum()`이나 `average()`를 호출할 수 없다.

상품의 가격을 숫자 스트림으로 변환해야 한다.

```java
IntStream priceStream =
        products.stream()
                .mapToInt(
                        Product::getPrice
                );
```

`IntStream`에서는 다음 연산을 사용할 수 있다.

```java
sum()
average()
max()
min()
```

예시는 다음과 같다.

```java
int sum =
        products.stream()
                .mapToInt(
                        Product::getPrice
                )
                .sum();
```

```java
double average =
        products.stream()
                .mapToInt(
                        Product::getPrice
                )
                .average()
                .orElse(0.0);
```

`average()`는 요소가 없는 경우 결과가 존재하지 않을 수 있으므로 `OptionalDouble`을 반환한다.

따라서 `getAsDouble()`보다는 `orElse()`를 사용하면 빈 스트림도 안전하게 처리할 수 있다.

---

### 3.8 `sorted()`

`sorted()`는 스트림의 요소를 정렬하는 중간 연산이다.

가격을 기준으로 오름차순 정렬하는 코드는 다음과 같다.

```java
products.stream()
        .sorted(
                Comparator.comparingInt(
                        Product::getPrice
                )
        )
```

람다로 직접 비교할 수도 있다.

```java
products.stream()
        .sorted((first, second) ->
                Integer.compare(
                        first.getPrice(),
                        second.getPrice()
                )
        )
```

정수 값을 비교할 때 단순 뺄셈보다 `Integer.compare()`나 `Comparator.comparingInt()`를 사용하는 편이 더 명확하고 안전하다.

---

### 3.9 자주 사용하는 스트림 연산

| 연산           | 역할               | 구분    |
| ------------ | ---------------- | ----- |
| `filter()`   | 조건에 맞는 요소만 유지    | 중간 연산 |
| `map()`      | 요소를 다른 값으로 변환    | 중간 연산 |
| `flatMap()`  | 중첩된 스트림을 평탄화     | 중간 연산 |
| `sorted()`   | 요소 정렬            | 중간 연산 |
| `distinct()` | 중복 제거            | 중간 연산 |
| `limit()`    | 앞에서부터 지정한 개수만 유지 | 중간 연산 |
| `forEach()`  | 요소별 작업 수행        | 종료 연산 |
| `collect()`  | 컬렉션 등으로 수집       | 종료 연산 |
| `toList()`   | 리스트로 수집          | 종료 연산 |
| `count()`    | 요소 개수 반환         | 종료 연산 |
| `sum()`      | 숫자 스트림의 합계 반환    | 종료 연산 |
| `average()`  | 숫자 스트림의 평균 반환    | 종료 연산 |

---

## 4. 파일 구조

| 파일             | 역할                        |
| -------------- | ------------------------- |
| `Product.java` | 상품 이름과 가격을 저장하는 클래스       |
| `Order.java`   | 주문 번호와 상품 이름 목록을 저장하는 클래스 |
| `Main.java`    | 스트림 연산을 단계별로 실행하는 진입점     |

실행 명령은 다음과 같다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 5. Step by Step

### Step 1. 스트림을 생성하고 전체 출력하기 (`Product.java`, `Main.java`)

**목표**: 상품 리스트에서 스트림을 생성하고 `forEach()`로 각 상품을 출력한다.

**할 일**

1. 이름과 가격을 필드로 갖는 `Product` 클래스를 작성한다.
2. 상품 다섯 개를 저장하는 `List<Product>`를 생성한다.
3. `stream()`으로 스트림을 생성한다.
4. `forEach()`로 상품 이름과 가격을 출력한다.

<details>
<summary>힌트 보기</summary>

```java
class Product {

    private final String name;
    private final int price;

    Product(
            String name,
            int price
    ) {
        this.name = name;
        this.price = price;
    }

    String getName() {
        return name;
    }

    int getPrice() {
        return price;
    }
}
```

```java
List<Product> products =
        new ArrayList<>(
                Arrays.asList(
                        new Product("연필", 500),
                        new Product("공책", 1200),
                        new Product("지우개", 300),
                        new Product("필통", 3000),
                        new Product("볼펜", 800)
                )
        );

products.stream()
        .forEach(product ->
                System.out.println(
                        product.getName()
                        + " ("
                        + product.getPrice()
                        + "원)"
                )
        );
```

`stream()`으로 상품 스트림을 생성하고 `forEach()`에서 각 상품을 처리한다.

`forEach()`는 스트림을 종료하는 연산이다.

</details>

**확인**

* 상품 다섯 개가 모두 출력되는지 확인한다.
* 상품 이름과 가격이 `이름 (가격원)` 형식으로 출력되는지 확인한다.
* `forEach()`가 종료 연산이라는 점을 확인한다.

---

### Step 2. `filter()`로 조건에 맞는 상품만 남기기 (`Main.java`)

**목표**: 가격이 1,000원 이상인 상품만 출력한다.

**할 일**

1. 상품 리스트에서 스트림을 생성한다.
2. `filter()`로 가격이 1,000원 이상인 상품만 남긴다.
3. `forEach()`로 결과를 출력한다.

<details>
<summary>힌트 보기</summary>

```java
products.stream()
        .filter(product ->
                product.getPrice() >= 1000
        )
        .forEach(product ->
                System.out.println(
                        product.getName()
                        + " ("
                        + product.getPrice()
                        + "원)"
                )
        );
```

`filter()`의 조건식이 `true`인 상품만 `forEach()`로 전달된다.

</details>

**확인**

* 공책과 필통만 출력되는지 확인한다.
* 연필, 지우개, 볼펜이 제외되는지 확인한다.
* `filter()`가 중간 연산이라는 점을 확인한다.

---

### Step 3. `map()`으로 상품 이름 추출하기 (`Main.java`)

**목표**: `Product` 객체를 상품 이름인 `String`으로 변환한다.

**할 일**

1. 상품 스트림을 생성한다.
2. `map()`으로 각 상품을 상품 이름으로 변환한다.
3. 이름을 한 줄씩 출력한다.

<details>
<summary>힌트 보기</summary>

```java
products.stream()
        .map(Product::getName)
        .forEach(
                System.out::println
        );
```

람다로 작성하면 다음과 같다.

```java
products.stream()
        .map(product ->
                product.getName()
        )
        .forEach(name ->
                System.out.println(name)
        );
```

`map()` 전에는 `Product` 객체가 흐르고, `map()` 이후에는 `String` 값이 흐른다.

</details>

**확인**

* 상품 이름 다섯 개만 출력되는지 확인한다.
* `map()` 전후의 스트림 타입이 달라진다는 점을 확인한다.
* `map()`이 일대일 변환이라는 점을 설명할 수 있는지 확인한다.

---

### Step 4. `map()`과 `flatMap()` 비교하기 (`Order.java`, `Main.java`)

**목표**: 주문마다 저장된 상품 목록을 중첩된 리스트와 평탄화된 리스트로 각각 변환한다.

**할 일**

1. 주문 번호와 상품 목록을 갖는 `Order` 클래스를 작성한다.
2. 두 개의 주문을 생성한다.
3. `map()`으로 각 주문의 상품 목록을 추출한다.
4. `flatMap()`으로 모든 주문의 상품을 하나의 스트림으로 연결한다.
5. 두 결과의 구조를 비교한다.

<details>
<summary>힌트 보기</summary>

```java
class Order {

    private final int id;
    private final List<String> items;

    Order(
            int id,
            List<String> items
    ) {
        this.id = id;
        this.items = items;
    }

    int getId() {
        return id;
    }

    List<String> getItems() {
        return items;
    }
}
```

```java
List<Order> orders =
        Arrays.asList(
                new Order(
                        1,
                        Arrays.asList(
                                "연필",
                                "공책"
                        )
                ),
                new Order(
                        2,
                        Arrays.asList(
                                "필통",
                                "볼펜",
                                "공책"
                        )
                )
        );
```

```java
List<List<String>> byMap =
        orders.stream()
                .map(Order::getItems)
                .collect(
                        Collectors.toList()
                );

System.out.println(
        "map     : "
        + byMap
);
```

```java
List<String> byFlatMap =
        orders.stream()
                .flatMap(order ->
                        order.getItems()
                                .stream()
                )
                .collect(
                        Collectors.toList()
                );

System.out.println(
        "flatMap : "
        + byFlatMap
);
```

`map()`은 주문 하나를 상품 목록 하나로 변환하므로 결과가 `List<List<String>>`가 된다.

`flatMap()`은 각 상품 목록을 스트림으로 변환하고 하나로 연결하므로 결과가 `List<String>`이 된다.

</details>

**확인**

* `map()` 결과가 `[[연필, 공책], [필통, 볼펜, 공책]]`인지 확인한다.
* `flatMap()` 결과가 `[연필, 공책, 필통, 볼펜, 공책]`인지 확인한다.
* `flatMap()`에서 `.stream()`을 반환해야 하는 이유를 설명할 수 있는지 확인한다.

---

### Step 5. `filter()`, `map()`, `collect()` 연결하기 (`Main.java`)

**목표**: 가격이 1,000원 이상인 상품의 이름을 새로운 리스트로 만든다.

**할 일**

1. `filter()`로 가격이 1,000원 이상인 상품을 선택한다.
2. `map()`으로 상품을 이름으로 변환한다.
3. `collect()`로 이름을 리스트에 저장한다.
4. 결과를 출력한다.

<details>
<summary>힌트 보기</summary>

```java
List<String> expensiveNames =
        products.stream()
                .filter(product ->
                        product.getPrice() >= 1000
                )
                .map(Product::getName)
                .collect(
                        Collectors.toList()
                );

System.out.println(
        expensiveNames
);
```

Java 16 이상에서는 다음과 같이 작성할 수도 있다.

```java
List<String> expensiveNames =
        products.stream()
                .filter(product ->
                        product.getPrice() >= 1000
                )
                .map(Product::getName)
                .toList();
```

</details>

**확인**

* 결과가 `[공책, 필통]`인지 확인한다.
* `filter()`와 `map()`이 중간 연산인지 확인한다.
* `collect()` 또는 `toList()`가 종료 연산인지 확인한다.

---

### Step 6. 개수, 합계, 평균, 정렬 구하기 (`Main.java`)

**목표**: 상품 데이터를 이용해 개수, 가격 합계, 평균, 가격순 목록을 구한다.

**할 일**

1. `count()`로 가격이 1,000원 이상인 상품 개수를 구한다.
2. `mapToInt()`와 `sum()`으로 전체 가격 합계를 구한다.
3. `mapToInt()`와 `average()`로 전체 가격 평균을 구한다.
4. `sorted()`로 가격 오름차순 정렬을 수행한다.
5. 정렬된 상품의 이름을 리스트로 만든다.

<details>
<summary>힌트 보기</summary>

```java
long count =
        products.stream()
                .filter(product ->
                        product.getPrice() >= 1000
                )
                .count();
```

```java
int sum =
        products.stream()
                .mapToInt(
                        Product::getPrice
                )
                .sum();
```

```java
double average =
        products.stream()
                .mapToInt(
                        Product::getPrice
                )
                .average()
                .orElse(0.0);
```

```java
List<String> byPrice =
        products.stream()
                .sorted(
                        Comparator.comparingInt(
                                Product::getPrice
                        )
                )
                .map(Product::getName)
                .collect(
                        Collectors.toList()
                );
```

```java
System.out.println(
        "1000원 이상 개수: "
        + count
);

System.out.println(
        "전체 가격 합계: "
        + sum
);

System.out.println(
        "전체 가격 평균: "
        + average
);

System.out.println(
        "가격 오름차순: "
        + byPrice
);
```

</details>

**확인**

* 가격이 1,000원 이상인 상품 개수가 `2`인지 확인한다.
* 전체 가격 합계가 `5800`인지 확인한다.
* 전체 가격 평균이 `1160.0`인지 확인한다.
* 가격 오름차순 결과가 `[지우개, 연필, 볼펜, 공책, 필통]`인지 확인한다.

---

### Step 7. Main에서 전체 실행하기 (`Main.java`)

**목표**: 스트림의 생성, 필터링, 변환, 평탄화, 수집, 통계 연산을 순서대로 실행한다.

**할 일**

1. `forEach()`로 전체 상품을 출력한다.
2. `filter()`로 조건에 맞는 상품을 출력한다.
3. `map()`으로 상품 이름만 출력한다.
4. `map()`과 `flatMap()`의 결과를 비교한다.
5. `filter()`, `map()`, `collect()`를 연결해 리스트를 생성한다.
6. 개수, 합계, 평균, 정렬 결과를 출력한다.

**확인**

다음 명령으로 실행했을 때 1번 섹션의 출력과 같은 결과가 나오는지 확인한다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 6. 학습 체크

* [ ] 스트림이 컬렉션 데이터를 처리하는 기능이라는 점을 설명할 수 있다.
* [ ] 스트림 생성, 중간 연산, 종료 연산의 흐름을 설명할 수 있다.
* [ ] 종료 연산이 호출되어야 연산이 실행된다는 점을 이해했다.
* [ ] `filter()`에 조건을 반환하는 람다를 전달할 수 있다.
* [ ] `map()`으로 요소를 다른 타입의 값으로 변환할 수 있다.
* [ ] `map()`이 일대일 변환이라는 점을 설명할 수 있다.
* [ ] `flatMap()`으로 중첩된 데이터를 평탄화할 수 있다.
* [ ] `flatMap()`의 람다가 스트림을 반환해야 하는 이유를 설명할 수 있다.
* [ ] `collect()`와 `toList()`를 이용해 결과를 리스트로 만들 수 있다.
* [ ] `mapToInt()`가 필요한 이유를 설명할 수 있다.
* [ ] `count()`, `sum()`, `average()`를 사용할 수 있다.
* [ ] `sorted()`와 `Comparator`로 요소를 정렬할 수 있다.

---

## 7. 최종 완성 체크리스트

* [ ] `Product` 클래스에 이름과 가격이 정의되어 있다.
* [ ] `Order` 클래스에 주문 번호와 상품 목록이 정의되어 있다.
* [ ] `forEach()`로 상품 전체를 출력했다.
* [ ] `filter()`로 가격이 1,000원 이상인 상품만 남겼다.
* [ ] `map()`으로 상품 이름을 추출했다.
* [ ] `map()` 결과가 중첩 리스트로 생성되는 것을 확인했다.
* [ ] `flatMap()`으로 중첩된 상품 목록을 평탄화했다.
* [ ] `filter()`, `map()`, `collect()`로 `[공책, 필통]`을 생성했다.
* [ ] 가격이 1,000원 이상인 상품 개수를 계산했다.
* [ ] 전체 가격 합계와 평균을 계산했다.
* [ ] 가격을 기준으로 상품을 오름차순 정렬했다.
* [ ] 전체 실행 결과가 정상적으로 출력된다.

---

## 8. 선택 도전 과제

1. **저가 상품 필터링**: 가격이 500원 이하인 상품만 출력한다.
2. **문자열 변환**: 각 상품을 `이름: 가격원` 형식의 문자열로 변환한다.
3. **가장 비싼 상품 조회**: `max()` 또는 `sorted()`와 `limit(1)`을 사용해 가장 비싼 상품을 조회한다.
4. **중복 제거와 정렬**: 주문의 모든 상품을 `flatMap()`으로 평탄화한 뒤 `distinct()`와 `sorted()`를 적용한다.
5. **가격대별 그룹화**: `Collectors.groupingBy()`를 이용해 상품을 가격대별로 분류한다.
6. **합계 누적**: `reduce()`를 사용해 상품 가격 합계를 계산한다.
