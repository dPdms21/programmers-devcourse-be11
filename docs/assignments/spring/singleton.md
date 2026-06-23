# 싱글톤 패턴 익히기

> 싱글톤(Singleton)은 특정 클래스의 객체가 프로그램 전체에서 하나만 생성되도록 제한하는 디자인 패턴이다.
>
> 번호표 발급기가 여러 개 생성되었을 때 번호가 중복되는 문제를 확인하고, 싱글톤 패턴을 적용해 하나의 객체와 상태를 공유하도록 개선한다.
>
> 아래 Step을 순서대로 진행하면 마지막 Step에서 싱글톤이 동작하는 프로그램을 완성할 수 있다.
>
> 각 Step의 힌트는 접혀 있다. 외부에서 객체를 생성하지 못하게 하는 방법을 먼저 생각한 뒤 필요한 경우 힌트를 확인한다.

---

## 0. 먼저 알아둘 점

싱글톤 패턴은 특정 클래스의 객체가 프로그램 전체에서 하나만 존재하도록 보장하는 패턴이다.

번호표 발급기, 설정 관리자, 로그 기록기처럼 여러 객체가 생성되었을 때 상태가 분리되면 문제가 발생하는 경우에 사용할 수 있다.

싱글톤 패턴은 다음 세 가지 요소로 구성된다.

1. 생성자를 `private`으로 선언해 외부 객체 생성을 제한한다.
2. 클래스 내부에 `static` 인스턴스를 하나 보관한다.
3. `getInstance()`를 통해 해당 인스턴스를 반환한다.

객체를 생성하는 시점에 따라 다음 두 가지 방식으로 구분할 수 있다.

* eager 초기화: 클래스가 로딩될 때 객체를 생성한다.
* lazy 초기화: `getInstance()`가 처음 호출될 때 객체를 생성한다.

단순한 lazy 초기화는 여러 스레드가 동시에 `getInstance()`를 호출하면 객체가 두 개 이상 생성될 수 있다.

이 과제에서는 기본적으로 단일 스레드 환경을 기준으로 구현하고, 멀티스레드 안전성은 선택 도전 과제에서 다룬다.

---

## 1. 무엇을 만드나요?

객체가 여러 개 생성되었을 때 발생하는 문제와 싱글톤 적용 후의 차이를 확인하는 `Main`을 완성한다.

최종 실행 결과는 다음과 같다.

```text
===== 1. 싱글톤 없이: 번호표 두 대 =====
A 기계가 발급: 1번
B 기계가 발급: 1번
A 기계가 발급: 2번
B 기계가 발급: 2번

===== 2. 싱글톤 적용: 번호표는 하나뿐 =====
1번 창구가 발급: 1번
2번 창구가 발급: 2번
1번 창구가 발급: 3번
3번 창구가 발급: 4번
같은 기계인가? true

===== 3. lazy 초기화: 설정 관리자 =====
앱 설정 - 테마: dark
앱 설정 - 테마: dark
같은 설정 객체인가? true
```

첫 번째 단계에서는 번호표 발급기 객체가 여러 개 생성되어 각 객체가 독립적인 번호를 발급하는 문제를 확인한다.

두 번째 단계에서는 싱글톤 패턴을 적용해 모든 창구가 동일한 발급기를 사용하도록 개선한다.

세 번째 단계에서는 lazy 초기화 방식으로 설정 관리자 객체를 구현한다.

---

## 2. 학습 목표

| 개념                         | 학습 위치                              |
| -------------------------- | ---------------------------------- |
| 객체가 여러 개 생성되었을 때의 상태 분리 문제 | Step 1 (`NaiveTicketMachine.java`) |
| eager 방식의 싱글톤 구현           | Step 2 (`TicketMachine.java`)      |
| 동일 객체 여부와 상태 공유 확인         | Step 3 (`Main.java`)               |
| lazy 초기화 방식과 멀티스레드 한계      | Step 4 (`Settings.java`)           |
| 전체 실행 흐름 확인                | Step 5 (`Main.java`)               |

---

## 3. 핵심 개념

### 3.1 싱글톤이 해결하는 문제

객체마다 인스턴스 필드는 별도로 생성된다.

번호표 발급기 객체를 두 개 생성하면 각 객체의 `lastNumber`는 각각 `0`부터 시작한다.

따라서 두 발급기가 모두 `1번`, `2번`을 발급하는 중복 문제가 발생한다.

```text
발급기 A
lastNumber = 2

발급기 B
lastNumber = 2
```

설정 관리자도 여러 개 생성되면 한 객체에서 변경한 설정이 다른 객체에는 반영되지 않을 수 있다.

프로그램 전체가 동일한 상태를 공유해야 한다면 객체 생성을 하나로 제한할 필요가 있다.

---

### 3.2 싱글톤의 기본 구성

```java
class TicketMachine {

    private static final TicketMachine instance =
            new TicketMachine();

    private TicketMachine() {
    }

    static TicketMachine getInstance() {
        return instance;
    }
}
```

싱글톤은 다음 세 가지 요소로 구성된다.

#### `private` 생성자

```java
private TicketMachine() {
}
```

생성자를 `private`으로 선언하면 클래스 외부에서 다음 코드를 작성할 수 없다.

```java
new TicketMachine();
```

외부 코드가 임의로 객체를 추가 생성하지 못하도록 제한한다.

#### `static` 인스턴스

```java
private static final TicketMachine instance =
        new TicketMachine();
```

`static` 필드는 클래스에 하나만 존재한다.

따라서 클래스 내부에서 생성한 하나의 인스턴스를 모든 호출자가 공유할 수 있다.

#### `getInstance()`

```java
static TicketMachine getInstance() {
    return instance;
}
```

외부에서는 생성자를 호출할 수 없으므로 `getInstance()`를 통해서만 객체를 전달받는다.

`getInstance()`는 호출할 때마다 같은 인스턴스를 반환한다.

---

### 3.3 참조 비교

객체를 `==`로 비교하면 두 변수가 같은 객체를 참조하는지 확인할 수 있다.

```java
TicketMachine w1 =
        TicketMachine.getInstance();

TicketMachine w2 =
        TicketMachine.getInstance();

System.out.println(w1 == w2);
```

두 변수가 동일한 싱글톤 인스턴스를 참조하므로 결과는 `true`다.

---

### 3.4 eager 초기화

eager 초기화는 클래스가 로딩될 때 인스턴스를 미리 생성하는 방식이다.

```java
private static final TicketMachine instance =
        new TicketMachine();
```

구조가 단순하고 JVM의 클래스 로딩 과정에서 한 번만 초기화되므로 별도의 동기화 없이 안전하게 사용할 수 있다.

다만 프로그램에서 해당 객체를 사용하지 않더라도 인스턴스가 생성된다.

---

### 3.5 lazy 초기화

lazy 초기화는 `getInstance()`가 처음 호출될 때 인스턴스를 생성하는 방식이다.

```java
private static Settings instance;

static Settings getInstance() {
    if (instance == null) {
        instance = new Settings();
    }

    return instance;
}
```

객체를 실제로 사용할 때까지 생성을 미룰 수 있다.

하지만 여러 스레드가 동시에 다음 조건을 통과하면 객체가 두 개 이상 생성될 수 있다.

```java
if (instance == null) {
    instance = new Settings();
}
```

따라서 멀티스레드 환경에서는 동기화나 다른 안전한 초기화 방식을 적용해야 한다.

---

### 3.6 싱글톤 구조 정리

```text
외부 객체 생성
→ private 생성자로 제한

유일한 객체 보관
→ static 필드 사용

객체 접근
→ getInstance() 사용

동일 상태 공유
→ 모든 코드가 같은 인스턴스 참조
```

---

## 4. 파일 구조

| 파일                        | 역할                            |
| ------------------------- | ----------------------------- |
| `NaiveTicketMachine.java` | 싱글톤을 적용하지 않은 번호표 발급기          |
| `TicketMachine.java`      | eager 방식의 싱글톤 번호표 발급기         |
| `Settings.java`           | lazy 방식의 싱글톤 설정 관리자           |
| `Main.java`               | 객체 중복 문제와 싱글톤 적용 결과를 실행하는 진입점 |

실행 명령은 다음과 같다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 5. Step by Step

### Step 1. 객체가 여러 개일 때의 문제 확인하기 (`NaiveTicketMachine.java`)

**목표**: 객체가 여러 개 생성되었을 때 인스턴스 상태가 서로 분리되는 문제를 확인한다.

**할 일**

1. `NaiveTicketMachine`을 작성한다.
2. `issue()`가 호출될 때마다 번호를 1씩 증가시킨다.
3. `main()`에서 발급기 객체를 두 개 생성한다.
4. 두 발급기에서 번갈아 번호를 발급한다.
5. 번호가 중복되는지 확인한다.

<details>
<summary>힌트 보기</summary>

```java
class NaiveTicketMachine {

    private int lastNumber = 0;

    int issue() {
        lastNumber++;

        return lastNumber;
    }
}
```

```java
NaiveTicketMachine a =
        new NaiveTicketMachine();

NaiveTicketMachine b =
        new NaiveTicketMachine();

System.out.println(a.issue());
System.out.println(b.issue());
```

각 객체는 자신의 `lastNumber`를 별도로 관리한다.

따라서 `a`와 `b`가 모두 `1`을 반환한다.

</details>

**확인**

* A 발급기와 B 발급기가 모두 `1번`을 발급하는지 확인한다.
* 인스턴스 필드가 객체마다 별도로 생성된다는 점을 설명할 수 있는지 확인한다.

---

### Step 2. eager 방식의 싱글톤 구현하기 (`TicketMachine.java`)

**목표**: 번호표 발급기 객체가 하나만 생성되도록 제한한다.

**할 일**

1. `TicketMachine`의 생성자를 `private`으로 선언한다.
2. 클래스 내부에 `private static final` 인스턴스를 생성한다.
3. `getInstance()`를 작성한다.
4. 번호를 발급하는 `issue()`를 구현한다.

<details>
<summary>힌트 보기</summary>

```java
class TicketMachine {

    private static final TicketMachine instance =
            new TicketMachine();

    private int lastNumber = 0;

    private TicketMachine() {
    }

    static TicketMachine getInstance() {
        return instance;
    }

    int issue() {
        lastNumber++;

        return lastNumber;
    }
}
```

생성자가 `private`이므로 클래스 외부에서는 `new TicketMachine()`을 호출할 수 없다.

객체는 클래스 내부에서 한 번만 생성되고 `getInstance()`를 통해 반환된다.

</details>

**확인**

* `new TicketMachine()`을 작성했을 때 컴파일 오류가 발생하는지 확인한다.
* `TicketMachine.getInstance()`로 객체를 전달받을 수 있는지 확인한다.
* 인스턴스가 `static` 필드에 하나만 저장되는지 확인한다.

---

### Step 3. 동일 객체와 공유 상태 확인하기 (`Main.java`)

**목표**: 여러 변수가 동일한 싱글톤 객체를 참조하고 상태를 공유하는지 확인한다.

**할 일**

1. `getInstance()`를 여러 번 호출한다.
2. 반환된 객체를 `w1`, `w2`, `w3`에 저장한다.
3. 각 변수에서 번갈아 `issue()`를 호출한다.
4. 번호가 중복 없이 이어지는지 확인한다.
5. `w1 == w2`를 출력한다.

<details>
<summary>힌트 보기</summary>

```java
TicketMachine w1 =
        TicketMachine.getInstance();

TicketMachine w2 =
        TicketMachine.getInstance();

TicketMachine w3 =
        TicketMachine.getInstance();

System.out.println(w1.issue());
System.out.println(w2.issue());
System.out.println(w1.issue());
System.out.println(w3.issue());

System.out.println(w1 == w2);
```

모든 변수는 동일한 `TicketMachine` 객체를 참조한다.

따라서 `lastNumber` 상태를 공유하고 번호가 `1`, `2`, `3`, `4` 순서로 증가한다.

</details>

**확인**

* 발급 번호가 중복 없이 이어지는지 확인한다.
* `w1 == w2` 결과가 `true`인지 확인한다.
* 각 변수가 동일한 객체의 상태를 공유한다는 점을 설명할 수 있는지 확인한다.

---

### Step 4. lazy 초기화 방식 구현하기 (`Settings.java`)

**목표**: `getInstance()`가 처음 호출될 때 객체를 생성하는 방식을 구현한다.

**할 일**

1. `Settings`의 `instance`를 `null` 상태로 선언한다.
2. 생성자를 `private`으로 선언한다.
3. `getInstance()`에서 인스턴스가 없을 때만 객체를 생성한다.
4. 설정값을 저장하는 `theme` 필드를 작성한다.
5. 설정값 조회와 변경 메서드를 작성한다.

<details>
<summary>힌트 보기</summary>

```java
class Settings {

    private static Settings instance;

    private String theme = "dark";

    private Settings() {
    }

    static Settings getInstance() {
        if (instance == null) {
            instance =
                    new Settings();
        }

        return instance;
    }

    String getTheme() {
        return theme;
    }

    void setTheme(String theme) {
        this.theme = theme;
    }
}
```

`instance`는 처음에 `null`이다.

`getInstance()`가 처음 호출되면 `Settings` 객체를 생성하고, 이후 호출부터는 기존 객체를 반환한다.

단순한 lazy 초기화 방식은 멀티스레드 환경에서 안전하지 않을 수 있다.

</details>

**확인**

* `Settings.getInstance()`를 호출하기 전까지 객체가 생성되지 않는지 확인한다.
* 두 변수가 동일한 설정 객체를 참조하는지 확인한다.
* 한쪽에서 변경한 `theme` 값이 다른 쪽에서도 조회되는지 확인한다.
* `s1 == s2` 결과가 `true`인지 확인한다.

---

### Step 5. Main에서 전체 실행하기 (`Main.java`)

**목표**: 객체 중복 문제, eager 싱글톤, lazy 싱글톤을 순서대로 실행한다.

**할 일**

1. `NaiveTicketMachine` 객체 두 개를 생성해 번호 중복 문제를 확인한다.
2. `TicketMachine.getInstance()`를 여러 번 호출해 상태 공유를 확인한다.
3. 참조 비교를 통해 같은 객체인지 확인한다.
4. `Settings.getInstance()`를 통해 lazy 초기화와 설정값 공유를 확인한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {

    public static void main(String[] args) {
        System.out.println(
                "===== 1. 싱글톤 없이 ====="
        );

        NaiveTicketMachine a =
                new NaiveTicketMachine();

        NaiveTicketMachine b =
                new NaiveTicketMachine();

        System.out.println(
                a.issue()
                + ", "
                + b.issue()
        );

        System.out.println(
                "\n===== 2. 싱글톤 적용 ====="
        );

        TicketMachine w1 =
                TicketMachine.getInstance();

        TicketMachine w2 =
                TicketMachine.getInstance();

        System.out.println(
                w1.issue()
                + ", "
                + w2.issue()
        );

        System.out.println(
                w1 == w2
        );

        System.out.println(
                "\n===== 3. lazy 초기화 ====="
        );

        Settings s1 =
                Settings.getInstance();

        s1.setTheme("dark");

        Settings s2 =
                Settings.getInstance();

        System.out.println(
                s2.getTheme()
        );

        System.out.println(
                s1 == s2
        );
    }
}
```

출력 문구를 1번 섹션의 실행 결과와 맞도록 수정한다.

</details>

**확인**

다음 명령으로 실행했을 때 1번 섹션의 출력과 같은 결과가 나오는지 확인한다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 6. 학습 체크

* [ ] 객체가 여러 개 생성되었을 때 상태가 분리되는 이유를 설명할 수 있다.
* [ ] 싱글톤을 적용해야 하는 상황을 예로 들 수 있다.
* [ ] 생성자를 `private`으로 선언하는 이유를 설명할 수 있다.
* [ ] `static` 필드에 인스턴스를 저장하는 이유를 설명할 수 있다.
* [ ] `getInstance()`가 항상 동일한 객체를 반환하는 이유를 설명할 수 있다.
* [ ] `==`를 이용해 같은 객체인지 확인할 수 있다.
* [ ] eager 초기화와 lazy 초기화의 차이를 설명할 수 있다.
* [ ] 단순한 lazy 초기화가 멀티스레드 환경에서 안전하지 않은 이유를 설명할 수 있다.

---

## 7. 최종 완성 체크리스트

* [ ] `TicketMachine`의 생성자가 `private`으로 선언되어 있다.
* [ ] 클래스 외부에서 `new TicketMachine()`을 호출할 수 없다.
* [ ] `TicketMachine`의 인스턴스가 `static` 필드에 하나만 저장된다.
* [ ] `getInstance()`가 동일한 객체를 반환한다.
* [ ] 여러 창구에서 번호를 발급해도 번호가 중복되지 않는다.
* [ ] `w1 == w2` 결과가 `true`다.
* [ ] `Settings`가 lazy 초기화 방식으로 구현되어 있다.
* [ ] 설정값이 여러 호출자 사이에서 공유된다.
* [ ] 단계별 실행 결과가 정상적으로 출력된다.

---

## 8. 선택 도전 과제

1. **번호 초기화**: `TicketMachine`에 번호를 `0`으로 변경하는 `reset()`을 추가하고 모든 참조에서 변경 결과가 공유되는지 확인한다.
2. **현재 번호 조회**: 번호를 증가시키지 않고 현재 마지막 번호를 반환하는 `peek()`를 추가한다.
3. **멀티스레드 안전성**: lazy 방식의 `getInstance()`에 `synchronized`를 적용하고 동시 호출 시 객체 생성이 어떻게 달라지는지 확인한다.
4. **enum 싱글톤**: `enum Settings { INSTANCE; }` 형태로 싱글톤을 구현하고 기존 방식과 비교한다.
5. **싱글톤의 한계**: 전역 상태 증가, 테스트 어려움, 의존관계 은닉 등의 단점을 정리하고 IoC와 DI가 이러한 문제를 어떻게 완화하는지 확인한다.
