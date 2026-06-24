# 자바 람다 익히기

> 람다(lambda)는 익명 클래스를 간결하게 표현하는 문법이다.
>
> 익명 클래스에서 시작해 람다 표현식으로 단계적으로 줄이는 과정을 통해 람다의 문법과 동작을 확인한다.
>
> 마지막에는 함수형 인터페이스인 `Comparator`를 람다로 구현해 문자열 리스트를 정렬한다.
>
> 아래 Step을 순서대로 진행하면 람다의 기본 문법과 활용 방법을 익힐 수 있다.

---

## 0. 먼저 알아둘 점

람다는 추상 메서드가 하나인 함수형 인터페이스를 구현할 때 사용할 수 있다.

함수형 인터페이스는 다음과 같이 하나의 추상 메서드만 가진다.

```java
@FunctionalInterface
interface Operation {

    int apply(int a, int b);
}
```

람다는 새로운 종류의 함수가 아니라 함수형 인터페이스의 추상 메서드를 간결하게 구현하는 표현이다.

다음 익명 클래스와 람다는 같은 동작을 수행한다.

```java
Operation add = new Operation() {

    @Override
    public int apply(int a, int b) {
        return a + b;
    }
};
```

```java
Operation add =
        (a, b) -> a + b;
```

이 과제에서는 다음 내용을 다룬다.

* 함수형 인터페이스
* 익명 클래스와 람다의 관계
* 람다 표현식의 축약 규칙
* 매개변수 개수에 따른 람다 문법
* `Comparator`를 활용한 리스트 정렬

직접 정의하는 제네릭 타입은 다루지 않고, `Comparator<String>`처럼 Java에서 제공하는 제네릭 인터페이스를 구체 타입과 함께 사용한다.

---

## 1. 무엇을 만드나요?

익명 클래스를 람다로 변경하고, 매개변수 개수에 따른 람다 표현식을 작성한 뒤 문자열 리스트를 정렬하는 프로그램을 완성한다.

최종 실행 결과는 다음과 같다.

```text
===== 1. 익명 클래스와 람다 =====
익명 클래스 add: 7
람다 add: 7

===== 2. 람다로 만든 연산 =====
3 + 4 = 7
9 - 2 = 7
3 * 5 = 15

===== 3. 매개변수 개수별 람다 =====
(0개) 안녕하세요, 람다!
(1개) [로그] 시작합니다
(2개) 10 + 20 = 30

===== 4. Comparator를 이용한 길이순 정렬 =====
정렬 전: [가나다, 가, 라마]
정렬 후: [가, 라마, 가나다]
```

익명 클래스와 람다가 동일한 결과를 출력하는지 확인하고, 람다 문법을 실제 정렬 기능에 적용한다.

---

## 2. 학습 목표

| 개념                          | 학습 위치                                |
| --------------------------- | ------------------------------------ |
| 함수형 인터페이스와 익명 클래스           | Step 1 (`Operation.java`)            |
| 익명 클래스를 람다로 변경              | Step 2 (`Main.java`)                 |
| 람다 문법의 축약 과정                | Step 3 (`Main.java`)                 |
| 매개변수 개수에 따른 람다 표현식          | Step 4 (`Printer.java`, `Main.java`) |
| `Comparator` 람다를 활용한 리스트 정렬 | Step 5 (`Main.java`)                 |

---

## 3. 핵심 개념

### 3.1 함수형 인터페이스

함수형 인터페이스는 추상 메서드가 하나인 인터페이스다.

```java
@FunctionalInterface
interface Operation {

    int apply(int a, int b);
}
```

`@FunctionalInterface`는 해당 인터페이스가 함수형 인터페이스임을 명시한다.

필수 애너테이션은 아니지만 추상 메서드를 두 개 이상 선언하면 컴파일 오류를 발생시켜 잘못된 변경을 방지한다.

람다는 함수형 인터페이스의 하나뿐인 추상 메서드 구현을 표현한다.

```java
Operation add =
        (a, b) -> a + b;
```

이 코드에서 `(a, b) -> a + b`는 `Operation`의 `apply()` 메서드 구현이다.

람다는 단독으로 사용할 수 없으며 어떤 함수형 인터페이스 타입으로 사용되는지 결정되어야 한다.

---

### 3.2 익명 클래스와 람다

익명 클래스는 별도의 클래스 이름을 선언하지 않고 인터페이스나 추상 클래스를 즉시 구현하는 방식이다.

```java
Operation add = new Operation() {

    @Override
    public int apply(int a, int b) {
        return a + b;
    }
};
```

함수형 인터페이스를 익명 클래스로 구현하면 인터페이스명, 메서드명, 매개변수 타입, `return` 등 반복되는 코드가 포함된다.

람다는 이 구조에서 추상 메서드 구현에 필요한 핵심 부분만 남긴다.

```java
Operation add =
        (a, b) -> a + b;
```

두 코드는 모두 다음 호출에서 같은 결과를 반환한다.

```java
add.apply(3, 4);
```

람다는 익명 클래스의 모든 기능을 대체하는 문법이 아니라 함수형 인터페이스의 단일 추상 메서드를 간결하게 구현하는 문법이다.

---

### 3.3 람다 표현식 구조

람다 표현식은 매개변수, 화살표 연산자, 실행 본문으로 구성된다.

```text
(매개변수) -> 실행 내용
```

예시는 다음과 같다.

```java
(a, b) -> a + b
```

* `(a, b)`: 메서드가 전달받는 매개변수
* `->`: 매개변수와 실행 본문을 구분
* `a + b`: 실행 결과

---

### 3.4 람다 문법 축약

다음 세 표현은 모두 같은 동작을 수행한다.

```java
Operation add1 =
        (int a, int b) -> {
            return a + b;
        };
```

```java
Operation add2 =
        (a, b) -> {
            return a + b;
        };
```

```java
Operation add3 =
        (a, b) -> a + b;
```

함수형 인터페이스의 메서드 선언을 통해 매개변수 타입을 추론할 수 있으므로 타입을 생략할 수 있다.

본문이 하나의 표현식이라면 중괄호와 `return`, 세미콜론을 생략할 수 있다.

본문이 여러 문장이라면 중괄호를 사용해야 한다.

반환값이 있는 경우 중괄호 안에서 `return`을 직접 작성한다.

```java
Operation add =
        (a, b) -> {
            int result = a + b;

            return result;
        };
```

중괄호를 사용하면서 반환값이 필요한 경우 `return`만 생략할 수는 없다.

---

### 3.5 매개변수 개수에 따른 표현

#### 매개변수 0개

매개변수가 없으면 빈 괄호를 작성한다.

```java
() -> System.out.println("Hello");
```

괄호는 생략할 수 없다.

#### 매개변수 1개

매개변수가 하나라면 괄호를 생략할 수 있다.

```java
message ->
        System.out.println(message);
```

괄호를 작성해도 된다.

```java
(message) ->
        System.out.println(message);
```

매개변수 타입을 직접 작성할 때는 괄호가 필요하다.

```java
(String message) ->
        System.out.println(message);
```

#### 매개변수 2개 이상

매개변수가 두 개 이상이면 괄호가 필요하다.

```java
(a, b) -> a + b
```

---

### 3.6 함수형 인터페이스의 호출

람다를 선언한 뒤에는 함수형 인터페이스에 정의된 메서드 이름으로 호출한다.

```java
Operation add =
        (a, b) -> a + b;

int result =
        add.apply(3, 4);
```

람다 표현식에는 메서드 이름이 나타나지 않지만, 변수의 타입이 `Operation`이므로 `apply()` 구현으로 사용된다.

---

### 3.7 Comparator와 정렬

`Comparator<T>`는 두 값을 비교하는 함수형 인터페이스다.

핵심 추상 메서드는 다음과 같다.

```java
int compare(T first, T second);
```

비교 결과는 다음 의미를 가진다.

| 반환값 | 의미            |
| --- | ------------- |
| 음수  | 첫 번째 값이 앞에 위치 |
| 0   | 두 값의 순서가 동일   |
| 양수  | 두 번째 값이 앞에 위치 |

문자열 길이를 기준으로 정렬하는 람다는 다음과 같다.

```java
(s1, s2) ->
        s1.length() - s2.length()
```

첫 번째 문자열이 더 짧으면 음수가 반환되므로 첫 번째 문자열이 앞에 위치한다.

다만 정수 비교에서는 뺄셈보다 `Integer.compare()`를 사용하는 방식이 더 명확하고 안전하다.

```java
(s1, s2) ->
        Integer.compare(
                s1.length(),
                s2.length()
        )
```

---

## 4. 파일 구조

| 파일               | 역할                                        |
| ---------------- | ----------------------------------------- |
| `Operation.java` | 두 정수를 전달받아 정수 결과를 반환하는 함수형 인터페이스          |
| `Printer.java`   | 문자열 하나를 전달받아 출력하는 함수형 인터페이스               |
| `Main.java`      | 익명 클래스, 람다 연산, 매개변수별 람다, 리스트 정렬을 실행하는 진입점 |

실행 명령은 다음과 같다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 5. Step by Step

### Step 1. 함수형 인터페이스와 익명 클래스 작성하기 (`Operation.java`, `Main.java`)

**목표**: 함수형 인터페이스를 작성하고 익명 클래스로 구현한다.

**할 일**

1. `Operation` 인터페이스를 작성한다.
2. `int apply(int a, int b)`를 추상 메서드로 선언한다.
3. `@FunctionalInterface`를 적용한다.
4. `main()`에서 익명 클래스로 덧셈 기능을 구현한다.
5. `apply(3, 4)`의 결과를 출력한다.

<details>
<summary>힌트 보기</summary>

```java
@FunctionalInterface
interface Operation {

    int apply(int a, int b);
}
```

```java
Operation add =
        new Operation() {

            @Override
            public int apply(
                    int a,
                    int b
            ) {
                return a + b;
            }
        };

System.out.println(
        add.apply(3, 4)
);
```

`Operation`에는 추상 메서드가 하나만 있으므로 함수형 인터페이스가 된다.

익명 클래스는 `apply()`를 재정의해 덧셈 결과를 반환한다.

</details>

**확인**

* `Operation`에 추상 메서드가 하나만 존재하는지 확인한다.
* 익명 클래스가 `apply()`를 재정의하는지 확인한다.
* 실행 결과가 `7`인지 확인한다.

---

### Step 2. 익명 클래스를 람다로 변경하기 (`Main.java`)

**목표**: 익명 클래스와 같은 동작을 수행하는 람다를 작성한다.

**할 일**

1. 익명 클래스 버전을 `addAnon`에 저장한다.
2. 람다 버전을 `addLambda`에 저장한다.
3. 두 객체의 `apply(3, 4)` 결과를 각각 출력한다.
4. 두 방식이 같은 결과를 반환하는지 확인한다.

<details>
<summary>힌트 보기</summary>

```java
Operation addAnon =
        new Operation() {

            @Override
            public int apply(
                    int a,
                    int b
            ) {
                return a + b;
            }
        };

Operation addLambda =
        (a, b) -> a + b;

System.out.println(
        "익명 클래스 add: "
        + addAnon.apply(3, 4)
);

System.out.println(
        "람다 add: "
        + addLambda.apply(3, 4)
);
```

`addAnon`과 `addLambda`는 모두 `Operation`의 `apply()`를 구현한다.

표현 방식만 다르고 수행하는 동작은 같다.

</details>

**확인**

* 익명 클래스와 람다 모두 `Operation` 타입인지 확인한다.
* 두 결과가 모두 `7`인지 확인한다.
* 람다가 함수형 인터페이스의 추상 메서드 구현이라는 점을 설명할 수 있는지 확인한다.

---

### Step 3. 람다 축약 과정 확인하기 (`Main.java`)

**목표**: 람다의 완전한 형태에서 한 줄 표현식으로 축약하는 과정을 확인한다.

**할 일**

1. 매개변수 타입과 중괄호를 모두 작성한 람다를 만든다.
2. 매개변수 타입을 생략한 람다를 만든다.
3. 중괄호와 `return`을 생략한 람다를 만든다.
4. 세 람다가 같은 결과를 반환하는지 확인한다.
5. 뺄셈과 곱셈 람다를 작성한다.

<details>
<summary>힌트 보기</summary>

```java
Operation add1 =
        (int a, int b) -> {
            return a + b;
        };

Operation add2 =
        (a, b) -> {
            return a + b;
        };

Operation add3 =
        (a, b) -> a + b;

Operation sub =
        (a, b) -> a - b;

Operation mul =
        (a, b) -> a * b;
```

```java
System.out.println(
        "3 + 4 = "
        + add3.apply(3, 4)
);

System.out.println(
        "9 - 2 = "
        + sub.apply(9, 2)
);

System.out.println(
        "3 * 5 = "
        + mul.apply(3, 5)
);
```

한 줄짜리 표현식에서는 중괄호와 `return`을 생략할 수 있다.

</details>

**확인**

* `add1`, `add2`, `add3`가 같은 결과를 반환하는지 확인한다.
* `sub.apply(9, 2)` 결과가 `7`인지 확인한다.
* `mul.apply(3, 5)` 결과가 `15`인지 확인한다.

---

### Step 4. 매개변수 개수별 람다 작성하기 (`Printer.java`, `Main.java`)

**목표**: 매개변수가 0개, 1개, 2개인 람다의 표현 차이를 확인한다.

**할 일**

1. `Runnable`을 사용해 매개변수가 없는 람다를 작성한다.
2. `Printer` 함수형 인터페이스를 작성한다.
3. `Printer`를 사용해 매개변수가 하나인 람다를 작성한다.
4. `Operation`을 사용해 매개변수가 두 개인 람다를 작성한다.
5. 각 람다를 실행한다.

<details>
<summary>힌트 보기</summary>

```java
@FunctionalInterface
interface Printer {

    void print(String message);
}
```

```java
Runnable hello =
        () -> System.out.println(
                "(0개) 안녕하세요, 람다!"
        );

hello.run();

Printer log =
        message ->
                System.out.println(
                        "(1개) [로그] "
                        + message
                );

log.print("시작합니다");

Operation add =
        (a, b) -> a + b;

System.out.println(
        "(2개) 10 + 20 = "
        + add.apply(10, 20)
);
```

매개변수가 없으면 `()`를 작성해야 한다.

매개변수가 하나라면 괄호를 생략할 수 있다.

매개변수가 두 개 이상이면 괄호가 필요하다.

</details>

**확인**

* 매개변수가 없는 람다에 빈 괄호가 있는지 확인한다.
* 매개변수가 하나인 람다에서 괄호를 생략할 수 있는지 확인한다.
* 매개변수가 두 개인 람다에 괄호가 있는지 확인한다.
* 각 람다가 정상적으로 실행되는지 확인한다.

---

### Step 5. Comparator로 문자열 리스트 정렬하기 (`Main.java`)

**목표**: 정렬 기준을 람다로 전달해 문자열을 길이순으로 정렬한다.

**할 일**

1. 문자열을 저장하는 `ArrayList`를 생성한다.
2. 정렬 전 리스트를 출력한다.
3. `sort()`에 길이를 비교하는 람다를 전달한다.
4. 정렬 후 리스트를 출력한다.
5. 문자열이 짧은 순서로 정렬되었는지 확인한다.

<details>
<summary>힌트 보기</summary>

```java
import java.util.ArrayList;
import java.util.Arrays;
```

```java
ArrayList<String> names =
        new ArrayList<>(
                Arrays.asList(
                        "가나다",
                        "가",
                        "라마"
                )
        );

System.out.println(
        "정렬 전: "
        + names
);

names.sort(
        (s1, s2) ->
                Integer.compare(
                        s1.length(),
                        s2.length()
                )
);

System.out.println(
        "정렬 후: "
        + names
);
```

`Comparator<String>`의 `compare()` 메서드를 람다로 구현한 것이다.

첫 번째 문자열의 길이가 더 짧으면 음수가 반환되어 앞에 배치된다.

</details>

**확인**

* 정렬 전 결과가 `[가나다, 가, 라마]`인지 확인한다.
* 정렬 후 결과가 `[가, 라마, 가나다]`인지 확인한다.
* 람다가 `Comparator<String>`의 `compare()` 구현으로 사용된다는 점을 설명할 수 있는지 확인한다.

---

### Step 6. Main에서 전체 실행하기 (`Main.java`)

**목표**: 익명 클래스, 람다 연산, 매개변수별 람다, 리스트 정렬을 순서대로 실행한다.

**할 일**

1. 익명 클래스와 람다로 작성한 덧셈 결과를 비교한다.
2. 덧셈, 뺄셈, 곱셈 람다를 실행한다.
3. 매개변수가 0개, 1개, 2개인 람다를 실행한다.
4. `Comparator` 람다를 사용해 문자열 리스트를 정렬한다.
5. 각 단계의 결과가 예상 출력과 같은지 확인한다.

**확인**

다음 명령으로 실행했을 때 1번 섹션의 출력과 같은 결과가 나오는지 확인한다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 6. 학습 체크

* [ ] 함수형 인터페이스가 추상 메서드를 하나만 가진다는 점을 설명할 수 있다.
* [ ] `@FunctionalInterface`의 역할을 설명할 수 있다.
* [ ] 익명 클래스와 람다가 같은 추상 메서드를 구현한다는 점을 이해했다.
* [ ] 람다의 매개변수와 실행 본문을 구분할 수 있다.
* [ ] 매개변수 타입을 생략할 수 있는 이유를 설명할 수 있다.
* [ ] 한 줄 표현식에서 중괄호와 `return`을 생략할 수 있다.
* [ ] 여러 문장으로 된 람다에서 중괄호와 `return`을 사용할 수 있다.
* [ ] 매개변수 개수에 따른 괄호 규칙을 설명할 수 있다.
* [ ] 함수형 인터페이스의 메서드로 람다를 호출할 수 있다.
* [ ] `Comparator` 람다를 사용해 리스트를 정렬할 수 있다.

---

## 7. 최종 완성 체크리스트

* [ ] `Operation`이 함수형 인터페이스로 선언되어 있다.
* [ ] `Printer`가 함수형 인터페이스로 선언되어 있다.
* [ ] 익명 클래스로 `Operation`을 구현했다.
* [ ] 익명 클래스를 같은 동작의 람다로 변경했다.
* [ ] 덧셈, 뺄셈, 곱셈 람다가 정상적으로 동작한다.
* [ ] 매개변수가 없는 `Runnable` 람다를 작성했다.
* [ ] 매개변수가 하나인 `Printer` 람다를 작성했다.
* [ ] 매개변수가 두 개인 `Operation` 람다를 작성했다.
* [ ] `Comparator` 람다로 문자열을 길이순 정렬했다.
* [ ] 단계별 실행 결과가 정상적으로 출력된다.

---

## 8. 선택 도전 과제

1. **나눗셈 연산**: `Operation`으로 나눗셈 람다를 작성하고 두 번째 값이 `0`일 때 발생하는 결과를 확인한다.
2. **길이 내림차순 정렬**: 문자열이 긴 순서로 정렬되도록 `Comparator` 람다를 변경한다.
3. **사전순 정렬**: `compareTo()`를 사용해 문자열을 사전순으로 정렬한다.
4. **Predicate 활용**: `Predicate<String>`을 람다로 구현해 문자열이 비어 있는지 검사한다.
5. **메서드 참조**: `System.out::println`과 같은 메서드 참조 문법을 조사하고 기존 람다 표현식과 비교한다.
