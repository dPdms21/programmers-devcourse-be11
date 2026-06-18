# 파일 입출력을 활용한 회원 관리 5

기존 회원 관리 프로그램에 파일 저장과 불러오기 기능을 추가한다.

회원 목록을 텍스트 파일에 저장하고 프로그램 시작 시 다시 불러와, 프로그램을 종료한 뒤에도 회원 정보가 유지되도록 구현한다.

---

## 1. 먼저 알아둘 점

이 과제는 다음 기능이 구현된 회원 관리 4를 기반으로 한다.

* `Member` 인터페이스
* `NormalMember`, `VipMember` 구현 클래스
* `PricePlan` 열거형
* `List<Member>` 기반 `MemberManager`
* 회원 추가, 조회, 수정, 삭제
* 입력 예외 처리

이번 과제에서는 기존 구조에 회원 목록의 저장과 불러오기 기능을 추가한다.

저장 대상은 회원 목록이며, 요금제는 프로그램을 실행할 때마다 다시 선택한다.

파일 입출력 과정에서는 예외가 발생할 수 있으므로 `try-catch` 또는 try-with-resources를 사용해야 한다.

---

## 2. 구현 내용

회원 정보를 추가한 뒤 프로그램을 종료해도 회원 목록이 파일에 남도록 구현한다.

다음 실행에서 프로그램이 시작되면 저장된 파일을 읽어 회원 객체를 다시 생성한다.

```text
1차 실행
회원 2명 추가
프로그램 종료

2차 실행
전체 회원 조회

[VIP] 홍길동 / hong@a.com / 010-1111
       (혜택: 10% 할인 + 무료배송)

[일반] 김철수 / kim@b.com / 010-2222
       (혜택: 기본 서비스)
```

주요 기능은 다음과 같다.

* 회원 객체를 CSV 문자열로 변환
* 전체 회원 목록 파일 저장
* 파일 내용을 회원 객체로 복원
* 회원 등급에 따른 구현 객체 생성
* 프로그램 시작 시 자동 불러오기
* 회원 추가, 수정, 삭제 시 자동 저장
* 파일이 없는 최초 실행 처리
* 잘못된 파일 데이터 처리

---

## 3. 학습 목표

| 개념                 | 학습 내용                |
| ------------------ | -------------------- |
| 파일 영속성             | 프로그램 종료 후에도 데이터 유지   |
| 객체 직렬화             | 회원 객체를 텍스트 형식으로 변환   |
| 역직렬화               | 텍스트를 읽어 회원 객체로 복원    |
| CSV 형식             | 회원 한 명을 한 줄의 문자열로 저장 |
| 다형성 복원             | 등급 값에 따라 다른 구현 객체 생성 |
| 파일 덮어쓰기            | 현재 회원 목록 전체를 다시 저장   |
| try-with-resources | 파일 입출력 자원 자동 종료      |
| 예외 처리              | 저장 및 불러오기 오류 처리      |

---

## 4. 핵심 개념

### 4.1 객체와 텍스트 변환

회원 객체를 텍스트 파일에 직접 저장할 수는 없다.

따라서 회원 객체의 정보를 한 줄의 문자열로 변환한다.

```text
VipMember 객체
→ VIP,홍길동,hong@a.com,010-1111
```

파일을 불러올 때는 문자열을 다시 분리해 회원 객체를 생성한다.

```text
VIP,홍길동,hong@a.com,010-1111
→ VipMember 객체
```

객체를 저장 가능한 형태로 변환하는 과정을 직렬화라고 한다.

저장된 데이터를 다시 객체로 만드는 과정을 역직렬화라고 한다.

---

### 4.2 CSV 저장 형식

회원 한 명은 다음 형식으로 저장한다.

```text
등급,이름,이메일,연락처
```

예시는 다음과 같다.

```text
VIP,홍길동,hong@a.com,010-1111
일반,김철수,kim@b.com,010-2222
```

파일의 각 줄은 회원 한 명을 의미한다.

---

### 4.3 전체 덮어쓰기 방식

가계부 파일 입출력 과제에서는 기존 내용 뒤에 새로운 내용을 추가하기 위해 이어쓰기 방식을 사용했다.

```java
new FileWriter(file, true);
```

회원 관리에서는 회원 정보가 수정되거나 삭제될 수 있으므로 현재 회원 목록 전체를 다시 저장해야 한다.

```java
new FileWriter(file);
```

두 번째 인자로 `true`를 전달하지 않으면 기존 파일 내용을 덮어쓴다.

| 방식   | 사용 상황                |
| ---- | -------------------- |
| 이어쓰기 | 기존 기록 뒤에 새로운 기록만 추가  |
| 덮어쓰기 | 현재 메모리 상태 전체를 파일에 반영 |

---

### 4.4 회원 등급 복원

파일에서 읽은 첫 번째 값은 회원 등급이다.

등급에 따라 생성할 구현 객체를 결정한다.

```java
Member member;

if (grade.equals("VIP")) {
    member = new VipMember(
            name,
            email,
            phone
    );
} else {
    member = new NormalMember(
            name,
            email,
            phone
    );
}
```

등급에 맞는 실제 객체를 생성해야 전체 조회 시 각 회원의 혜택이 올바르게 출력된다.

---

### 4.5 저장 시점

회원 목록이 변경된 직후 파일을 저장한다.

* 회원 추가 후 저장
* 회원 수정 후 저장
* 회원 삭제 후 저장

이를 통해 메모리의 회원 목록과 파일 내용을 동일하게 유지한다.

---

## 5. 파일 구조

| 파일                   | 역할                     |
| -------------------- | ---------------------- |
| `Member.java`        | 회원 공통 규약과 파일 저장 문자열 변환 |
| `NormalMember.java`  | 일반 회원 구현 클래스           |
| `VipMember.java`     | VIP 회원 구현 클래스          |
| `PricePlan.java`     | 요금제와 최대 회원 수 관리        |
| `MemberManager.java` | 회원 CRUD와 저장·불러오기 관리    |
| `Main.java`          | 요금제 선택과 메뉴 실행          |
| `members.txt`        | 회원 정보 저장 파일            |

필요한 주요 클래스는 다음과 같다.

```java
java.io.BufferedReader
java.io.File
java.io.FileReader
java.io.FileWriter
java.io.IOException
```

---

## 6. 단계별 구현

### Step 1. 회원 정보를 문자열로 변환

#### 목표

회원 객체를 CSV 형식의 한 줄 문자열로 변환한다.

#### 구현 내용

`Member` 인터페이스에 `toFileString()`을 추가한다.

기존 getter 메서드만으로 구현할 수 있으므로 `default` 메서드로 작성한다.

<details>
<summary>힌트 보기</summary>

```java
default String toFileString() {
    return getGrade()
            + ","
            + getName()
            + ","
            + getEmail()
            + ","
            + getPhone();
}
```

</details>

#### 확인

VIP 회원에서 다음 형식의 문자열이 반환되는지 확인한다.

```text
VIP,홍길동,hong@a.com,010-1111
```

---

### Step 2. 회원 목록 저장

#### 목표

현재 회원 목록 전체를 텍스트 파일에 저장한다.

#### 구현 내용

1. 저장 파일 경로를 상수로 선언한다.
2. `FileWriter`를 덮어쓰기 방식으로 생성한다.
3. 모든 회원을 반복한다.
4. `toFileString()` 결과를 한 줄씩 저장한다.
5. 입출력 예외를 처리한다.

<details>
<summary>힌트 보기</summary>

```java
private static final String FILE_PATH =
        "members.txt";

public void save() {
    try (FileWriter writer =
                 new FileWriter(FILE_PATH)) {

        for (Member member : members) {
            writer.write(
                    member.toFileString()
            );

            writer.write(
                    System.lineSeparator()
            );
        }

    } catch (IOException e) {
        System.out.println(
                "회원 정보 저장 중 오류가 발생했습니다: "
                + e.getMessage()
        );
    }
}
```

</details>

#### 확인

* `save()` 호출 후 `members.txt`가 생성되는지 확인한다.
* 회원 한 명이 파일 한 줄에 저장되는지 확인한다.
* 회원 목록 전체가 파일에 저장되는지 확인한다.
* 기존 파일 내용이 현재 목록으로 갱신되는지 확인한다.

---

### Step 3. 회원 목록 불러오기

#### 목표

파일에 저장된 회원 정보를 읽어 회원 객체로 복원한다.

#### 구현 내용

1. 저장 파일 객체를 생성한다.
2. 파일이 없으면 불러오기를 종료한다.
3. 파일을 한 줄씩 읽는다.
4. 빈 줄은 건너뛴다.
5. 각 줄을 쉼표로 분리한다.
6. 등급에 따라 회원 구현 객체를 생성한다.
7. 생성한 객체를 회원 목록에 추가한다.
8. 잘못된 파일 형식을 처리한다.

<details>
<summary>힌트 보기</summary>

```java
public void load() {
    File file = new File(FILE_PATH);

    if (!file.exists()) {
        return;
    }

    try (BufferedReader reader =
                 new BufferedReader(
                         new FileReader(file)
                 )) {

        String line;

        while ((line = reader.readLine())
                != null) {

            if (line.isBlank()) {
                continue;
            }

            String[] values =
                    line.split(",", -1);

            if (values.length != 4) {
                System.out.println(
                        "잘못된 회원 데이터입니다: "
                        + line
                );
                continue;
            }

            String grade = values[0];
            String name = values[1];
            String email = values[2];
            String phone = values[3];

            Member member;

            if (grade.equals("VIP")) {
                member = new VipMember(
                        name,
                        email,
                        phone
                );
            } else if (grade.equals("일반")) {
                member = new NormalMember(
                        name,
                        email,
                        phone
                );
            } else {
                System.out.println(
                        "알 수 없는 회원 등급입니다: "
                        + grade
                );
                continue;
            }

            members.add(member);
        }

    } catch (IOException e) {
        System.out.println(
                "회원 정보 불러오기 중 오류가 발생했습니다: "
                + e.getMessage()
        );
    }
}
```

`split(",", -1)`을 사용하면 마지막 값이 비어 있어도 배열 요소가 유지된다.

</details>

#### 확인

* 파일이 없을 때 오류 없이 시작되는지 확인한다.
* 파일의 각 줄이 회원 객체로 복원되는지 확인한다.
* VIP와 일반 회원이 올바른 구현 객체로 생성되는지 확인한다.
* 빈 줄이나 잘못된 형식이 있어도 프로그램이 종료되지 않는지 확인한다.

---

### Step 4. 프로그램 시작 시 자동 불러오기

#### 목표

`MemberManager` 객체가 생성될 때 기존 회원 목록을 자동으로 불러온다.

#### 구현 내용

생성자에서 최대 회원 수를 저장한 뒤 `load()`를 호출한다.

<details>
<summary>힌트 보기</summary>

```java
public MemberManager(int capacity) {
    this.capacity = capacity;
    load();
}
```

</details>

#### 확인

* 프로그램을 다시 실행한 뒤 전체 조회를 했을 때 기존 회원이 출력되는지 확인한다.
* 저장 파일이 없는 최초 실행에서도 정상적으로 시작되는지 확인한다.

---

### Step 5. 회원 추가 후 저장

#### 목표

회원이 추가되면 변경된 회원 목록을 파일에 반영한다.

<details>
<summary>힌트 보기</summary>

```java
public void add(Member member) {
    members.add(member);
    save();
}
```

</details>

#### 확인

회원 추가 직후 `members.txt`에 새로운 회원 정보가 저장되는지 확인한다.

---

### Step 6. 회원 수정 후 저장

#### 목표

회원 정보가 수정되면 파일 내용도 갱신한다.

<details>
<summary>힌트 보기</summary>

```java
public boolean update(
        String email,
        String name,
        String newEmail,
        String phone
) {
    Member member =
            findByEmail(email);

    if (member == null) {
        return false;
    }

    member.update(
            name,
            newEmail,
            phone
    );

    save();

    return true;
}
```

</details>

#### 확인

* 수정 성공 후 파일의 기존 회원 정보가 변경되는지 확인한다.
* 수정할 회원이 없으면 파일 저장이 발생하지 않는지 확인한다.

새 이메일이 다른 회원의 이메일과 중복되는지도 확인하는 것이 좋다.

---

### Step 7. 회원 삭제 후 저장

#### 목표

회원이 삭제되면 파일에서도 해당 회원 정보를 제거한다.

<details>
<summary>힌트 보기</summary>

```java
public boolean delete(String email) {
    Member member =
            findByEmail(email);

    if (member == null) {
        return false;
    }

    members.remove(member);

    save();

    return true;
}
```

</details>

#### 확인

* 회원 삭제 후 해당 회원의 줄이 파일에서 사라지는지 확인한다.
* 삭제할 회원이 없으면 파일 저장이 발생하지 않는지 확인한다.

---

### Step 8. 종료 후 재실행 확인

#### 목표

파일 저장과 불러오기 기능이 정상적으로 연결되었는지 확인한다.

#### 확인 순서

1. 프로그램을 실행한다.
2. 일반 회원과 VIP 회원을 추가한다.
3. 프로그램을 종료한다.
4. 프로그램을 다시 실행한다.
5. 이전 실행과 같은 요금제 또는 충분한 정원의 요금제를 선택한다.
6. 전체 회원 조회를 실행한다.
7. 저장했던 회원이 출력되는지 확인한다.
8. 회원을 수정하거나 삭제한다.
9. 프로그램을 다시 실행해 변경 내용이 유지되는지 확인한다.

---

## 7. 주의할 점

### 7.1 요금제 정원보다 저장된 회원이 많은 경우

요금제는 실행할 때마다 다시 선택하지만 회원 목록은 파일에서 불러온다.

따라서 저장된 회원 수보다 작은 정원의 요금제를 선택할 수 있다.

예를 들어 저장된 회원이 15명인데 `LITE` 요금제의 정원이 10명이라면, 불러온 시점부터 정원을 초과한 상태가 된다.

다음 중 하나의 정책을 결정해야 한다.

* 저장된 회원 수보다 작은 요금제 선택을 제한한다.
* 정원을 초과한 회원도 불러오되 추가 기능만 제한한다.
* 요금제 정보도 파일에 함께 저장한다.

기본 과제에서는 기존 회원을 모두 불러오고, `isFull()`을 통해 새로운 회원 추가만 제한할 수 있다.

---

### 7.2 쉼표가 포함된 입력

현재 저장 형식은 쉼표를 구분자로 사용한다.

이름이나 연락처에 쉼표가 포함되면 올바르게 분리되지 않는다.

기본 과제에서는 입력에 쉼표를 허용하지 않도록 처리할 수 있다.

복잡한 CSV 형식을 지원하려면 쉼표 이스케이프 규칙이나 CSV 라이브러리가 필요하다.

---

### 7.3 파일 수정 주의

`members.txt`를 사용자가 직접 수정하면 잘못된 형식의 데이터가 들어갈 수 있다.

따라서 불러오기 과정에서 배열 길이와 등급 값을 확인해야 한다.

---

## 8. 최종 점검

* [ ] `Member`에 `toFileString()`을 추가했다.
* [ ] 회원 객체를 CSV 문자열로 변환한다.
* [ ] 회원 목록 전체를 덮어쓰기 방식으로 저장한다.
* [ ] 파일 내용을 한 줄씩 읽는다.
* [ ] 각 줄을 회원 객체로 복원한다.
* [ ] 등급에 따라 `NormalMember` 또는 `VipMember`를 생성한다.
* [ ] 프로그램 시작 시 회원 목록을 불러온다.
* [ ] 회원 추가 후 파일을 저장한다.
* [ ] 회원 수정 후 파일을 저장한다.
* [ ] 회원 삭제 후 파일을 저장한다.
* [ ] 파일이 없는 최초 실행을 처리한다.
* [ ] 잘못된 파일 형식을 처리한다.
* [ ] 파일 입출력에 try-with-resources를 사용한다.
* [ ] 프로그램을 다시 실행해 회원 정보가 유지되는지 확인한다.

---

## 9. 선택 도전 과제

1. **요금제 저장**: 회원 목록과 함께 요금제를 저장하고 다음 실행 시 복원한다.
2. **CSV 입력 검증**: 이름, 이메일, 연락처에 쉼표가 포함되지 않도록 검사한다.
3. **종료 시 저장**: 회원 변경마다 저장하지 않고 프로그램 종료 시 한 번만 저장하도록 변경한다.
4. **객체 직렬화**: `Serializable`, `ObjectOutputStream`, `ObjectInputStream`을 사용해 객체를 직접 저장한다.
5. **백업 파일 생성**: 저장 전에 기존 파일을 백업한다.
6. **JSON 형식 적용**: 회원 정보를 JSON 형식으로 저장하고 불러온다.
7. **임시 파일 저장**: 임시 파일에 먼저 저장한 뒤 기존 파일과 교체해 저장 중 오류로 인한 데이터 손상을 줄인다.
