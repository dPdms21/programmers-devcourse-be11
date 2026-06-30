# 추상화 + 데코레이터 패턴 — 알림 발송 시스템 만들기

## 학습 목표

이번 과제에서는 다음 두 가지를 직접 구현하며 확인한다.

1. **추상화**: 알림 채널을 이메일, 문자, 카카오톡으로 교체해도 클라이언트 코드는 변경되지 않는다.
2. **데코레이터**: 기존 발송 클래스를 수정하지 않고 로깅, 재시도, 시간 측정과 같은 부가 기능을 추가한다.

> 트랜잭션 예제인 `UserServiceTx`와 동일한 구조다. 같은 인터페이스를 구현하고, 같은 타입을 멤버로 보유한 뒤, 자신의 작업을 수행하고 실제 객체에 위임하는 구조를 새로운 도메인에 적용한다.

---

## 시나리오

서비스에서 사용자에게 알림을 발송한다. 처음에는 이메일만 사용했지만, 문자와 카카오톡으로도 알림을 보내야 한다.

운영팀에서는 다음과 같은 기능을 요구한다.

* 알림을 발송할 때마다 로그를 남긴다.
* 네트워크 오류로 발송에 실패하면 최대 3번까지 자동으로 재시도한다.
* 느린 알림 채널을 확인할 수 있도록 발송 시간을 밀리초 단위로 측정한다.

이러한 요구사항을 각 알림 채널 클래스에 직접 추가하지 않고 해결하는 것이 핵심이다.

---

## 제공되는 시작 코드

```java
// NotificationSender.java ── 공통 인터페이스 (수정 금지)
public interface NotificationSender {
    void send(String to, String message);
}
```

```java
// EmailNotificationSender.java ── 실제 구현체 예시 (그대로 사용)
public class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(String to, String message) {
        // 실제로는 메일 서버를 호출하지만, 과제에서는 콘솔 출력으로 대체한다.
        System.out.printf("[EMAIL] to=%s : %s%n", to, message);
    }
}
```

```java
// FlakyEmailSender.java ── 재시도 테스트용으로 처음 두 번은 실패한다. (그대로 사용)
public class FlakyEmailSender implements NotificationSender {
    private int attempt = 0;

    @Override
    public void send(String to, String message) {
        attempt++;

        if (attempt < 3) {
            throw new RuntimeException(
                    "일시적 네트워크 오류 (시도 " + attempt + ")"
            );
        }

        System.out.printf(
                "[EMAIL] (시도 %d 성공) to=%s : %s%n",
                attempt,
                to,
                message
        );
    }
}
```

---

## Part A — 추상화: 채널 교체하기

### A-1. 알림 채널 구현

`SmsNotificationSender`, `KakaoNotificationSender`를 만들고 `NotificationSender`를 구현한다.

출력 내용은 `EmailNotificationSender`와 동일한 형식을 사용하되 각각 `[SMS]`, `[KAKAO]`로 표시한다.

### A-2. NotificationService 작성

알림을 사용하는 클라이언트인 `NotificationService`를 작성한다.

이 클래스는 `EmailNotificationSender`, `SmsNotificationSender`, `KakaoNotificationSender`와 같은 구체 클래스를 직접 알지 못해야 한다. 오직 `NotificationSender` 인터페이스에만 의존한다.

```java
public class NotificationService {
    private final NotificationSender sender;

    public NotificationService(NotificationSender sender) {
        this.sender = sender;
    }

    public void notifyUser(String to, String message) {
        // TODO: sender를 이용해 알림을 발송한다.
    }
}
```

### A-3. 추상화의 효과 확인

`main`에서 `NotificationService`에 주입하는 구현체를 Email → SMS → Kakao 순서로 변경하며 실행한다.

`NotificationService` 코드를 변경하지 않은 상태에서 실제 알림 발송 채널만 변경되어야 한다.

> **체크포인트**: 구현체를 변경해도 클라이언트 코드가 변경되지 않는 것이 추상화의 효과다. 이것이 가능한 이유를 한 문장의 주석으로 작성한다.

---

## Part B — 데코레이터: 기존 클래스를 수정하지 않고 기능 추가하기

**Part A에서 만든 `EmailNotificationSender` 등의 실제 구현체는 수정하지 않는다.** 부가 기능은 모두 데코레이터를 통해 추가한다.

각 데코레이터는 다음 세 가지 요소를 만족해야 한다.

1. `NotificationSender`를 구현한다.
2. `NotificationSender` 타입의 객체를 멤버인 `delegate`로 가진다.
3. 자신의 작업을 수행한 뒤 `delegate.send(...)`를 호출하여 실제 발송 작업을 위임한다.

### B-1. 로깅 데코레이터

알림 발송 전과 후에 로그를 남긴다.

```java
public class LoggingNotificationSender implements NotificationSender {
    private final NotificationSender delegate;

    public LoggingNotificationSender(NotificationSender delegate) {
        this.delegate = delegate;
    }

    @Override
    public void send(String to, String message) {
        // TODO 1: 수신자를 포함한 "발송 시작" 로그를 남긴다.
        // TODO 2: delegate.send(to, message)를 호출해 실제 발송을 위임한다.
        // TODO 3: "발송 완료" 로그를 남긴다.
    }
}
```

### B-2. 재시도 데코레이터

알림 발송 과정에서 예외가 발생하면 최대 3회까지 재시도한다.

세 번 모두 실패하면 마지막 예외를 다시 던진다.

재시도 동작은 `FlakyEmailSender`를 감싸서 확인한다.

### B-3. 시간 측정 데코레이터

알림 발송에 걸린 시간을 밀리초 단위로 측정하여 출력한다.

시간 측정에는 `System.currentTimeMillis()` 또는 `System.nanoTime()`을 사용한다.

---

## Part C — 데코레이터 쌓기

데코레이터는 여러 겹으로 조합할 수 있다. 다음과 같이 알림 발송 객체를 조립하여 실행한다.

```java
NotificationSender sender =
        new TimingNotificationSender(
                new LoggingNotificationSender(
                        new RetryNotificationSender(
                                new FlakyEmailSender()
                        )
                )
        );

new NotificationService(sender)
        .notifyUser("user@test.com", "안녕하세요");
```

각 객체는 다음 역할을 담당한다.

```text
TimingNotificationSender
└── 전체 알림 발송 시간 측정
    LoggingNotificationSender
    └── 발송 시작과 완료 로그 기록
        RetryNotificationSender
        └── 실패 시 최대 3회까지 재시도
            FlakyEmailSender
            └── 실제 알림 발송
```

### C-1. 데코레이터 조합 실행

위 조합을 실행하여 로깅, 재시도, 시간 측정이 모두 동작하는지 확인한다.

### C-2. 데코레이터 순서 비교

`LoggingNotificationSender`와 `RetryNotificationSender`가 감싸는 순서를 서로 변경하여 실행 결과를 비교한다.

```text
Logging(Retry(...))
Retry(Logging(...))
```

다음 내용을 확인한다.

* 재시도 과정에서 로그가 한 번 출력되는지 세 번 출력되는지 확인한다.
* 데코레이터의 순서에 따라 로그 출력 횟수가 달라지는 이유를 설명한다.

> **체크포인트**: 데코레이터는 동일한 객체를 감싸더라도 조합 순서에 따라 동작의 의미와 범위가 달라진다.

---

## 도전 과제

### D-1. 속도 제한 데코레이터

1초에 한 건만 알림을 통과시키는 `RateLimitNotificationSender`를 추가한다.

제한을 초과한 호출은 다음 알림을 발송할 수 있을 때까지 대기시킨다.

### D-2. 조건부 발송 데코레이터

메시지에 특정 금칙어가 포함되어 있으면 실제 발송 객체에 위임하지 않고 알림을 차단하는 데코레이터를 추가한다.

이를 통해 데코레이터가 항상 실제 객체에 위임해야 하는 것은 아니라는 점을 확인한다.

### D-3. Spring DI로 조립

데코레이터 조합을 `@Configuration`과 `@Bean`을 사용한 Spring 설정으로 옮긴다.

알림 발송 구현체와 데코레이터 체인을 설정 클래스에서 조립하고, `NotificationService`에는 최종적으로 구성된 `NotificationSender`를 주입한다.
