# 예외 처리 3전략 + 파일 로깅 만들기 (재시도 복구 & 바탕화면 로그 쌓기)

> 예외를 다루는 세 가지 방법인 **복구 / 회피 / 전환**을 실제 코드로 익히고, 그 과정을 **바탕화면의 로그 파일에 누적 기록**한다.
> 특히 **재시도(retry)로 일시적 오류를 복구**하고 각 시도를 로그로 남기는 것이 이번 과제의 중심이다.
> **아래 Step을 순서대로 진행하면 마지막 Step에서 바탕화면에 로그가 쌓이는 프로그램이 완성된다.**
>
> 각 Step의 힌트는 접혀 있다. 먼저 예외를 어떻게 처리하는 것이 적절한지 고민하고, 막히면 펼쳐 확인한다.

---

## 0. 먼저 알아둘 점

* 예외 처리의 큰 원칙은 **모든 예외는 복구되거나 분명히 통보되어야 한다는 것**이다. 단순히 `catch`로 잡고 아무것도 하지 않는 **예외 블랙홀은 사용하지 않는다.**
* 이번 과제에서는 이 원칙을 지키는 세 가지 방법을 코드로 구현한다. ① **복구**(재시도) ② **회피**(호출자에게 넘김) ③ **전환**(의미 있는 예외로 변경)
* **로그는 파일로 남긴다.** 바탕화면에 `app-logs` 폴더를 만들고 그 안의 `app.log`에 한 줄씩 **이어 쓴다(append).** 프로그램을 다시 실행해도 기존 내용이 지워지지 않고 계속 누적된다.
* **바탕화면 경로에 주의한다.** 일반적으로 `사용자홈/Desktop`이지만, 한글 Windows나 OneDrive 환경에서는 위치가 다르거나 `OneDrive/바탕 화면` 등으로 리디렉션될 수 있다. 폴더가 보이지 않으면 `logger.getLogFilePath()`로 **실제 저장 경로를 출력하여 확인한다.**
* 파일 입출력에서는 `try-with-resources`를 통한 자동 `close`와 체크 예외인 `IOException`, `SQLException`을 사용한다.

---

## 1. 무엇을 만드는가?

세 가지 예외 처리 전략을 실행하고 그 결과를 콘솔과 **로그 파일** 양쪽에 남기는 프로그램을 완성한다.

**콘솔 출력**

```text
===== 1) 예외 복구: 재시도 (3번째에 성공) =====
최종 결과: 데이터-OK

===== 2) 예외 복구 실패: 재시도 모두 실패 -> 통보 =====
실패 통보: 재시도 3회 모두 실패했습니다.

===== 3) 예외 전환: 아이디 중복 -> 의미 있는 예외 =====
잡힘: 이미 존재하는 아이디입니다: kim
원인 보존: java.sql.SQLException: Duplicate entry

===== 로그 파일 위치 =====
C:\Users\사용자\Desktop\app-logs\app.log
```

**바탕화면 `app-logs/app.log` 내용**

```text
2026-06-24 08:43:15 [WARN] 1번째 시도 실패: 일시적 연결 오류 (호출 1)
2026-06-24 08:43:15 [WARN] 2번째 시도 실패: 일시적 연결 오류 (호출 2)
2026-06-24 08:43:15 [INFO] 3번째 시도 성공: 데이터-OK
2026-06-24 08:43:15 [WARN] 1번째 시도 실패: 일시적 연결 오류 (호출 1)
2026-06-24 08:43:15 [WARN] 2번째 시도 실패: 일시적 연결 오류 (호출 2)
2026-06-24 08:43:15 [WARN] 3번째 시도 실패: 일시적 연결 오류 (호출 3)
2026-06-24 08:43:15 [ERROR] 재시도 3회 모두 실패
2026-06-24 08:43:15 [ERROR] 아이디 중복: kim
```

핵심은 **재시도로 복구되는 과정인 `WARN → WARN → INFO`와 해당 기록이 파일에 그대로 누적되는 모습을 확인하는 것**이다.

## 2. 학습 목표

| 개념                      | 학습 위치                       |
| ----------------------- | --------------------------- |
| 파일 로거: 폴더 생성과 append 누적 | Step 1 (`FileLogger.java`)  |
| 예외 복구: 재시도(retry)와 통보   | Step 2 (`DataService.java`) |
| 예외 회피: `throws`와 다시 던지기 | Step 3 (`DataService.java`) |
| 예외 전환: 의미 있는 예외와 원인 보존  | Step 4 (`DataService.java`) |
| 전체 실행과 바탕화면 로그 확인       | Step 5 (`Main.java`)        |

## 3. 핵심 개념

### (1) 예외 처리 3전략 한눈에 보기

| 전략              | 한 줄 설명                   | 핵심                                 |
| --------------- | ------------------------ | ---------------------------------- |
| 복구(recovery)    | 문제를 해결해 **정상 흐름으로 되돌린다** | 재시도, 대체값 사용. `catch` 후 무시는 복구가 아니다 |
| 회피(avoidance)   | **호출한 쪽으로 넘긴다**          | `throws` 또는 잡았다가 `throw e`         |
| 전환(translation) | **더 적절한 예외로 바꾸어 던진다**    | 원인(cause)을 함께 전달한다                 |

### (2) 예외 복구 = 재시도

일시적인 네트워크 오류 등은 다시 시도하면 성공할 수 있다. 정해진 횟수만큼 재시도하고, 성공하면 **정상값을 반환하여 복구**한다. 끝까지 실패하면 **예외를 던져 분명히 통보**한다.

```text
시도 1 실패(WARN) → 시도 2 실패(WARN) → 시도 3 성공(INFO) → 정상 반환
시도 1~3 모두 실패 → ERROR 기록 → 예외 throw
```

### (3) 예외 전환 = 의미 부여 + 원인 보존

저수준의 `SQLException`을 호출자가 이해할 수 있는 의미 있는 예외로 변경한다. 이때 **원래 예외를 cause로 반드시 전달해야 스택 트레이스가 보존된다.**

```java
catch (SQLException e) {
    throw new DuplicateUserIdException(id, e);
}
```

### (4) 파일 로깅 3요소

```text
① 폴더 생성    logDir.mkdirs();
② 이어쓰기     new FileWriter(logFile, true)
③ 자동 닫기    try (FileWriter fw = ...) { ... }
```

```text
복구 = 되돌리기
회피 = 넘기기
전환 = 바꾸기 + 원인 보존
로그 = mkdirs + append
```

## 4. 파일 구조

| 파일                  | 역할                                |
| ------------------- | --------------------------------- |
| `FileLogger.java`   | 바탕화면 폴더에 로그를 누적 기록                |
| `FlakyService.java` | 정해진 횟수만큼 실패하는 가짜 서비스이며 재시도 시연에 사용 |
| `DataService.java`  | 세 가지 예외 처리 전략과 사용자 정의 예외          |
| `Main.java`         | 시나리오를 실행하는 진입점                    |

> 실행: `javac *.java` → `java -Dstdout.encoding=UTF-8 Main`

---

## 5. Step by Step

### Step 1. 바탕화면에 로그를 쌓는 FileLogger 만들기 (`FileLogger.java`)

**목표**: 바탕화면에 `app-logs` 폴더를 만들고 `app.log`에 **이어 쓰는** 로거를 만든다.

**할 일**

1. 생성자에서 `사용자홈/Desktop/app-logs/app.log` 경로를 설정한다. `System.getProperty("user.home")`을 사용한다.
2. `log(level, message)`에서 폴더가 없으면 `mkdirs()`로 생성하고, `시간 [레벨] 메시지`를 append 모드로 한 줄 작성한다.
3. 실제 저장 경로를 반환하는 `getLogFilePath()`도 만든다.

<details>
<summary>힌트 보기</summary>

```java
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class FileLogger {
    private final File logDir;
    private final File logFile;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    FileLogger() {
        String home = System.getProperty("user.home");
        this.logDir = new File(home, "Desktop/app-logs");
        this.logFile = new File(logDir, "app.log");
    }

    void log(String level, String message) {
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        String line = LocalDateTime.now().format(FMT)
                + " [" + level + "] "
                + message
                + System.lineSeparator();

        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(line);
        } catch (IOException e) {
            System.out.println("로그 기록 실패: " + e.getMessage());
        }
    }

    String getLogFilePath() {
        return logFile.getAbsolutePath();
    }
}
```

`FileWriter(logFile, true)`의 `true`가 핵심이다. 이를 생략하면 기존 내용을 덮어써서 마지막 기록만 남는다. `try-with-resources`를 사용하면 작업이 끝날 때 파일이 자동으로 닫힌다.

</details>

**확인**: `new FileLogger().log("INFO", "테스트")`를 한 번 호출했을 때 바탕화면에 `app-logs/app.log`가 생성되고 해당 기록이 들어 있으면 성공이다. 파일이 보이지 않으면 `getLogFilePath()`로 실제 경로를 확인한다.

---

### Step 2. 예외 복구 — 재시도하고 로그 남기기 (`FlakyService.java`, `DataService.java`)

**목표**: 일시적 오류를 **정해진 횟수만큼 재시도**하고, 성공하면 복구하며 끝까지 실패하면 통보한다. 각 시도는 로그로 남긴다.

`FlakyService`는 `failTimes`번 실패한 뒤 성공한다.

```java
// FlakyService.java
class FlakyService {
    private final int failTimes;
    private int callCount = 0;

    FlakyService(int failTimes) {
        this.failTimes = failTimes;
    }

    String fetch() throws java.sql.SQLException {
        callCount++;

        if (callCount <= failTimes) {
            throw new java.sql.SQLException(
                    "일시적 연결 오류 (호출 " + callCount + ")"
            );
        }

        return "데이터-OK";
    }
}
```

**할 일**

1. `DataService`가 `FileLogger`를 필드로 전달받도록 생성자 주입을 적용한다.
2. `fetchWithRetry(FlakyService flaky)`를 만들고 최대 세 번 반복하며 `flaky.fetch()`를 호출한다.
3. 성공하면 `INFO` 로그를 남기고 **결과를 반환**한다.
4. 실패하면 `WARN` 로그를 남기고 다시 시도한다.
5. 세 번 모두 실패하면 `ERROR` 로그를 남기고 `RuntimeException`을 던져 통보한다.

<details>
<summary>힌트 보기</summary>

```java
import java.sql.SQLException;

class DataService {
    private final FileLogger logger;

    DataService(FileLogger logger) {
        this.logger = logger;
    }

    String fetchWithRetry(FlakyService flaky) {
        int maxRetry = 3;

        for (int attempt = 1; attempt <= maxRetry; attempt++) {
            try {
                String result = flaky.fetch();
                logger.log(
                        "INFO",
                        attempt + "번째 시도 성공: " + result
                );
                return result;
            } catch (SQLException e) {
                logger.log(
                        "WARN",
                        attempt + "번째 시도 실패: " + e.getMessage()
                );
            }
        }

        logger.log("ERROR", "재시도 " + maxRetry + "회 모두 실패");

        throw new RuntimeException(
                "재시도 " + maxRetry + "회 모두 실패했습니다."
        );
    }
}
```

`return`이 `try` 안에 있는 것이 중요하다. 성공하는 순간 메서드가 종료되므로 더 이상 재시도하지 않는다.

단순히 `catch`에서 예외를 잡고 넘어가기만 하면 예외 블랙홀이 된다. 이 코드에서는 성공하면 결과를 반환하고, 모두 실패하면 예외를 던져 원칙을 지킨다.

</details>

**확인**: `FlakyService(2)`를 전달했을 때 로그가 `WARN`, `WARN`, `INFO` 순서로 남고 `데이터-OK`가 반환되면 성공이다. `FlakyService(99)`를 전달했을 때 `WARN` 세 번과 `ERROR`가 기록된 후 예외가 던져지면 성공이다.

---

### Step 3. 예외 회피 — 호출자에게 넘기기 (`DataService.java`)

**목표**: 자신이 처리하지 않고 **호출한 쪽으로 예외를 넘기는** 두 가지 방식을 익힌다.

**할 일**

1. `throws`를 사용해 그대로 넘기는 메서드를 만든다. 예: `void avoidByThrows(FlakyService f) throws SQLException`
2. 예외를 잡아서 **로그만 남긴 뒤 다시 던지는** 메서드를 만든다. `throw e`로 원래 예외를 그대로 던진다.

<details>
<summary>힌트 보기</summary>

```java
// (a) throws로 그대로 넘김
void avoidByThrows(FlakyService f) throws SQLException {
    f.fetch();
}

// (b) 로그 등 부가 작업 후 다시 던짐
void avoidByRethrow(FlakyService f) throws SQLException {
    try {
        f.fetch();
    } catch (SQLException e) {
        logger.log(
                "WARN",
                "회피: 여기서 처리하지 않고 호출자에게 넘김 - "
                        + e.getMessage()
        );
        throw e;
    }
}
```

회피는 해당 예외를 현재 메서드보다 호출한 쪽에서 처리하는 것이 적절하다는 분명한 이유가 있을 때 사용한다. 이유 없이 모든 예외를 `throws`로 넘기는 것은 적절하지 않다.

</details>

**확인**: 두 메서드에서 예외가 발생했을 때 현재 메서드에서 처리하지 않고 호출자에게 전달되면 성공이다. `avoidByRethrow()`는 예외를 다시 던지기 전에 로그가 한 줄 기록되어야 한다.

---

### Step 4. 예외 전환 — 의미 있는 예외로 바꾸기 (`DataService.java`)

**목표**: 저수준 `SQLException`을 호출자가 이해할 수 있는 **업무적 의미의 예외**로 바꾸어 던진다. 이때 **원인을 반드시 보존한다.**

**할 일**

1. `RuntimeException`을 상속한 `DuplicateUserIdException`을 만들고 **원인(cause)을 전달받는 생성자**를 작성한다.
2. `registerUser(id)`에서 `insertUser(id)`를 호출한다. 중복이면 SQLState가 `23000`인 `SQLException`이 발생한다.
3. SQLState가 `23000`이면 `ERROR` 로그를 남긴 뒤 `DuplicateUserIdException`으로 전환하여 던진다. 이때 원래 예외인 `e`를 함께 전달한다.

<details>
<summary>힌트 보기</summary>

```java
void registerUser(String id) {
    try {
        insertUser(id);
    } catch (SQLException e) {
        if ("23000".equals(e.getSQLState())) {
            logger.log("ERROR", "아이디 중복: " + id);
            throw new DuplicateUserIdException(id, e);
        }

        logger.log("ERROR", "회원 저장 중 DB 오류: " + id);
        throw new RuntimeException("회원 저장 중 DB 오류", e);
    }
}

void insertUser(String id) throws SQLException {
    throw new SQLException("Duplicate entry", "23000");
}

static class DuplicateUserIdException extends RuntimeException {
    DuplicateUserIdException(String id, Throwable cause) {
        super("이미 존재하는 아이디입니다: " + id, cause);
    }
}
```

`super(메시지, cause)`로 원인을 전달하는 것이 핵심이다. 이를 통해 이후 `e.getCause()`로 원래 `SQLException`을 확인할 수 있다.

Spring이 `SQLException`을 `DataAccessException`으로 전환하는 것도 같은 방식이다.

</details>

**확인**: `registerUser("kim")`을 호출했을 때 `DuplicateUserIdException`이 발생하고, 예외를 잡아 `getCause()`를 확인했을 때 원래 `SQLException`이 들어 있으면 성공이다.

---

### Step 5. Main에서 실행하고 바탕화면 로그 확인하기 (`Main.java`)

**목표**: 세 가지 전략을 실행하고 바탕화면에 로그가 **누적되는 것**을 직접 확인한다.

**할 일**

1. `FileLogger`와 `DataService`를 생성한다.
2. 복구 전략에서는 `FlakyService(2)`로 재시도 후 성공하는 경우와 `FlakyService(99)`로 모두 실패하는 경우를 각각 실행한다. 실패한 경우에는 `try-catch`로 예외를 받아 메시지를 출력한다.
3. 전환 전략에서는 `registerUser("kim")`을 `try-catch`로 실행하고 메시지와 `getCause()`를 출력한다.
4. 마지막에 `logger.getLogFilePath()`를 출력하고 해당 파일을 직접 열어 로그가 누적되었는지 확인한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {
    public static void main(String[] args) {
        FileLogger logger = new FileLogger();
        DataService service = new DataService(logger);

        try {
            System.out.println(
                    "최종 결과: "
                            + service.fetchWithRetry(new FlakyService(2))
            );
        } catch (RuntimeException e) {
            System.out.println("실패 통보: " + e.getMessage());
        }

        try {
            service.fetchWithRetry(new FlakyService(99));
        } catch (RuntimeException e) {
            System.out.println("실패 통보: " + e.getMessage());
        }

        try {
            service.registerUser("kim");
        } catch (DataService.DuplicateUserIdException e) {
            System.out.println("잡힘: " + e.getMessage());
            System.out.println("원인 보존: " + e.getCause());
        }

        System.out.println(
                "로그 파일: " + logger.getLogFilePath()
        );
    }
}
```

프로그램을 두 번 실행한 뒤 로그 파일을 확인한다. 기존 기록이 지워지지 않고 계속 누적되면 append가 정상적으로 적용된 것이다.

</details>

**확인**: `javac *.java` → `java -Dstdout.encoding=UTF-8 Main`을 실행한 뒤 바탕화면의 `app-logs/app.log`에 1번 섹션과 같은 로그가 누적되어 있으면 완성이다.

## 6. 학습 체크

* [ ] 예외 처리의 세 가지 전략인 복구, 회피, 전환을 각각 설명할 수 있다
* [ ] `catch` 후 예외를 무시하는 것이 복구가 아닌 이유를 설명할 수 있다
* [ ] 재시도에서 성공하면 반환하고 모두 실패하면 통보하는 흐름을 작성할 수 있다
* [ ] 예외 전환 시 원인(cause)을 보존하는 이유를 설명할 수 있다
* [ ] `FileWriter(file, true)`의 `true`가 의미하는 내용을 설명할 수 있다

## 7. 최종 완성 체크리스트

* [ ] 바탕화면에 `app-logs` 폴더와 `app.log` 파일이 생성된다
* [ ] 프로그램을 다시 실행해도 로그가 덮어쓰이지 않고 누적된다
* [ ] 재시도 성공 시 `WARN → INFO`, 모두 실패 시 `WARN → ERROR`가 기록된다
* [ ] `registerUser`에서 `DuplicateUserIdException`으로 전환되고 cause가 보존된다
* [ ] 콘솔에서 로그 파일의 실제 경로를 확인할 수 있다

## 8. 선택 도전 과제

1. 로그 레벨을 선택적으로 제외하는 기능을 추가한다. 예를 들어 `minLevel`을 두고 해당 레벨보다 낮은 로그는 파일에 기록하지 않는다.
2. 재시도 사이에 잠시 대기하는 **백오프**를 추가한다. `Thread.sleep(attempt * 200)`을 사용하면 시도 횟수가 증가할수록 대기 시간도 길어진다.
3. 로그 파일을 날짜별로 분리한다. 예를 들어 `app-2026-06-24.log`처럼 파일명에 현재 날짜를 포함한다.
4. `fetchWithRetry`에서 모든 재시도가 실패하면 마지막 `SQLException`을 **원인으로 보존**하여 던지도록 변경한다. 복구와 전환을 함께 적용하는 방식이다.
5. 회피와 전환의 차이를 한 문단으로 정리한다. 어떤 경우에 예외를 그대로 넘기고, 어떤 경우에 의미 있는 예외로 바꾸어 던지는지를 중심으로 정리한다.
