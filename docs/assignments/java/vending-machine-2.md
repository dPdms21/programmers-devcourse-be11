# 자판기 만들기 (객체지향 · 추상 클래스)

> 절차지향으로 구현한 자판기를 추상 클래스, 상속, 다형성을 활용해 객체지향 구조로 다시 설계한다. 음료의 공통 부분은 추상 클래스 `Drink`에 모으고, 음료마다 다른 동작은 추상 메서드로 강제해 각 음료 클래스에서 구현한다.

---

## 1. 무엇을 만드나요?

돈을 넣고, 번호로 음료를 선택하면 음료가 나오고 금액이 차감되는 자판기 프로그램을 구현한다.

프로그램을 종료하면 남은 금액을 잔돈으로 반환한다.

```text
============== 자판기 ==============
[1]콜라 : 500  [2]사이다 : 500  [3]환타 : 300  [4]물 : 200
[5]돈 넣기  [6]종료
현재 금액 : 0
====================================
원하는 메뉴를 선택하세요 > 5
넣을 금액 > 1000
1000원을 넣었습니다.

원하는 메뉴를 선택하세요 > 1
시원한 콜라가 나왔습니다.
```

기능은 절차지향 버전과 같지만, 구조는 객체지향 방식으로 설계한다.

거대한 `switch`문으로 음료별 로직을 반복하지 않고, 각 음료 객체가 자신의 동작을 직접 가지도록 만든다.

---

## 2. 학습 목표

| 개념      | 설명                                                   |
| ------- | ---------------------------------------------------- |
| 추상 클래스  | 음료의 공통 틀을 `Drink` 추상 클래스로 정의한다.                      |
| 추상 메서드  | 음료가 나오는 동작을 `dispense()`로 선언하고 자식 클래스에서 반드시 구현하게 한다. |
| 상속      | 각 음료 클래스가 `Drink`를 상속받아 이름과 가격 구조를 물려받는다.            |
| `super` | 자식 생성자에서 부모 생성자를 호출해 이름과 가격을 설정한다.                   |
| 오버라이딩   | 음료마다 `dispense()`를 다르게 구현한다.                         |
| 다형성     | `Drink[]` 배열에 여러 음료 객체를 담고 같은 방식으로 처리한다.             |
| 캡슐화     | 금액과 구매 로직을 `VendingMachine` 클래스 안에서 관리한다.            |

---

## 3. 핵심 개념

### 1. 추상 클래스

모든 음료는 이름과 가격을 공통으로 가진다.

이 공통 필드는 부모 클래스인 `Drink`에 작성한다.

하지만 음료가 나올 때 출력되는 메시지는 음료마다 다르므로, `dispense()`는 추상 메서드로 선언한다.

```java
public abstract void dispense();
```

추상 메서드는 본문이 없고, 자식 클래스가 반드시 구현해야 한다.

추상 클래스는 직접 객체를 생성할 수 없다.

따라서 `new Drink()`처럼 사용할 수 없고, `Coke`, `Cider` 같은 자식 클래스를 통해 사용한다.

### 2. 상속과 super

각 음료 클래스는 `Drink`를 상속받는다.

자식 클래스의 생성자에서는 `super(...)`를 사용해 부모 클래스의 생성자를 호출한다.

```java
public Coke() {
    super("콜라", 500);
}
```

이를 통해 음료 이름과 가격을 부모 클래스의 필드에 저장할 수 있다.

### 3. 오버라이딩

부모 클래스에서 선언한 `dispense()` 메서드는 각 음료 클래스에서 다르게 구현한다.

```java
@Override
public void dispense() {
    System.out.println("시원한 콜라가 나왔습니다.");
}
```

같은 `dispense()` 메서드라도 실제 객체가 무엇인지에 따라 다른 동작이 실행된다.

### 4. 다형성

부모 타입 배열인 `Drink[]`에 여러 자식 객체를 담을 수 있다.

```java
Drink[] drinks = {
    new Coke(),
    new Cider(),
    new Fanta(),
    new Water()
};
```

배열 타입은 `Drink`이지만 실제로는 서로 다른 음료 객체가 들어 있다.

```java
drinks[0].dispense();
```

이처럼 부모 타입으로 메서드를 호출해도 실제 객체의 오버라이딩된 메서드가 실행된다.

이를 통해 절차지향 버전의 반복적인 `switch` 분기를 줄일 수 있다.

---

## 4. 파일 구조

| 파일                    | 역할                                                          |
| --------------------- | ----------------------------------------------------------- |
| `Drink.java`          | 추상 클래스이다. 음료의 공통 필드, 생성자, getter, 추상 메서드 `dispense()`를 가진다. |
| `Coke.java`           | `Drink`를 상속한 콜라 클래스이다.                                      |
| `Cider.java`          | `Drink`를 상속한 사이다 클래스이다.                                     |
| `Fanta.java`          | `Drink`를 상속한 환타 클래스이다.                                      |
| `Water.java`          | `Drink`를 상속한 물 클래스이다.                                       |
| `VendingMachine.java` | 금액과 음료 목록을 관리한다. 돈 넣기, 메뉴 출력, 구매, 잔돈 반환 기능을 가진다.            |
| `Main.java`           | 메뉴를 입력받고 자판기 기능을 호출한다.                                      |

---

## 5. Step by Step

각 Step에는 목표, 할 일, 힌트, 확인 방법이 있다.

한 Step씩 구현한 뒤 실행 결과를 확인하면서 다음 Step으로 넘어간다.

---

### Step 1. 음료의 공통 틀 만들기 (`Drink.java`)

**목표**: 모든 음료가 공유하는 이름, 가격과 강제할 동작 `dispense()`를 가진 추상 클래스를 만든다.

**할 일**

1. `abstract class Drink`를 선언한다.
2. `name`, `price` 필드를 선언한다.
3. 자식 클래스에서 사용할 수 있도록 필드는 `protected`로 둔다.
4. 이름과 가격을 받는 생성자를 작성한다.
5. `getName()`, `getPrice()` 메서드를 작성한다.
6. 본문 없는 추상 메서드 `dispense()`를 선언한다.

**힌트**

```java
public abstract class Drink {
    protected String name;
    protected int price;

    public Drink(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public abstract void dispense();
}
```

**확인**: 컴파일 에러가 없으면 성공이다.

`new Drink(...)`처럼 직접 객체를 생성하려고 하면 추상 클래스는 객체를 만들 수 없다는 에러가 발생한다.

---

### Step 2. 콜라 클래스 만들기 (`Coke.java`)

**목표**: `Drink`를 상속한 콜라 클래스를 만들고, `dispense()`를 구현한다.

**할 일**

1. `Coke` 클래스가 `Drink`를 상속하도록 작성한다.
2. 생성자에서 `super("콜라", 500)`으로 부모 생성자를 호출한다.
3. `dispense()`를 오버라이딩한다.
4. 콜라가 나왔다는 메시지를 출력한다.

**힌트**

```java
public class Coke extends Drink {
    public Coke() {
        super("콜라", 500);
    }

    @Override
    public void dispense() {
        System.out.println("시원한 콜라가 나왔습니다.");
    }
}
```

```java
Drink coke = new Coke();

System.out.println(coke.getName() + " " + coke.getPrice());
coke.dispense();
```

**확인**: `콜라 500`과 콜라 출력 메시지가 나오면 성공이다.

---

### Step 3. 나머지 음료 클래스 만들기 (`Cider.java`, `Fanta.java`, `Water.java`)

**목표**: 콜라와 같은 구조로 사이다, 환타, 물 클래스를 만든다.

**할 일**

1. `Cider` 클래스를 만든다.
2. `Fanta` 클래스를 만든다.
3. `Water` 클래스를 만든다.
4. 각 클래스에서 `Drink`를 상속한다.
5. 각 생성자에서 음료 이름과 가격을 `super(...)`로 전달한다.
6. 각 음료에 맞게 `dispense()`를 오버라이딩한다.

| 클래스     | 생성자 설정              | `dispense()` 메시지 |
| ------- | ------------------- | ---------------- |
| `Cider` | `super("사이다", 500)` | 톡 쏘는 사이다가 나왔습니다. |
| `Fanta` | `super("환타", 300)`  | 달콤한 환타가 나왔습니다.   |
| `Water` | `super("물", 200)`   | 깔끔한 물이 나왔습니다.    |

**확인**: 네 음료 클래스가 모두 컴파일되고, 각 음료의 `dispense()` 메시지가 다르게 출력되면 성공이다.

---

### Step 4. 자판기 기본 기능 만들기 (`VendingMachine.java`)

**목표**: 돈과 음료 목록을 관리하는 자판기 클래스를 만든다.

**할 일**

1. `totalMoney` 필드를 선언한다.
2. `Drink[] drinks` 필드를 선언한다.
3. 생성자에서 현재 금액을 0으로 초기화한다.
4. 생성자에서 `drinks` 배열에 음료 4개를 담는다.
5. `insertMoney(int money)` 메서드로 금액을 추가한다.
6. `printMenu()` 메서드로 메뉴와 현재 금액을 출력한다.

**힌트**

```java
public class VendingMachine {
    private int totalMoney;
    private Drink[] drinks;

    public VendingMachine() {
        totalMoney = 0;

        drinks = new Drink[] {
            new Coke(),
            new Cider(),
            new Fanta(),
            new Water()
        };
    }

    public void insertMoney(int money) {
        totalMoney += money;
        System.out.println(money + "원을 넣었습니다.");
    }

    public void printMenu() {
        System.out.println("============== 자판기 ==============");
        System.out.println("[1]콜라 : 500  [2]사이다 : 500  [3]환타 : 300  [4]물 : 200");
        System.out.println("[5]돈 넣기  [6]종료");
        System.out.println("현재 금액 : " + totalMoney);
        System.out.println("====================================");
    }
}
```

**확인**: `VendingMachine` 객체를 만들고 `printMenu()`를 호출했을 때 메뉴가 출력되면 성공이다.

---

### Step 5. 구매와 잔돈 반환 구현하기 (`VendingMachine.java`)

**목표**: 번호로 음료를 구매하고, 종료 시 잔돈을 반환하는 기능을 만든다.

**할 일**

1. `buy(int menuNumber)` 메서드를 작성한다.
2. `drinks[menuNumber - 1]`로 선택한 음료를 가져온다.
3. 현재 금액이 음료 가격보다 작으면 부족 메시지를 출력하고 종료한다.
4. 현재 금액이 충분하면 음료 가격만큼 차감한다.
5. 선택한 음료의 `dispense()`를 호출한다.
6. `returnChange()` 메서드에서 현재 금액을 반환하고 0으로 초기화한다.

**힌트**

```java
public void buy(int menuNumber) {
    Drink drink = drinks[menuNumber - 1];

    if (totalMoney < drink.getPrice()) {
        System.out.println("금액이 부족합니다.");
        return;
    }

    totalMoney -= drink.getPrice();
    drink.dispense();
}

public int returnChange() {
    int change = totalMoney;
    totalMoney = 0;
    return change;
}
```

`buy()` 메서드 하나로 모든 음료를 처리할 수 있다.

선택한 음료 객체의 `dispense()`가 실행되기 때문이다.

**확인**: 다음 Step에서 메뉴와 연결해 돈 넣기, 구매, 잔돈 반환이 동작하면 성공이다.

---

### Step 6. 메뉴 루프로 연결하기 (`Main.java`)

**목표**: 메뉴를 반복 출력하고, 사용자 입력에 따라 자판기 기능을 호출한다.

**할 일**

1. `Scanner`를 생성한다.
2. `VendingMachine` 객체를 생성한다.
3. `while`문으로 메뉴를 반복 출력한다.
4. 1~4를 입력하면 `buy()`를 호출한다.
5. 5를 입력하면 넣을 금액을 입력받고 `insertMoney()`를 호출한다.
6. 6을 입력하면 잔돈을 반환하고 프로그램을 종료한다.
7. 잘못된 번호를 입력하면 안내 메시지를 출력한다.

**힌트**

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VendingMachine machine = new VendingMachine();

        while (true) {
            machine.printMenu();

            System.out.print("원하는 메뉴를 선택하세요 > ");
            int choice = sc.nextInt();

            if (choice >= 1 && choice <= 4) {
                machine.buy(choice);
            } else if (choice == 5) {
                System.out.print("넣을 금액 > ");
                int money = sc.nextInt();

                machine.insertMoney(money);
            } else if (choice == 6) {
                System.out.println("\n잔돈 " + machine.returnChange() + "원이 반환되었습니다.");
                return;
            } else {
                System.out.println("잘못 입력했습니다. 다시 입력해 주세요.");
            }
        }
    }
}
```

**확인**: 돈 넣기, 음료 구매, 금액 부족 처리, 종료 시 잔돈 반환이 모두 동작하면 성공이다.

---

### Step 7. 마무리 점검

**목표**: 추상 클래스, 상속, 다형성, 캡슐화 구조가 제대로 적용되었는지 확인하고 프로그램을 완성한다.

**점검 항목**

* [ ] `Drink`가 `abstract` 클래스로 선언되어 있는지 확인한다.
* [ ] `Drink`를 직접 `new` 할 수 없는지 확인한다.
* [ ] 네 음료 클래스가 모두 `dispense()`를 다르게 구현했는지 확인한다.
* [ ] `buy()` 하나로 네 음료가 모두 처리되는지 확인한다.
* [ ] 금액이 부족할 때 안내 메시지가 출력되는지 확인한다.
* [ ] 종료 시 남은 금액이 잔돈으로 반환되는지 확인한다.
* [ ] `totalMoney`가 `VendingMachine` 밖에서 직접 변경되지 않는지 확인한다.

여기까지 통과하면 객체지향 자판기 프로그램이 완성된다.

---

## 6. 객체지향 학습 체크

* [ ] `abstract` 클래스와 `abstract` 메서드를 작성한다.
* [ ] `extends`로 상속을 구현한다.
* [ ] `super(...)`로 부모 생성자를 호출한다.
* [ ] `@Override`로 메서드를 재정의한다.
* [ ] 부모 타입 배열 `Drink[]`에 자식 객체를 담는다.
* [ ] `drink.dispense()` 한 줄로 음료별 동작을 실행한다.
* [ ] 상태와 로직을 클래스 안에 캡슐화한다.

---

## 7. 최종 완성 체크리스트

* [ ] `Drink.java`에서 추상 클래스, 필드, 생성자, getter, 추상 메서드 `dispense()`를 구현한다.
* [ ] `Coke.java`, `Cider.java`, `Fanta.java`, `Water.java`에서 `Drink`를 상속하고 `dispense()`를 구현한다.
* [ ] `VendingMachine.java`에서 음료 배열, 돈 넣기, 메뉴 출력, 구매, 잔돈 반환을 구현한다.
* [ ] `Main.java`에서 메뉴 루프를 구현한다.
* [ ] 돈 넣기, 구매, 잔돈 반환이 모두 동작한다.

---

## 8. 선택 도전 과제

1. **인터페이스 버전**: `Drink`를 추상 클래스 대신 인터페이스로 변경해 차이 비교
2. **재고 관리**: 음료마다 남은 수량을 두고 품절 처리
3. **거스름돈 계산**: 동전 단위로 거스름돈 계산
4. **음료 추가**: 새 음료 클래스를 추가하고 배열에 등록해 확장성 확인
