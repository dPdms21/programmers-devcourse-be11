# JDBC를 활용한 회원 관리 6

기존 파일 기반 회원 관리 프로그램의 저장소를 MySQL 데이터베이스로 변경한다.

회원 추가, 조회, 수정, 삭제 기능을 각각 SQL의 `INSERT`, `SELECT`, `UPDATE`, `DELETE`와 연결하고, JDBC를 사용해 Java 프로그램과 데이터베이스를 연동한다.

---

## 1. 먼저 알아둘 점

이 과제는 다음 내용을 이해하고 있다는 전제로 진행한다.

* `Member` 인터페이스와 구현 클래스
* `NormalMember`, `VipMember`
* `PricePlan` 열거형
* 회원 추가, 조회, 수정, 삭제
* 파일 기반 회원 정보 저장과 복원
* JDBC 기본 사용법
* `Connection`
* `PreparedStatement`
* `ResultSet`
* try-with-resources

프로그램을 실행하려면 다음 환경이 준비되어 있어야 한다.

* MySQL 서버 실행
* 사용할 데이터베이스 생성
* `member` 테이블 생성
* MySQL JDBC 드라이버 추가
* 데이터베이스 연결 정보 설정

파일 기반 회원 관리에서는 회원 목록 전체를 메모리와 파일에 저장했다.

DB 기반 회원 관리에서는 데이터베이스가 저장소 역할을 하므로 회원 목록 전체를 `List<Member>`로 유지하거나 `save()`와 `load()`로 한꺼번에 저장할 필요가 없다.

각 기능을 실행할 때 필요한 데이터만 SQL로 조회하거나 변경한다.

---

## 2. 구현 내용

기존 회원 관리 기능은 유지하되 저장 방식을 파일에서 데이터베이스로 변경한다.

| 회원 관리 기능  | SQL               |
| --------- | ----------------- |
| 회원 추가     | `INSERT`          |
| 회원 조회     | `SELECT`          |
| 회원 수정     | `UPDATE`          |
| 회원 삭제     | `DELETE`          |
| 회원 수 확인   | `SELECT COUNT(*)` |
| 이메일 중복 확인 | `SELECT COUNT(*)` |

프로그램이 종료되어도 회원 정보는 데이터베이스에 유지된다.

```text id="cbhng8"
회원 추가
→ INSERT 실행
→ member 테이블에 행 저장

회원 조회
→ SELECT 실행
→ ResultSet을 Member 객체로 변환

회원 수정
→ UPDATE 실행
→ 해당 행의 값 변경

회원 삭제
→ DELETE 실행
→ 해당 행 제거
```

---

## 3. 학습 목표

| 개념                  | 학습 내용                  |
| ------------------- | ---------------------- |
| JDBC                | Java와 MySQL 데이터베이스 연결  |
| CRUD                | 회원 관리 기능을 SQL과 연결      |
| `Connection`        | 데이터베이스 연결 생성           |
| `PreparedStatement` | SQL과 입력값을 분리해 실행       |
| `ResultSet`         | 조회 결과 읽기               |
| 객체 복원               | 조회한 행을 실제 회원 구현 객체로 변환 |
| `executeUpdate()`   | 데이터 추가, 수정, 삭제 실행      |
| `executeQuery()`    | 조회 SQL 실행              |
| `COUNT(*)`          | 회원 수와 이메일 중복 여부 확인     |
| try-with-resources  | JDBC 자원 자동 종료          |

---

## 4. 핵심 개념

### 4.1 파일 저장에서 데이터베이스 저장으로 전환

파일 기반 버전과 DB 기반 버전의 차이는 다음과 같다.

| 구분      | 파일 기반 회원 관리                | DB 기반 회원 관리   |
| ------- | -------------------------- | ------------- |
| 저장소     | `members.txt`              | `member` 테이블  |
| 회원 목록   | `List<Member>`에 전체 저장      | DB에서 필요할 때 조회 |
| 프로그램 시작 | `load()` 실행                | 별도 불러오기 불필요   |
| 변경 후 처리 | 전체 파일 덮어쓰기                 | 대상 행만 변경      |
| 회원 추가   | `members.add()` 후 `save()` | `INSERT`      |
| 회원 조회   | 리스트 순회                     | `SELECT`      |
| 회원 수정   | 객체 변경 후 `save()`           | `UPDATE`      |
| 회원 삭제   | 리스트에서 제거 후 `save()`        | `DELETE`      |

파일에서는 수정이나 삭제 결과를 반영하기 위해 회원 목록 전체를 다시 저장했다.

데이터베이스에서는 조건에 맞는 행만 직접 추가하거나 변경할 수 있다.

---

### 4.2 회원 테이블 설계

회원 정보를 저장할 `member` 테이블을 생성한다.

```sql id="1m8dbi"
CREATE TABLE member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    grade VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20)
);
```

각 컬럼의 역할은 다음과 같다.

| 컬럼      | 역할               |
| ------- | ---------------- |
| `id`    | 회원을 구분하는 기본 키    |
| `grade` | 일반 회원과 VIP 회원 구분 |
| `name`  | 회원 이름            |
| `email` | 회원 이메일           |
| `phone` | 회원 연락처           |

`email`에는 `UNIQUE` 제약 조건을 적용해 데이터베이스에서도 중복 이메일 저장을 방지한다.

---

### 4.3 JDBC 실행 흐름

JDBC를 이용한 기본 실행 흐름은 다음과 같다.

```text id="jbikfw"
Connection 생성
→ SQL 작성
→ PreparedStatement 생성
→ ? 위치에 값 설정
→ SQL 실행
→ 조회라면 ResultSet 처리
→ 자원 종료
```

try-with-resources를 사용하면 `Connection`, `PreparedStatement`, `ResultSet`을 자동으로 닫을 수 있다.

---

### 4.4 PreparedStatement

사용자 입력값을 SQL 문자열에 직접 연결하지 않고 `?` 자리표시자를 사용한다.

```java id="g03q4x"
String sql =
        "INSERT INTO member "
        + "(grade, name, email, phone) "
        + "VALUES (?, ?, ?, ?)";
```

각 자리에는 타입에 맞는 메서드로 값을 설정한다.

```java id="025kl8"
preparedStatement.setString(1, member.getGrade());
preparedStatement.setString(2, member.getName());
preparedStatement.setString(3, member.getEmail());
preparedStatement.setString(4, member.getPhone());
```

`PreparedStatement`를 사용하면 SQL과 값을 분리할 수 있고 SQL Injection 위험도 줄일 수 있다.

---

### 4.5 executeUpdate()와 executeQuery()

SQL 종류에 따라 실행 메서드를 구분한다.

| 메서드               | 대상 SQL                       | 반환값         |
| ----------------- | ---------------------------- | ----------- |
| `executeUpdate()` | `INSERT`, `UPDATE`, `DELETE` | 변경된 행의 수    |
| `executeQuery()`  | `SELECT`                     | `ResultSet` |

수정과 삭제에서는 변경된 행의 수로 성공 여부를 판단할 수 있다.

```java id="4y0tct"
return preparedStatement.executeUpdate() > 0;
```

변경된 행이 없으면 조건에 맞는 회원이 없었다는 의미이다.

---

### 4.6 ResultSet을 회원 객체로 변환

조회 결과는 `ResultSet`으로 반환된다.

각 컬럼의 값을 읽고 `grade`에 따라 실제 구현 객체를 생성한다.

```java id="xf4ykn"
private Member toMember(ResultSet resultSet)
        throws SQLException {

    String grade =
            resultSet.getString("grade");

    String name =
            resultSet.getString("name");

    String email =
            resultSet.getString("email");

    String phone =
            resultSet.getString("phone");

    if (grade.equals("VIP")) {
        return new VipMember(
                name,
                email,
                phone
        );
    }

    return new NormalMember(
            name,
            email,
            phone
    );
}
```

파일 기반 버전에서 저장된 문자열의 등급 값을 기준으로 구현 객체를 복원한 것과 같은 원리이다.

데이터 출처만 텍스트 파일의 한 줄에서 데이터베이스의 한 행으로 변경된다.

---

## 5. 파일 구조

| 파일                   | 역할                     |
| -------------------- | ---------------------- |
| `Member.java`        | 회원 공통 기능을 정의하는 인터페이스   |
| `NormalMember.java`  | 일반 회원 구현 클래스           |
| `VipMember.java`     | VIP 회원 구현 클래스          |
| `PricePlan.java`     | 요금제와 최대 회원 수 관리        |
| `MemberManager.java` | JDBC를 이용한 회원 CRUD 처리   |
| `Main.java`          | 메뉴 출력과 사용자 입력 처리       |
| `member` 테이블         | 회원 정보를 저장하는 데이터베이스 테이블 |

필요한 JDBC 클래스는 다음과 같다.

```java id="oz8wgh"
java.sql.Connection
java.sql.DriverManager
java.sql.PreparedStatement
java.sql.ResultSet
java.sql.SQLException
```

---

## 6. 단계별 구현

### Step 1. 회원 테이블 생성

#### 목표

MySQL에 회원 정보를 저장할 테이블을 생성한다.

<details>
<summary>힌트 보기</summary>

```sql id="7fg7m3"
CREATE TABLE member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    grade VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20)
);
```

</details>

#### 확인

다음 명령을 실행해 테이블 구조를 확인한다.

```sql id="7trpf8"
DESC member;
```

---

### Step 2. 데이터베이스 연결 구현

#### 목표

`MemberManager`에서 MySQL 연결을 생성하는 메서드를 구현한다.

파일 기반 버전의 `members`, `save()`, `load()`는 제거한다.

<details>
<summary>힌트 보기</summary>

```java id="6hjgyw"
public class MemberManager {

    private final int capacity;

    public MemberManager(int capacity) {
        this.capacity = capacity;
    }

    private Connection connection() {
        String url =
                "jdbc:mysql://localhost:3306/java_basic";

        String username = "root";
        String password = "1234";

        try {
            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );

            return DriverManager.getConnection(
                    url,
                    username,
                    password
            );

        } catch (ClassNotFoundException
                 | SQLException e) {

            throw new RuntimeException(
                    "데이터베이스 연결에 실패했습니다.",
                    e
            );
        }
    }

    public int capacity() {
        return capacity;
    }
}
```

</details>

#### 확인

* MySQL 서버가 실행 중인지 확인한다.
* JDBC 드라이버가 프로젝트에 추가되어 있는지 확인한다.
* 연결 정보가 현재 환경과 일치하는지 확인한다.
* 연결 시 예외가 발생하지 않는지 확인한다.

데이터베이스 계정 정보는 실제 프로젝트에서 코드에 직접 작성하지 않고 환경변수나 별도 설정 파일로 분리하는 것이 좋다.

---

### Step 3. 회원 추가

#### 목표

회원 객체를 `member` 테이블에 추가한다.

<details>
<summary>힌트 보기</summary>

```java id="wye3wc"
public void add(Member member) {
    String sql =
            "INSERT INTO member "
            + "(grade, name, email, phone) "
            + "VALUES (?, ?, ?, ?)";

    try (
            Connection connection =
                    connection();

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql)
    ) {
        preparedStatement.setString(
                1,
                member.getGrade()
        );

        preparedStatement.setString(
                2,
                member.getName()
        );

        preparedStatement.setString(
                3,
                member.getEmail()
        );

        preparedStatement.setString(
                4,
                member.getPhone()
        );

        preparedStatement.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException(
                "회원 추가 중 오류가 발생했습니다.",
                e
        );
    }
}
```

</details>

#### 확인

회원 추가 후 다음 SQL로 저장 결과를 확인한다.

```sql id="7skgun"
SELECT * FROM member;
```

---

### Step 4. 조회 결과 변환 메서드 구현

#### 목표

`ResultSet`의 현재 행을 `Member` 객체로 변환한다.

<details>
<summary>힌트 보기</summary>

```java id="4kubf2"
private Member toMember(ResultSet resultSet)
        throws SQLException {

    String grade =
            resultSet.getString("grade");

    String name =
            resultSet.getString("name");

    String email =
            resultSet.getString("email");

    String phone =
            resultSet.getString("phone");

    if (grade.equals("VIP")) {
        return new VipMember(
                name,
                email,
                phone
        );
    }

    return new NormalMember(
            name,
            email,
            phone
    );
}
```

</details>

#### 확인

VIP 회원과 일반 회원이 각 등급에 맞는 구현 객체로 생성되는지 확인한다.

---

### Step 5. 이메일로 회원 조회

#### 목표

이메일이 일치하는 회원 한 명을 조회한다.

<details>
<summary>힌트 보기</summary>

```java id="bc9n9s"
public Member findByEmail(String email) {
    String sql =
            "SELECT grade, name, email, phone "
            + "FROM member "
            + "WHERE email = ?";

    try (
            Connection connection =
                    connection();

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql)
    ) {
        preparedStatement.setString(1, email);

        try (ResultSet resultSet =
                     preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return toMember(resultSet);
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException(
                "회원 조회 중 오류가 발생했습니다.",
                e
        );
    }

    return null;
}
```

</details>

#### 확인

* 존재하는 이메일을 조회하면 회원 객체가 반환되는지 확인한다.
* 존재하지 않는 이메일을 조회하면 `null`이 반환되는지 확인한다.

---

### Step 6. 이름으로 회원 조회

#### 목표

이름이 일치하는 회원을 조회한다.

이름은 중복될 수 있으므로 여러 회원을 조회해야 한다면 반환 타입을 `List<Member>`로 설계하는 것이 적절하다.

<details>
<summary>한 명만 반환하는 기본 구현</summary>

```java id="407juv"
public Member findByName(String name) {
    String sql =
            "SELECT grade, name, email, phone "
            + "FROM member "
            + "WHERE name = ?";

    try (
            Connection connection =
                    connection();

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql)
    ) {
        preparedStatement.setString(1, name);

        try (ResultSet resultSet =
                     preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return toMember(resultSet);
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException(
                "회원 조회 중 오류가 발생했습니다.",
                e
        );
    }

    return null;
}
```

</details>

#### 확인

이름이 일치하는 회원이 조회되는지 확인한다.

이름 중복을 허용한다면 여러 결과를 처리하도록 확장할 수 있다.

---

### Step 7. 전체 회원 조회

#### 목표

`member` 테이블의 모든 회원을 조회하고 출력한다.

<details>
<summary>힌트 보기</summary>

```java id="z4od7z"
public void printAll() {
    String sql =
            "SELECT grade, name, email, phone "
            + "FROM member";

    try (
            Connection connection =
                    connection();

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            ResultSet resultSet =
                    preparedStatement.executeQuery()
    ) {
        boolean empty = true;

        while (resultSet.next()) {
            Member member =
                    toMember(resultSet);

            member.printInfo();
            empty = false;
        }

        if (empty) {
            System.out.println(
                    "등록된 회원이 없습니다."
            );
        }

    } catch (SQLException e) {
        throw new RuntimeException(
                "전체 회원 조회 중 오류가 발생했습니다.",
                e
        );
    }
}
```

</details>

#### 확인

* 등록된 모든 회원이 출력되는지 확인한다.
* 회원이 없으면 안내 메시지가 출력되는지 확인한다.
* 등급에 맞는 회원 정보와 혜택이 출력되는지 확인한다.

---

### Step 8. 이메일 중복 확인

#### 목표

입력한 이메일이 이미 데이터베이스에 존재하는지 확인한다.

<details>
<summary>힌트 보기</summary>

```java id="5n6zv6"
public boolean existsEmail(String email) {
    String sql =
            "SELECT COUNT(*) "
            + "FROM member "
            + "WHERE email = ?";

    try (
            Connection connection =
                    connection();

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql)
    ) {
        preparedStatement.setString(1, email);

        try (ResultSet resultSet =
                     preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException(
                "이메일 중복 확인 중 오류가 발생했습니다.",
                e
        );
    }

    return false;
}
```

</details>

#### 확인

* 이미 등록된 이메일이면 `true`가 반환되는지 확인한다.
* 등록되지 않은 이메일이면 `false`가 반환되는지 확인한다.
* 데이터베이스의 `UNIQUE` 제약 조건도 함께 적용되어 있는지 확인한다.

---

### Step 9. 현재 회원 수 조회

#### 목표

데이터베이스에 저장된 전체 회원 수를 조회한다.

<details>
<summary>힌트 보기</summary>

```java id="48es0i"
public int size() {
    String sql =
            "SELECT COUNT(*) FROM member";

    try (
            Connection connection =
                    connection();

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            ResultSet resultSet =
                    preparedStatement.executeQuery()
    ) {
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }

    } catch (SQLException e) {
        throw new RuntimeException(
                "회원 수 조회 중 오류가 발생했습니다.",
                e
        );
    }

    return 0;
}
```

정원 초과 여부는 다음과 같이 판단한다.

```java id="er31l5"
public boolean isFull() {
    return size() >= capacity;
}
```

</details>

#### 확인

* 데이터베이스의 실제 회원 수와 반환값이 같은지 확인한다.
* 회원 수가 정원 이상이면 `isFull()`이 `true`를 반환하는지 확인한다.

---

### Step 10. 회원 수정

#### 목표

기존 이메일을 조건으로 회원 정보를 수정한다.

<details>
<summary>힌트 보기</summary>

```java id="0sldex"
public boolean update(
        String email,
        String name,
        String newEmail,
        String phone
) {
    String sql =
            "UPDATE member "
            + "SET name = ?, "
            + "email = ?, "
            + "phone = ? "
            + "WHERE email = ?";

    try (
            Connection connection =
                    connection();

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql)
    ) {
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, newEmail);
        preparedStatement.setString(3, phone);
        preparedStatement.setString(4, email);

        return preparedStatement.executeUpdate() > 0;

    } catch (SQLException e) {
        throw new RuntimeException(
                "회원 수정 중 오류가 발생했습니다.",
                e
        );
    }
}
```

</details>

#### 확인

* 수정 후 데이터베이스의 회원 정보가 변경되는지 확인한다.
* 존재하지 않는 이메일을 입력하면 `false`가 반환되는지 확인한다.
* 새 이메일이 다른 회원과 중복되는 경우를 처리하는지 확인한다.
* `WHERE` 조건에는 기존 이메일이 사용되는지 확인한다.

---

### Step 11. 회원 삭제

#### 목표

이메일이 일치하는 회원을 삭제한다.

<details>
<summary>힌트 보기</summary>

```java id="huv25h"
public boolean delete(String email) {
    String sql =
            "DELETE FROM member "
            + "WHERE email = ?";

    try (
            Connection connection =
                    connection();

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql)
    ) {
        preparedStatement.setString(1, email);

        return preparedStatement.executeUpdate() > 0;

    } catch (SQLException e) {
        throw new RuntimeException(
                "회원 삭제 중 오류가 발생했습니다.",
                e
        );
    }
}
```

</details>

#### 확인

* 회원 삭제 후 해당 행이 테이블에서 제거되는지 확인한다.
* 존재하지 않는 이메일을 입력하면 `false`가 반환되는지 확인한다.

---

### Step 12. Main 정리

#### 목표

파일 저장과 불러오기 코드를 제거하고 DB 기반 회원 관리 기능과 연결한다.

#### 구현 내용

* 프로그램 시작 시 `load()`를 호출하지 않는다.
* 회원 추가, 수정, 삭제 후 `save()`를 호출하지 않는다.
* 프로그램 종료 시 파일 저장을 실행하지 않는다.
* 기존 메뉴에서 `MemberManager`의 JDBC 메서드를 호출한다.

데이터베이스 변경 작업은 각 메서드가 실행될 때 즉시 반영된다.

#### 확인

* 회원 추가가 DB에 바로 반영되는지 확인한다.
* 회원 조회가 DB 데이터를 기준으로 실행되는지 확인한다.
* 수정과 삭제가 DB에 바로 반영되는지 확인한다.
* 프로그램을 다시 실행해도 회원 정보가 유지되는지 확인한다.

---

## 7. 주의할 점

### 7.1 DB 연결 정보

데이터베이스 URL, 사용자명, 비밀번호를 코드에 직접 작성하면 저장소에 민감한 정보가 노출될 수 있다.

실제 프로젝트에서는 환경변수나 별도 설정 파일로 분리해야 한다.

---

### 7.2 연결 자원 종료

`Connection`, `PreparedStatement`, `ResultSet`은 사용 후 닫아야 한다.

try-with-resources를 사용하면 정상 실행과 예외 발생 여부에 관계없이 자원이 자동으로 정리된다.

---

### 7.3 이름 중복

이메일은 `UNIQUE`이므로 한 명을 식별할 수 있지만 이름은 중복될 수 있다.

이름 조회 결과를 한 명만 반환하면 같은 이름의 다른 회원이 누락될 수 있다.

필요하면 `List<Member>`를 반환해 조회된 모든 회원을 처리한다.

---

### 7.4 수정 이메일 중복

회원 수정 시 새로운 이메일이 기존 회원의 이메일과 중복되면 `UNIQUE` 제약 조건에 의해 SQL 실행이 실패한다.

수정 전 중복 여부를 확인하거나 예외를 구분해 사용자에게 안내할 수 있다.

---

### 7.5 예외 처리 방식

모든 `SQLException`을 단순히 `RuntimeException`으로 변환하면 사용자에게 적절한 오류 원인을 안내하기 어렵다.

기본 과제에서는 예외를 변환해 처리할 수 있지만 다음 상황은 구분하는 것이 좋다.

* 데이터베이스 연결 실패
* 이메일 중복
* SQL 문법 오류
* 테이블 또는 컬럼 불일치

---

## 8. 최종 점검

* [ ] MySQL에 `member` 테이블을 생성했다.
* [ ] JDBC 드라이버를 프로젝트에 추가했다.
* [ ] 데이터베이스 연결 메서드를 구현했다.
* [ ] `PreparedStatement`의 `?`에 값을 설정했다.
* [ ] 회원 추가를 `INSERT`로 구현했다.
* [ ] 회원 조회를 `SELECT`로 구현했다.
* [ ] 회원 수와 이메일 중복을 `COUNT(*)`로 확인했다.
* [ ] 회원 수정을 `UPDATE`로 구현했다.
* [ ] 회원 삭제를 `DELETE`로 구현했다.
* [ ] 조회 결과를 실제 회원 구현 객체로 변환했다.
* [ ] 변경 SQL에 `executeUpdate()`를 사용했다.
* [ ] 조회 SQL에 `executeQuery()`를 사용했다.
* [ ] JDBC 자원에 try-with-resources를 사용했다.
* [ ] 파일 기반 `save()`와 `load()`를 제거했다.
* [ ] 프로그램을 다시 실행해도 DB의 회원 정보가 유지된다.
* [ ] DB 연결 정보를 외부 설정으로 분리할 필요성을 이해했다.
