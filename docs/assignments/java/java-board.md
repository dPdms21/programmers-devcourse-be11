# 로그인 게시판 만들기

> JDBC, DAO, DTO, 인터페이스를 활용해 로그인 기반 게시판을 구현한다.
> 사용자는 회원가입과 로그인을 할 수 있고, 로그인한 사용자는 글 등록, 목록 조회, 수정, 삭제를 수행할 수 있다.
> 핵심은 화면 흐름, 데이터 접근, 데이터 운반 역할을 분리하는 것이다.

---

## 0. 먼저 알아둘 점

이 과제는 JDBC 기초와 인터페이스를 학습했다는 전제로 진행한다.

실행에 필요한 요소는 다음과 같다.

* MySQL
* `notice` 데이터베이스
* `user`, `content` 테이블
* JDBC 드라이버
* DB 접속 정보를 담은 환경변수 또는 `.env` 설정

학습용 구현이므로 단순화된 부분이 있다.

* 비밀번호를 평문으로 저장하고 비교한다.
* 콘솔 입력은 `Scanner`로 처리한다.
* 로그인 상태는 프로그램 실행 중 `NoticeImpl` 필드에 저장한다.
* 글 수정과 삭제는 로그인한 사용자의 글만 대상으로 한다.

이번 과제에서는 실무 수준의 보안보다 계층 분리, JDBC CRUD, 로그인 상태 관리, 권한 확인 흐름을 이해하는 데 집중한다.

---

## 1. 무엇을 만드는가?

로그인 기반 콘솔 게시판을 만든다.

구현 기능은 다음과 같다.

* 회원가입
* 로그인
* 글 등록
* 글 목록 조회
* 내 글 수정
* 내 글 삭제
* 로그아웃
* 회원 탈퇴
* 프로그램 종료

메뉴 예시는 다음과 같다.

```text
===== 게시판 =====
1. 로그인
2. 회원가입
3. 글 등록
4. 글 목록
5. 글 수정
6. 글 삭제
7. 로그아웃
8. 회원 탈퇴
9. 종료
=================
메뉴 선택:
```

로그인에 성공하면 프로그램 내부에 로그인 상태가 저장된다. 글 등록, 수정, 삭제는 로그인한 사용자만 수행할 수 있다.

---

## 2. 학습 목표

| 개념        | 내용                                                         |
| --------- | ---------------------------------------------------------- |
| 계층 분리     | `NoticeImpl`은 화면 흐름과 입력을 담당하고, `NoticeDAO`는 SQL과 DB 접근을 담당 |
| 인터페이스     | `Notice` 인터페이스로 게시판 기능을 선언하고 구현체에서 기능 작성                   |
| DTO       | `SignInResponseDTO`로 로그인 결과와 사용자 정보를 함께 전달                 |
| 로그인 상태 관리 | `NoticeImpl`의 `status`, `userId`, `name` 필드로 로그인 상태 유지     |
| 인가 처리     | `checkSignIn()`으로 로그인 여부를 확인한 뒤 글 등록, 수정, 삭제 수행            |
| JDBC CRUD | `PreparedStatement`와 `ResultSet`으로 회원과 게시글 데이터 처리          |
| 두 테이블 처리  | `user` 테이블과 `content` 테이블을 함께 사용                           |

---

## 3. 핵심 개념

### (1) 계층 분리

게시판 기능은 역할에 따라 나누어 구현한다.

```text
Start
→ 메뉴 출력과 프로그램 실행 흐름 제어

NoticeImpl
→ 사용자 입력, 결과 출력, 로그인 상태 관리, DAO 호출

NoticeDAO
→ DB 연결, SQL 실행, 결과 반환

DB
→ user, content 테이블 저장
```

`NoticeImpl`은 SQL을 직접 실행하지 않고 `NoticeDAO`를 호출한다. `NoticeDAO`는 화면 출력이나 사용자 입력을 처리하지 않고 DB 작업만 담당한다.

이렇게 역할을 나누면 화면 흐름을 수정하더라도 SQL 코드의 변경을 줄일 수 있고, DB 접근 로직을 수정하더라도 메뉴 흐름에 미치는 영향을 줄일 수 있다.

### (2) DTO

로그인 결과는 단순히 성공 또는 실패만으로 표현하기 어렵다.

로그인 결과는 다음 세 가지로 나뉜다.

| 결과               | 의미                       |
| ---------------- | ------------------------ |
| `null`           | 아이디가 존재하지 않음             |
| `status = false` | 아이디는 존재하지만 비밀번호가 일치하지 않음 |
| `status = true`  | 로그인 성공, 사용자 아이디와 이름 반환   |

`boolean` 하나만 반환하면 로그인 성공 여부는 알 수 있지만, 로그인한 사용자의 아이디와 이름을 함께 전달하기 어렵다.

따라서 `SignInResponseDTO`를 사용해 로그인 상태, 사용자 아이디, 사용자 이름을 한 객체로 묶어 전달한다.

### (3) 로그인 상태

로그인 상태는 `NoticeImpl`의 필드에 저장한다.

```java
private boolean status;
private String userId;
private String name;
```

로그인에 성공하면 다음과 같이 상태를 저장한다.

```java
setUserInfo(true, res.getUserId(), res.getName());
```

로그아웃하면 다음과 같이 상태를 초기화한다.

```java
setUserInfo(false, null, null);
```

프로그램의 메뉴 루프가 같은 `NoticeImpl` 객체를 계속 사용하므로, 한 번 로그인하면 프로그램이 종료되거나 로그아웃하기 전까지 로그인 상태가 유지된다.

### (4) 인가 확인

글 등록, 수정, 삭제는 로그인한 사용자만 수행할 수 있어야 한다.

이를 위해 기능 실행 전에 `checkSignIn()`으로 로그인 여부를 확인한다.

```java
public boolean checkSignIn() {
    if (!status) {
        System.out.println("로그인 먼저!");
        return false;
    }

    return true;
}
```

로그인하지 않은 상태라면 기능 실행을 중단한다. 로그인한 상태라면 현재 로그인한 사용자의 `userId`를 기준으로 글 등록, 수정, 삭제를 진행한다.

---

## 4. 파일 구조

| 파일                       | 역할                              | 계층     |
| ------------------------ | ------------------------------- | ------ |
| `Notice.java`            | 게시판 기능을 선언하는 인터페이스              | 인터페이스  |
| `NoticeImpl.java`        | 메뉴 출력, 입력 처리, 로그인 상태 관리, DAO 호출 | 화면·흐름  |
| `NoticeDAO.java`         | DB 연결, SQL 실행, 회원과 게시글 CRUD 처리  | 데이터 접근 |
| `SignInResponseDTO.java` | 로그인 결과와 사용자 정보를 전달              | DTO    |
| `Start.java`             | 프로그램 시작점과 메뉴 루프 실행              | 실행     |
| `user` 테이블               | 회원 정보 저장                        | DB     |
| `content` 테이블            | 게시글 정보 저장                       | DB     |

---

## 5. Step by Step

### Step 1. DB 테이블 만들기

회원 정보를 저장할 `user` 테이블과 게시글 정보를 저장할 `content` 테이블을 만든다.

`content` 테이블은 글 작성자의 아이디를 `user_id`로 가진다.

```sql
CREATE TABLE user (
    user_id VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE content (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

`user` 테이블은 회원 아이디, 비밀번호, 이름을 저장한다.

`content` 테이블은 게시글 번호, 작성자 아이디, 글 내용, 작성 시각을 저장한다. `created` 컬럼은 기본값으로 현재 시각을 사용한다.

확인할 내용은 다음과 같다.

```sql
DESC user;
DESC content;
```

---

### Step 2. 인터페이스와 메뉴 루프 만들기

게시판 기능을 `Notice` 인터페이스에 선언한다.

```java
public interface Notice {

    int printMenu();

    void signUp();

    void signIn();

    void newNotice();

    void getList();

    void updateNotice();

    void deleteNotice();

    void signOut();

    void leave();
}
```

`Start` 클래스에서는 `NoticeImpl` 객체를 생성하고 메뉴 번호에 따라 기능을 호출한다.

```java
public class Start {

    public static void main(String[] args) {
        Notice notice = new NoticeImpl();

        while (true) {
            int choice = notice.printMenu();

            switch (choice) {
                case 1 -> notice.signIn();
                case 2 -> notice.signUp();
                case 3 -> notice.newNotice();
                case 4 -> notice.getList();
                case 5 -> notice.updateNotice();
                case 6 -> notice.deleteNotice();
                case 7 -> notice.signOut();
                case 8 -> notice.leave();
                case 9 -> {
                    System.out.println("프로그램 종료");
                    return;
                }
                default -> System.out.println("잘못된 입력");
            }
        }
    }
}
```

먼저 메뉴가 반복 출력되고, `9`를 입력했을 때 프로그램이 종료되는지 확인한다.

---

### Step 3. DAO와 DB 연결 만들기

SQL을 담당할 `NoticeDAO`를 만든다. DB 연결은 `getConnection()` 메서드로 분리한다.

현재 구현은 `.env`에서 DB 접속 정보를 읽어온다.

```java
public class NoticeDAO {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private Connection getConnection() {
        String url = dotenv.get("DB_URL");
        String username = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

`NoticeImpl`은 `NoticeDAO`를 필드로 가진다.

```java
private NoticeDAO noticeDAO = new NoticeDAO();
private boolean status;
private String userId;
private String name;
private Scanner scanner = new Scanner(System.in);
```

`NoticeImpl`은 사용자 입력과 흐름을 처리하고, DB 작업이 필요할 때 `noticeDAO`를 호출한다.

---

### Step 4. 회원가입 구현하기

회원가입은 아이디 중복 확인 후 새 회원 정보를 `user` 테이블에 저장하는 흐름이다.

DAO에서 아이디 존재 여부를 확인한다.

```java
public boolean checkUserId(String userId) {
    String sql = "select count(*) from user where user_id = ?";

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setString(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    } catch (SQLException e) {
        throw new RuntimeException("아이디 중복 확인 중 오류 발생", e);
    }
}
```

DAO에서 회원 정보를 저장한다.

```java
public boolean signupExc(String userId, String password, String name) {
    String sql = "insert into user (user_id, password, name) values (?, ?, ?)";

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setString(1, userId);
        ps.setString(2, password);
        ps.setString(3, name);

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("회원가입 중 오류 발생", e);
    }
}
```

`NoticeImpl`에서는 사용자 입력을 받고 DAO를 호출한다.

```java
public void signUp() {
    System.out.print("아이디: ");
    String userId = scanner.nextLine();

    if (noticeDAO.checkUserId(userId)) {
        System.out.println("이미 가입된 사용자");
        return;
    }

    System.out.print("비밀번호: ");
    String password = scanner.nextLine();

    System.out.print("이름: ");
    String name = scanner.nextLine();

    if (noticeDAO.signupExc(userId, password, name)) {
        System.out.println("회원가입 완료");
    }
}
```

회원가입 후 DB의 `user` 테이블에 데이터가 저장되는지 확인한다. 같은 아이디로 다시 가입했을 때 중복 안내가 출력되는지도 확인한다.

---

### Step 5. 로그인과 로그인 상태 구현하기

로그인은 아이디와 비밀번호를 확인하고, 성공 시 로그인 상태를 저장하는 흐름이다.

로그인 결과를 전달할 DTO를 만든다.

```java
public class SignInResponseDTO {

    private boolean status;
    private String userId;
    private String name;

    public SignInResponseDTO(boolean status, String userId, String name) {
        this.status = status;
        this.userId = userId;
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
```

DAO에서 아이디로 회원을 조회하고 비밀번호를 비교한다.

```java
public SignInResponseDTO signInExc(String userId, String password) {
    String sql = "select user_id, password, name from user where user_id = ?";

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setString(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                return null;
            }

            String dbPassword = rs.getString("password");
            String name = rs.getString("name");

            if (!dbPassword.equals(password)) {
                return new SignInResponseDTO(false, null, null);
            }

            return new SignInResponseDTO(true, userId, name);
        }
    } catch (SQLException e) {
        throw new RuntimeException("로그인 중 오류 발생", e);
    }
}
```

`NoticeImpl`에서는 DTO 결과에 따라 로그인 성공, 아이디 없음, 비밀번호 불일치를 구분한다.

```java
public void signIn() {
    System.out.print("아이디: ");
    String userId = scanner.nextLine();

    System.out.print("비밀번호: ");
    String password = scanner.nextLine();

    SignInResponseDTO res = noticeDAO.signInExc(userId, password);

    if (res == null) {
        System.out.println("존재하지 않음");
        return;
    }

    if (res.isStatus()) {
        setUserInfo(true, res.getUserId(), res.getName());
        System.out.println(name + "님 로그인 완료");
    } else {
        System.out.println("비밀번호 불일치");
    }
}
```

로그인 상태 저장은 별도 메서드로 분리한다.

```java
private void setUserInfo(boolean status, String userId, String name) {
    this.status = status;
    this.userId = userId;
    this.name = name;
}
```

로그인 성공 후 사용자 이름이 출력되고, 이후 기능에서 로그인 상태가 유지되는지 확인한다.

---

### Step 6. 글 등록과 전체 목록 조회 구현하기

글 등록은 로그인한 사용자만 수행할 수 있다. 먼저 로그인 여부를 확인한다.

```java
public boolean checkSignIn() {
    if (!status) {
        System.out.println("로그인 먼저!");
        return false;
    }

    return true;
}
```

DAO에서 게시글을 저장한다.

```java
public boolean newNotice(String userId, String content) {
    String sql = "INSERT INTO content (user_id, content) VALUES (?, ?)";

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setString(1, userId);
        ps.setString(2, content);

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("글 등록 중 오류 발생", e);
    }
}
```

`NoticeImpl`에서는 로그인 여부를 확인한 뒤 글 내용을 입력받고 DAO를 호출한다.

```java
public void newNotice() {
    if (!checkSignIn()) {
        return;
    }

    System.out.print("내용: ");
    String content = scanner.nextLine();

    if (noticeDAO.newNotice(userId, content)) {
        System.out.println("글 등록 완료");
    }
}
```

전체 목록 조회는 로그인 여부와 관계없이 수행할 수 있다.

```java
public List getList() {
    String sql = "SELECT id, user_id, content, created FROM content ORDER BY id DESC";
    List list = new ArrayList<>();

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
    ) {
        while (rs.next()) {
            int id = rs.getInt("id");
            String userId = rs.getString("user_id");
            String content = rs.getString("content");
            String created = rs.getTimestamp("created")
                    .toLocalDateTime()
                    .format(FORMATTER);

            list.add("[" + id + "] " + userId + " - " + content + " - " + created);
        }

        return list;
    } catch (SQLException e) {
        throw new RuntimeException("글 목록 조회 중 오류 발생", e);
    }
}
```

글 등록 후 목록 조회에서 최신 글이 위에 출력되는지 확인한다. 로그아웃 상태에서는 글 등록이 막히는지 확인한다.

---

### Step 7. 내 글 수정과 삭제 구현하기

수정과 삭제는 로그인한 사용자의 글만 대상으로 한다.

먼저 DAO에서 로그인한 사용자의 글 목록만 조회한다.

```java
public List getListByUserId(String userId) {
    String sql = "SELECT id, user_id, content, created FROM content WHERE user_id = ? ORDER BY id DESC";
    List list = new ArrayList<>();

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setString(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String writer = rs.getString("user_id");
                String content = rs.getString("content");
                String created = rs.getTimestamp("created")
                        .toLocalDateTime()
                        .format(FORMATTER);

                list.add("[" + id + "] " + writer + " - " + content + " - " + created);
            }
        }

        return list;
    } catch (SQLException e) {
        throw new RuntimeException("사용자 글 목록 조회 중 오류 발생", e);
    }
}
```

글 수정은 글 번호와 로그인한 사용자 아이디를 함께 조건으로 사용한다.

```java
public boolean updateNotice(int id, String userId, String content) {
    String sql = "UPDATE content SET content = ?, created = ? WHERE id = ? AND user_id = ?";

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setString(1, content);
        ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
        ps.setInt(3, id);
        ps.setString(4, userId);

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("글 수정 중 오류 발생", e);
    }
}
```

글 삭제도 글 번호와 로그인한 사용자 아이디를 함께 조건으로 사용한다.

```java
public boolean deleteNotice(int id, String userId) {
    String sql = "DELETE FROM content WHERE id = ? AND user_id = ?";

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setInt(1, id);
        ps.setString(2, userId);

        return ps.executeUpdate() == 1;
    } catch (SQLException e) {
        throw new RuntimeException("글 삭제 중 오류 발생", e);
    }
}
```

`WHERE id = ? AND user_id = ?` 조건을 사용하면 다른 사용자의 글 번호를 입력하더라도 수정하거나 삭제할 수 없다.

수정과 삭제 흐름은 다음과 같다.

```text
로그인 여부 확인
→ 로그인한 사용자의 글 목록 조회
→ 수정 또는 삭제할 글 번호 입력
→ 글 번호와 userId를 조건으로 UPDATE 또는 DELETE 실행
→ 실행 결과에 따라 성공 또는 실패 출력
```

---

### Step 8. 로그아웃과 회원 탈퇴 구현하기

로그아웃은 로그인 상태를 초기화한다.

```java
public void signOut() {
    setUserInfo(false, null, null);
    System.out.println("로그아웃 완료");
}
```

회원 탈퇴는 회원 정보와 해당 회원이 작성한 글을 함께 삭제한다.

DAO에서 회원의 글을 삭제한다.

```java
public void deleteContentExc(String userId) {
    String sql = "DELETE FROM content WHERE user_id = ?";

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setString(1, userId);
        ps.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("회원 글 삭제 중 오류 발생", e);
    }
}
```

DAO에서 회원 정보를 삭제한다.

```java
public boolean leaveExc(String userId) {
    String sql = "DELETE FROM user WHERE user_id = ?";

    try (
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
    ) {
        ps.setString(1, userId);

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        throw new RuntimeException("회원 탈퇴 중 오류 발생", e);
    }
}
```

`NoticeImpl`에서는 탈퇴할 아이디를 입력받고, 존재하는 회원이면 해당 회원의 글을 먼저 삭제한 뒤 회원을 삭제한다.

```java
public void leave() {
    System.out.print("탈퇴할 아이디: ");
    String deleteUserId = scanner.nextLine();

    if (!noticeDAO.checkUserId(deleteUserId)) {
        System.out.println("존재하지 않는 사용자");
        return;
    }

    noticeDAO.deleteContentExc(deleteUserId);

    if (noticeDAO.leaveExc(deleteUserId)) {
        System.out.println("회원 탈퇴 완료");

        if (deleteUserId.equals(userId)) {
            signOut();
        }
    }
}
```

외래키가 있는 구조라면 회원보다 게시글을 먼저 삭제해야 참조 무결성 오류를 피할 수 있다. 현재 구조에서도 글을 먼저 삭제하면 탈퇴 후 남는 게시글을 방지할 수 있다.

---

### Step 9. 마무리 점검

완성 후 다음 항목을 확인한다.

* [ ] 회원가입이 정상 동작한다
* [ ] 중복 아이디 가입이 차단된다
* [ ] 로그인 성공, 아이디 없음, 비밀번호 불일치가 구분된다
* [ ] 로그인 성공 후 로그인 상태가 유지된다
* [ ] 로그인하지 않은 상태에서 글 등록, 수정, 삭제가 차단된다
* [ ] 글 등록 후 목록에서 최신순으로 조회된다
* [ ] 수정과 삭제는 로그인한 사용자의 글만 대상으로 한다
* [ ] 다른 사용자의 글 번호를 입력해도 수정·삭제되지 않는다
* [ ] 로그아웃 시 로그인 상태가 초기화된다
* [ ] 회원 탈퇴 시 회원 정보와 작성 글이 삭제된다
* [ ] `NoticeImpl`에는 SQL이 없고, `NoticeDAO`에는 화면 입력 처리가 없다
* [ ] DB 작업은 `PreparedStatement`와 try-with-resources를 사용한다

---

## 6. 학습 체크

* [ ] `Notice`, `NoticeImpl`, `NoticeDAO`, `SignInResponseDTO`, `Start`의 역할을 설명할 수 있다
* [ ] 화면 흐름과 DB 접근을 분리하는 이유를 설명할 수 있다
* [ ] 로그인 결과를 DTO로 전달하는 이유를 설명할 수 있다
* [ ] 로그인 상태를 필드로 유지하는 방식을 설명할 수 있다
* [ ] `checkSignIn()`이 필요한 이유를 설명할 수 있다
* [ ] `WHERE id = ? AND user_id = ?` 조건으로 본인 글만 수정·삭제하는 이유를 설명할 수 있다
* [ ] `PreparedStatement`가 필요한 이유를 설명할 수 있다
* [ ] try-with-resources로 DB 자원을 정리하는 흐름을 설명할 수 있다

---

## 7. 최종 완성 체크리스트

* [ ] `user`, `content` 테이블을 만들었다
* [ ] `Notice` 인터페이스를 작성했다
* [ ] `NoticeImpl`에서 메뉴, 입력, 출력, 로그인 상태를 관리한다
* [ ] `NoticeDAO`에서 DB 연결과 SQL 실행을 담당한다
* [ ] `SignInResponseDTO`로 로그인 결과를 전달한다
* [ ] `Start`에서 메뉴 루프를 실행한다
* [ ] 회원가입과 로그인이 동작한다
* [ ] 글 등록, 목록 조회, 수정, 삭제가 동작한다
* [ ] 로그아웃과 회원 탈퇴가 동작한다
* [ ] 계층 분리, 로그인 상태 관리, 인가 확인이 적용되어 있다

---

## 8. 도전 과제

1. **비밀번호 해싱**: 평문 비밀번호 대신 해시 값을 저장하고 검증하는 구조로 개선
2. **입력 처리 개선**: 하나의 `Scanner`를 공유하고 `nextLine()` 기준으로 입력 방식을 통일
3. **트랜잭션 처리**: 회원 탈퇴 시 게시글 삭제와 회원 삭제를 하나의 트랜잭션으로 묶어 처리
4. **본인 글만 수정·삭제 보장**: `WHERE id = ? AND user_id = ?` 조건으로 다른 사용자의 글 수정·삭제 차단
5. **외래키와 CASCADE 적용**: `content.user_id`에 외래키를 걸고 `ON DELETE CASCADE`로 회원 탈퇴 시 게시글 자동 삭제
6. **검색과 페이징**: 게시글 검색 기능과 페이지 단위 목록 조회 기능 추가
7. **게시글 DTO 적용**: 문자열 목록 대신 `ContentDTO`로 게시글 목록 데이터를 전달
