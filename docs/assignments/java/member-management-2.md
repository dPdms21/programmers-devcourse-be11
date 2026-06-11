# 회원 관리 만들기 (객체지향 · 추상 클래스)

> 2차원 배열로 구현한 회원 관리 프로그램을 객체지향과 추상 클래스를 활용해 다시 설계한다. 핵심 전환은 `String[][]` 배열에서 `Member` 객체 배열로 바꾸는 것이다. 회원 등급을 일반 회원과 VIP 회원으로 나누고, 등급마다 다른 혜택을 가지도록 구현한다.

---

## 1. 무엇을 만드나요?

요금제로 정원을 정하고, 회원을 추가, 조회, 수정, 삭제하는 콘솔 프로그램을 구현한다.

회원은 일반 회원 또는 VIP 회원 등급을 가진다.

등급에 따라 출력되는 혜택이 달라진다.

```text
[수행할 업무 - 현재 회원수 : 1/20]
[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)
[4]전체조회 [5]수정 [6]삭제 [7]종료
> 4
[VIP] 홍길동 / hong@a.com / 010-1111 (혜택: 10% 할인 + 무료배송)
[일반] 김철수 / kim@b.com / 010-2222 (혜택: 기본 서비스)
```

기능은 기존 2차원 배열 버전과 같지만, 구조를 객체지향 방식으로 개선한다.

기존에는 `members[i][0]`, `members[i][1]`, `members[i][2]`처럼 인덱스로 회원 정보를 다뤘다.

이번 과제에서는 회원 정보를 `Member` 객체로 묶어 관리한다.

---

## 2. 학습 목표

| 개념          | 설명                                             |
| ----------- | ---------------------------------------------- |
| 클래스로 데이터 묶기 | `String[][]` 대신 `Member` 객체로 회원 정보를 표현한다.      |
| 추상 클래스      | 회원의 공통 틀을 `Member` 추상 클래스로 정의한다.               |
| 추상 메서드      | 등급과 혜택을 자식 클래스에서 반드시 구현하도록 강제한다.               |
| 상속          | `NormalMember`, `VipMember`가 `Member`를 상속받는다.  |
| `super`     | 자식 생성자에서 부모 생성자를 호출해 공통 필드를 초기화한다.             |
| 오버라이딩       | 등급별로 `getGrade()`, `getBenefit()`을 다르게 구현한다.   |
| 다형성         | `Member[]` 배열에 일반 회원과 VIP 회원을 함께 담아 처리한다.      |
| 캡슐화         | 회원 데이터와 CRUD 로직을 `MemberManager` 클래스 안에서 관리한다. |

---

## 3. 핵심 개념

### 1. 2차원 배열에서 객체 배열로 전환

기존 2차원 배열 구조에서는 회원 한 명의 정보를 인덱스로 관리했다.

```text
members[i][0] → 이름
members[i][1] → 이메일
members[i][2] → 연락처
```

이 방식은 열 번호의 의미를 기억해야 하므로 실수하기 쉽다.

객체 배열로 바꾸면 회원 정보를 더 명확하게 다룰 수 있다.

```text
String[][] members  →  Member[] members
members[i][1]       →  members[i].getEmail()
```

`Member` 객체가 이름, 이메일, 연락처를 하나로 묶어 가지므로 코드의 의미가 더 분명해진다.

### 2. 추상 클래스 Member

모든 회원은 이름, 이메일, 연락처를 공통으로 가진다.

이 공통 정보는 부모 클래스인 `Member`에 둔다.

하지만 회원 등급과 혜택은 일반 회원과 VIP 회원이 다르므로 추상 메서드로 선언한다.

```java
public abstract String getGrade();
public abstract String getBenefit();
```

자식 클래스는 이 메서드를 반드시 구현해야 한다.

### 3. 상속과 다형성

`NormalMember`와 `VipMember`는 `Member`를 상속받는다.

```java
Member[] members = new Member[capacity];

members[0] = new NormalMember("김철수", "kim@b.com", "010-2222");
members[1] = new VipMember("홍길동", "hong@a.com", "010-1111");
```

배열 타입은 `Member` 하나이지만, 실제로는 일반 회원과 VIP 회원 객체가 함께 들어갈 수 있다.

```java
members[i].printInfo();
```

같은 `printInfo()`를 호출해도 각 객체가 구현한 등급과 혜택에 따라 다른 결과가 출력된다.

### 4. MemberManager 캡슐화

기존에는 회원 배열, 현재 회원 수, 추가, 조회, 수정, 삭제 로직이 `main`과 여러 `static` 메서드에 흩어져 있었다.

이번 구조에서는 `MemberManager` 클래스가 회원 배열과 CRUD 로직을 관리한다.

이를 통해 회원 관리 책임을 한 클래스 안에 모을 수 있다.

---

## 4. 파일 구조

| 파일                   | 역할                                                     |
| -------------------- | ------------------------------------------------------ |
| `Member.java`        | 추상 클래스이다. 회원의 공통 필드, 생성자, getter, 추상 메서드, 출력 메서드를 가진다. |
| `NormalMember.java`  | `Member`를 상속한 일반 회원 클래스이다.                             |
| `VipMember.java`     | `Member`를 상속한 VIP 회원 클래스이다.                            |
| `MemberManager.java` | 회원 배열과 추가, 조회, 수정, 삭제 기능을 관리한다.                        |
| `Main.java`          | 요금제 선택과 메뉴 루프를 담당한다.                                   |

---

## 5. Step by Step

각 Step에는 목표, 할 일, 힌트, 확인 방법이 있다.

한 Step씩 구현한 뒤 실행 결과를 확인하면서 다음 Step으로 넘어간다.

---

### Step 1. 회원 공통 틀 만들기 (`Member.java`)

**목표**: 모든 회원이 공유하는 정보와 등급별로 강제할 메서드를 가진 추상 클래스를 만든다.

**할 일**

1. `abstract class Member`를 선언한다.
2. `name`, `email`, `phone` 필드를 선언한다.
3. 자식 클래스에서 사용할 수 있도록 필드는 `protected`로 둔다.
4. 이름, 이메일, 연락처를 받는 생성자를 작성한다.
5. `getName()`, `getEmail()`, `getPhone()` 메서드를 작성한다.
6. `getGrade()`, `getBenefit()`을 추상 메서드로 선언한다.
7. 공통 출력 메서드 `printInfo()`를 작성한다.
8. 수정용 메서드 `update()`를 작성한다.

**힌트**

```java
public abstract class Member {
    protected String name;
    protected String email;
    protected String phone;

    public Member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public abstract String getGrade();
    public abstract String getBenefit();

    public void printInfo() {
        System.out.println("[" + getGrade() + "] " + name + " / " + email
                + " / " + phone + " (혜택: " + getBenefit() + ")");
    }

    public void update(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
```

`printInfo()`는 부모 클래스에 구현되어 있지만, 내부에서 자식 클래스가 구현한 `getGrade()`와 `getBenefit()`을 사용한다.

**확인**: 컴파일 에러가 없으면 성공이다.

`new Member(...)`처럼 직접 객체를 생성하려고 하면 추상 클래스는 객체를 만들 수 없다는 에러가 발생한다.

---

### Step 2. 일반 회원 클래스 만들기 (`NormalMember.java`)

**목표**: `Member`를 상속한 일반 회원 클래스를 만든다.

**할 일**

1. `NormalMember` 클래스가 `Member`를 상속하도록 작성한다.
2. 생성자에서 `super(...)`로 부모 생성자를 호출한다.
3. `getGrade()`를 오버라이딩한다.
4. `getBenefit()`을 오버라이딩한다.
5. 일반 회원 등급과 혜택을 반환한다.

**힌트**

```java
public class NormalMember extends Member {
    public NormalMember(String name, String email, String phone) {
        super(name, email, phone);
    }

    @Override
    public String getGrade() {
        return "일반";
    }

    @Override
    public String getBenefit() {
        return "기본 서비스";
    }
}
```

**확인**: 일반 회원 객체를 만들어 `printInfo()`를 호출했을 때 `[일반]` 형식으로 출력되면 성공이다.

---

### Step 3. VIP 회원 클래스 만들기 (`VipMember.java`)

**목표**: `Member`를 상속한 VIP 회원 클래스를 만든다.

**할 일**

1. `VipMember` 클래스가 `Member`를 상속하도록 작성한다.
2. 생성자에서 `super(...)`로 부모 생성자를 호출한다.
3. `getGrade()`를 오버라이딩한다.
4. `getBenefit()`을 오버라이딩한다.
5. VIP 회원 등급과 혜택을 반환한다.

**힌트**

```java
public class VipMember extends Member {
    public VipMember(String name, String email, String phone) {
        super(name, email, phone);
    }

    @Override
    public String getGrade() {
        return "VIP";
    }

    @Override
    public String getBenefit() {
        return "10% 할인 + 무료배송";
    }
}
```

**확인**: VIP 회원 객체를 만들어 `printInfo()`를 호출했을 때 `[VIP]` 형식과 VIP 혜택이 출력되면 성공이다.

---

### Step 4. 회원 관리자 만들기 (`MemberManager.java`)

**목표**: 회원 배열과 현재 회원 수를 관리하고, 회원을 추가하는 기능을 만든다.

**할 일**

1. `Member[] members` 필드를 선언한다.
2. `memberCnt` 필드를 선언한다.
3. 생성자에서 정원만큼 `Member[]` 배열을 생성한다.
4. `memberCnt`를 0으로 초기화한다.
5. `isFull()` 메서드로 정원 초과 여부를 확인한다.
6. `existsEmail(String email)` 메서드로 이메일 중복 여부를 확인한다.
7. `add(Member member)` 메서드로 회원을 배열에 추가한다.
8. 현재 회원 수와 정원을 반환하는 getter를 작성한다.

**힌트**

```java
public class MemberManager {
    private Member[] members;
    private int memberCnt;

    public MemberManager(int capacity) {
        members = new Member[capacity];
        memberCnt = 0;
    }

    public boolean isFull() {
        return memberCnt == members.length;
    }

    public boolean existsEmail(String email) {
        for (int i = 0; i < memberCnt; i++) {
            if (members[i].getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    public void add(Member member) {
        members[memberCnt] = member;
        memberCnt++;
    }

    public int getCount() {
        return memberCnt;
    }

    public int getCapacity() {
        return members.length;
    }
}
```

`memberCnt`까지만 순회하면 빈 칸인 `null`에 접근하지 않아도 된다.

따라서 기존 2차원 배열 버전보다 null 처리가 줄어든다.

**확인**: `MemberManager`를 만들고 회원을 추가했을 때 `getCount()`가 증가하면 성공이다.

---

### Step 5. 조회 기능 구현하기 (`MemberManager.java`)

**목표**: 이메일과 이름으로 회원을 찾고, 전체 회원을 출력한다.

**할 일**

1. `findByEmail(String email)` 메서드를 작성한다.
2. 이메일이 일치하는 회원을 찾으면 해당 `Member`를 반환한다.
3. 없으면 `null`을 반환한다.
4. `findByName(String name)` 메서드를 작성한다.
5. 이름이 일치하는 회원을 찾으면 해당 `Member`를 반환한다.
6. 없으면 `null`을 반환한다.
7. `printAll()` 메서드로 등록된 회원 전체를 출력한다.

**힌트**

```java
public Member findByEmail(String email) {
    for (int i = 0; i < memberCnt; i++) {
        if (members[i].getEmail().equals(email)) {
            return members[i];
        }
    }

    return null;
}

public Member findByName(String name) {
    for (int i = 0; i < memberCnt; i++) {
        if (members[i].getName().equals(name)) {
            return members[i];
        }
    }

    return null;
}

public void printAll() {
    for (int i = 0; i < memberCnt; i++) {
        members[i].printInfo();
    }
}
```

조회 결과 출력은 `Main.java`에서 처리한다.

`Member member = manager.findByEmail(email)`처럼 찾은 뒤, `member == null`이면 없다는 메시지를 출력하고, 아니면 `member.printInfo()`를 호출한다.

**확인**: 추가한 회원을 이메일이나 이름으로 찾을 수 있고, 전체 조회가 동작하면 성공이다.

---

### Step 6. 수정과 삭제 기능 구현하기 (`MemberManager.java`)

**목표**: 이메일로 회원을 찾아 수정하거나 삭제한다.

**할 일**

1. `update()` 메서드를 작성한다.
2. 이메일로 회원을 찾는다.
3. 회원이 없으면 `false`를 반환한다.
4. 회원이 있으면 해당 객체의 `update()`를 호출한다.
5. 수정 후 `true`를 반환한다.
6. `delete(String email)` 메서드를 작성한다.
7. 이메일로 삭제할 회원의 인덱스를 찾는다.
8. 회원이 없으면 `false`를 반환한다.
9. 회원이 있으면 뒤 회원들을 한 칸씩 앞으로 당긴다.
10. 마지막 칸을 `null`로 정리한다.
11. `memberCnt`를 1 감소시킨다.
12. 삭제 후 `true`를 반환한다.

**힌트**

```java
public boolean update(String email, String name, String newEmail, String phone) {
    Member member = findByEmail(email);

    if (member == null) {
        return false;
    }

    member.update(name, newEmail, phone);
    return true;
}
```

```java
public boolean delete(String email) {
    int idx = -1;

    for (int i = 0; i < memberCnt; i++) {
        if (members[i].getEmail().equals(email)) {
            idx = i;
            break;
        }
    }

    if (idx == -1) {
        return false;
    }

    for (int i = idx; i < memberCnt - 1; i++) {
        members[i] = members[i + 1];
    }

    members[memberCnt - 1] = null;
    memberCnt--;

    return true;
}
```

기존 2차원 배열 버전에서는 삭제할 때 이름, 이메일, 연락처 칸을 각각 앞으로 당겼다.

객체 배열에서는 회원 객체 참조 하나만 앞으로 당기면 된다.

**확인**: 수정 후 조회했을 때 정보가 바뀌고, 가운데 회원을 삭제해도 전체 조회에서 빈칸 없이 이어지면 성공이다.

---

### Step 7. 요금제와 메뉴 루프 연결하기 (`Main.java`)

**목표**: 요금제로 정원을 정하고, 메뉴를 반복하며 `MemberManager` 기능을 호출한다.

회원 추가 시 등급을 선택해 알맞은 회원 객체를 생성한다.

**할 일**

1. 요금제를 입력받는다.
2. 선택한 요금제를 기준으로 정원을 계산한다.
3. `MemberManager` 객체를 생성한다.
4. `while`문으로 메뉴를 반복 출력한다.
5. 메뉴 번호에 따라 추가, 조회, 전체 조회, 수정, 삭제, 종료를 처리한다.
6. 회원 추가 시 등급을 입력받는다.
7. 일반 회원이면 `NormalMember` 객체를 생성한다.
8. VIP 회원이면 `VipMember` 객체를 생성한다.
9. 생성한 회원 객체를 `manager.add()`로 추가한다.

**힌트**

```java
Scanner sc = new Scanner(System.in);

System.out.println("[1]Lite:10 [2]Basic:20 [3]Premium:30");
int plan = Integer.parseInt(sc.nextLine());

MemberManager manager = new MemberManager(plan * 10);
```

```java
if (manager.isFull()) {
    System.out.println("회원이 꽉 찼습니다.");
} else {
    System.out.println("등급 [1]일반 [2]VIP");
    int grade = Integer.parseInt(sc.nextLine());

    System.out.print("이름 > ");
    String name = sc.nextLine();

    System.out.print("이메일 > ");
    String email = sc.nextLine();

    System.out.print("연락처 > ");
    String phone = sc.nextLine();

    if (manager.existsEmail(email)) {
        System.out.println("이미 존재하는 회원입니다.");
    } else {
        Member member = (grade == 2)
                ? new VipMember(name, email, phone)
                : new NormalMember(name, email, phone);

        manager.add(member);
    }
}
```

입력은 `Integer.parseInt(sc.nextLine())`으로 처리하면 숫자 입력 뒤 줄바꿈이 남아 다음 입력이 밀리는 문제를 줄일 수 있다.

`Scanner`는 하나만 만들어 공유한다.

**확인**: 등급을 선택해 회원을 추가하고, 전체 조회에서 등급과 혜택이 다르게 출력되면 성공이다.

---

### Step 8. 마무리 점검

**목표**: 추상 클래스, 다형성, 캡슐화 구조와 예외 상황 처리를 확인하고 프로그램을 완성한다.

**점검 항목**

* [ ] `Member`가 `abstract` 클래스로 선언되어 있는지 확인한다.
* [ ] `Member`를 직접 `new` 할 수 없는지 확인한다.
* [ ] 일반 회원과 VIP 회원이 `getBenefit()`을 다르게 구현했는지 확인한다.
* [ ] `Member[]` 배열에 일반 회원과 VIP 회원이 함께 담기는지 확인한다.
* [ ] 회원 데이터와 CRUD 로직이 `MemberManager` 안에서 관리되는지 확인한다.
* [ ] 정원 초과 처리가 되는지 확인한다.
* [ ] 이메일 중복 처리가 되는지 확인한다.
* [ ] 없는 회원 조회, 수정, 삭제 처리가 되는지 확인한다.
* [ ] 삭제 후 목록이 빈칸 없이 이어지는지 확인한다.

여기까지 통과하면 객체지향 회원 관리 프로그램이 완성된다.

---

## 6. 객체지향 학습 체크

* [ ] `String[][]` 대신 `Member` 객체로 데이터를 묶는다.
* [ ] `abstract` 클래스와 추상 메서드를 작성한다.
* [ ] `extends`로 상속을 구현한다.
* [ ] `super(...)`로 부모 생성자를 호출한다.
* [ ] `@Override`로 등급별 메서드를 구현한다.
* [ ] `Member[]`에 자식 객체들을 담아 다형성을 활용한다.
* [ ] CRUD 로직을 `MemberManager`로 캡슐화한다.

---

## 7. 최종 완성 체크리스트

* [ ] `Member.java`에서 추상 클래스, 공통 필드, 생성자, getter, 추상 메서드, `printInfo()`를 구현한다.
* [ ] `NormalMember.java`, `VipMember.java`에서 `Member`를 상속하고 등급과 혜택을 구현한다.
* [ ] `MemberManager.java`에서 추가, 조회, 전체 조회, 수정, 삭제를 구현한다.
* [ ] `Main.java`에서 요금제 선택, 메뉴 루프, 등급 선택을 구현한다.
* [ ] 추가, 조회, 수정, 삭제가 모두 동작한다.

---

## 8. 선택 도전 과제

1. **정렬 기능**: `Comparable<Member>`를 구현해 이름순 또는 등급순 정렬
2. **등급 추가**: VVIP 등 새 등급을 클래스 하나 추가로 확장
3. **등급별 요금 계산**: `getMonthlyFee()` 같은 추상 메서드를 추가해 등급별 요금 계산
4. **컬렉션 적용**: 고정 배열 대신 `ArrayList<Member>`로 변경해 정원과 배열 당기기 코드 제거
5. **파일 저장**: 프로그램을 종료해도 회원이 유지되도록 파일 입출력 추가
