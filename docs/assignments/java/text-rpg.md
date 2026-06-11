# 텍스트 RPG 전투 만들기 (객체지향 · 생성자)

> 용사와 몬스터가 턴제로 싸우는 콘솔 게임을 구현한다. 캐릭터를 만들 때 생성자가 호출되고, 객체끼리 메서드로 상호작용하는 흐름을 통해 클래스, 객체, 캡슐화, 생성자 오버로딩, `this`, 생성자 체이닝을 학습한다.

---

## 1. 무엇을 만드나요?

용사가 몬스터들과 차례로 싸우는 텍스트 RPG 전투 프로그램을 구현한다.

용사와 몬스터는 서로 번갈아 공격한다.

공격을 받으면 HP가 감소하고, HP가 0이 되면 쓰러진다.

```text
=== 전투 시작! 용사 vs 슬라임 ===
용사 (HP: 100)
슬라임 (HP: 30)

용사의 공격! 슬라임에게 25 피해
슬라임 (HP: 5)
슬라임의 공격! 용사에게 5 피해
용사 (HP: 95)
용사의 공격! 슬라임에게 25 피해
슬라임을 쓰러뜨렸다!

=== 다음 상대: 고블린 ===
...
```

---

## 2. 학습 목표

| 개념         | 설명                                  |
| ---------- | ----------------------------------- |
| 클래스와 객체    | 캐릭터 클래스를 만들고 용사와 몬스터 객체를 생성한다.      |
| 생성자        | 캐릭터를 `new` 할 때 이름, HP, 공격력을 초기화한다.  |
| `this` 키워드 | 생성자에서 필드와 매개변수를 구분한다.               |
| 캡슐화        | 필드는 `private`으로 숨기고, 행동은 메서드로 제공한다. |
| 생성자 오버로딩   | 매개변수가 다른 생성자를 여러 개 만든다.             |
| 생성자 체이닝    | `this(...)`로 같은 클래스의 다른 생성자를 호출한다.  |
| 객체 여러 개 관리 | 몬스터 여러 마리를 배열로 관리한다.                |

---

## 3. 핵심 개념

### 1. 클래스와 객체

`Character` 클래스는 캐릭터를 만들기 위한 설계도이다.

이 클래스를 사용해 용사, 슬라임, 고블린, 드래곤 같은 객체를 생성한다.

각 객체는 이름, HP, 공격력을 가진다.

### 2. 생성자

생성자는 객체를 만들 때 자동으로 호출된다.

객체가 생성될 때 이름, HP, 공격력 같은 초기값을 설정한다.

```java
public Character(String name, int hp, int power) {
    this.name = name;
    this.hp = hp;
    this.power = power;
}
```

```java
Character hero = new Character("용사", 100, 25);
```

### 3. this 키워드

매개변수와 필드 이름이 같을 때 `this`를 사용해 현재 객체의 필드를 가리킨다.

```java
this.name = name;
```

`this.name`은 현재 객체의 필드이고, `name`은 생성자로 전달받은 매개변수이다.

### 4. 캡슐화

HP를 외부에서 직접 바꾸지 못하도록 필드를 `private`으로 선언한다.

상태 변경은 `takeDamage()` 같은 메서드를 통해 이루어지도록 한다.

---

## 4. 파일 구조

| 파일               | 역할                               |
| ---------------- | -------------------------------- |
| `Character.java` | 캐릭터 클래스이다. 필드, 생성자, 전투 메서드를 가진다. |
| `Main.java`      | 용사와 몬스터 객체를 만들고 전투를 진행한다.        |

상속과 다형성으로 확장하면 `Character`를 부모 클래스로 두고 `Hero`, `Monster` 클래스로 나눌 수 있다.

---

## 5. Step by Step

각 Step에는 목표, 할 일, 힌트, 확인 방법이 있다.

한 Step씩 구현한 뒤 실행 결과를 확인하면서 다음 Step으로 넘어간다.

---

### Step 1. 캐릭터 설계도와 생성자 작성 (`Character.java`)

**목표**: 이름, HP, 공격력을 가진 캐릭터 클래스를 만들고, 생성자로 객체를 초기화한다.

**할 일**

1. `name`, `hp`, `power` 필드를 선언한다.
2. 필드는 외부에서 직접 접근하지 못하도록 `private`으로 선언한다.
3. 이름, HP, 공격력을 전달받는 생성자를 작성한다.
4. 생성자에서 `this`를 사용해 필드에 값을 저장한다.
5. 상태를 출력하는 `showStatus()` 메서드를 작성한다.
6. `Main.java`에서 캐릭터 객체를 생성하고 상태를 출력한다.

**힌트**

```java
public class Character {
    private String name;
    private int hp;
    private int power;

    public Character(String name, int hp, int power) {
        this.name = name;
        this.hp = hp;
        this.power = power;
    }

    public void showStatus() {
        System.out.println(name + " (HP: " + hp + ")");
    }
}
```

```java
Character hero = new Character("용사", 100, 25);
hero.showStatus();
```

**확인**: `용사 (HP: 100)`이 출력되면 성공이다.

---

### Step 2. 캐릭터 행동 메서드 작성 (`Character.java`)

**목표**: 공격하고, 피해를 받고, 생존 여부를 판단하는 메서드를 추가한다.

**할 일**

1. `isAlive()` 메서드를 작성한다.
2. HP가 0보다 크면 살아있는 것으로 판단한다.
3. `takeDamage(int dmg)` 메서드를 작성한다.
4. 피해량만큼 HP를 감소시킨다.
5. HP가 0보다 작아지지 않도록 제한한다.
6. `attack(Character target)` 메서드를 작성한다.
7. 상대에게 자신의 공격력만큼 피해를 준다.

**힌트**

```java
public boolean isAlive() {
    return hp > 0;
}

public void takeDamage(int dmg) {
    hp -= dmg;

    if (hp < 0) {
        hp = 0;
    }
}

public void attack(Character target) {
    System.out.println(name + "의 공격! " + target.name + "에게 " + power + " 피해");
    target.takeDamage(power);
}
```

같은 클래스 안에서는 다른 객체의 `private` 필드에도 접근할 수 있다.

따라서 `target.name`처럼 상대 객체의 이름 필드를 사용할 수 있다.

**확인**: 컴파일 에러가 없으면 성공이다.

---

### Step 3. 한 대씩 주고받기 (`Main.java`)

**목표**: 용사와 몬스터 두 객체를 만들어 서로 한 번씩 공격한다.

**할 일**

1. 용사 객체를 생성한다.
2. 몬스터 객체를 생성한다.
3. 용사가 몬스터를 공격한다.
4. 몬스터 상태를 출력한다.
5. 몬스터가 용사를 공격한다.
6. 용사 상태를 출력한다.

**힌트**

```java
Character hero = new Character("용사", 100, 25);
Character monster = new Character("슬라임", 30, 5);

hero.attack(monster);
monster.showStatus();

monster.attack(hero);
hero.showStatus();
```

**확인**: 공격 메시지가 출력되고 HP가 감소하면 성공이다.

---

### Step 4. 턴제 전투 반복 (`Main.java`)

**목표**: 용사와 몬스터 중 하나가 쓰러질 때까지 번갈아 공격한다.

**할 일**

1. `while`문으로 용사와 몬스터가 모두 살아있는 동안 반복한다.
2. 용사가 몬스터를 공격한다.
3. 몬스터 상태를 출력한다.
4. 몬스터가 쓰러졌으면 전투를 종료한다.
5. 몬스터가 살아있으면 용사를 공격한다.
6. 용사 상태를 출력한다.
7. 전투가 끝나면 결과를 출력한다.

**힌트**

```java
while (hero.isAlive() && monster.isAlive()) {
    hero.attack(monster);
    monster.showStatus();

    if (!monster.isAlive()) {
        System.out.println("몬스터를 쓰러뜨렸다!");
        break;
    }

    monster.attack(hero);
    hero.showStatus();
}
```

**확인**: 전투가 자동으로 진행되고, 한쪽 HP가 0이 되면 종료되면 성공이다.

---

### Step 5. 생성자 오버로딩과 체이닝 (`Character.java`)

**목표**: 이름만 전달해 기본 스탯을 가진 몬스터를 만들 수 있도록 생성자를 추가한다.

**할 일**

1. 이름만 받는 생성자를 추가한다.
2. 이름만 받는 생성자 안에서 `this(...)`를 사용한다.
3. 기존 생성자를 호출해 기본 HP와 공격력을 설정한다.

**힌트**

```java
public Character(String name) {
    this(name, 30, 5);
}
```

```java
Character slime = new Character("슬라임");
Character goblin = new Character("고블린", 50, 8);
```

같은 이름의 생성자를 매개변수만 다르게 여러 개 두는 것을 생성자 오버로딩이라고 한다.

한 생성자에서 `this(...)`로 같은 클래스의 다른 생성자를 호출하는 것을 생성자 체이닝이라고 한다.

**확인**: `new Character("슬라임")`만으로 기본 HP와 공격력을 가진 몬스터가 생성되면 성공이다.

---

### Step 6. 몬스터 여러 마리와 연속 전투 (`Main.java`)

**목표**: 여러 몬스터를 배열로 만들고, 용사가 차례로 상대하도록 구현한다.

**할 일**

1. 용사 객체를 생성한다.
2. 몬스터 여러 마리를 배열로 생성한다.
3. 기본 생성자와 커스텀 생성자를 섞어 사용한다.
4. 용사가 살아있는 동안 몬스터 배열을 순서대로 반복한다.
5. 각 몬스터와 턴제 전투를 진행한다.
6. 모든 몬스터를 이기면 클리어를 출력한다.
7. 도중에 용사가 쓰러지면 게임 오버를 출력한다.

**힌트**

```java
Character hero = new Character("용사", 100, 25);

Character[] monsters = {
    new Character("슬라임"),
    new Character("고블린", 50, 8),
    new Character("드래곤", 120, 20)
};

for (Character monster : monsters) {
    System.out.println("\n=== 다음 상대 ===");
    monster.showStatus();

    // Step 4의 전투 반복을 hero vs monster 구조로 실행

    if (!hero.isAlive()) {
        System.out.println("게임 오버");
        break;
    }
}
```

**확인**: 용사가 슬라임, 고블린, 드래곤을 차례로 상대하면 성공이다.

---

### Step 7. 마무리 다듬기

**목표**: 캡슐화, 생성자, 전투 흐름을 점검하고 프로그램을 완성한다.

**점검 항목**

* [ ] 필드가 `private`으로 선언되어 있는지 확인한다.
* [ ] 외부에서 필드를 직접 바꾸지 않고 메서드로만 다루는지 확인한다.
* [ ] 이름만 받는 생성자와 전체 값을 받는 생성자가 모두 동작하는지 확인한다.
* [ ] HP가 음수로 내려가지 않는지 확인한다.
* [ ] 모든 몬스터를 이기면 클리어가 출력되는지 확인한다.
* [ ] 용사가 쓰러지면 게임 오버가 출력되는지 확인한다.

여기까지 통과하면 RPG 전투 게임이 완성된다.

---

## 6. 객체지향 · 생성자 학습 체크

* [ ] 클래스 하나로 여러 객체를 생성한다.
* [ ] 생성자로 객체의 초기 상태를 설정한다.
* [ ] `new`를 사용할 때 생성자가 호출되는 흐름을 이해한다.
* [ ] `this`로 필드와 매개변수를 구분한다.
* [ ] 필드를 `private`으로 선언해 캡슐화한다.
* [ ] 생성자 오버로딩을 사용한다.
* [ ] `this(...)`로 생성자 체이닝을 사용한다.
* [ ] 객체를 배열로 모아 관리한다.

---

## 7. 최종 완성 체크리스트

* [ ] `Character.java`에서 필드, 생성자 2개, `attack()`, `takeDamage()`, `isAlive()`, `showStatus()`를 구현한다.
* [ ] `Main.java`에서 용사와 몬스터 배열을 생성하고 연속 전투를 진행한다.
* [ ] 생성자 오버로딩과 생성자 체이닝을 사용한다.
* [ ] 전투가 끝까지 진행되고 클리어 또는 게임 오버를 처리한다.

---

## 8. 선택 도전 과제

1. **상속 적용**: `Character`를 부모로 두고 `Hero`, `Monster` 클래스로 분리
2. **다형성 적용**: `Character` 타입 배열에 여러 자식 객체를 함께 담아 처리
3. **메서드 오버라이딩**: 몬스터 종류마다 `attack()`을 다르게 재정의
4. **스킬과 방어력**: 방어력 필드를 추가해 받는 피해 감소
5. **플레이어 선택**: 매 턴 공격, 방어, 회복 중 하나를 입력받아 진행
