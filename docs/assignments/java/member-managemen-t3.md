# 회원 관리 만들기 (객체지향 · 인터페이스)

> 회원 관리 프로그램을 인터페이스, 구현, 다형성을 활용해 객체지향 구조로 설계한다. 추상 클래스 버전과 기능은 같지만, 회원의 규약을 인터페이스 `Member`로 정의한다. 이 과제는 추상 클래스와 인터페이스의 차이를 비교하기 위한 학습용 예제이다.

---

## 0. 먼저 알아둘 점

이 회원 관리 예제는 사실 추상 클래스가 더 잘 맞는 구조이다.

회원은 이름, 이메일, 연락처라는 공통 상태를 가진다.

또한 getter, `update()`, `printInfo()` 같은 공통 구현도 많이 공유한다.

하지만 인터페이스는 일반적인 인스턴스 필드와 생성자를 가질 수 없기 때문에, 이름, 이메일, 연락처 필드와 getter, `update()` 코드가 각 클래스에 반복된다.

이 과제의 목표는 다음과 같다.

1. 인터페이스로 같은 회원 관리 프로그램을 구현한다.
2. 이 예제에서는 왜 추상 클래스가 더 적합한지 비교한다.
3. `default` 메서드로 인터페이스의 공통 동작을 일부 줄이는 방법을 확인한다.

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

추상 클래스 버전과 기능은 같지만, 이번 과제에서는 회원의 공통 틀을 인터페이스로 정의한다.

---

## 2. 핵심 개념

### 1. 인터페이스

인터페이스는 클래스가 반드시 갖춰야 할 기능의 목록을 정의한다.

`Member` 인터페이스는 회원이 가져야 할 이름 조회, 이메일 조회, 연락처 조회, 등급 조회, 혜택 조회, 정보 수정 기능을 선언한다.

인터페이스를 구현하는 클래스는 선언된 메서드를 모두 구현해야 한다.

인터페이스 자체는 일반적인 인스턴스 필드나 생성자를 가질 수 없다.

따라서 이름, 이메일, 연락처는 `NormalMember`, `VipMember` 클래스가 각각 직접 가진다.

### 2. default 메서드

Java 8부터 인터페이스에서도 `default` 메서드를 사용할 수 있다.

`default` 메서드는 인터페이스 안에 기본 구현을 작성할 수 있는 메서드이다.

`printInfo()`는 `getGrade()`, `getName()`, `getEmail()`, `getPhone()`, `getBenefit()` 같은 다른 메서드만 사용한다.

따라서 인터페이스 안에서 `default` 메서드로 구현할 수 있다.

### 3. 추상 클래스 버전과의 차이

| 구분              | 추상 클래스 `Member`                     | 인터페이스 `Member`                  |
| --------------- | ----------------------------------- | ------------------------------- |
| 이름, 이메일, 연락처 필드 | 부모 클래스에 한 번만 둔다.                    | 각 회원 클래스가 직접 가진다.               |
| 생성자             | 부모 생성자를 만들 수 있고 `super(...)`로 호출한다. | 생성자가 없다.                        |
| getter          | 부모 클래스에서 한 번 구현하고 자식이 물려받는다.        | 각 회원 클래스가 직접 구현한다.              |
| `update()`      | 부모 클래스에서 한 번 구현하고 자식이 물려받는다.        | 각 회원 클래스가 직접 구현한다.              |
| `printInfo()`   | 부모 클래스의 일반 메서드로 구현한다.               | 인터페이스의 `default` 메서드로 구현할 수 있다. |
| 확장 방식           | 단일 상속만 가능하다.                        | 여러 인터페이스를 동시에 구현할 수 있다.         |

이 예제처럼 공통 상태와 공통 구현이 많은 경우에는 추상 클래스가 중복을 줄이는 데 유리하다.

인터페이스는 상태보다 기능 규약을 강제하거나, 여러 규약을 동시에 적용해야 할 때 더 적합하다.

---

## 3. 파일 구조

| 파일                   | 역할                                                     |
| -------------------- | ------------------------------------------------------ |
| `Member.java`        | 인터페이스이다. 회원이 갖춰야 할 메서드 목록과 `default printInfo()`를 가진다. |
| `NormalMember.java`  | `Member`를 구현한 일반 회원 클래스이다.                             |
| `VipMember.java`     | `Member`를 구현한 VIP 회원 클래스이다.                            |
| `MemberManager.java` | 회원 배열과 추가, 조회, 수정, 삭제 기능을 관리한다.                        |
| `Main.java`          | 요금제 선택과 메뉴 루프를 담당한다.                                   |

---

## 4. Step by Step

각 Step에는 목표, 할 일, 힌트, 확인 방법이 있다.

한 Step씩 구현한 뒤 실행 결과를 확인하면서 다음 Step으로 넘어간다.

---

### Step 1. 회원 규약 만들기 (`Member.java`)

**목표**: 회원이 갖춰야 할 메서드를 인터페이스로 선언하고, 공통 출력은 `default` 메서드로 구현한다.

**할 일**

1. `interface Member`를 선언한다.
2. `getName()` 메서드를 선언한다.
3. `getEmail()` 메서드를 선언한다.
4. `getPhone()` 메서드를 선언한다.
5. `getGrade()` 메서드를 선언한다.
6. `getBenefit()` 메서드를 선언한다.
7. `update()` 메서드를 선언한다.
8. `printInfo()`는 `default` 메서드로 구현한다.

**힌트**

```java
public interface Member {
    String getName();
    String getEmail();
    String getPhone();
    String getGrade();
    String getBenefit();
    void update(String name, String email, String phone);

    default void printInfo() {
        System.out.println("[" + getGrade() + "] " + getName() + " / " + getEmail()
                + " / " + getPhone() + " (혜택: " + getBenefit() + ")");
    }
}
```

`default printInfo()`는 인터페이스 안의 다른 메서드들을 호출해 공통 출력 형식을 만든다.

**확인**: 컴파일 에러가 없으면 성공이다.

---

### Step 2. 일반 회원 클래스 만들기 (`NormalMember.java`)

**목표**: `Member`를 구현한 일반 회원 클래스를 만든다.

인터페이스에는 공통 필드가 없으므로 이름, 이메일, 연락처를 직접 가진다.

**할 일**

1. `NormalMember` 클래스가 `Member`를 구현하도록 작성한다.
2. `name`, `email`, `phone` 필드를 선언한다.
3. 이름, 이메일, 연락처를 받는 생성자를 작성한다.
4. `getName()`을 구현한다.
5. `getEmail()`을 구현한다.
6. `getPhone()`을 구현한다.
7. `getGrade()`를 구현한다.
8. `getBenefit()`을 구현한다.
9. `update()`를 구현한다.

**힌트**

```java
public class NormalMember implements Member {
    private String name;
    private String email;
    private String phone;

    public NormalMember(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getGrade() {
        return "일반";
    }

    @Override
    public String getBenefit() {
        return "기본 서비스";
    }

    @Override
    public void update(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
```

**확인**: 일반 회원 객체를 만들어 `printInfo()`를 호출했을 때 `[일반]` 형식으로 출력되면 성공이다.

---

### Step 3. VIP 회원 클래스 만들기 (`VipMember.java`)

**목표**: `Member`를 구현한 VIP 회원 클래스를 만든다.

**할 일**

1. `VipMember` 클래스가 `Member`를 구현하도록 작성한다.
2. `name`, `email`, `phone` 필드를 선언한다.
3. 이름, 이메일, 연락처를 받는 생성자를 작성한다.
4. `getName()`을 구현한다.
5. `getEmail()`을 구현한다.
6. `getPhone()`을 구현한다.
7. `getGrade()`를 구현한다.
8. `getBenefit()`을 구현한다.
9. `update()`를 구현한다.

**힌트**

```java
public class VipMember implements Member {
    private String name;
    private String email;
    private String phone;

    public VipMember(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getGrade() {
        return "VIP";
    }

    @Override
    public String getBenefit() {
        return "10% 할인 + 무료배송";
    }

    @Override
    public void update(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
```

`NormalMember`와 `VipMember`는 필드, 생성자, getter, `update()` 코드가 거의 동일하다.

이는 인터페이스가 공통 상태와 공통 구현을 물려주지 못하기 때문에 생기는 중복이다.

추상 클래스 버전에서는 이 중복을 부모 클래스에 한 번만 작성할 수 있다.

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

`Member[]`는 인터페이스 타입 배열이지만, 그 안에는 `NormalMember`, `VipMember` 같은 구현 객체를 담을 수 있다.

사용하는 쪽인 `MemberManager`는 추상 클래스 버전인지 인터페이스 버전인지와 상관없이 `Member` 타입으로 회원을 처리할 수 있다.

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

`printAll()`에서는 인터페이스의 `default printInfo()` 메서드가 호출된다.

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

객체 배열에서는 삭제 후 회원 객체 참조 하나만 앞으로 당기면 된다.

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

**목표**: 인터페이스, 다형성, 중복 코드 구조와 예외 상황 처리를 확인하고 프로그램을 완성한다.

**점검 항목**

* [ ] `Member`가 인터페이스로 선언되어 있는지 확인한다.
* [ ] `printInfo()`만 `default` 메서드로 구현되어 있는지 확인한다.
* [ ] 일반 회원과 VIP 회원이 모든 메서드를 구현했는지 확인한다.
* [ ] `Member[]` 배열에 일반 회원과 VIP 회원이 함께 담기는지 확인한다.
* [ ] 추가, 조회, 수정, 삭제가 모두 동작하는지 확인한다.
* [ ] `NormalMember`와 `VipMember`의 중복 코드를 확인한다.
* [ ] 추상 클래스 버전이었다면 줄어들 수 있는 코드가 무엇인지 비교한다.

여기까지 통과하면 인터페이스 기반 회원 관리 프로그램이 완성된다.

---

## 5. 두 버전 비교 정리

* 공통 상태와 공통 구현이 많으면 추상 클래스가 유리하다.
* 이름, 이메일, 연락처, getter, `update()`처럼 모든 회원이 공유하는 코드는 추상 클래스에서 한 번만 작성할 수 있다.
* 인터페이스는 상태 없는 기능 규약을 강제할 때 유리하다.
* 인터페이스는 여러 규약을 동시에 적용해야 할 때 유리하다.
* 예를 들어 `implements Member, Comparable<Member>`처럼 다중 구현이 가능하다.

한 줄로 정리하면 다음과 같다.

```text
상태와 공통 구현을 물려주려면 추상 클래스,
기능 규약만 강제하거나 다중 적용이 필요하면 인터페이스를 사용한다.
```

---

## 6. 선택 도전 과제

1. **다중 구현**: `Member`와 `Comparable<Member>`를 함께 구현해 이름순 정렬
2. **중복 줄이기**: 인터페이스 버전의 중복을 추상 클래스로 바꿔보고 코드 감소량 비교
3. **default 메서드 확장**: 등급별 인사말 같은 공통 동작을 `default`로 추가
4. **컬렉션 적용**: `Member[]` 대신 `ArrayList<Member>` 사용
