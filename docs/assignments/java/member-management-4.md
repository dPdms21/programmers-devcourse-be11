# 회원 관리 종합판

## 1. 목표

기존 회원 관리 과제에 인터페이스, 컬렉션, 제네릭, 열거형, 예외 처리를 함께 적용한다.

`List<Member>`에 일반 회원과 VIP 회원을 함께 저장하며 인터페이스 타입을 활용한 다형성을 이해한다.

이 과제에서는 다음 개념을 종합적으로 사용한다.

* 인터페이스와 `default` 메서드
* `List`와 `ArrayList`
* 제네릭
* 다형성
* `enum`
* `try-catch`

---

## 2. 먼저 알아둘 점

회원은 일반 회원과 VIP 회원으로 구분하며, 등급마다 제공되는 혜택이 다르다.

모든 회원이 공통으로 가져야 하는 동작은 `Member` 인터페이스에 정의하고, `NormalMember`와 `VipMember`가 이를 각각 구현한다.

회원 정보는 `List<Member>`에 저장한다. 인터페이스 타입인 `Member`를 사용하므로 서로 다른 구현체를 하나의 리스트에서 관리할 수 있다.

인터페이스는 인스턴스 필드를 가질 수 없으므로 이름, 이메일, 연락처와 같은 공통 필드가 각 구현 클래스에 중복된다. 대신 공통 출력 동작은 인터페이스의 `default` 메서드로 구현해 중복을 줄인다.

---

## 3. 구현 기능

일반 회원과 VIP 회원을 추가하고 조회, 수정, 삭제하는 회원 관리 프로그램을 구현한다.

주요 기능은 다음과 같다.

* 요금제 선택
* 일반 회원과 VIP 회원 추가
* 이메일로 회원 조회
* 이름으로 회원 조회
* 전체 회원 조회
* 회원 정보 수정
* 회원 삭제
* 중복 이메일 검사
* 요금제별 최대 정원 관리
* 잘못된 숫자 입력 처리

실행 예시는 다음과 같다.

```text
[1]Lite:10 [2]Basic:20 [3]Premium:30
> 2

[1]추가 [2]메일조회 [3]이름조회 [4]전체 [5]수정 [6]삭제 [7]종료
> 1

등급 [1]일반 [2]VIP
> 2

이름 > 홍길동
이메일 > hong@a.com
연락처 > 010-1111

[VIP] 홍길동 / hong@a.com / 010-1111 (혜택: 10% 할인 + 무료배송)
[일반] 김철수 / kim@b.com / 010-2222 (혜택: 기본 서비스)
```

---

## 4. 학습 목표

| 개념            | 학습 내용                        |
| ------------- | ---------------------------- |
| 인터페이스         | 회원 구현체가 따라야 할 공통 규약 정의       |
| `default` 메서드 | 공통 출력 동작을 인터페이스에 구현          |
| 다형성           | `List<Member>`에 여러 회원 구현체 저장 |
| 컬렉션           | 고정 크기 배열 대신 `ArrayList` 사용   |
| 제네릭           | `List<Member>`로 저장 타입 제한     |
| `enum`        | 요금제와 최대 회원 수 관리              |
| 예외 처리         | 잘못된 숫자 입력을 `try-catch`로 처리   |

---

## 5. 핵심 개념

### 5.1 `Member` 인터페이스와 `default` 메서드

모든 회원이 제공해야 하는 메서드를 `Member` 인터페이스에 정의한다.

공통 출력 기능은 다른 추상 메서드를 호출하는 방식으로 구현할 수 있으므로 `default` 메서드로 작성한다.

```java
public interface Member {
    String getName();  String getEmail();  String getPhone();
    String getGrade(); String getBenefit();
    void update(String name, String email, String phone);

    default void printInfo() {   // 다른 메서드만 써서 구현 → 인터페이스에 둘 수 있음
        System.out.println("[" + getGrade() + "] " + getName() + " / "
            + getEmail() + " / " + getPhone() + " (혜택: " + getBenefit() + ")");
    }
}
```

`NormalMember`와 `VipMember`는 각각 자신의 등급과 혜택을 반환한다. `printInfo()`는 실제 객체의 메서드를 호출하므로 등급에 따라 다른 내용이 출력된다.

---

### 5.2 인터페이스와 컬렉션을 활용한 다형성

`List<Member>`에는 `Member` 인터페이스를 구현한 모든 객체를 저장할 수 있다.

```java
List<Member> members = new ArrayList<>();
members.add(new NormalMember(...));
members.add(new VipMember(...));
for (Member m : members) m.printInfo();   // 각자 등급/혜택이 알아서 다르게 출력!
```

각 요소의 선언 타입은 `Member`로 같지만 실제 객체는 `NormalMember` 또는 `VipMember`이다.

반복문에서 동일하게 `printInfo()`를 호출해도 실제 객체에 구현된 `getGrade()`와 `getBenefit()`이 실행된다.

* 인터페이스 → 공통 규약과 다형성 제공
* 제네릭 → 리스트에 저장할 타입 제한
* 컬렉션 → 여러 객체 저장 및 관리

---

### 5.3 `enum`을 이용한 요금제 관리

요금제는 정해진 종류와 최대 회원 수를 가지므로 `enum`으로 표현한다.

```java
PricePlan.BASIC.getCapacity()
```

각 열거 상수는 자신의 최대 정원을 저장한다.

* `LITE` → 10명
* `BASIC` → 20명
* `PREMIUM` → 30명

문자열이나 숫자만 사용하는 것보다 허용되는 값을 명확하게 제한할 수 있다.

---

### 5.4 `try-catch`를 이용한 입력 처리

숫자를 입력해야 하는 위치에 문자가 입력되면 `Integer.parseInt()`에서 `NumberFormatException`이 발생한다.

이를 `try-catch`로 처리해 프로그램이 종료되지 않도록 한다.

입력은 하나의 `Scanner`에서 `nextLine()`으로 통일해 숫자 입력 후 개행 문자가 남는 문제도 방지한다.

---

## 6. 파일 구조

| 파일                   | 역할                                          |
| -------------------- | ------------------------------------------- |
| `Member.java`        | 회원 공통 규약과 `default printInfo()`를 정의하는 인터페이스 |
| `NormalMember.java`  | 일반 회원 구현 클래스                                |
| `VipMember.java`     | VIP 회원 구현 클래스                               |
| `PricePlan.java`     | 요금제와 최대 정원을 관리하는 열거형                        |
| `MemberManager.java` | `List<Member>` 기반 회원 저장소와 CRUD 기능           |
| `Main.java`          | 요금제 선택, 메뉴 반복, 입력 예외 처리                     |

필요한 주요 클래스는 다음과 같다.

```java
java.util.List
java.util.ArrayList
java.util.Scanner
```

---

## 7. 단계별 구현

각 단계는 목표, 구현 내용, 힌트, 확인 방법 순서로 진행한다.

힌트는 먼저 직접 구현한 뒤 막히는 경우에 확인한다.

---

### Step 1. `Member` 인터페이스 구현

#### 목표

회원 구현체가 반드시 제공해야 하는 메서드를 인터페이스로 정의하고 공통 출력 기능을 `default` 메서드로 구현한다.

#### 구현 내용

1. 이름, 이메일, 연락처를 반환하는 메서드를 정의한다.
2. 등급과 혜택을 반환하는 메서드를 정의한다.
3. 회원 정보를 수정하는 `update()`를 정의한다.
4. 공통 출력 기능을 `default printInfo()`로 구현한다.

<details>
<summary>힌트 보기</summary>

```java
public interface Member {
    String getName();
    String getEmail();
    String getPhone();
    String getGrade();
    String getBenefit();
    void update(String name, String email, String phone);

    default void printInfo() {
        System.out.println("[" + getGrade() + "] " + getName() + " / "
            + getEmail() + " / " + getPhone() + " (혜택: " + getBenefit() + ")");
    }
}
```

</details>

#### 확인

컴파일 오류가 발생하지 않으면 인터페이스의 기본 구조가 완성된 것이다.

---

### Step 2. 회원 등급 구현체 작성

#### 목표

`Member` 인터페이스를 구현하는 일반 회원과 VIP 회원 클래스를 작성한다.

#### 구현 내용

1. 이름, 이메일, 연락처 필드를 선언한다.
2. 생성자에서 회원 정보를 초기화한다.
3. 인터페이스의 getter 메서드를 구현한다.
4. 회원 등급에 따라 `getGrade()`와 `getBenefit()`을 다르게 구현한다.
5. `update()`에서 회원 정보를 변경한다.

<details>
<summary>힌트 보기</summary>

```java
public class NormalMember implements Member {
    private String name, email, phone;
    public NormalMember(String name, String email, String phone) {
        this.name = name; this.email = email; this.phone = phone;
    }
    @Override public String getName()  { return name; }
    @Override public String getEmail() { return email; }
    @Override public String getPhone() { return phone; }
    @Override public String getGrade()   { return "일반"; }
    @Override public String getBenefit() { return "기본 서비스"; }
    @Override public void update(String name, String email, String phone) {
        this.name = name; this.email = email; this.phone = phone;
    }
}
```

`VipMember`는 거의 똑같고 `getGrade()` → `"VIP"`, `getBenefit()` → `"10% 할인 + 무료배송"` 만 다릅니다. (이 중복이 인터페이스의 한계예요.)

</details>

#### 확인

각 객체에서 `printInfo()`를 호출했을 때 등급과 혜택이 다르게 출력되는지 확인한다.

---

### Step 3. 요금제 `enum` 구현

#### 목표

요금제별 최대 회원 수를 열거형으로 관리한다.

#### 구현 내용

1. `LITE`, `BASIC`, `PREMIUM` 상수를 정의한다.
2. 각 상수에 최대 회원 수를 저장한다.
3. `getCapacity()`로 최대 회원 수를 반환한다.
4. 사용자의 숫자 선택을 열거 상수로 변환하는 `from()`을 구현한다.

<details>
<summary>힌트 보기</summary>

```java
public enum PricePlan {
    LITE(10), BASIC(20), PREMIUM(30);
    private final int capacity;
    PricePlan(int capacity) { this.capacity = capacity; }
    public int getCapacity() { return capacity; }

    public static PricePlan from(int choice) {
        switch (choice) {
            case 1: return LITE;
            case 2: return BASIC;
            case 3: return PREMIUM;
            default: return null;
        }
    }
}
```

</details>

#### 확인

다음 실행 결과가 `20`인지 확인한다.

```java
PricePlan.BASIC.getCapacity()
```

---

### Step 4. 회원 저장소와 추가 기능 구현

#### 목표

`List<Member>`에 서로 다른 등급의 회원 객체를 저장한다.

#### 구현 내용

1. `List<Member>` 타입의 회원 목록을 선언한다.
2. 생성자에서 최대 정원을 전달받는다.
3. 현재 회원 수가 최대 정원에 도달했는지 확인한다.
4. 이메일 중복 여부를 검사한다.
5. `Member` 타입으로 회원 객체를 추가한다.
6. 현재 회원 수와 최대 정원을 반환한다.

<details>
<summary>힌트 보기</summary>

```java
import java.util.ArrayList;
import java.util.List;

public class MemberManager {
    private final List<Member> members = new ArrayList<>();   // 인터페이스 타입 + 제네릭
    private final int capacity;
    public MemberManager(int capacity) { this.capacity = capacity; }

    public boolean isFull() { return members.size() >= capacity; }

    public boolean existsEmail(String email) {
        for (Member m : members) if (m.getEmail().equals(email)) return true;
        return false;
    }
    public void add(Member m) { members.add(m); }   // 일반이든 VIP든 Member로 추가!

    public int size()     { return members.size(); }
    public int capacity() { return capacity; }
}
```

`add(Member m)` 하나로 일반·VIP를 모두 받아요. 이게 인터페이스 + 다형성의 힘이에요.

</details>

#### 확인

* 일반 회원과 VIP 회원을 같은 목록에 추가할 수 있는지 확인한다.
* 회원 추가 후 `size()`가 증가하는지 확인한다.
* 최대 정원에 도달하면 `isFull()`이 `true`를 반환하는지 확인한다.
* 이미 저장된 이메일을 전달하면 `existsEmail()`이 `true`를 반환하는지 확인한다.

---

### Step 5. 조회, 수정, 삭제 기능 구현

#### 목표

`List`에 저장된 회원을 조회하고 수정하거나 삭제한다.

#### 구현 내용

1. 이메일로 회원을 조회한다.
2. 이름으로 회원을 조회한다.
3. 전체 회원 정보를 출력한다.
4. 이메일을 기준으로 회원 정보를 수정한다.
5. 이메일을 기준으로 회원을 삭제한다.

<details>
<summary>힌트 보기</summary>

```java
public Member findByEmail(String email) {
    for (Member m : members) if (m.getEmail().equals(email)) return m;
    return null;
}
public Member findByName(String name) {
    for (Member m : members) if (m.getName().equals(name)) return m;
    return null;
}
public void printAll() {
    if (members.isEmpty()) { System.out.println("등록된 회원이 없습니다."); return; }
    for (Member m : members) m.printInfo();   // 다형성! 등급별로 다르게 출력
}
public boolean update(String email, String name, String newEmail, String phone) {
    Member m = findByEmail(email);
    if (m == null) return false;
    m.update(name, newEmail, phone);
    return true;
}
public boolean delete(String email) {
    Member m = findByEmail(email);
    if (m == null) return false;
    members.remove(m);    // 당기기·null 자동!
    return true;
}
```

</details>

#### 확인

* 이메일로 정확한 회원을 조회할 수 있는지 확인한다.
* 이름으로 회원을 조회할 수 있는지 확인한다.
* 전체 조회에서 각 등급의 혜택이 다르게 출력되는지 확인한다.
* 회원 정보 수정 후 변경된 내용이 출력되는지 확인한다.
* 회원 삭제 후 남은 요소가 빈 공간 없이 유지되는지 확인한다.
* 존재하지 않는 회원을 수정하거나 삭제하면 `false`를 반환하는지 확인한다.

---

### Step 6. 입력 예외 처리 구현

#### 목표

숫자 입력 위치에 잘못된 값이 들어와도 프로그램이 종료되지 않도록 한다.

#### 구현 내용

1. `Scanner`로 한 줄을 입력받는다.
2. 입력값의 앞뒤 공백을 제거한다.
3. `Integer.parseInt()`로 정수로 변환한다.
4. 변환에 실패하면 `NumberFormatException`을 처리한다.
5. 잘못된 입력을 나타내는 `-1`을 반환한다.

<details>
<summary>힌트 보기</summary>

```java
static int readInt(Scanner sc) {
    try {
        return Integer.parseInt(sc.nextLine().trim());
    } catch (NumberFormatException e) {
        return -1;   // 잘못된 입력
    }
}
```

`Scanner`는 하나만 만들어 공유하고 `nextLine()`으로 읽으면 입력 꼬임도 함께 피해요.

</details>

#### 확인

메뉴에서 숫자 대신 `abc`를 입력해도 프로그램이 종료되지 않고 다시 입력을 받을 수 있는지 확인한다.

---

### Step 7. `main` 통합

#### 목표

요금제를 선택한 뒤 회원 관리 메뉴를 반복 실행한다.

회원 추가 시 등급을 선택해 알맞은 구현 객체를 생성한다.

#### 구현 내용

1. 하나의 `Scanner` 객체를 생성한다.
2. 요금제를 선택할 때까지 입력을 반복한다.
3. 선택한 요금제의 최대 정원으로 `MemberManager`를 생성한다.
4. 현재 회원 수와 최대 정원을 출력한다.
5. 메뉴 입력을 받아 각 기능을 호출한다.
6. 회원 추가 시 일반 회원과 VIP 회원 중 하나를 생성한다.
7. 종료 메뉴를 선택하면 프로그램을 종료한다.

<details>
<summary>힌트 보기</summary>

```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("[1]Lite:10 [2]Basic:20 [3]Premium:30");
        PricePlan plan = null;
        while (plan == null) {
            plan = PricePlan.from(readInt(sc));
            if (plan == null) System.out.println("1~3 중에서 선택하세요.");
        }
        MemberManager manager = new MemberManager(plan.getCapacity());

        while (true) {
            System.out.println("\n[현재 " + manager.size() + "/" + manager.capacity() + "]");
            System.out.println("[1]추가 [2]메일조회 [3]이름조회 [4]전체 [5]수정 [6]삭제 [7]종료");
            int menu = readInt(sc);

            switch (menu) {
                case 1: {   // 추가
                    if (manager.isFull()) { System.out.println("정원이 찼습니다."); break; }
                    System.out.println("등급 [1]일반 [2]VIP");
                    int grade = readInt(sc);
                    System.out.print("이름 > ");   String name  = sc.nextLine();
                    System.out.print("이메일 > "); String email = sc.nextLine();
                    System.out.print("연락처 > "); String phone = sc.nextLine();
                    if (manager.existsEmail(email)) { System.out.println("이미 있는 회원입니다."); break; }
                    Member m = (grade == 2)
                            ? new VipMember(name, email, phone)
                            : new NormalMember(name, email, phone);
                    manager.add(m);   // 다형성: Member로 추가
                    System.out.println("추가되었습니다.");
                    break;
                }
                // case 2~6: findByEmail/findByName/printAll/update/delete 호출
                case 7: System.out.println("이용해주셔서 감사합니다."); return;
                default: System.out.println("1~7 중에서 선택하세요.");
            }
        }
    }
    static int readInt(Scanner sc) {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}
```

</details>

#### 확인

* [ ] `Member`가 인터페이스로 선언되어 있는가?
* [ ] 공통 출력 기능이 `default` 메서드로 구현되어 있는가?
* [ ] `List<Member>`에 일반 회원과 VIP 회원이 함께 저장되는가?
* [ ] 요금제를 `enum`으로 관리하는가?
* [ ] 잘못된 숫자 입력이 들어와도 프로그램이 종료되지 않는가?
* [ ] 회원 삭제에 `List.remove()`를 사용하는가?
* [ ] 최대 정원 이상으로 회원이 추가되지 않는가?
* [ ] 중복 이메일이 등록되지 않는가?

모든 항목을 통과하면 회원 관리 종합판 구현이 완료된 것이다.

---

## 8. 학습 체크

* [ ] 인터페이스와 `default` 메서드를 사용했다.
* [ ] `List<Member>`에 서로 다른 회원 구현체를 저장했다.
* [ ] 인터페이스 타입을 활용한 다형성을 이해했다.
* [ ] 제네릭을 사용해 저장 가능한 타입을 제한했다.
* [ ] 고정 크기 배열을 `ArrayList`로 변경했다.
* [ ] 요금제를 `enum`으로 구현했다.
* [ ] `try-catch`로 잘못된 숫자 입력을 처리했다.
* [ ] `List`의 `remove()`를 사용해 회원을 삭제했다.
* [ ] 인터페이스와 추상 클래스의 구조 차이를 이해했다.

---

## 9. 최종 완성 체크리스트

* [ ] `Member.java` 인터페이스 구현
* [ ] `default printInfo()` 구현
* [ ] `NormalMember.java` 구현
* [ ] `VipMember.java` 구현
* [ ] `PricePlan.java` 열거형 구현
* [ ] `List<Member>` 기반 `MemberManager.java` 구현
* [ ] 회원 추가 기능 구현
* [ ] 이메일 및 이름 조회 기능 구현
* [ ] 전체 조회 기능 구현
* [ ] 회원 수정 기능 구현
* [ ] 회원 삭제 기능 구현
* [ ] 중복 이메일 검사 구현
* [ ] 최대 정원 검사 구현
* [ ] `try-catch` 기반 입력 처리 구현
* [ ] `Main.java` 메뉴 반복 구현

---

## 10. 선택 도전 과제

1. **추상 클래스로 중복 제거**: 인터페이스 대신 추상 클래스를 사용해 이름, 이메일, 연락처 필드를 한곳에서 관리하고 구현 코드 비교
2. **Repository 인터페이스**: `MemberRepository`에 추가, 조회, 삭제 규약을 정의하고 `MemberManager`에서 구현
3. **제네릭 Repository**: `Repository<T>` 형태의 제네릭 인터페이스나 클래스를 만들어 여러 타입에 재사용
4. **Map 적용**: 이메일을 키로 사용하는 `Map<String, Member>`를 적용해 조회와 삭제 구조 개선
5. **회원 등급 enum화**: 등급과 혜택을 `Grade` 열거형으로 분리하고 일반 회원과 VIP 회원 구현체 통합
6. **커스텀 예외**: 중복 이메일 입력 시 `DuplicateEmailException`을 발생시키도록 구현
7. **Stream 적용**: `members.stream()`을 활용해 회원 조회와 정렬 기능 구현
