# SOLID 5원칙 익히기 (나쁜 코드를 좋은 코드로 리팩터링하기)

> 객체지향 설계의 5가지 원칙인 SOLID를 **나쁜 예 → 좋은 예**로 직접 개선하며 익힌다.
> 단순히 원칙을 암기하는 것이 아니라, 변경에 강한 코드 구조가 필요한 이유를 직접 확인하는 것이 목표다.
> 아래 Step을 순서대로 진행하면 마지막 Step에서 5가지 원칙이 모두 동작하는 프로그램을 완성할 수 있다.
>
> 각 Step의 힌트는 접혀 있다. 나쁜 예를 먼저 직접 수정한 뒤 필요한 경우 힌트를 확인한다.

---

## 0. 먼저 알아둘 점

* SOLID는 하나의 큰 프로그램이 아니라 5개의 독립적인 객체지향 설계 원칙이다. 이 과제에서는 각 원칙마다 짧고 독립적인 예제를 하나씩 리팩터링한다.
* 모든 Step은 **나쁜 코드 확인 → 문제점 분석 → 좋은 구조로 개선**하는 흐름으로 진행한다.
* 인터페이스, 상속, 다형성은 학습했다는 전제로 진행한다. SOLID는 이러한 기능을 언제, 왜 사용해야 하는지 판단하는 원칙이다.
* `ArrayList<String>`처럼 구체 타입이 지정된 컬렉션만 사용한다. `<K, V>`와 같이 직접 정의하는 제네릭은 다루지 않는다.

---

## 1. 무엇을 만드나요?

5가지 원칙의 개선된 설계를 모두 실행하는 `Main`을 완성한다.

최종 실행 결과는 다음과 같다.

```text
===== SRP: 단일 책임 =====
- 오늘은 자바를 배웠다
- SOLID는 어렵지만 재밌다

===== OCP: 개방-폐쇄 =====
일반 회원 -> 10000원
골드 회원 -> 9000원
VIP 회원 -> 8000원

===== LSP: 리스코프 치환 =====
냠냠 먹습니다
냠냠 먹습니다
훨훨 납니다
첨벙 헤엄칩니다

===== ISP: 인터페이스 분리 =====
구형 프린터: 인쇄만 합니다
복합기: 인쇄
복합기: 스캔

===== DIP: 의존관계 역전 =====
[이메일] 주문이 완료되었습니다
[SMS] 주문이 완료되었습니다
```

핵심은 출력 결과 자체가 아니라 해당 출력을 만드는 클래스 구조다.

각 Step에서 문제가 있는 구조를 개선된 구조로 변경하는 과정이 주요 과제다.

---

## 2. 학습 목표

| 개념                      | 학습 위치                |
| ----------------------- | -------------------- |
| SRP — 클래스는 하나의 책임만 담당   | Step 1 (`Srp.java`)  |
| OCP — 기존 코드 수정 없이 확장    | Step 2 (`Ocp.java`)  |
| LSP — 자식이 부모를 안전하게 대체   | Step 3 (`Lsp.java`)  |
| ISP — 인터페이스를 작은 단위로 분리  | Step 4 (`Isp.java`)  |
| DIP — 추상화에 의존하고 구현체를 주입 | Step 5 (`Dip.java`)  |
| 다형성을 활용한 전체 실행          | Step 6 (`Main.java`) |

---

## 3. 핵심 개념

SOLID는 코드 변경 시 발생하는 영향을 줄이고, 유지보수와 확장이 쉬운 구조를 만들기 위한 5가지 설계 원칙이다.

### (1) SRP — 단일 책임 원칙

> 한 클래스는 변경되어야 하는 이유가 하나여야 한다.

일기 내용 관리, 파일 저장, 화면 출력을 하나의 클래스가 모두 담당하면 여러 변경 이유를 갖게 된다.

저장 방식만 변경하더라도 일기 내용을 관리하는 클래스까지 수정해야 하므로 책임별로 클래스를 분리한다.

---

### (2) OCP — 개방-폐쇄 원칙

> 소프트웨어 요소는 확장에는 열려 있고, 수정에는 닫혀 있어야 한다.

다음과 같이 회원 등급을 조건문으로 구분하면 등급이 추가될 때마다 기존 메서드를 수정해야 한다.

```java
if (grade.equals("GOLD")) {
    // 골드 회원 처리
} else if (grade.equals("VIP")) {
    // VIP 회원 처리
}
```

인터페이스와 구현 클래스를 사용하면 기존 코드를 수정하지 않고 새로운 클래스를 추가하는 방식으로 확장할 수 있다.

---

### (3) LSP — 리스코프 치환 원칙

> 부모 타입을 사용하는 위치에 자식 객체를 넣어도 프로그램이 정상적으로 동작해야 한다.

`Bird`가 `fly()`를 제공하지만 자식 클래스인 `Penguin`이 해당 메서드에서 예외를 발생시키면, `Bird` 타입을 사용하는 코드가 정상적으로 동작하지 않는다.

모든 새가 공통으로 수행할 수 있는 기능과 특정 새만 수행할 수 있는 기능을 구분해 상속 구조를 설계한다.

---

### (4) ISP — 인터페이스 분리 원칙

> 큰 인터페이스 하나보다 역할별로 분리된 작은 인터페이스를 사용한다.

`print()`, `scan()`, `fax()`를 모두 포함한 `Machine` 인터페이스를 단순 프린터가 구현하면 사용하지 않는 메서드까지 강제로 구현해야 한다.

기능별로 인터페이스를 분리하고 각 클래스가 필요한 인터페이스만 구현하도록 한다.

---

### (5) DIP — 의존관계 역전 원칙

> 상위 모듈은 구체 클래스가 아니라 추상화에 의존해야 한다.

알림 서비스가 `EmailSender`를 직접 생성하면 SMS 전송 방식으로 변경할 때 서비스 코드도 수정해야 한다.

`MessageSender` 인터페이스에 의존하고 실제 구현체는 생성자를 통해 외부에서 주입받도록 한다.

```text
S — 책임 하나
O — 수정하지 않고 확장
L — 자식이 부모를 안전하게 대체
I — 인터페이스 분리
D — 추상화에 의존하고 구현체 주입
```

---

## 4. 파일 구조

원칙별로 하나의 파일을 사용한다.

| 파일          | 역할                                                |
| ----------- | ------------------------------------------------- |
| `Srp.java`  | 일기장 예제 — 책임 분리 (`Journal`, `JournalSaver`)        |
| `Ocp.java`  | 할인 정책 예제 — 인터페이스를 통한 확장 (`DiscountPolicy` 외)      |
| `Lsp.java`  | 새 예제 — 안전한 상속 (`Bird`, `FlyingBird`, `Penguin` 외) |
| `Isp.java`  | 복합기 예제 — 인터페이스 분리 (`Printer`, `Scanner` 외)        |
| `Dip.java`  | 알림 예제 — 의존성 주입 (`MessageSender` 외)                |
| `Main.java` | 5가지 원칙을 모두 실행하는 진입점                               |

실행 명령은 다음과 같다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 5. Step by Step

### Step 1. SRP — 일기장의 책임을 둘로 나누기 (`Srp.java`)

**목표**: 여러 책임이 섞인 클래스를 책임별로 분리한다.

아래 코드는 `Journal`이 일기 내용 관리와 저장 기능을 함께 담당하는 나쁜 예다.

```java
// 나쁜 예: 내용 관리와 저장이라는 두 가지 변경 이유를 가짐
class Journal {
    private ArrayList<String> entries = new ArrayList<>();

    void add(String text) {
        entries.add(text);
    }

    void saveToFile(String filename) {
        // 파일에 저장하는 코드
    }
}
```

**할 일**

1. `Journal`에는 일기 내용과 관련된 기능만 남긴다.

   * `add()`
   * 내용을 문자열로 반환하는 `getText()`
2. 저장과 출력 책임은 새로운 클래스인 `JournalSaver`로 옮긴다.

<details>
<summary>힌트 보기</summary>

```java
import java.util.ArrayList;

class Journal {
    private ArrayList<String> entries = new ArrayList<>();

    void add(String text) {
        entries.add(text);
    }

    String getText() {
        StringBuilder sb = new StringBuilder();

        for (String e : entries) {
            sb.append("- ")
                    .append(e)
                    .append("\n");
        }

        return sb.toString();
    }
}

class JournalSaver {
    void print(Journal j) {
        System.out.print(j.getText());
    }
}
```

`JournalSaver`는 `Journal` 객체를 매개변수로 전달받아 사용한다.

이 구조에서는 저장이나 출력 방식이 변경되어도 `Journal`을 수정할 필요가 없다.

</details>

**확인**

* `Journal`에 저장 코드가 없는지 확인한다.
* 출력 기능을 `JournalSaver`가 담당하는지 확인한다.

---

### Step 2. OCP — if-else 대신 인터페이스로 확장하기 (`Ocp.java`)

**목표**: 새로운 회원 등급이 추가되어도 기존 코드를 수정하지 않고 클래스를 추가하는 방식으로 확장한다.

```java
// 나쁜 예: 등급이 늘어날 때마다 기존 메서드를 수정해야 함
class DiscountCalculator {
    int calc(String grade, int price) {
        if (grade.equals("GOLD")) {
            return price * 90 / 100;
        } else if (grade.equals("VIP")) {
            return price * 80 / 100;
        } else {
            return price;
        }
    }
}
```

**할 일**

1. `DiscountPolicy` 인터페이스를 작성한다.
2. `int discount(int price)` 메서드를 선언한다.
3. 등급별 구현 클래스를 작성한다.

   * `BasicDiscount`
   * `GoldDiscount`
   * `VipDiscount`
4. 이후 `SilverDiscount`를 추가할 때 기존 할인 정책 코드를 수정하지 않는지 확인한다.

<details>
<summary>힌트 보기</summary>

```java
interface DiscountPolicy {
    int discount(int price);
}

class BasicDiscount implements DiscountPolicy {
    public int discount(int price) {
        return price;
    }
}

class GoldDiscount implements DiscountPolicy {
    public int discount(int price) {
        return price * 90 / 100;
    }
}

class VipDiscount implements DiscountPolicy {
    public int discount(int price) {
        return price * 80 / 100;
    }
}
```

새로운 등급은 새로운 구현 클래스를 추가하는 방식으로 확장한다.

인터페이스를 사용하는 기존 할인 정책 코드는 수정하지 않는다.

</details>

**확인**

10000원을 기준으로 다음 결과가 반환되는지 확인한다.

* 일반 회원: 10000원
* 골드 회원: 9000원
* VIP 회원: 8000원

---

### Step 3. LSP — 억지 상속을 끊기 (`Lsp.java`)

**목표**: 부모 타입을 자식 객체로 대체해도 예외가 발생하지 않는 상속 구조를 구현한다.

```java
// 나쁜 예: 부모가 제공하는 동작을 자식이 정상적으로 수행하지 못함
class Bird {
    void fly() {
        System.out.println("훨훨 납니다");
    }
}

class Penguin extends Bird {
    void fly() {
        throw new RuntimeException(
                "펭귄은 날 수 없습니다."
        );
    }
}
```

다음 코드는 실행 시 예외가 발생한다.

```java
Bird bird = new Penguin();
bird.fly();
```

`Penguin`은 `Bird` 타입의 동작을 안전하게 대체하지 못한다.

**할 일**

1. `Bird`에는 모든 새가 수행할 수 있는 기능만 둔다.

   * 예: `eat()`
2. `fly()`는 날 수 있는 새만 갖도록 `FlyingBird`로 분리한다.
3. `Sparrow`는 `FlyingBird`를 상속한다.
4. `Penguin`은 `Bird`를 상속한다.
5. `Penguin`에는 필요에 따라 `swim()`을 추가한다.

<details>
<summary>힌트 보기</summary>

```java
class Bird {
    void eat() {
        System.out.println("냠냠 먹습니다");
    }
}

class FlyingBird extends Bird {
    void fly() {
        System.out.println("훨훨 납니다");
    }
}

class Sparrow extends FlyingBird {
}

class Penguin extends Bird {
    void swim() {
        System.out.println("첨벙 헤엄칩니다");
    }
}
```

`Bird` 타입으로 사용할 때는 모든 새가 수행할 수 있는 `eat()`만 호출한다.

따라서 어떤 하위 객체가 전달되어도 상위 타입의 동작을 정상적으로 수행할 수 있다.

</details>

**확인**

`Bird` 배열에 `Sparrow`와 `Penguin`을 저장하고 `eat()`을 호출했을 때 모두 정상적으로 동작하는지 확인한다.

---

### Step 4. ISP — 큰 인터페이스를 쪼개기 (`Isp.java`)

**목표**: 사용하지 않는 메서드를 억지로 구현하지 않도록 인터페이스를 기능별로 분리한다.

```java
// 나쁜 예: 인쇄 기능만 필요한 클래스가 scan()과 fax()까지 구현해야 함
interface Machine {
    void print();

    void scan();

    void fax();
}

class SimplePrinter implements Machine {
    public void print() {
        System.out.println("인쇄");
    }

    public void scan() {
        throw new RuntimeException(
                "스캔 기능을 지원하지 않습니다."
        );
    }

    public void fax() {
        throw new RuntimeException(
                "팩스 기능을 지원하지 않습니다."
        );
    }
}
```

**할 일**

1. `Machine`을 기능별 인터페이스로 분리한다.

   * `Printer`
   * `Scanner`
   * `Faxer`
2. 각 인터페이스에는 하나의 기능만 선언한다.
3. `SimplePrinter`는 `Printer`만 구현한다.
4. 인쇄와 스캔 기능을 제공하는 `SmartMachine`은 `Printer`, `Scanner`를 구현한다.

<details>
<summary>힌트 보기</summary>

```java
interface Printer {
    void print();
}

interface Scanner {
    void scan();
}

interface Faxer {
    void fax();
}

class SimplePrinter implements Printer {
    public void print() {
        System.out.println(
                "구형 프린터: 인쇄만 합니다"
        );
    }
}

class SmartMachine implements Printer, Scanner {
    public void print() {
        System.out.println(
                "복합기: 인쇄"
        );
    }

    public void scan() {
        System.out.println(
                "복합기: 스캔"
        );
    }
}
```

`SimplePrinter`는 지원하지 않는 기능을 구현하지 않아도 된다.

Java는 여러 인터페이스를 동시에 구현할 수 있으므로 필요한 기능만 조합할 수 있다.

`Scanner` 인터페이스는 `java.util.Scanner`와 이름이 같으므로 같은 파일에서 `java.util.Scanner`를 import하지 않도록 주의한다.

</details>

**확인**

`SimplePrinter`에 사용하지 않는 메서드가 강제로 구현되어 있지 않은지 확인한다.

---

### Step 5. DIP — 구체 클래스 대신 추상에 의존하기 (`Dip.java`)

**목표**: 상위 모듈이 구체 클래스가 아닌 인터페이스에 의존하고, 실제 구현체는 외부에서 주입받도록 한다.

```java
// 나쁜 예: 알림 서비스가 구체 클래스인 EmailSender를 직접 생성함
class NotificationService {
    private EmailSender sender =
            new EmailSender();

    void notifyUser(String msg) {
        sender.send(msg);
    }
}
```

이 구조에서는 SMS 전송 방식으로 변경할 때 `NotificationService`의 코드를 수정해야 한다.

**할 일**

1. `MessageSender` 인터페이스를 작성한다.

   * `void send(String msg)`
2. `EmailSender`와 `SmsSender`가 `MessageSender`를 구현하도록 한다.
3. `NotificationService`는 `MessageSender` 타입의 필드를 갖는다.
4. 실제 구현체는 생성자를 통해 전달받는다.

<details>
<summary>힌트 보기</summary>

```java
interface MessageSender {
    void send(String msg);
}

class EmailSender implements MessageSender {
    public void send(String msg) {
        System.out.println(
                "[이메일] " + msg
        );
    }
}

class SmsSender implements MessageSender {
    public void send(String msg) {
        System.out.println(
                "[SMS] " + msg
        );
    }
}

class NotificationService {
    private MessageSender sender;

    NotificationService(
            MessageSender sender
    ) {
        this.sender = sender;
    }

    void notifyUser(String msg) {
        sender.send(msg);
    }
}
```

이메일과 SMS 구현체의 선택은 다음과 같이 외부에서 결정한다.

```java
new NotificationService(
        new SmsSender()
);
```

`NotificationService` 내부 코드를 수정하지 않고 전송 방식을 교체할 수 있다.

</details>

**확인**

같은 `NotificationService`에 `EmailSender`와 `SmsSender`를 각각 주입했을 때 서로 다른 출력이 발생하는지 확인한다.

---

### Step 6. 마무리 — Main에서 5원칙을 모두 실행 (`Main.java`)

**목표**: 지금까지 구현한 5가지 원칙의 개선된 구조를 `Main`에서 실행한다.

**할 일**

1. Step 1부터 Step 5까지 작성한 클래스들을 `Main`의 `main()`에서 차례로 사용한다.
2. OCP, LSP, DIP는 배열과 다형성을 활용해 실행한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {
    public static void main(String[] args) {
        // OCP: 인터페이스 배열로 여러 구현체 처리
        DiscountPolicy[] policies = {
                new BasicDiscount(),
                new GoldDiscount(),
                new VipDiscount()
        };

        String[] names = {
                "일반",
                "골드",
                "VIP"
        };

        for (int i = 0; i < policies.length; i++) {
            System.out.println(
                    names[i]
                            + " 회원 -> "
                            + policies[i].discount(10000)
                            + "원"
            );
        }

        // LSP: Bird 타입으로 공통 행동 실행
        Bird[] birds = {
                new Sparrow(),
                new Penguin()
        };

        for (Bird bird : birds) {
            bird.eat();
        }

        // DIP: 구현체를 외부에서 주입
        new NotificationService(
                new EmailSender()
        ).notifyUser(
                "주문이 완료되었습니다"
        );

        new NotificationService(
                new SmsSender()
        ).notifyUser(
                "주문이 완료되었습니다"
        );
    }
}
```

SRP와 ISP도 같은 방식으로 객체를 생성하고 메서드를 호출한다.

1번 섹션의 출력 결과와 일치하는지 확인한다.

</details>

**확인**

다음 명령으로 실행했을 때 1번 섹션의 출력 결과와 같은지 확인한다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 6. 학습 체크

* [ ] SRP: 클래스가 변경되어야 하는 이유가 두 개 이상인지 기준으로 책임을 점검할 수 있다.
* [ ] OCP: `if-else` 분기를 인터페이스와 다형성으로 변경하면 기존 코드 수정 없이 확장할 수 있는 이유를 설명할 수 있다.
* [ ] LSP: 자식 클래스가 부모 클래스의 동작 계약을 위반할 때 발생하는 문제를 설명할 수 있다.
* [ ] ISP: 인터페이스를 기능별로 분리했을 때 불필요한 메서드 구현이 사라지는 이유를 설명할 수 있다.
* [ ] DIP: 의존성 주입이 구현체 교체를 쉽게 만드는 이유를 설명할 수 있다.

---

## 7. 최종 완성 체크리스트

* [ ] `Srp.java` — `Journal`에 저장 또는 출력 코드가 없다.
* [ ] `Ocp.java` — 새로운 등급을 새로운 클래스 추가만으로 구현할 수 있다.
* [ ] `Lsp.java` — `Bird` 타입으로 사용할 때 예외가 발생하지 않는다.
* [ ] `Isp.java` — 지원하지 않는 기능을 예외로 처리하는 빈 메서드가 없다.
* [ ] `Dip.java` — `NotificationService`가 `new EmailSender()`를 직접 호출하지 않는다.
* [ ] `Main.java` — 1번 섹션과 같은 출력 결과가 나온다.

---

## 8. 선택 도전 과제

1. **OCP 확장**: 5% 할인을 적용하는 `SilverDiscount`를 추가하고 기존 파일을 수정하지 않았는지 확인한다.
2. **DIP 테스트 구현체**: 콘솔에 출력하는 대신 전송한 메시지를 리스트에 저장하는 `MockSender`를 구현해 주입한다.
3. **ISP 기능 확장**: `Faxer`까지 구현하는 `AllInOneMachine`을 만들고 `Printer`, `Scanner`, `Faxer` 타입으로 각각 전달해 호출한다.
4. **LSP 정사각형 문제**: 정사각형과 직사각형의 상속 관계에서 `setWidth()`와 `setHeight()`가 LSP를 위반하는 이유를 조사한다.
5. **SOLID 통합**: 다섯 가지 원칙을 온라인 주문과 같은 하나의 작은 시나리오에 적용하고, 자연스럽게 적용되는 부분과 불필요하게 복잡해지는 부분을 비교한다.
