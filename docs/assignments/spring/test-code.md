# JUnit 5 테스트 코드 작성하기

> 테스트 코드는 프로그램의 동작 결과를 사람이 직접 확인하지 않고 코드로 자동 검증하는 방법이다.
>
> 메모리 기반 `ProductDao`를 대상으로 JUnit 5의 테스트 생명주기, 단언, 예외 검증, 비활성화 기능을 단계별로 구현한다.
>
> 마지막에는 Spring 테스트 컨텍스트를 사용해 `ProductDao`를 직접 생성하지 않고 빈으로 주입받는 구조까지 확인한다.
>
> 아래 Step을 순서대로 진행하면 정상 동작과 예외 상황을 자동으로 검증하는 테스트 묶음을 완성할 수 있다.

---

## 0. 먼저 알아둘 점

이 과제에서는 테스트 코드 작성 방법에 집중하기 위해 데이터베이스 대신 `HashMap`에 데이터를 저장하는 `ProductDao`를 사용한다.

실제 데이터베이스와 연결된 DAO를 테스트할 때도 given-when-then 구조와 단언 방법은 동일하게 적용할 수 있다.

메모리 저장 방식에서는 데이터베이스 예외 대신 Java의 일반 예외를 사용한다.

| 데이터베이스 기반 DAO  | 메모리 기반 DAO               | 발생 상황          |
| -------------- | ------------------------ | -------------- |
| `SQLException` | `IllegalStateException`  | 동일한 ID를 중복 저장  |
| `SQLException` | `NoSuchElementException` | 존재하지 않는 ID를 조회 |

JUnit 5가 프로젝트에 설정되어 있다고 가정한다.

Gradle 프로젝트에서는 일반적으로 다음 의존성과 설정이 필요하다.

```groovy
testImplementation 'org.junit.jupiter:junit-jupiter'

test {
    useJUnitPlatform()
}
```

테스트는 IDE의 실행 버튼이나 다음 명령으로 실행할 수 있다.

```bash
./gradlew test
```

이 과제에서는 `assertThrows()`에 전달하는 실행 코드를 익명 `Executable` 클래스로 구현한다.

Spring을 이용한 테스트 빈 주입은 Step 6에서 다룬다.

---

## 1. 무엇을 만드나요?

다음 `Product`와 `ProductDao`는 테스트 대상 코드다.

`ProductDaoTest`에서 저장, 조회, 예외 발생 여부를 검증한다.

```java
public class Product {

    private String id;
    private String name;
    private int price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
```

```java
import java.util.HashMap;
import java.util.NoSuchElementException;

public class ProductDao {

    private final HashMap<String, Product> store =
            new HashMap<>();

    public void add(Product product) {
        if (store.containsKey(
                product.getId()
        )) {
            throw new IllegalStateException(
                    "이미 존재하는 id: "
                    + product.getId()
            );
        }

        store.put(
                product.getId(),
                product
        );
    }

    public Product get(String id) {
        Product found =
                store.get(id);

        if (found == null) {
            throw new NoSuchElementException(
                    "없는 id: "
                    + id
            );
        }

        return found;
    }

    public int getCount() {
        return store.size();
    }

    public void deleteAll() {
        store.clear();
    }
}
```

테스트를 모두 실행하면 다음 항목이 표시된다.

```text
ProductDaoTest
  ✓ add()
  ✓ get()
  ✓ add_중복_id_예외()
  ✓ get_없는_id_예외()
  ⊘ 일부러_실패하는_테스트()
```

`@Disabled`가 적용된 테스트는 실행되지 않는다.

---

## 2. 학습 목표

| 개념                                | 학습 위치  |
| --------------------------------- | ------ |
| 테스트 클래스와 `@BeforeEach`            | Step 1 |
| given-when-then과 `assertEquals()` | Step 2 |
| 조회 결과 검증                          | Step 3 |
| `assertThrows()`와 익명 `Executable` | Step 4 |
| `@Disabled`와 실패 메시지               | Step 5 |
| Spring 테스트 컨텍스트와 빈 주입             | Step 6 |

---

## 3. 핵심 개념

### 3.1 테스트 코드

테스트 코드는 실행 결과가 예상한 값과 같은지 자동으로 확인한다.

다음과 같이 출력문으로 결과를 확인할 수도 있다.

```java
System.out.println(
        dao.getCount()
);
```

하지만 출력 결과는 사람이 직접 읽고 판단해야 한다.

JUnit의 단언을 사용하면 기대값과 실제값을 코드가 비교한다.

```java
assertEquals(
        1,
        dao.getCount()
);
```

값이 다르면 테스트가 실패하고 기대값과 실제값이 함께 출력된다.

테스트 코드를 사용하면 다음과 같은 장점이 있다.

* 동작 결과를 자동으로 검증할 수 있다.
* 여러 테스트를 한 번에 반복 실행할 수 있다.
* 코드를 변경한 뒤 기존 기능이 깨졌는지 확인할 수 있다.
* 실패한 위치와 원인을 빠르게 확인할 수 있다.

---

### 3.2 given-when-then

테스트는 일반적으로 given, when, then의 세 단계로 구성한다.

```text
given
→ 테스트에 필요한 데이터와 상태를 준비

when
→ 검증할 기능을 실행

then
→ 실제 결과와 기대 결과를 비교
```

상품 추가 테스트는 다음과 같이 구성할 수 있다.

```java
@Test
void add() {
    // given
    Product product =
            newProduct(
                    "p1",
                    "연필",
                    500
            );

    // when
    dao.add(product);

    // then
    assertEquals(
            1,
            dao.getCount()
    );
}
```

given-when-then은 애너테이션이나 문법이 아니라 테스트 코드를 읽기 쉽게 나누는 작성 방식이다.

---

### 3.3 `@BeforeEach`

`@BeforeEach`가 적용된 메서드는 각 `@Test` 메서드가 실행되기 전에 호출된다.

```java
@BeforeEach
void setUp() {
    dao.deleteAll();
}
```

테스트가 세 개라면 `setUp()`도 각 테스트 직전에 한 번씩 총 세 번 실행된다.

```text
setUp()
→ 첫 번째 테스트

setUp()
→ 두 번째 테스트

setUp()
→ 세 번째 테스트
```

테스트마다 저장소를 초기화하면 이전 테스트에서 저장한 데이터가 다음 테스트에 영향을 주지 않는다.

각 테스트가 서로 독립적으로 실행되도록 만드는 것이 중요하다.

---

### 3.4 `assertEquals()`

`assertEquals()`는 기대값과 실제값이 같은지 검증한다.

```java
assertEquals(
        expected,
        actual
);
```

예시는 다음과 같다.

```java
assertEquals(
        1,
        dao.getCount()
);
```

첫 번째 인자에는 기대값을, 두 번째 인자에는 실제 실행 결과를 작성한다.

순서를 반대로 작성해도 비교는 가능하지만 실패 메시지의 expected와 actual이 반대로 표시되어 원인을 파악하기 어려울 수 있다.

---

### 3.5 `assertThrows()`

`assertThrows()`는 특정 코드를 실행했을 때 예상한 예외가 발생하는지 검증한다.

```java
assertThrows(
        IllegalStateException.class,
        action
);
```

첫 번째 인자에는 기대하는 예외 타입을 전달한다.

두 번째 인자에는 예외가 발생해야 하는 실행 코드를 전달한다.

이 과제에서는 `Executable` 익명 클래스를 사용한다.

```java
Executable action =
        new Executable() {

            @Override
            public void execute() {
                dao.add(product);
            }
        };
```

```java
assertThrows(
        IllegalStateException.class,
        action
);
```

예외가 발생하지 않거나 다른 타입의 예외가 발생하면 테스트가 실패한다.

---

### 3.6 `@Disabled`

`@Disabled`는 특정 테스트를 실행 대상에서 제외한다.

```java
@Disabled(
        "학습용 실패 테스트"
)
@Test
void 일부러_실패하는_테스트() {
}
```

테스트가 아직 완성되지 않았거나 실패 결과를 확인하기 위한 예제를 일시적으로 제외할 때 사용할 수 있다.

다만 실제 오류가 있는 테스트를 장기간 비활성화하면 문제를 놓칠 수 있으므로 이유를 명확하게 작성해야 한다.

---

### 3.7 실패 메시지

다음 테스트는 실제 개수가 `1`인데 `2`를 기대하므로 실패한다.

```java
assertEquals(
        2,
        dao.getCount()
);
```

JUnit은 다음과 같은 메시지를 제공한다.

```text
expected: <2> but was: <1>
```

* `expected`: 테스트 코드에서 기대한 값
* `actual` 또는 `was`: 실제 실행 결과

실패 메시지를 통해 예상과 실제 결과의 차이를 확인할 수 있다.

---

### 3.8 Spring 테스트 컨텍스트

일반적인 단위 테스트에서는 테스트 코드가 직접 객체를 생성할 수 있다.

```java
private ProductDao dao =
        new ProductDao();
```

Spring 테스트에서는 컨테이너를 실행하고 컨테이너가 관리하는 빈을 주입받을 수 있다.

```java
@SpringJUnitConfig(
        AppConfig.class
)
class ProductDaoTest {

    @Autowired
    private ProductDao dao;
}
```

`@SpringJUnitConfig`는 다음 두 애너테이션을 결합한 형태다.

```text
@ExtendWith(SpringExtension.class)
+
@ContextConfiguration(classes = AppConfig.class)
```

* `SpringExtension`: JUnit 5 테스트에 Spring 기능을 연결한다.
* `ContextConfiguration`: 사용할 Spring 설정 클래스를 지정한다.

Spring 테스트 컨텍스트가 실행되면 `@Autowired`를 통해 등록된 빈을 주입받을 수 있다.

---

## 4. 파일 구조

| 파일                    | 역할                                   |
| --------------------- | ------------------------------------ |
| `Product.java`        | 상품 ID, 이름, 가격을 저장하는 도메인 클래스          |
| `ProductDao.java`     | `HashMap`을 이용해 상품을 저장하고 조회하는 테스트 대상  |
| `ProductDaoTest.java` | 상품 저장, 조회, 예외 상황을 검증하는 테스트 클래스       |
| `AppConfig.java`      | `ProductDao`를 Spring 빈으로 등록하는 설정 클래스 |

---

## 5. Step by Step

### Step 1. 테스트 클래스와 초기 상태 구성하기 (`ProductDaoTest.java`)

**목표**: 테스트 클래스를 작성하고 각 테스트가 동일한 상태에서 시작하도록 구성한다.

**할 일**

1. `ProductDaoTest` 클래스를 작성한다.
2. `ProductDao`를 필드로 선언한다.
3. `@BeforeEach`가 적용된 `setUp()`을 작성한다.
4. `setUp()`에서 모든 상품을 삭제한다.
5. 테스트용 상품을 생성하는 `newProduct()`를 작성한다.

<details>
<summary>힌트 보기</summary>

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ProductDaoTest {

    private final ProductDao dao =
            new ProductDao();

    @BeforeEach
    void setUp() {
        dao.deleteAll();
    }

    private Product newProduct(
            String id,
            String name,
            int price
    ) {
        Product product =
                new Product();

        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
```

`setUp()`은 각 테스트가 실행되기 전에 호출되며 저장소를 비운다.

따라서 모든 테스트는 상품이 없는 상태에서 시작한다.

</details>

**확인**

* 테스트 클래스가 정상적으로 컴파일되는지 확인한다.
* `@BeforeEach`가 적용된 메서드가 존재하는지 확인한다.
* `deleteAll()`을 통해 테스트마다 저장소가 초기화되는지 확인한다.
* 테스트용 상품 생성 메서드가 정상적으로 동작하는지 확인한다.

---

### Step 2. 상품 추가 테스트 작성하기 (`ProductDaoTest.java`)

**목표**: 상품을 추가한 뒤 상품 개수가 증가하는지 검증한다.

**할 일**

1. `add()` 테스트 메서드를 작성한다.
2. 초기 상품 개수가 `0`인지 검증한다.
3. 테스트할 상품을 생성한다.
4. `dao.add()`를 호출한다.
5. 상품 개수가 `1`인지 검증한다.

<details>
<summary>힌트 보기</summary>

```java
@Test
void add() {
    // given
    assertEquals(
            0,
            dao.getCount()
    );

    Product product =
            newProduct(
                    "p1",
                    "연필",
                    500
            );

    // when
    dao.add(product);

    // then
    assertEquals(
            1,
            dao.getCount()
    );
}
```

상품 추가 기능을 검증할 때 조회 기능에 의존하지 않고 개수 변화를 기준으로 확인한다.

</details>

**확인**

* 테스트 실행 전 상품 개수가 `0`인지 확인한다.
* 상품을 추가한 뒤 개수가 `1`인지 확인한다.
* 테스트가 given-when-then 구조로 구분되어 있는지 확인한다.

---

### Step 3. 상품 조회 테스트 작성하기 (`ProductDaoTest.java`)

**목표**: 상품 ID로 조회한 결과가 저장한 상품 정보와 같은지 검증한다.

**할 일**

1. 조회할 상품을 생성한다.
2. given 단계에서 상품을 미리 저장한다.
3. `dao.get()`으로 상품을 조회한다.
4. 조회한 상품의 ID, 이름, 가격을 검증한다.

<details>
<summary>힌트 보기</summary>

```java
@Test
void get() {
    // given
    Product product =
            newProduct(
                    "p1",
                    "연필",
                    500
            );

    dao.add(product);

    // when
    Product found =
            dao.get("p1");

    // then
    assertEquals(
            product.getId(),
            found.getId()
    );

    assertEquals(
            product.getName(),
            found.getName()
    );

    assertEquals(
            product.getPrice(),
            found.getPrice()
    );
}
```

조회 테스트에서는 조회 대상 데이터가 필요하므로 given 단계에서 상품을 먼저 저장한다.

</details>

**확인**

* 조회한 상품의 ID가 같은지 확인한다.
* 조회한 상품의 이름이 같은지 확인한다.
* 조회한 상품의 가격이 같은지 확인한다.
* `get()` 테스트가 정상적으로 통과하는지 확인한다.

---

### Step 4. 예외 테스트 작성하기 (`ProductDaoTest.java`)

**목표**: 잘못된 요청에서 예상한 예외가 발생하는지 검증한다.

#### 중복 ID 추가

동일한 ID의 상품을 두 번 추가하면 `IllegalStateException`이 발생해야 한다.

<details>
<summary>힌트 보기</summary>

```java
@Test
void add_중복_id_예외() {
    // given
    final Product product =
            newProduct(
                    "dup",
                    "지우개",
                    300
            );

    dao.add(product);

    Executable action =
            new Executable() {

                @Override
                public void execute() {
                    dao.add(product);
                }
            };

    // when & then
    assertThrows(
            IllegalStateException.class,
            action
    );
}
```

</details>

#### 존재하지 않는 ID 조회

등록되지 않은 ID로 조회하면 `NoSuchElementException`이 발생해야 한다.

<details>
<summary>힌트 보기</summary>

```java
@Test
void get_없는_id_예외() {
    // given
    String id =
            "없는_id";

    Executable action =
            new Executable() {

                @Override
                public void execute() {
                    dao.get(id);
                }
            };

    // when & then
    assertThrows(
            NoSuchElementException.class,
            action
    );
}
```

</details>

익명 클래스 내부에서 사용하는 지역변수는 `final`이거나 사실상 final이어야 한다.

람다로 변경하면 다음과 같이 작성할 수 있다.

```java
assertThrows(
        IllegalStateException.class,
        () -> dao.add(product)
);
```

**확인**

* 중복 ID 추가 시 `IllegalStateException`이 발생하는지 확인한다.
* 없는 ID 조회 시 `NoSuchElementException`이 발생하는지 확인한다.
* 예상한 예외 타입과 실제 예외 타입이 일치하는지 확인한다.
* 두 예외 테스트가 정상적으로 통과하는지 확인한다.

---

### Step 5. 실패 테스트와 `@Disabled` 확인하기 (`ProductDaoTest.java`)

**목표**: 테스트 실패 메시지를 확인하고 특정 테스트를 비활성화하는 방법을 익힌다.

**할 일**

1. 일부러 잘못된 기대값을 사용하는 테스트를 작성한다.
2. `@Disabled`를 적용한다.
3. `@Disabled`를 잠시 제거하고 테스트를 실행한다.
4. 실패 메시지의 기대값과 실제값을 확인한다.
5. 확인 후 다시 `@Disabled`를 적용한다.

<details>
<summary>힌트 보기</summary>

```java
@Disabled(
        "잘못된 기대값을 확인하는 학습용 테스트"
)
@Test
void 일부러_실패하는_테스트() {
    // given
    Product product =
            newProduct(
                    "fail_demo",
                    "공책",
                    1000
            );

    // when
    dao.add(product);

    // then
    assertEquals(
            2,
            dao.getCount()
    );
}
```

실제 상품 개수는 `1`이므로 테스트를 활성화하면 다음과 같은 실패 메시지가 발생한다.

```text
expected: <2> but was: <1>
```

</details>

**확인**

* `@Disabled`가 적용된 테스트가 실행되지 않는지 확인한다.
* `@Disabled`를 제거했을 때 테스트가 실패하는지 확인한다.
* 실패 메시지에서 기대값과 실제값을 구분할 수 있는지 확인한다.
* 확인 후 테스트를 다시 비활성화했는지 확인한다.

---

### Step 6. Spring 컨테이너에서 빈 주입받기 (`AppConfig.java`, `ProductDaoTest.java`)

**목표**: 테스트 코드에서 `ProductDao`를 직접 생성하지 않고 Spring 컨테이너가 관리하는 빈을 주입받는다.

**할 일**

1. `AppConfig`를 작성한다.
2. `@Configuration`을 적용한다.
3. `@Bean`으로 `ProductDao`를 등록한다.
4. 테스트 클래스에 `@SpringJUnitConfig`를 적용한다.
5. `ProductDao` 필드에 `@Autowired`를 적용한다.
6. 직접 작성한 `new ProductDao()`를 제거한다.
7. 기존 테스트가 그대로 통과하는지 확인한다.

<details>
<summary>힌트 보기</summary>

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ProductDao productDao() {
        return new ProductDao();
    }
}
```

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(
        AppConfig.class
)
class ProductDaoTest {

    @Autowired
    private ProductDao dao;

    @BeforeEach
    void setUp() {
        dao.deleteAll();
    }
}
```

`@SpringJUnitConfig(AppConfig.class)`를 통해 Spring 테스트 컨텍스트가 생성된다.

`AppConfig`에 등록된 `ProductDao` 빈이 `@Autowired` 필드에 주입된다.

Spring 빈은 기본적으로 싱글톤으로 관리되므로 테스트 사이의 데이터가 남지 않도록 `@BeforeEach`에서 초기화해야 한다.

`@SpringJUnitConfig`를 적용하지 않으면 Spring 컨테이너가 실행되지 않아 `dao`가 주입되지 않는다.

</details>

**확인**

* `AppConfig`에 `ProductDao` 빈이 등록되어 있는지 확인한다.
* 테스트 클래스에 `@SpringJUnitConfig`가 적용되어 있는지 확인한다.
* `ProductDao` 필드에 `@Autowired`가 적용되어 있는지 확인한다.
* 테스트 코드에서 `new ProductDao()`가 제거되었는지 확인한다.
* 저장, 조회, 예외 테스트가 모두 통과하는지 확인한다.

---

### Step 7. 전체 테스트 실행하기

**목표**: 작성한 테스트를 한 번에 실행하고 결과를 확인한다.

**할 일**

1. `ProductDaoTest` 전체를 실행한다.
2. 정상 동작 테스트가 모두 통과하는지 확인한다.
3. 예외 테스트가 모두 통과하는지 확인한다.
4. `@Disabled` 테스트가 건너뛰어지는지 확인한다.

예상 결과는 다음과 같다.

```text
ProductDaoTest
  ✓ add()
  ✓ get()
  ✓ add_중복_id_예외()
  ✓ get_없는_id_예외()
  ⊘ 일부러_실패하는_테스트()
```

Gradle에서는 다음 명령으로 전체 테스트를 실행할 수 있다.

```bash
./gradlew test
```

---

## 6. 학습 체크

* [ ] 출력문이 아닌 단언으로 결과를 자동 검증해야 하는 이유를 설명할 수 있다.
* [ ] given-when-then 구조를 구분해 테스트를 작성할 수 있다.
* [ ] `@BeforeEach`가 각 테스트 실행 전에 호출된다는 점을 설명할 수 있다.
* [ ] 테스트를 서로 독립적으로 만들어야 하는 이유를 설명할 수 있다.
* [ ] `assertEquals()`에 기대값과 실제값을 올바른 순서로 전달할 수 있다.
* [ ] 저장 개수의 변화를 이용해 추가 기능을 검증할 수 있다.
* [ ] 조회한 객체의 필드를 각각 검증할 수 있다.
* [ ] `assertThrows()`를 사용해 예외 타입을 검증할 수 있다.
* [ ] 익명 `Executable`로 예외 발생 코드를 전달할 수 있다.
* [ ] `@Disabled`의 역할과 사용 시 주의점을 설명할 수 있다.
* [ ] JUnit 실패 메시지에서 기대값과 실제값을 구분할 수 있다.
* [ ] `@SpringJUnitConfig`의 역할을 설명할 수 있다.
* [ ] `@Autowired`를 통해 테스트 대상 빈을 주입받을 수 있다.

---

## 7. 최종 완성 체크리스트

* [ ] `ProductDaoTest`가 작성되어 있다.
* [ ] `@BeforeEach`에서 `deleteAll()`을 호출한다.
* [ ] 모든 테스트가 동일한 초기 상태에서 시작한다.
* [ ] `add()` 테스트가 상품 개수의 변화를 검증한다.
* [ ] `get()` 테스트가 상품의 ID, 이름, 가격을 검증한다.
* [ ] 중복 ID 추가 예외를 검증한다.
* [ ] 존재하지 않는 ID 조회 예외를 검증한다.
* [ ] 예외 테스트가 익명 `Executable`과 `assertThrows()`로 작성되어 있다.
* [ ] 학습용 실패 테스트에 `@Disabled`가 적용되어 있다.
* [ ] `AppConfig`에 `ProductDao` 빈이 등록되어 있다.
* [ ] `ProductDaoTest`에 `@SpringJUnitConfig`가 적용되어 있다.
* [ ] `ProductDao`가 `@Autowired`로 주입된다.
* [ ] 전체 테스트 실행 결과가 정상적으로 표시된다.

---

## 8. 선택 도전 과제

1. **상품 수정 테스트**: `ProductDao`에 `update(Product)`를 추가하고 상품 이름과 가격이 변경되는지 검증한다.
2. **상품 삭제 테스트**: `delete(String id)`를 추가하고 삭제 후 상품 개수가 감소하는지 검증한다.
3. **여러 상품 추가**: 상품을 세 개 추가하며 개수가 `0`, `1`, `2`, `3`으로 변경되는지 단계별로 검증한다.
4. **람다 예외 테스트**: 익명 `Executable`을 람다로 변경하고 두 작성 방식의 차이를 비교한다.
5. **예외 메시지 검증**: `assertThrows()`가 반환한 예외 객체에서 메시지를 가져와 `"없는 id"`가 포함되어 있는지 확인한다.
6. **테스트 이름 표시**: `@DisplayName`을 적용해 IDE에 표시되는 테스트 이름을 변경한다.
7. **테스트 생명주기 비교**: `@BeforeAll`과 `@BeforeEach`의 실행 시점과 사용 목적을 비교한다.
