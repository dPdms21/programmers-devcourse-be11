# 싱글톤 레지스트리와 무상태 익히기

> 서버 환경에서는 하나의 서비스 객체를 여러 스레드가 동시에 공유한다.
>
> 싱글톤 객체가 요청별 데이터를 필드에 저장하면 다른 스레드가 해당 값을 덮어써 데이터가 섞일 수 있다.
>
> 이 과제에서는 싱글톤 서비스의 상태 공유 문제를 재현하고, 요청 데이터를 파라미터와 지역변수로 처리하는 무상태 구조로 개선한다.
>
> 아래 Step을 순서대로 진행하면 멀티스레드 환경에서도 안전하게 사용할 수 있는 서비스 객체를 완성할 수 있다.

---

## 0. 먼저 알아둘 점

이 과제는 싱글톤 패턴의 심화 과정이다.

기본 싱글톤을 구성하는 `private` 생성자, `static` 인스턴스, `getInstance()`의 역할을 이해하고 있다고 가정한다.

웹 서버는 여러 사용자의 요청을 동시에 처리한다. 일반적으로 각 요청은 별도의 스레드에서 실행되며, 여러 스레드가 동일한 서비스 객체를 공유할 수 있다.

이 과정에서 다음 두 가지를 이해해야 한다.

1. 요청마다 서비스 객체를 새로 생성하면 객체 생성과 가비지 컬렉션 비용이 증가한다.
2. 하나의 객체를 공유할 때 요청별 데이터를 필드에 저장하면 다른 스레드의 데이터와 섞일 수 있다.

따라서 서버에서 공유하는 서비스 객체는 가능한 한 무상태로 설계해야 한다.

무상태 객체는 요청별 데이터를 인스턴스 필드에 저장하지 않고 파라미터와 지역변수로 처리한다.

Spring의 애플리케이션 컨텍스트는 객체를 생성하고 관리하는 IoC 컨테이너이며, 기본적으로 빈 객체를 싱글톤으로 관리하는 싱글톤 레지스트리 역할도 담당한다.

---

## 1. 무엇을 만드나요?

하나의 싱글톤 서비스를 여러 스레드가 동시에 사용할 때 발생하는 문제를 확인하고, 무상태 구조로 개선한다.

최종 실행 결과는 다음과 같다.

```text
===== 같은 싱글톤을 30개 스레드가 동시에 사용 =====
[필드에 저장] 데이터 엉킴: 29건 / 30건
[파라미터로] 데이터 엉킴: 0건 / 30건

===== 필드에 저장할 수 있는 값 =====
kim 조회 [DB연결]
lee 조회 [DB연결]
같은 DAO인가? true
```

데이터 엉킴 건수는 스레드 실행 순서에 따라 달라질 수 있다.

핵심은 요청 데이터를 필드에 저장한 서비스에서는 잘못된 결과가 발생할 수 있지만, 파라미터와 지역변수만 사용하는 서비스에서는 데이터가 섞이지 않는다는 점이다.

---

## 2. 학습 목표

| 개념                | 학습 위치                               |
| ----------------- | ----------------------------------- |
| 서버에서 객체를 공유하는 이유  | 핵심 개념 3.1                           |
| 서비스 객체와 싱글톤 공유 구조 | 핵심 개념 3.2                           |
| 상태를 가진 싱글톤의 문제    | Step 1 (`GreetingServiceBad.java`)  |
| 무상태 서비스 구현        | Step 2 (`GreetingServiceGood.java`) |
| 필드에 저장할 수 있는 값 구분 | Step 3 (`Dao.java`)                 |
| 멀티스레드 환경에서 결과 비교  | Step 4 (`Main.java`)                |
| Spring의 싱글톤 레지스트리 | 핵심 개념 3.6                           |

---

## 3. 핵심 개념

### 3.1 서버에서 객체를 공유하는 이유

요청을 처리할 때마다 여러 객체를 새로 생성하면 짧은 시간에 많은 객체가 만들어진다.

요청 하나를 처리하는 데 객체 다섯 개가 필요하다고 가정하면 다음과 같다.

```text
요청 1건
→ 객체 5개 생성

초당 요청 500건
→ 초당 객체 2,500개 생성

1분
→ 객체 150,000개 생성

1시간
→ 객체 9,000,000개 생성
```

객체 생성과 제거가 반복되면 메모리 사용량과 가비지 컬렉션 부담이 증가한다.

따라서 요청마다 달라지는 상태가 없고 기능만 제공하는 객체는 하나만 생성해 여러 요청이 공유하도록 구성할 수 있다.

이러한 객체를 서비스 객체라고 한다.

---

### 3.2 서비스 객체

서비스 객체는 사용자별 상태를 저장하기보다 특정 기능을 제공하는 역할을 담당한다.

예를 들어 사용자 조회, 주문 계산, 메시지 전송과 같은 기능을 수행할 수 있다.

```text
요청 스레드 A ─┐
요청 스레드 B ─┼─→ 하나의 서비스 객체
요청 스레드 C ─┘
```

Java 웹 환경의 서블릿도 일반적으로 하나의 객체를 여러 요청 스레드가 동시에 사용한다.

여러 요청이 같은 객체를 공유하므로 서비스 객체는 멀티스레드 환경을 고려해 설계해야 한다.

---

### 3.3 상태를 가진 싱글톤의 문제

싱글톤 객체의 인스턴스 필드는 모든 스레드가 공유한다.

요청별 데이터를 필드에 저장하면 다른 스레드가 해당 값을 덮어쓸 수 있다.

```text
공유 필드: name

스레드 A
→ name = "철수"

스레드 B
→ name = "영희"

스레드 A
→ name 반환
→ "영희"
```

스레드 A는 `"철수"`를 저장했지만 값을 반환하기 전에 스레드 B가 `"영희"`로 변경했다.

따라서 스레드 A는 자신이 전달한 값과 다른 결과를 받게 된다.

이처럼 여러 스레드가 공유 데이터에 동시에 접근해 실행 결과가 달라지는 문제를 레이스 컨디션이라고 한다.

---

### 3.4 무상태 객체

무상태 객체는 요청별 데이터를 인스턴스 필드에 저장하지 않는다.

요청 데이터는 메서드의 파라미터로 전달하고, 처리 중 발생하는 값은 지역변수에 저장한다.

```java
String greet(String name) {
    return name;
}
```

파라미터와 지역변수는 각 스레드의 호출 스택에 별도로 생성된다.

따라서 다른 스레드가 값을 직접 변경할 수 없다.

```text
스레드 A의 스택
→ name = "철수"

스레드 B의 스택
→ name = "영희"
```

동일한 싱글톤 객체의 메서드를 호출하더라도 각 호출의 파라미터와 지역변수는 서로 분리된다.

---

### 3.5 필드에 저장할 수 있는 값

모든 필드를 제거해야 하는 것은 아니다.

요청마다 변경되지 않고 여러 스레드가 안전하게 공유할 수 있는 값은 필드에 저장할 수 있다.

| 구분           | 예                                 | 필드 사용          |
| ------------ | --------------------------------- | -------------- |
| 상수           | 변경되지 않는 설정값                       | 가능             |
| 불변 객체        | 생성 후 상태가 변하지 않는 객체                | 가능             |
| 협력 객체 참조     | `UserDao`가 참조하는 `ConnectionMaker` | 가능             |
| 요청별 사용자 정보   | `userId`, 사용자 이름                  | 사용하지 않음        |
| 처리 중간 값      | 합계, 임시 계산 결과                      | 사용하지 않음        |
| 변경 가능한 공유 상태 | 공용 카운터, 현재 사용자                    | 동기화 없이 사용하지 않음 |

협력 객체를 필드에 저장할 때도 해당 객체가 스레드 안전한지 확인해야 한다.

---

### 3.6 싱글톤 레지스트리

Spring의 애플리케이션 컨텍스트는 객체를 생성하고 의존관계를 설정하며 관리한다.

별도의 범위 설정이 없다면 Spring 빈은 기본적으로 싱글톤 범위로 관리된다.

```text
ApplicationContext

GreetingService ─→ 인스턴스 1개
UserDao ─────────→ 인스턴스 1개
ConnectionMaker ─→ 인스턴스 1개
```

애플리케이션 코드에서 직접 `private` 생성자와 `getInstance()`를 작성하지 않아도 컨테이너가 하나의 객체를 생성하고 재사용한다.

이처럼 싱글톤 객체를 생성하고 보관하며 반환하는 기능을 싱글톤 레지스트리라고 한다.

Spring이 관리하는 싱글톤 빈도 여러 스레드가 공유할 수 있으므로 무상태로 설계하는 것이 중요하다.

---

### 3.7 서버 싱글톤 구조 정리

```text
서비스 객체
→ 하나만 생성
→ 여러 요청 스레드가 공유

요청별 데이터
→ 필드에 저장하지 않음
→ 파라미터와 지역변수로 처리

공유해야 하는 협력 객체
→ 필드로 참조 가능
→ 스레드 안전성 확인 필요
```

---

## 4. 파일 구조

| 파일                         | 역할                                    |
| -------------------------- | ------------------------------------- |
| `GreetingServiceBad.java`  | 요청 데이터를 필드에 저장하는 상태 기반 서비스            |
| `GreetingServiceGood.java` | 파라미터와 지역변수를 사용하는 무상태 서비스              |
| `Dao.java`                 | 협력 객체는 필드에 저장하고 요청 데이터는 파라미터로 처리하는 예제 |
| `Main.java`                | 여러 스레드로 서비스를 호출하고 결과를 비교하는 진입점        |

실행 명령은 다음과 같다.

```bash
javac *.java
java -Dstdout.encoding=UTF-8 Main
```

---

## 5. Step by Step

### Step 1. 요청 데이터를 필드에 저장해 문제 재현하기 (`GreetingServiceBad.java`)

**목표**: 싱글톤 서비스가 요청 데이터를 인스턴스 필드에 저장할 때 멀티스레드 환경에서 데이터가 섞이는 문제를 확인한다.

**할 일**

1. `GreetingServiceBad`를 싱글톤으로 구현한다.
2. 요청 이름을 저장하는 `name` 필드를 작성한다.
3. `greet()`에서 전달받은 이름을 필드에 저장한다.
4. 다른 스레드가 실행될 수 있도록 잠시 대기한다.
5. 필드에 저장된 값을 반환한다.
6. 여러 스레드에서 서로 다른 이름으로 동시에 호출한다.

<details>
<summary>힌트 보기</summary>

```java
class GreetingServiceBad {

    private static final GreetingServiceBad instance =
            new GreetingServiceBad();

    private String name;

    private GreetingServiceBad() {
    }

    static GreetingServiceBad getInstance() {
        return instance;
    }

    String greet(String requestName) {
        this.name = requestName;

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return this.name;
    }
}
```

`name`은 싱글톤 객체에 하나만 존재하는 인스턴스 필드다.

모든 스레드가 같은 필드를 읽고 쓰기 때문에 한 스레드가 저장한 값을 다른 스레드가 덮어쓸 수 있다.

`Thread.sleep(5)`는 다른 스레드가 실행될 가능성을 높여 문제를 쉽게 확인하기 위한 코드다.

`InterruptedException`이 발생하면 인터럽트 상태를 복구하기 위해 다음 코드를 사용한다.

```java
Thread.currentThread().interrupt();
```

</details>

**확인**

* 여러 스레드가 동시에 `greet()`를 호출했을 때 반환값이 요청값과 다른 경우가 발생하는지 확인한다.
* 데이터 엉킴 건수가 실행마다 달라질 수 있는지 확인한다.
* 인스턴스 필드가 모든 스레드에 공유된다는 점을 설명할 수 있는지 확인한다.

---

### Step 2. 무상태 서비스로 개선하기 (`GreetingServiceGood.java`)

**목표**: 요청 데이터를 필드에 저장하지 않고 파라미터와 지역변수만 사용하도록 개선한다.

**할 일**

1. `GreetingServiceGood`을 싱글톤으로 구현한다.
2. 요청 데이터를 저장하는 인스턴스 필드를 만들지 않는다.
3. `greet()`의 파라미터를 그대로 사용해 결과를 반환한다.
4. 여러 스레드에서 동시에 호출한다.
5. 반환값이 요청값과 같은지 확인한다.

<details>
<summary>힌트 보기</summary>

```java
class GreetingServiceGood {

    private static final GreetingServiceGood instance =
            new GreetingServiceGood();

    private GreetingServiceGood() {
    }

    static GreetingServiceGood getInstance() {
        return instance;
    }

    String greet(String requestName) {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return requestName;
    }
}
```

`requestName`은 메서드 파라미터이며 각 호출 스레드의 스택에 별도로 저장된다.

다른 스레드가 해당 값을 덮어쓸 수 없으므로 여러 요청이 동시에 실행되어도 데이터가 섞이지 않는다.

</details>

**확인**

* `GreetingServiceGood`에 요청 데이터를 저장하는 필드가 없는지 확인한다.
* 여러 스레드에서 실행했을 때 데이터 엉킴 건수가 `0`인지 확인한다.
* 파라미터와 지역변수가 스레드별로 분리되는 이유를 설명할 수 있는지 확인한다.

---

### Step 3. 필드에 저장할 수 있는 협력 객체 확인하기 (`Dao.java`)

**목표**: 요청마다 변경되지 않는 협력 객체는 필드로 참조할 수 있다는 점을 확인한다.

**할 일**

1. `ConnectionMaker` 인터페이스를 작성한다.
2. `SimpleConnectionMaker`를 싱글톤으로 구현한다.
3. `UserDao`를 싱글톤으로 구현한다.
4. `UserDao`가 `ConnectionMaker`를 필드로 참조하도록 한다.
5. 요청별 데이터인 `userId`는 `findUser()`의 파라미터로 전달한다.

<details>
<summary>힌트 보기</summary>

```java
interface ConnectionMaker {

    String makeConnection();
}
```

```java
class SimpleConnectionMaker
        implements ConnectionMaker {

    private static final SimpleConnectionMaker instance =
            new SimpleConnectionMaker();

    private SimpleConnectionMaker() {
    }

    static SimpleConnectionMaker getInstance() {
        return instance;
    }

    @Override
    public String makeConnection() {
        return "DB연결";
    }
}
```

```java
class UserDao {

    private static final UserDao instance =
            new UserDao();

    private final ConnectionMaker connectionMaker =
            SimpleConnectionMaker.getInstance();

    private UserDao() {
    }

    static UserDao getInstance() {
        return instance;
    }

    String findUser(String userId) {
        return userId
                + " 조회 ["
                + connectionMaker.makeConnection()
                + "]";
    }
}
```

`connectionMaker`는 요청별로 달라지는 값이 아니라 모든 요청에서 사용하는 협력 객체다.

따라서 필드에 참조를 저장할 수 있다.

반면 `userId`는 요청마다 달라지므로 필드가 아니라 파라미터로 전달한다.

</details>

**확인**

* `UserDao.getInstance()`가 항상 같은 객체를 반환하는지 확인한다.
* `connectionMaker`가 요청 도중 변경되지 않는지 확인한다.
* `userId`가 필드가 아닌 파라미터로 전달되는지 확인한다.
* 여러 사용자 ID를 전달했을 때 각 요청에 맞는 결과가 출력되는지 확인한다.

---

### Step 4. Main에서 멀티스레드 결과 비교하기 (`Main.java`)

**목표**: 상태 기반 서비스와 무상태 서비스를 같은 멀티스레드 조건에서 실행해 차이를 확인한다.

**할 일**

1. 여러 개의 스레드를 생성한다.
2. 각 스레드가 서로 다른 이름으로 `GreetingServiceBad`를 호출하도록 한다.
3. 요청한 이름과 반환된 이름이 다른 경우를 계산한다.
4. 동일한 방식으로 `GreetingServiceGood`을 실행한다.
5. 두 서비스의 데이터 엉킴 건수를 비교한다.
6. `UserDao`의 싱글톤 여부와 사용자별 조회 결과를 확인한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {

    private static int badMismatch;
    private static int goodMismatch;

    public static void main(String[] args)
            throws InterruptedException {

        int threadCount = 30;

        testBadService(threadCount);
        testGoodService(threadCount);

        System.out.println(
                "===== 같은 싱글톤을 "
                + threadCount
                + "개 스레드가 동시에 사용 ====="
        );

        System.out.println(
                "[필드에 저장] 데이터 엉킴: "
                + badMismatch
                + "건 / "
                + threadCount
                + "건"
        );

        System.out.println(
                "[파라미터로] 데이터 엉킴: "
                + goodMismatch
                + "건 / "
                + threadCount
                + "건"
        );

        System.out.println(
                "\n===== 필드에 저장할 수 있는 값 ====="
        );

        UserDao dao1 =
                UserDao.getInstance();

        UserDao dao2 =
                UserDao.getInstance();

        System.out.println(
                dao1.findUser("kim")
        );

        System.out.println(
                dao2.findUser("lee")
        );

        System.out.println(
                "같은 DAO인가? "
                + (dao1 == dao2)
        );
    }

    private static void testBadService(
            int threadCount
    ) throws InterruptedException {

        Thread[] threads =
                new Thread[threadCount];

        for (int i = 0;
             i < threadCount;
             i++) {

            final String requestName =
                    "손님" + i;

            threads[i] =
                    new Thread(() -> {
                        String result =
                                GreetingServiceBad
                                        .getInstance()
                                        .greet(
                                                requestName
                                        );

                        if (!result.equals(
                                requestName
                        )) {
                            synchronized (
                                    Main.class
                            ) {
                                badMismatch++;
                            }
                        }
                    });
        }

        startAndJoin(threads);
    }

    private static void testGoodService(
            int threadCount
    ) throws InterruptedException {

        Thread[] threads =
                new Thread[threadCount];

        for (int i = 0;
             i < threadCount;
             i++) {

            final String requestName =
                    "손님" + i;

            threads[i] =
                    new Thread(() -> {
                        String result =
                                GreetingServiceGood
                                        .getInstance()
                                        .greet(
                                                requestName
                                        );

                        if (!result.equals(
                                requestName
                        )) {
                            synchronized (
                                    Main.class
                            ) {
                                goodMismatch++;
                            }
                        }
                    });
        }

        startAndJoin(threads);
    }

    private static void startAndJoin(
            Thread[] threads
    ) throws InterruptedException {

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
```

각 스레드는 `start()`로 실행하고, 모든 스레드가 종료될 때까지 `join()`으로 대기한다.

`badMismatch`와 `goodMismatch`도 여러 스레드가 공유하므로 증가 연산을 `synchronized`로 보호한다.

</details>

**확인**

* 상태 기반 서비스의 데이터 엉킴 건수가 `0`보다 큰지 확인한다.
* 무상태 서비스의 데이터 엉킴 건수가 `0`인지 확인한다.
* `dao1 == dao2` 결과가 `true`인지 확인한다.
* `kim`, `lee` 요청이 각각 올바르게 출력되는지 확인한다.

데이터 엉킴 건수는 실행 환경과 스레드 실행 순서에 따라 달라질 수 있다.

---

## 6. 학습 체크

* [ ] 서버에서 서비스 객체를 요청마다 생성하지 않고 공유하는 이유를 설명할 수 있다.
* [ ] 서비스 객체가 기능 중심으로 설계되는 이유를 설명할 수 있다.
* [ ] 싱글톤 인스턴스 필드가 모든 스레드에 공유된다는 점을 이해했다.
* [ ] 요청별 데이터를 필드에 저장하면 데이터가 섞일 수 있는 이유를 설명할 수 있다.
* [ ] 레이스 컨디션이 무엇인지 설명할 수 있다.
* [ ] 파라미터와 지역변수가 스레드별로 분리되는 이유를 설명할 수 있다.
* [ ] 필드에 저장할 수 있는 값과 저장하면 안 되는 값을 구분할 수 있다.
* [ ] Spring의 애플리케이션 컨텍스트가 싱글톤 레지스트리 역할을 한다는 점을 이해했다.
* [ ] Spring 싱글톤 빈을 무상태로 설계해야 하는 이유를 설명할 수 있다.

---

## 7. 최종 완성 체크리스트

* [ ] `GreetingServiceBad`가 요청 데이터를 인스턴스 필드에 저장한다.
* [ ] `GreetingServiceBad`를 여러 스레드에서 실행했을 때 잘못된 결과가 발생한다.
* [ ] `GreetingServiceGood`에는 요청 데이터를 저장하는 인스턴스 필드가 없다.
* [ ] `GreetingServiceGood`은 요청 데이터를 파라미터와 지역변수로 처리한다.
* [ ] `GreetingServiceGood`의 데이터 엉킴 건수가 `0`이다.
* [ ] `UserDao`가 싱글톤으로 구현되어 있다.
* [ ] `UserDao`가 `ConnectionMaker`를 협력 객체 필드로 참조한다.
* [ ] 요청별 `userId`는 파라미터로 전달된다.
* [ ] `UserDao.getInstance()`의 반환값을 `==`로 비교했을 때 `true`다.
* [ ] 멀티스레드 비교 결과가 정상적으로 출력된다.

---

## 8. 선택 도전 과제

1. **레이스 컨디션 반복 확인**: `GreetingServiceBad`를 여러 번 실행하고 데이터 엉킴 건수가 실행마다 달라지는지 확인한다.
2. **번호 발급 동기화**: 기본 싱글톤 과제의 `TicketMachine.issue()`를 여러 스레드에서 호출하고 `lastNumber++`에서 발생할 수 있는 문제를 확인한다.
3. **공유 상태 구분**: 여러 요청이 공유해야 하는 상태와 요청별로 분리해야 하는 상태의 기준을 정리한다.
4. **싱글톤 레지스트리 구현**: IoC 컨테이너가 생성한 객체를 내부에 보관하고 요청할 때마다 동일한 객체를 반환하도록 개선한다.
5. **변경 가능한 협력 객체 확인**: 필드로 참조한 협력 객체가 내부 상태를 변경할 때 발생할 수 있는 문제를 조사한다.
