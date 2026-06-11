# 자판기 만들기 (객체지향 · 인터페이스)

> 자판기를 인터페이스, 구현, 다형성을 활용해 객체지향 구조로 설계한다. 추상 클래스 버전과 기능은 같지만, 음료의 규약을 인터페이스 `Drink`로 정하고 각 음료 클래스가 그 규약을 직접 구현한다.

---

## 1. 무엇을 만드나요?

돈을 넣고 번호로 음료를 선택하면 음료가 나오고 금액이 차감되는 자판기 프로그램을 구현한다.

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

원하는 메뉴를 선택하세요 > 2
톡 쏘는 사이다가 나왔습니다.
```

추상 클래스 버전과 기능은 같지만, 이번 과제에서는 음료의 공통 상태를 부모 클래스에 두지 않고 각 음료 클래스가 직접 가진다.

자판기는 각 음료를 `Drink` 인터페이스 타입으로 다루며, 실제 동작은 각 음료 클래스의 구현에 따라 실행된다.

---

## 2. 학습 목표

| 개념               | 설명                                         |
| ---------------- | ------------------------------------------ |
| 인터페이스            | 음료가 갖춰야 할 메서드 규약을 `Drink` 인터페이스로 정의한다.     |
| 구현               | 각 음료 클래스가 `implements Drink`로 인터페이스를 구현한다. |
| 오버라이딩            | 음료마다 `dispense()`를 다르게 구현한다.               |
| 다형성              | `Drink[]` 배열에 여러 음료 객체를 담고 같은 방식으로 처리한다.   |
| 추상 클래스와 인터페이스 비교 | 같은 자판기를 두 방식으로 구현하며 구조 차이를 비교한다.           |

---

## 3. 핵심 개념

### 1. 인터페이스

인터페이스는 클래스가 반드시 갖춰야 할 기능의 목록을 정의한다.

`Drink` 인터페이스는 음료가 가져야 할 이름 조회, 가격 조회, 음료 제공 기능을 선언한다.

```java
public interface Drink {
    String getName();
    int getPrice();
    void dispense();
}
```

인터페이스를 구현하는 클래스는 선언된 메서드를 모두 구현해야 한다.

인터페이스 자체는 상태를 저장하는 일반 필드나 생성자를 가질 수 없다.

따라서 이름과 가격은 각 음료 클래스가 직접 가진다.

### 2. 추상 클래스 버전과의 차이

| 구분                        | 추상 클래스 `Drink`                      | 인터페이스 `Drink`                      |
| ------------------------- | ----------------------------------- | ---------------------------------- |
| 필드                        | 부모 클래스에 `name`, `price`를 한 번만 둔다.   | 각 음료 클래스가 `name`, `price`를 직접 가진다. |
| 생성자                       | 부모 생성자를 만들 수 있고 `super(...)`로 호출한다. | 생성자가 없다.                           |
| `getName()`, `getPrice()` | 부모 클래스에서 한 번 구현하고 자식이 물려받는다.        | 각 음료 클래스가 직접 구현한다.                 |
| 공통 구현                     | 공통 코드를 물려줄 수 있다.                    | 기본적으로 공통 구현을 물려주기보다 규약을 강제한다.      |
| 확장 방식                     | 단일 상속만 가능하다.                        | 여러 인터페이스를 동시에 구현할 수 있다.            |

인터페이스는 공통 코드를 물려주는 구조보다는, 반드시 가져야 할 기능을 강제하는 구조에 가깝다.

대신 여러 인터페이스를 동시에 구현할 수 있다는 장점이 있다.

예를 들어 하나의 클래스가 `Drink`와 `Comparable`을 함께 구현할 수 있다.

### 3. 다형성

자판기는 음료 객체들을 `Drink` 인터페이스 타입으로 다룬다.

```java
Drink[] drinks = {
    new Coke(),
    new Cider(),
    new Fanta(),
    new Water()
};
```

배열 타입은 `Drink`이지만 실제 객체는 `Coke`, `Cider`, `Fanta`, `Water`이다.

```java
drinks[1].dispense();
```

이때 실제로 실행되는 메서드는 `Cider` 클래스에서 구현한 `dispense()`이다.

사용하는 쪽인 `VendingMachine`은 추상 클래스 버전인지 인터페이스 버전인지와 상관없이 `Drink` 타입으로 음료를 처리할 수 있다.

---

## 4. 파일 구조

| 파일                    | 역할                                               |
| --------------------- | ------------------------------------------------ |
| `Drink.java`          | 인터페이스이다. 음료가 갖춰야 할 메서드 목록을 선언한다.                 |
| `Coke.java`           | `Drink`를 구현한 콜라 클래스이다.                           |
| `Cider.java`          | `Drink`를 구현한 사이다 클래스이다.                          |
| `Fanta.java`          | `Drink`를 구현한 환타 클래스이다.                           |
| `Water.java`          | `Drink`를 구현한 물 클래스이다.                            |
| `VendingMachine.java` | 금액과 음료 목록을 관리한다. 돈 넣기, 메뉴 출력, 구매, 잔돈 반환 기능을 가진다. |
| `Main.java`           | 메뉴를 입력받고 자판기 기능을 호출한다.                           |

---

## 5. Step by Step

각 Step에는 목표, 할 일, 힌트, 확인 방법이 있다.

한 Step씩 구현한 뒤 실행 결과를 확인하면서 다음 Step으로 넘어간다.

---

### Step 1. 음료 규약 만들기 (`Drink.java`)

**목표**: 모든 음료가 갖춰야 할 메서드 3개를 인터페이스로 선언한다.

**할 일**

1. `interface Drink`를 선언한다.
2. `getName()` 메서드를 선언한다.
3. `getPrice()` 메서드를 선언한다.
4. `dispense()` 메서드를 선언한다.
5. 메서드는 본문 없이 선언만 작성한다.

**힌트**

```java
public interface Drink {
    String getName();
    int getPrice();
    void dispense();
}
```

인터페이스의 메서드는 자동으로 `public abstract` 성격을 가진다.

일반적인 인스턴스 필드나 생성자는 작성하지 않는다.

**확인**: 컴파일 에러가 없으면 성공이다.

---

### Step 2. 콜라 클래스 만들기 (`Coke.java`)

**목표**: `Drink`를 구현한 콜라 클래스를 만든다.

**할 일**

1. `Coke` 클래스가 `Drink`를 구현하도록 작성한다.
2. `name`, `price` 필드를 직접 선언한다.
3. `getName()`을 구현한다.
4. `getPrice()`를 구현한다.
5. `dispense()`를 구현한다.
6. 콜라가 나왔다는 메시지를 출력한다.

**힌트**

```java
public class Coke implements Drink {
    private String name = "콜라";
    private int price = 500;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPrice() {
        return price;
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

추상 클래스 버전에서는 `super("콜라", 500)`으로 부모 필드에 이름과 가격을 저장했다.

인터페이스 버전에서는 공통 필드와 생성자를 물려받지 않으므로, 각 음료 클래스가 이름과 가격을 직접 가진다.

**확인**: `콜라 500`과 콜라 출력 메시지가 나오면 성공이다.

---

### Step 3. 나머지 음료 클래스 만들기 (`Cider.java`, `Fanta.java`, `Water.java`)

**목표**: 콜라와 같은 구조로 사이다, 환타, 물 클래스를 만든다.

**할 일**

1. `Cider` 클래스를 만든다.
2. `Fanta` 클래스를 만든다.
3. `Water` 클래스를 만든다.
4. 각 클래스가 `Drink`를 구현하도록 작성한다.
5. 각 클래스에서 `name`, `price`를 직접 가진다.
6. 각 클래스에서 `getName()`, `getPrice()`, `dispense()`를 구현한다.

| 클래스     | 이름 / 가격         | `dispense()` 메시지 |
| ------- | --------------- | ---------------- |
| `Cider` | `"사이다"` / `500` | 톡 쏘는 사이다가 나왔습니다. |
| `Fanta` | `"환타"` / `300`  | 달콤한 환타가 나왔습니다.   |
| `Water` | `"물"` / `200`   | 깔끔한 물이 나왔습니다.    |

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

`Drink[]`는 인터페이스 타입 배열이지만, 그 안에는 `Coke`, `Cider`, `Fanta`, `Water` 같은 구현 객체를 담을 수 있다.

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

이 부분은 추상 클래스 버전과 거의 동일하다.

사용하는 쪽에서는 `Drink`가 추상 클래스인지 인터페이스인지 크게 달라지지 않는다.

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

**목표**: 인터페이스, 구현, 다형성 구조가 제대로 적용되었는지 확인하고 프로그램을 완성한다.

**점검 항목**

* [ ] `Drink`가 인터페이스로 선언되어 있는지 확인한다.
* [ ] `Drink`에 메서드 선언만 있는지 확인한다.
* [ ] 네 음료 클래스가 모두 `implements Drink`로 세 메서드를 구현했는지 확인한다.
* [ ] 각 음료가 이름과 가격을 직접 가지고 있는지 확인한다.
* [ ] `buy()` 하나로 네 음료가 모두 처리되는지 확인한다.
* [ ] 금액 부족 처리가 동작하는지 확인한다.
* [ ] 잔돈 반환이 동작하는지 확인한다.

여기까지 통과하면 인터페이스 기반 자판기 프로그램이 완성된다.

---

## 6. 객체지향 학습 체크

* [ ] `interface`를 선언한다.
* [ ] `implements`로 인터페이스를 구현한다.
* [ ] `@Override`로 메서드를 구현한다.
* [ ] 인터페이스 타입 배열 `Drink[]`에 구현 객체를 담는다.
* [ ] `drink.dispense()` 한 줄로 음료별 동작을 실행한다.
* [ ] 추상 클래스 버전과 인터페이스 버전의 차이를 설명할 수 있다.

---

## 7. 최종 완성 체크리스트

* [ ] `Drink.java`에서 인터페이스와 메서드 3개를 선언한다.
* [ ] `Coke.java`, `Cider.java`, `Fanta.java`, `Water.java`에서 `Drink`를 구현하고 세 메서드를 작성한다.
* [ ] `VendingMachine.java`에서 음료 배열, 돈 넣기, 메뉴 출력, 구매, 잔돈 반환을 구현한다.
* [ ] `Main.java`에서 메뉴 루프를 구현한다.
* [ ] 돈 넣기, 구매, 잔돈 반환이 모두 동작한다.

---

## 8. 선택 도전 과제

1. **두 버전 비교**: 추상 클래스 버전과 인터페이스 버전을 표로 비교
2. **다중 구현**: `Drink`와 다른 인터페이스를 함께 구현해 인터페이스의 장점 확인
3. **기본 메서드**: Java 8 `default` 메서드로 공통 동작 추가
4. **재고 관리**: 음료별 남은 수량을 두고 품절 처리
5. **음료 추가**: 새 음료 클래스를 추가하고 배열에 등록해 확장성 확인

---

## 9. 한 줄 정리

* 상태나 공통 구현을 물려주고 싶을 때는 추상 클래스를 사용한다.
* 기능 규약만 강제하고 여러 규약을 동시에 적용하고 싶을 때는 인터페이스를 사용한다.
