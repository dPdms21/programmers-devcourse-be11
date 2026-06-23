# 제어의 역전(IoC) 익히기

> 제어의 역전(Inversion of Control)은 객체 생성과 실행 흐름의 제어권을 애플리케이션 코드가 아닌 외부 컨테이너나 프레임워크에 맡기는 개념이다.
>
> 커피 머신 예제를 통해 직접 제어, 의존성 주입, IoC 컨테이너, 실행 흐름의 역전 과정을 단계별로 확인한다.
>
> 아래 Step을 순서대로 진행하면 마지막 Step에서 IoC가 동작하는 프로그램을 완성할 수 있다.
>
> 각 Step의 힌트는 접혀 있다. 제어권을 외부로 분리하는 방법을 먼저 생각한 뒤 필요한 경우 힌트를 확인한다.

---

## 0. 먼저 알아둘 점

IoC는 DIP, DI와 함께 사용되지만 각각 의미가 다르다.

| 용어  | 구분    | 핵심 내용                         |
| --- | ----- | ----------------------------- |
| DIP | 원칙    | 구체 클래스가 아닌 추상화에 의존한다          |
| DI  | 구현 기법 | 필요한 객체를 직접 생성하지 않고 외부에서 주입받는다 |
| IoC | 상위 개념 | 객체 생성과 실행 흐름의 제어권을 외부에 맡긴다    |

DI는 IoC를 구현하는 대표적인 방법이다.

`NotificationService`가 `MessageSender`를 생성자로 주입받는 구조 역시 객체 생성과 선택의 제어권을 외부로 이동한 IoC의 사례다.

이 과제에서는 다음 두 가지 제어의 역전을 확인한다.

* 객체 생성과 조립의 제어 역전: Step 1~3
* 실행 흐름의 제어 역전: Step 4

Spring과 같은 프레임워크에서는 IoC 컨테이너가 객체 생성, 의존관계 설정, 객체 생명주기 관리를 담당한다.

이 과제에서는 동작 원리를 확인하기 위해 간단한 컨테이너를 직접 구현한다.

---

## 1. 무엇을 만드나요?

제어권이 단계별로 외부로 이동하는 과정을 보여주는 `Main`을 완성한다.

최종 실행 결과는 다음과 같다.

```text
===== 2. DI: 제어를 바깥(main)으로 =====
콜롬비아 원두로 커피를 내립니다
에티오피아 원두로 커피를 내립니다

===== 3. IoC 컨테이너: 조립까지 위임 =====
콜롬비아 원두로 커피를 내립니다

===== 4. 헐리우드 원칙: 흐름의 역전 =====
[시스템] 버튼이 눌렸습니다
내 코드 실행: 좋아요!
```

Step 1은 IoC를 적용하기 전의 구조를 확인하는 단계다.

Step 2부터 객체 선택과 생성, 실행 흐름의 제어권을 단계적으로 외부에 위임한다.

---

## 2. 학습 목표

| 개념                     | 학습 위치                                    |
| ---------------------- | ---------------------------------------- |
| 직접 객체를 생성하는 구조의 한계     | Step 1 (`CoffeeMaker.java`)              |
| DI를 통한 객체 선택 권한 분리     | Step 2 (`Bean.java`, `CoffeeMaker.java`) |
| IoC 컨테이너를 통한 객체 생성과 조립 | Step 3 (`CoffeeContainer.java`)          |
| 실행 흐름의 제어 역전           | Step 4 (`Hollywood.java`)                |
| 전체 객체 조립과 실행           | Step 5 (`Main.java`)                     |

---

## 3. 핵심 개념

### 3.1 제어권

프로그램에서 제어권은 크게 다음 두 가지로 구분할 수 있다.

* 어떤 객체를 만들고 서로 연결할 것인가
* 어떤 시점에 어떤 코드를 실행할 것인가

일반적인 코드에서는 애플리케이션이 직접 `new`로 객체를 생성하고 메서드를 호출한다.

IoC 구조에서는 이러한 제어권을 컨테이너나 프레임워크에 맡긴다.

```text
[IoC 적용 전]

애플리케이션 코드
→ 의존 객체 직접 생성
→ 메서드 직접 호출


[IoC 적용 후]

컨테이너
→ 객체 생성 및 조립
→ 완성된 객체 제공

프레임워크
→ 필요한 시점에 애플리케이션 코드 호출
```

---

### 3.2 객체 생성과 조립의 제어 역전

`CoffeeMaker`가 사용할 원두 객체를 내부에서 직접 생성하면 원두 종류를 변경할 때마다 `CoffeeMaker`를 수정해야 한다.

```java
private Bean bean = new ColombiaBean();
```

생성자 주입을 적용하면 `CoffeeMaker`는 전달받은 `Bean`을 사용하기만 한다.

어떤 구현체를 사용할지는 외부에서 결정한다.

```java
CoffeeMaker maker =
        new CoffeeMaker(
                new EthiopiaBean()
        );
```

객체 생성과 조립을 컨테이너로 이동하면 사용하는 쪽에서는 완성된 객체를 요청하기만 한다.

```java
CoffeeMaker maker =
        container.getCoffeeMaker();
```

---

### 3.3 실행 흐름의 제어 역전

일반적인 라이브러리는 애플리케이션 코드가 필요할 때 직접 호출한다.

프레임워크 구조에서는 애플리케이션이 특정 코드를 등록하고, 프레임워크가 적절한 시점에 해당 코드를 호출한다.

이를 헐리우드 원칙이라고도 한다.

> 먼저 호출하지 말고, 필요한 시점에 호출받는다.

버튼 예제에서는 `LikeAction`을 버튼에 등록하고, 실제 `onClick()` 호출 시점은 `Button`이 결정한다.

```text
애플리케이션
→ 리스너 등록

Button
→ 버튼 입력 감지
→ 등록된 리스너 호출
```

---

### 3.4 IoC, DI, DIP의 관계

```text
DIP
→ 추상화에 의존하도록 설계하는 원칙

DI
→ 의존 객체를 외부에서 전달하는 구현 방법

IoC
→ 객체 생성과 실행 흐름의 제어권을 외부에 맡기는 상위 개념
```

DIP를 적용해 인터페이스에 의존하고, DI로 구현 객체를 전달하면 객체 생성과 선택의 제어권을 외부로 이동할 수 있다.

Spring에서는 IoC 컨테이너가 이 과정을 자동으로 처리한다.

---

## 4. 파일 구조

| 파일                     | 역할                         |
| ---------------------- | -------------------------- |
| `Bean.java`            | `Bean` 인터페이스와 원두 구현 클래스 정의 |
| `CoffeeMaker.java`     | 원두를 주입받아 커피를 만드는 클래스       |
| `CoffeeContainer.java` | 객체 생성과 조립을 담당하는 IoC 컨테이너   |
| `Hollywood.java`       | 버튼과 콜백을 통한 실행 흐름의 역전 구현    |
| `Main.java`            | 단계별 구조를 실행하는 진입점           |

실행 명령은 다음과 같다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 5. Step by Step

### Step 1. 직접 제어의 한계 확인하기 (`CoffeeMaker.java`)

**목표**: 클래스가 의존 객체를 직접 생성할 때 발생하는 문제를 확인한다.

**할 일**

1. `CoffeeMaker`가 내부에서 `ColombiaBean`을 직접 생성하도록 구현한다.
2. 에티오피아 원두로 변경하려면 어떤 코드를 수정해야 하는지 확인한다.

<details>
<summary>힌트 보기</summary>

```java
class CoffeeMaker {

    private Bean bean =
            new ColombiaBean();

    void brew() {
        System.out.println(
                bean.name()
                + "로 커피를 내립니다"
        );
    }
}
```

이 구조에서는 사용할 원두가 `CoffeeMaker` 내부에 고정된다.

원두 구현체를 변경하려면 `CoffeeMaker`의 코드도 수정해야 한다.

</details>

**확인**

원두를 변경하려면 `CoffeeMaker` 내부의 객체 생성 코드를 수정해야 한다는 점을 설명할 수 있는지 확인한다.

---

### Step 2. DI로 객체 선택 권한 분리하기 (`Bean.java`, `CoffeeMaker.java`)

**목표**: `CoffeeMaker`가 원두를 직접 생성하지 않고 외부에서 주입받도록 변경한다.

**할 일**

1. `Bean` 인터페이스를 작성한다.
2. `ColombiaBean`, `EthiopiaBean`이 `Bean`을 구현하도록 한다.
3. `CoffeeMaker`가 `Bean`을 생성자로 전달받도록 한다.
4. `CoffeeMaker` 내부에서 구체 구현체를 직접 생성하지 않는다.

<details>
<summary>힌트 보기</summary>

```java
interface Bean {

    String name();
}

class ColombiaBean
        implements Bean {

    @Override
    public String name() {
        return "콜롬비아 원두";
    }
}

class EthiopiaBean
        implements Bean {

    @Override
    public String name() {
        return "에티오피아 원두";
    }
}
```

```java
class CoffeeMaker {

    private final Bean bean;

    CoffeeMaker(Bean bean) {
        this.bean = bean;
    }

    void brew() {
        System.out.println(
                bean.name()
                + "로 커피를 내립니다"
        );
    }
}
```

사용할 원두는 객체를 생성하는 외부 코드에서 결정한다.

```java
CoffeeMaker maker =
        new CoffeeMaker(
                new EthiopiaBean()
        );
```

</details>

**확인**

* `CoffeeMaker` 내부에 `new ColombiaBean()`이 없는지 확인한다.
* 콜롬비아 원두와 에티오피아 원두를 각각 주입할 수 있는지 확인한다.
* 구현체를 변경해도 `CoffeeMaker`를 수정하지 않는지 확인한다.

---

### Step 3. IoC 컨테이너에 객체 조립 위임하기 (`CoffeeContainer.java`)

**목표**: 원두와 커피 머신의 생성 및 조립 책임을 컨테이너에 맡긴다.

**할 일**

1. `CoffeeContainer`를 작성한다.
2. `getCoffeeMaker()`에서 원두 객체를 생성한다.
3. 생성한 원두를 `CoffeeMaker`에 주입한다.
4. 사용하는 쪽에서는 컨테이너에 완성된 객체를 요청한다.

<details>
<summary>힌트 보기</summary>

```java
class CoffeeContainer {

    CoffeeMaker getCoffeeMaker() {
        Bean bean =
                new ColombiaBean();

        return new CoffeeMaker(bean);
    }
}
```

사용하는 쪽에서는 다음과 같이 컨테이너를 통해 객체를 얻는다.

```java
CoffeeContainer container =
        new CoffeeContainer();

CoffeeMaker maker =
        container.getCoffeeMaker();

maker.brew();
```

원두 선택과 `CoffeeMaker` 조립은 컨테이너가 담당한다.

</details>

**확인**

* `main()`에 `new CoffeeMaker()`가 없는지 확인한다.
* `main()`에 `new ColombiaBean()`이 없는지 확인한다.
* 컨테이너를 통해 완성된 `CoffeeMaker`를 전달받는지 확인한다.

---

### Step 4. 헐리우드 원칙으로 실행 흐름 위임하기 (`Hollywood.java`)

**목표**: 애플리케이션이 직접 콜백을 호출하지 않고 시스템이 필요한 시점에 호출하도록 구현한다.

**할 일**

1. `ClickListener` 인터페이스를 작성한다.
2. `Button`이 `ClickListener`를 등록받도록 한다.
3. `Button.press()`에서 등록된 리스너의 `onClick()`을 호출한다.
4. `LikeAction`이 `ClickListener`를 구현하도록 한다.

<details>
<summary>힌트 보기</summary>

```java
interface ClickListener {

    void onClick();
}
```

```java
class Button {

    private ClickListener listener;

    void setListener(
            ClickListener listener
    ) {
        this.listener = listener;
    }

    void press() {
        System.out.println(
                "[시스템] 버튼이 눌렸습니다"
        );

        listener.onClick();
    }
}
```

```java
class LikeAction
        implements ClickListener {

    @Override
    public void onClick() {
        System.out.println(
                "내 코드 실행: 좋아요!"
        );
    }
}
```

애플리케이션은 리스너를 등록할 뿐 `onClick()`을 직접 호출하지 않는다.

실행 시점은 `Button`이 결정한다.

</details>

**확인**

`main()`에서 `onClick()`을 직접 호출하지 않고 `button.press()`를 실행했을 때 `LikeAction`이 호출되는지 확인한다.

---

### Step 5. Main에서 단계별 실행하기 (`Main.java`)

**목표**: DI, IoC 컨테이너, 실행 흐름의 역전을 순서대로 실행한다.

**할 일**

1. 콜롬비아 원두와 에티오피아 원두를 각각 `CoffeeMaker`에 주입한다.
2. `CoffeeContainer`를 통해 완성된 `CoffeeMaker`를 전달받는다.
3. `Button`에 `LikeAction`을 등록하고 `press()`를 호출한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {

    public static void main(String[] args) {
        System.out.println(
                "===== 2. DI ====="
        );

        new CoffeeMaker(
                new ColombiaBean()
        ).brew();

        new CoffeeMaker(
                new EthiopiaBean()
        ).brew();

        System.out.println(
                "\n===== 3. IoC 컨테이너 ====="
        );

        CoffeeContainer container =
                new CoffeeContainer();

        CoffeeMaker maker =
                container.getCoffeeMaker();

        maker.brew();

        System.out.println(
                "\n===== 4. 헐리우드 원칙 ====="
        );

        Button button =
                new Button();

        button.setListener(
                new LikeAction()
        );

        button.press();
    }
}
```

</details>

**확인**

다음 명령으로 실행했을 때 1번 섹션의 출력과 같은 결과가 나오는지 확인한다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 6. 학습 체크

* [ ] IoC, DI, DIP의 차이와 관계를 설명할 수 있다.
* [ ] 객체를 직접 생성할 때 발생하는 결합 문제를 설명할 수 있다.
* [ ] DI를 적용하면 객체 선택 권한이 외부로 이동하는 이유를 설명할 수 있다.
* [ ] IoC 컨테이너가 객체 생성과 조립을 담당한다는 점을 이해했다.
* [ ] 컨테이너를 사용했을 때 `main()`의 책임이 줄어드는 이유를 설명할 수 있다.
* [ ] 실행 흐름의 역전에서 시스템이 애플리케이션 코드를 호출한다는 점을 이해했다.
* [ ] 이벤트와 콜백 구조에서 호출 방향을 설명할 수 있다.

---

## 7. 최종 완성 체크리스트

* [ ] `CoffeeMaker`가 원두 구현체를 직접 생성하지 않는다.
* [ ] `CoffeeMaker`는 `Bean` 인터페이스에 의존한다.
* [ ] 원두 구현체는 생성자를 통해 주입된다.
* [ ] `CoffeeContainer`가 원두 생성과 커피 머신 조립을 담당한다.
* [ ] `main()`은 컨테이너를 통해 `CoffeeMaker`를 전달받는다.
* [ ] `Button`이 등록된 `ClickListener`를 호출한다.
* [ ] `main()`에서 `onClick()`을 직접 호출하지 않는다.
* [ ] 단계별 실행 결과가 정상적으로 출력된다.

---

## 8. 선택 도전 과제

1. **원두 선택 기능**: `CoffeeContainer`에 `getCoffeeMaker(String type)`을 작성하고 `"colombia"`, `"ethiopia"` 값에 따라 다른 원두를 조립한다.
2. **의존 객체 추가**: `MilkFrother`를 추가하고 `CoffeeMaker`가 `Bean`과 `MilkFrother`를 모두 주입받도록 한다.
3. **다중 리스너**: 하나의 `Button`에 여러 `ClickListener`를 등록하고 `press()` 시 모든 리스너를 호출한다.
4. **객체 범위 비교**: 컨테이너가 요청마다 새로운 `CoffeeMaker`를 반환하는 방식과 하나의 객체를 재사용하는 싱글턴 방식을 비교한다.
5. **Spring IoC 조사**: Spring의 `@Component`, `@Autowired`가 객체 생성과 의존성 주입을 어떻게 자동화하는지 정리한다.
