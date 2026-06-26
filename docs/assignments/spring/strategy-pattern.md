# 전략 패턴 리팩토링 (별도 클래스 → 익명 클래스 → 람다)

> 전략 패턴에서 **변하는 부분(전략)** 을 어떻게 끼워 넣는지를 **별도 클래스 → 익명 클래스 → 람다** 순으로 줄여가며 익힌다.
> 핵심은 익명 클래스가 편리한 이유다. 그 이유는 **지역 변수 캡처**이며, 코드를 통해 직접 확인한다.
> **아래 Step을 순서대로 진행하면 마지막 Step에서 같은 동작을 세 가지 방식으로 작성할 수 있다.**
>
> 각 Step의 힌트는 접혀 있다. 먼저 별도 클래스를 메서드 안으로 옮기는 방법을 고민하고, 막히면 펼쳐 확인한다.

---

## 0. 먼저 알아둘 점

* 전략 패턴은 코드를 **둘로 나눈다**. **컨텍스트(context)** 는 변하지 않는 공통 흐름인 자원 열기·실행·정리를 담당하고, **전략(strategy)** 은 무슨 작업을 할지와 같은 변하는 부분을 담당한다. 컨텍스트는 **인터페이스에만 의존**하고 실제 전략은 **런타임에 주입**받는다. 따라서 새 기능을 추가해도 컨텍스트는 수정하지 않고 전략만 새로 만들 수 있다. 이는 **OCP**에 해당한다.

* 원래 예제는 JDBC의 `Connection`, `PreparedStatement`를 사용하지만, 실제 DB 없이 실행할 수 있도록 **인메모리 방식으로 단순화**했다. 패턴 구조는 동일하다.

  | 원래 JDBC                                                   | 이 과제의 인메모리 구조                 |
  | --------------------------------------------------------- | ----------------------------- |
  | `jdbcContextWithStatementStrategy`                        | `context`                     |
  | `StatementStrategy.makeStatement(conn)` + `executeUpdate` | `StatementStrategy.run(db)`   |
  | `Connection`과 자원 정리                                       | `Database.open()` / `close()` |
  | 별도 전략 클래스 `UserDAODeleteAll` 등                            | `DeleteAllStrategy` 등         |

* 인터페이스, 익명 클래스, 람다는 알고 있다고 가정한다. `StatementStrategy`는 추상 메서드가 하나인 **함수형 인터페이스**이므로 람다로 작성할 수 있다.

---

## 1. 무엇을 만드는가?

같은 컨텍스트인 열기 → 전략 실행 → 닫기 흐름에 **전략만 바꾸어 끼우는** 프로그램을 완성한다. 전략은 세 가지 방식으로 작성한다.

```text
== (별도 클래스) deleteAll ==
  [컨텍스트] 연결 열기
  [전략-별도클래스] 전체 삭제
  [컨텍스트] 연결 닫기

== (익명 클래스) add(김) ==
  [컨텍스트] 연결 열기
  [전략-익명] 추가: 김
  [컨텍스트] 연결 닫기

== (람다) add(이) ==
  [컨텍스트] 연결 열기
  [전략-람다] 추가: 이
  [컨텍스트] 연결 닫기

현재 사용자 수: 2
사용자: 김
사용자: 이
```

세 경우 모두 **연결 열기 → 전략 실행 → 연결 닫기** 흐름은 동일하다. **변하지 않는 부분은 컨텍스트가 담당하고 변하는 부분만 전략이 담당하는 것**이 핵심이다.

## 2. 학습 목표

| 개념                     | 학습 위치                                                 |
| ---------------------- | ----------------------------------------------------- |
| 전략 인터페이스와 컨텍스트의 공통 흐름  | Step 1 (`StatementStrategy.java`, `UserDao.java`)     |
| 전략을 별도 클래스로 분리         | Step 2 (`DeleteAllStrategy.java`, `AddStrategy.java`) |
| 익명 클래스로 인라인하고 지역 변수 캡처 | Step 3 (`UserDao.java`)                               |
| 람다로 단순화                | Step 4 (`UserDao.java`)                               |
| 전체 실행                  | Step 5 (`Main.java`)                                  |

## 3. 핵심 개념

### (1) 전략 패턴 = 컨텍스트(불변) + 전략(가변)

```text
컨텍스트(context)            전략(strategy)
─────────────────           ──────────────
db.open();        ┐         무슨 작업을 할지 결정한다
strategy.run(db); ┼ 공통     · 전체 삭제
db.close();       ┘         · 사용자 추가
```

컨텍스트는 `StatementStrategy`라는 **인터페이스에만** 의존한다. 따라서 새 작업이 생겨도 컨텍스트는 그대로 두고 **전략만 새로** 만들면 된다.

### (2) 별도 클래스의 불편함

전략을 별도 클래스로 만들면 작업마다 **클래스 파일이 하나씩** 생성된다. 또한 작업에 데이터가 필요한 경우, 예를 들어 추가할 `user`가 필요하다면 클래스는 해당 값을 **생성자로 받아 필드에 저장**해야 한다. 한 번만 사용하는 작업이라면 이러한 구조가 번거로울 수 있다.

### (3) 익명 클래스의 장점 = 지역 변수 캡처

익명 클래스는 메서드 **안에 바로** 작성하므로 별도 파일이 생성되지 않는다. 또한 바깥 메서드의 **지역 변수를 그대로 가져다 사용할 수 있다.** 이를 지역 변수 캡처라고 한다.

따라서 `user`를 생성자로 넘길 필요 없이 바로 사용할 수 있다.

```java
// 별도 클래스: user를 생성자로 받아야 한다.
new AddStrategy(user);

// 익명 클래스: 바깥의 user를 캡처하므로 생성자가 필요하지 않다.
new StatementStrategy() {
    public void run(Database db) {
        db.getUsers().add(user);
    }
};
```

### (4) 람다 = 함수형 인터페이스를 더 짧게 표현

`StatementStrategy`는 추상 메서드가 하나이므로 익명 클래스를 **람다로** 줄일 수 있다.

```java
context(db -> db.getUsers().add(user));
```

```text
변하는 부분은 전략, 변하지 않는 부분은 컨텍스트로 분리한다.
별도 클래스 → 익명 클래스 → 람다 순서로 코드를 단순화한다.
```

## 4. 파일 구조

| 파일                                            | 역할                                      |
| --------------------------------------------- | --------------------------------------- |
| `User.java`                                   | 사용자 도메인 클래스이며 id와 name을 가진다             |
| `Database.java`                               | 인메모리 저장과 열기·닫기 동작을 담당한다                 |
| `StatementStrategy.java`                      | `void run(Database db)`를 정의하는 전략 인터페이스다 |
| `DeleteAllStrategy.java` / `AddStrategy.java` | Step 2에서 사용하는 별도 클래스 전략이다               |
| `UserDao.java`                                | 컨텍스트와 `deleteAll`, `add`를 가진다           |
| `Main.java`                                   | 전체 시나리오를 실행한다                           |

> 실행: `javac *.java` → `java -Dstdout.encoding=UTF-8 Main`

---

## 5. Step by Step

### Step 1. 전략 인터페이스와 컨텍스트 만들기 (`StatementStrategy.java`, `UserDao.java`)

**목표**: 변하지 않는 공통 흐름인 컨텍스트와 변하는 부분의 틀인 전략 인터페이스를 만든다.

`Database`는 실제 DB 대신 리스트에 저장하고 열기와 닫기를 흉내 낸다.

```java
// Database.java
import java.util.ArrayList;
import java.util.List;

class Database {
    private List<User> users = new ArrayList<>();

    void open() {
        System.out.println("  [컨텍스트] 연결 열기");
    }

    void close() {
        System.out.println("  [컨텍스트] 연결 닫기");
    }

    List<User> getUsers() {
        return users;
    }
}
```

**할 일**

1. `StatementStrategy` 인터페이스를 만든다. 메서드는 `void run(Database db);`로 정의한다.
2. `UserDao`에 `Database`를 주입받고 **컨텍스트**인 `context(StatementStrategy strategy)`를 만든다. 실행 순서는 열기 → `strategy.run(db)` → 닫기다.

<details>
<summary>힌트 보기</summary>

```java
// StatementStrategy.java
@FunctionalInterface
interface StatementStrategy {
    void run(Database db);
}
```

```java
// UserDao.java
class UserDao {
    private Database db;

    UserDao(Database db) {
        this.db = db;
    }

    void context(StatementStrategy strategy) {
        db.open();
        strategy.run(db);
        db.close();
    }
}
```

`context`는 전략이 무엇인지 알 필요가 없다. `StatementStrategy` 인터페이스만 보고 `run`을 호출한다. 이것이 컨텍스트가 인터페이스에만 의존한다는 의미다.

</details>

**확인**: `context`가 컴파일되고 어떤 전략이든 전달받아 열기 → 실행 → 닫기 흐름을 수행할 준비가 되면 통과한다.

---

### Step 2. 전략을 별도 클래스로 만들기 (`DeleteAllStrategy.java`, `AddStrategy.java`)

**목표**: 리팩토링 전의 형태로 전략마다 클래스를 따로 만든다.

**할 일**

1. `StatementStrategy`를 구현하는 `DeleteAllStrategy`를 만들고 `run`에서 전체 삭제를 수행한다.
2. `AddStrategy`를 만들고 추가할 `user`를 **생성자로 받아 필드에 저장**한 뒤 `run`에서 추가한다.
3. `UserDao`에서 해당 전략들을 생성하여 `context`에 넘기는 `deleteAll()`, `add(user)`를 만든다.

<details>
<summary>힌트 보기</summary>

```java
// DeleteAllStrategy.java
class DeleteAllStrategy implements StatementStrategy {
    public void run(Database db) {
        db.getUsers().clear();
        System.out.println("  [전략-별도클래스] 전체 삭제");
    }
}
```

```java
// AddStrategy.java
class AddStrategy implements StatementStrategy {
    private final User user;

    AddStrategy(User user) {
        this.user = user;
    }

    public void run(Database db) {
        db.getUsers().add(user);
        System.out.println("  [전략-별도클래스] 추가: " + user.getName());
    }
}
```

```java
// UserDao.java
void deleteAll() {
    context(new DeleteAllStrategy());
}

void add(User user) {
    context(new AddStrategy(user));
}
```

`AddStrategy`가 `user`를 **생성자로 받아 필드에 저장**하는 점을 확인한다. 별도 클래스는 바깥 메서드의 지역 변수를 직접 사용할 수 없기 때문이다. 이 번거로움을 Step 3에서 제거한다.

</details>

**확인**: `deleteAll()`, `add(user)`가 동작하고 콘솔에 전체 삭제와 사용자 추가 내용이 출력되면 성공한다.

---

### Step 3. 익명 클래스로 리팩토링 (`UserDao.java`)

**목표**: 별도 클래스를 메서드 **안의 익명 클래스**로 옮긴다. `user`는 캡처하므로 생성자가 사라진다.

**할 일**

1. `deleteAll()`의 전략을 익명 클래스로 메서드 안에 인라인한다.
2. `add(user)`의 전략도 익명 클래스로 변경하고 `user`를 캡처하여 바로 사용한다. 이에 따라 `AddStrategy` 생성자 호출이 사라진다.

<details>
<summary>힌트 보기</summary>

```java
void deleteAll() {
    StatementStrategy strategy = new StatementStrategy() {
        @Override
        public void run(Database db) {
            db.getUsers().clear();
            System.out.println("  [전략-익명] 전체 삭제");
        }
    };

    context(strategy);
}
```

```java
void add(User user) {
    StatementStrategy strategy = new StatementStrategy() {
        @Override
        public void run(Database db) {
            db.getUsers().add(user);
            System.out.println("  [전략-익명] 추가: " + user.getName());
        }
    };

    context(strategy);
}
```

Step 2의 `AddStrategy` 클래스 파일과 `private final User user`, 생성자가 사라진다. 익명 클래스가 바깥의 `user`를 캡처하기 때문이다.

캡처되는 지역 변수는 `final`이거나 사실상 `final`이어야 한다.

</details>

**확인**: 별도 전략 클래스 없이도 같은 결과가 출력되면 성공한다. `add`에서 `new AddStrategy(...)`가 사라졌는지 확인한다.

---

### Step 4. 람다로 한 번 더 줄이기 (`UserDao.java`)

**목표**: 익명 클래스를 **람다**로 줄인다. `StatementStrategy`가 함수형 인터페이스이므로 가능하다.

**할 일**

1. Step 3의 익명 클래스를 람다로 변경한다. `new StatementStrategy() { ... run ... }`을 `db -> { ... }` 형태로 작성한다.

<details>
<summary>힌트 보기</summary>

```java
void deleteAll() {
    context(db -> {
        db.getUsers().clear();
        System.out.println("  [전략-람다] 전체 삭제");
    });
}
```

```java
void add(User user) {
    context(db -> {
        db.getUsers().add(user);
        System.out.println("  [전략-람다] 추가: " + user.getName());
    });
}
```

`new StatementStrategy() { public void run(Database db) { ... } }`에서 클래스 생성, 메서드 이름, 타입이 생략되고 `db -> { ... }`만 남는다.

</details>

**확인**: 람다 버전이 익명 클래스 버전과 동일하게 동작하면 성공한다.

---

### Step 5. Main에서 실행하기

**목표**: 세 방식이 모두 같은 컨텍스트 흐름을 사용하는지 확인한다.

**할 일**

1. `Database`, `UserDao`를 만든다.
2. `deleteAll`, `add(김)`, `add(이)`를 호출하고 마지막에 사용자 목록을 출력한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        UserDao dao = new UserDao(db);

        dao.deleteAll();
        dao.add(new User("u1", "김"));
        dao.add(new User("u2", "이"));

        System.out.println("\n현재 사용자 수: " + db.getUsers().size());

        for (User u : db.getUsers()) {
            System.out.println("사용자: " + u.getName());
        }
    }
}
```

어느 스타일을 사용하더라도 연결 열기 → 전략 실행 → 연결 닫기 흐름은 동일하다.

</details>

**확인**: `javac *.java` → `java -Dstdout.encoding=UTF-8 Main`을 실행한 뒤 1번 섹션과 같이 컨텍스트 흐름이 반복되고 사용자 두 명이 출력되면 완성이다.

## 6. 학습 체크

* [ ] 컨텍스트와 전략이 각각 무엇을 담당하는지 설명할 수 있다
* [ ] 컨텍스트가 인터페이스에만 의존하는 구조가 OCP와 어떤 관련이 있는지 설명할 수 있다
* [ ] 별도 클래스 전략이 `user`를 생성자로 받아야 했던 이유를 설명할 수 있다
* [ ] 익명 클래스가 지역 변수를 캡처하여 생성자가 필요하지 않은 이유를 설명할 수 있다
* [ ] 함수형 인터페이스를 람다로 줄일 수 있는 이유를 설명할 수 있다

## 7. 최종 완성 체크리스트

* [ ] `context`가 `StatementStrategy`에만 의존한다
* [ ] 별도 클래스 → 익명 클래스 → 람다의 세 가지 버전을 작성했다
* [ ] 익명 클래스와 람다 버전에서 `new AddStrategy(...)`가 사라졌다
* [ ] 세 방식 모두 열기 → 전략 실행 → 닫기 흐름이 동일하다
* [ ] 사용자 추가와 삭제 결과가 올바르게 출력된다

## 8. 선택 도전 과제

1. `get(id)` 전략을 추가하여 아이디로 사용자 한 명을 찾는다. 컨텍스트는 그대로 두고 전략만 새로 만들 수 있는지 확인한다.
2. `deleteAll`처럼 **캡처할 값이 없는** 전략은 별도 클래스, 익명 클래스, 람다 중 어떤 방식이 가장 적절한지 비교한다.
3. 람다 본문이 한 줄이면 중괄호도 생략할 수 있다. `context(db -> db.getUsers().clear())` 형태로 변경한다.
4. 원래 JDBC 코드처럼 전략이 **무엇을 할지를 만들어 반환**하고 컨텍스트가 **실행**하도록 역할을 나눈다. `StatementStrategy`가 작업 객체를 반환하고 컨텍스트가 이를 실행하도록 변경한다.
5. 컨텍스트를 `UserDao` 밖의 **별도 클래스인 `JdbcContext`** 로 분리하고 `UserDao`가 이를 주입받아 사용하도록 변경한다.
