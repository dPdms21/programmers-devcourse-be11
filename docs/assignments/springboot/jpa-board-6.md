# JPA 게시판 6 - 품질 높이기

> JPA 기반 게시판 과제에서 AOP 로깅과 계층별 테스트를 적용한다.
> 새 기능을 추가하기보다, 지금까지 구현한 게시판 기능이 정상 동작하는지 확인하고 유지보수할 수 있는 기반을 만든다.
> 컨트롤러 요청은 AOP로 자동 로깅하고, 핵심 로직은 서비스, 리포지토리, 컨트롤러 테스트로 검증한다.

---

## 0. 먼저 알아둘 점

이 과제는 1편부터 5편까지의 회원가입, 로그인, 게시글 목록 조회, 상세 조회, 글쓰기, 수정, 삭제, 파일 기능이 완료되었다는 전제로 진행한다.

이번 단계에서 작성할 요소는 다음과 같다.

* 컨트롤러 요청 로깅을 담당하는 `LoggingAspect`
* 테스트용 H2 설정
* 서비스 단위 테스트
* 리포지토리 슬라이스 테스트
* 컨트롤러 슬라이스 테스트

이번 단계의 목표는 화면을 매번 직접 조작하지 않아도 코드가 주요 로직을 자동으로 검증하도록 만드는 것이다. 테스트는 이후 리팩터링이나 기능 수정 시 기존 동작이 깨졌는지 확인하는 안전망 역할을 한다.

AOP 의존성은 별도로 추가하지 않는다. 현재 프로젝트는 `spring-boot-starter-data-jpa`를 사용하고 있고, 관련 의존성을 통해 AOP에 필요한 요소가 함께 포함되어 있으므로 `@Aspect`를 사용할 수 있다.

테스트 도구는 Spring Boot 테스트 스타터에 포함되어 있다. JUnit 5, Mockito, AssertJ, MockMvc는 별도 추가 없이 사용할 수 있다. 다만 리포지토리 테스트에서 인메모리 DB를 사용하기 위해 H2는 테스트 런타임 의존성으로 추가한다.

---

## 1. 무엇을 만드는가?

이번 단계에서는 컨트롤러 요청을 자동으로 로깅하고, 회원 기능의 핵심 로직을 테스트로 검증한다.

구현할 내용은 다음과 같다.

| 항목                        | 역할                  | 계층과 도구                 |
| ------------------------- | ------------------- | ---------------------- |
| `LoggingAspect`           | 컨트롤러 메서드 실행 전후 로깅   | AOP                    |
| `MemberServiceTest`       | 회원가입과 로그인 서비스 로직 검증 | Mockito                |
| `MemberRepositoryTest`    | 회원 조회 쿼리 메서드 검증     | `@DataJpaTest`         |
| `MemberApiControllerTest` | HTTP 요청과 응답 형식 검증   | `@WebMvcTest`, MockMvc |

동작 예시는 다음과 같다.

```text
[요청 로깅]
브라우저에서 게시글 목록 요청
→ 콘솔에 컨트롤러 메서드 시작 로그 출력
→ 실제 컨트롤러 메서드 실행
→ 콘솔에 컨트롤러 메서드 종료 로그와 실행 시간 출력

[테스트 실행]
./gradlew test
→ 서비스 테스트 통과
→ 리포지토리 테스트 통과
→ 컨트롤러 테스트 통과
→ BUILD SUCCESSFUL
```

---

## 2. 학습 목표

| 개념                                   | 학습 위치          |
| ------------------------------------ | -------------- |
| AOP로 공통 관심사 분리                       | Step 1, Step 2 |
| `@Aspect`, `@Pointcut`, `@Around` 사용 | Step 1, Step 2 |
| `ProceedingJoinPoint`로 대상 메서드 실행     | Step 2         |
| 테스트용 H2 인메모리 DB 설정                   | Step 3         |
| Mockito로 서비스 단위 테스트 작성               | Step 4         |
| `@DataJpaTest`로 리포지토리 테스트 작성         | Step 5         |
| `@WebMvcTest`와 MockMvc로 컨트롤러 테스트 작성  | Step 6         |

---

## 3. 핵심 개념

### (1) AOP와 공통 관심사

로깅은 여러 컨트롤러에서 반복될 수 있는 공통 작업이다. 각 컨트롤러 메서드마다 직접 로그 코드를 작성하면 핵심 로직과 부가 로직이 섞이고, 변경할 때도 여러 곳을 수정해야 한다.

AOP는 이런 공통 관심사를 별도 클래스로 분리하고, 지정한 대상 메서드 실행 전후에 자동으로 끼워 넣는 방식이다.

| 용어                    | 의미                              |
| --------------------- | ------------------------------- |
| `@Aspect`             | 공통 관심사를 모아둔 클래스                 |
| `@Pointcut`           | 공통 관심사를 적용할 대상 지정               |
| `@Around`             | 대상 메서드 실행 전후를 감싸는 방식            |
| `ProceedingJoinPoint` | 실제로 실행될 대상 메서드 정보와 실행 기능을 가진 객체 |

Spring은 대상 빈을 프록시로 감싸고, 요청이 들어오면 프록시가 먼저 실행된다. 프록시는 로깅 같은 부가 작업을 수행한 뒤 `proceed()`로 실제 대상 메서드를 호출한다.

### (2) AOP와 `@RestControllerAdvice`

AOP와 `@RestControllerAdvice`는 모두 공통 처리를 분리하는 데 사용하지만, 적용 목적이 다르다.

| 상황                                   | 도구                      |
| ------------------------------------ | ----------------------- |
| 로깅, 실행 시간 측정처럼 여러 계층에 적용할 수 있는 범용 작업 | AOP                     |
| 예외를 HTTP 상태 코드와 응답 형식으로 변환하는 웹 계층 작업 | `@RestControllerAdvice` |

AOP는 메서드 실행 전후에 끼워 넣는 데 적합하다. `@RestControllerAdvice`는 컨트롤러에서 발생한 예외를 공통 HTTP 응답으로 변환하는 데 적합하다.

### (3) 테스트 피라미드

테스트는 범위에 따라 실행 속도와 검증 대상이 달라진다.

| 테스트 종류         | 도구                | 검증 대상      | Spring 컨텍스트   | 속도 |
| -------------- | ----------------- | ---------- | ------------- | -- |
| 서비스 단위 테스트     | Mockito           | 서비스 로직     | 사용하지 않음       | 빠름 |
| 리포지토리 슬라이스 테스트 | `@DataJpaTest`    | JPA 쿼리 메서드 | JPA 관련 일부만 로딩 | 보통 |
| 웹 슬라이스 테스트     | `@WebMvcTest`     | 컨트롤러 요청·응답 | 웹 관련 일부만 로딩   | 보통 |
| 통합 테스트         | `@SpringBootTest` | 전체 흐름      | 전체 로딩         | 느림 |

단위 테스트는 빠르게 핵심 로직을 검증하는 데 적합하다. 슬라이스 테스트는 필요한 계층만 로딩해 특정 계층의 동작을 확인하는 데 적합하다. 통합 테스트는 전체 흐름을 확인할 수 있지만 실행 비용이 크므로 필요한 경우에 제한적으로 사용한다.

### (4) Mockito

Mockito는 실제 의존 객체 대신 가짜 객체를 만들어 테스트하는 도구다.

서비스 테스트에서 실제 리포지토리를 사용하면 DB가 필요하고 테스트가 느려진다. 서비스 로직만 검증하려면 리포지토리와 매퍼를 Mock으로 대체하고, 특정 메서드가 호출될 때 어떤 값을 반환할지 미리 정한다.

자주 사용하는 방식은 다음과 같다.

| 코드                                       | 의미                   |
| ---------------------------------------- | -------------------- |
| `given(mock.method()).willReturn(value)` | Mock 메서드 호출 결과 지정    |
| `verify(mock).method()`                  | 특정 메서드가 호출되었는지 확인    |
| `verify(mock, never()).method()`         | 특정 메서드가 호출되지 않았는지 확인 |
| `any()`                                  | 어떤 인자든 허용            |

### (5) 슬라이스 테스트

슬라이스 테스트는 전체 애플리케이션이 아니라 필요한 계층만 로딩해 검증하는 테스트 방식이다.

`@DataJpaTest`는 JPA 관련 빈과 테스트 DB를 중심으로 실행된다. 리포지토리 쿼리 메서드가 실제 DB에서 의도대로 동작하는지 확인할 수 있다.

`@WebMvcTest`는 웹 계층을 중심으로 실행된다. 컨트롤러의 요청 경로, 상태 코드, JSON 응답, 예외 처리 결과를 확인할 수 있다. 서비스나 리포지토리는 로딩하지 않으므로 필요한 의존성은 Mock으로 대체한다.

---

## Step 1. AOP 적용 대상 정하기

컨트롤러 요청을 자동으로 로깅하기 위해 `LoggingAspect` 클래스를 작성한다.

작성할 내용은 다음과 같다.

* `aop/LoggingAspect` 클래스 생성
* `@Aspect` 적용
* `@Component` 적용
* 컨트롤러 패키지를 대상으로 하는 `@Pointcut` 작성

힌트는 다음과 같다.

```java
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.jpaboard.controller..*(..))")
    public void controllerLayer() {
    }
}
```

`@Aspect`는 이 클래스가 공통 관심사를 담는 Aspect임을 나타낸다. `@Component`는 Spring 빈으로 등록하기 위해 필요하다.

`execution(* com.example.jpaboard.controller..*(..))`의 의미는 다음과 같다.

| 표현                                   | 의미                               |
| ------------------------------------ | -------------------------------- |
| `*`                                  | 반환 타입 무관                         |
| `com.example.jpaboard.controller..*` | `controller` 패키지와 하위 패키지의 모든 클래스 |
| `(..)`                               | 파라미터 개수와 타입 무관                   |

`@Pointcut` 메서드는 대상 범위에 이름을 붙이는 역할을 한다. 이후 `@Around("controllerLayer()")`처럼 재사용할 수 있다.

---

## Step 2. `@Around`로 실행 전후 로깅하기

컨트롤러 메서드 실행 전후에 로그를 출력한다.

작성할 내용은 다음과 같다.

* `@Around("controllerLayer()")` 메서드 작성
* `ProceedingJoinPoint` 파라미터 사용
* 실행 전 시작 로그 출력
* `proceed()`로 실제 컨트롤러 메서드 실행
* 실행 후 종료 로그와 실행 시간 출력
* 대상 메서드의 반환값을 그대로 반환

힌트는 다음과 같다.

```java
@Around("controllerLayer()")
public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    String name = joinPoint.getSignature().toShortString();
    long start = System.currentTimeMillis();

    System.out.println("===> 시작: " + name);

    try {
        return joinPoint.proceed();
    } finally {
        long took = System.currentTimeMillis() - start;
        System.out.println("<=== 종료: " + name + " (" + took + "ms)");
    }
}
```

`joinPoint.proceed()`는 실제 대상 메서드를 실행한다. 이 호출을 누락하면 컨트롤러 메서드가 실행되지 않는다.

`finally` 블록에 종료 로그를 두면 대상 메서드에서 예외가 발생하더라도 종료 로그를 출력할 수 있다.

---

## Step 3. 테스트 환경 설정하기

리포지토리 테스트에서 운영 DB를 사용하지 않도록 테스트 전용 H2 인메모리 DB를 설정한다.

`build.gradle`에 H2 테스트 런타임 의존성을 추가한다.

```gradle
testRuntimeOnly 'com.h2database:h2'
```

`src/test/resources/application.yaml`을 작성한다.

```yaml
file:
  upload-dir: ./build/test-uploads

spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
  sql:
    init:
      mode: never
```

설정 의미는 다음과 같다.

| 설정                      | 의미                       |
| ----------------------- | ------------------------ |
| `file.upload-dir`       | 테스트에서 사용할 파일 저장 경로       |
| `jdbc:h2:mem:testdb`    | 테스트 JVM 안에서 동작하는 인메모리 DB |
| `MODE=MySQL`            | MySQL 문법 호환 모드           |
| `DB_CLOSE_DELAY=-1`     | 테스트 중 DB가 바로 닫히지 않도록 유지  |
| `ddl-auto: create-drop` | 테스트 시작 시 테이블 생성, 종료 시 삭제 |
| `sql.init.mode: never`  | 운영용 `data.sql` 실행 방지     |

H2 인메모리 DB는 테스트 실행 중에만 생성되고, 테스트가 끝나면 사라진다. 따라서 매번 깨끗한 상태에서 테스트를 실행할 수 있다.

---

## Step 4. 서비스 단위 테스트 작성하기

`MemberService`를 대상으로 서비스 로직을 테스트한다. 리포지토리와 매퍼는 실제 객체가 아니라 Mock으로 대체한다.

작성할 내용은 다음과 같다.

* `MemberServiceTest` 생성
* `@ExtendWith(MockitoExtension.class)` 적용
* `MemberRepository` Mock 생성
* `MemberMapper` Mock 생성
* `MemberService`에 Mock 주입
* 로그인 성공 테스트
* 로그인 실패 테스트
* 회원가입 성공 테스트
* 회원가입 중복 예외 테스트

테스트 구조는 다음과 같다.

```java
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberMapper memberMapper;

    @InjectMocks
    MemberService memberService;
}
```

로그인 성공 테스트 예시는 다음과 같다.

```java
@Test
void login_성공() {
    Member member = Member.builder()
            .userId("hong")
            .password("1234")
            .userName("홍길동")
            .build();

    given(memberRepository.findByUserId("hong"))
            .willReturn(Optional.of(member));

    LoginRequestDto request = new LoginRequestDto();
    request.setUsername("hong");
    request.setPassword("1234");

    Optional<Member> result = memberService.login(request);

    assertThat(result).isPresent();
}
```

회원 객체를 직접 만든 것과 Mock이 해당 객체를 반환하도록 설정하는 것은 별개다. `given()`으로 Mock 동작을 지정해야 서비스가 리포지토리 호출 결과를 받을 수 있다.

회원가입 중복 예외 테스트 예시는 다음과 같다.

```java
@Test
void join_중복이면_예외() {
    MemberJoinRequestDto request = new MemberJoinRequestDto();
    request.setUserId("hong");

    given(memberRepository.existsByUserId("hong"))
            .willReturn(true);

    assertThatThrownBy(() -> memberService.join(request))
            .isInstanceOf(DuplicateUserIdException.class);

    verify(memberRepository, never()).save(any());
}
```

중복 아이디가 존재하면 예외가 발생해야 하고, 저장 메서드는 호출되지 않아야 한다.

Mockito 기반 단위 테스트는 Spring 컨텍스트를 띄우지 않는다. 따라서 빠르게 실행되지만, Spring 프록시 기반 기능인 트랜잭션은 이 테스트에서 동작하지 않는다.

---

## Step 5. 리포지토리 테스트 작성하기

`MemberRepository`의 쿼리 메서드를 실제 DB에서 검증한다.

작성할 내용은 다음과 같다.

* `MemberRepositoryTest` 생성
* `@DataJpaTest` 적용
* `@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)` 적용
* 테스트 전 회원 데이터 저장
* `existsByUserId()` 검증
* `findByUserId()` 검증

테스트 구조는 다음과 같다.

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(Member.builder()
                .userId("hong")
                .password("1234")
                .userName("홍길동")
                .build());
    }
}
```

아이디 존재 여부 테스트는 다음과 같다.

```java
@Test
void existsByUserId_존재하면_true() {
    assertThat(memberRepository.existsByUserId("hong")).isTrue();
    assertThat(memberRepository.existsByUserId("nobody")).isFalse();
}
```

아이디 조회 테스트는 다음과 같다.

```java
@Test
void findByUserId_있으면_회원() {
    assertThat(memberRepository.findByUserId("hong")).isPresent();
    assertThat(memberRepository.findByUserId("nobody")).isEmpty();
}
```

`@DataJpaTest`는 JPA 관련 부품만 로딩한다. 각 테스트는 기본적으로 트랜잭션 안에서 실행되고 종료 후 롤백되므로 테스트 간 데이터가 섞이지 않는다.

---

## Step 6. 컨트롤러 테스트 작성하기

`MemberApiController`의 HTTP 요청과 응답을 MockMvc로 검증한다. 서비스는 Mock으로 대체한다.

작성할 내용은 다음과 같다.

* `MemberApiControllerTest` 생성
* `@WebMvcTest(MemberApiController.class)` 적용
* `MockMvc` 주입
* `MemberService`를 Mock으로 주입
* 회원가입 성공 응답 검증
* 회원가입 중복 예외 응답 검증

테스트 구조는 다음과 같다.

```java
@WebMvcTest(MemberApiController.class)
class MemberApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MemberService memberService;
}
```

회원가입 성공 테스트는 다음과 같다.

```java
@Test
void join_성공() throws Exception {
    String json = """
            {"userId":"newbie","password":"1234","userName":"새싹"}
            """;

    mockMvc.perform(post("/api/members/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("/members/login"));
}
```

회원가입 중복 예외 테스트는 다음과 같다.

```java
@Test
void join_중복이면_409() throws Exception {
    willThrow(new DuplicateUserIdException("이미 존재하는 아이디입니다."))
            .given(memberService).join(any());

    String json = """
            {"userId":"hong","password":"1234","userName":"홍길동"}
            """;

    mockMvc.perform(post("/api/members/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("이미 존재하는 아이디입니다."));
}
```

`@WebMvcTest`는 웹 계층만 로딩한다. 컨트롤러가 의존하는 서비스는 실제 구현체가 로딩되지 않으므로 `@MockitoBean`으로 Mock을 제공해야 한다.

`GlobalExceptionHandler`가 함께 로딩되면 컨트롤러에서 발생한 예외가 409 응답으로 변환되는지까지 검증할 수 있다.

---

## Step 7. 전체 테스트 실행하기

작성한 테스트를 전체 실행한다.

```bash
./gradlew test
```

확인할 내용은 다음과 같다.

* `MemberServiceTest`가 통과한다.
* `MemberRepositoryTest`가 통과한다.
* `MemberApiControllerTest`가 통과한다.
* 테스트 실패 없이 `BUILD SUCCESSFUL`이 출력된다.
* 브라우저로 컨트롤러 요청을 보냈을 때 콘솔에 시작 로그와 종료 로그가 출력된다.

---

## 완성 체크리스트

* [ ] `LoggingAspect` 클래스를 작성했다
* [ ] `LoggingAspect`에 `@Aspect`와 `@Component`를 적용했다
* [ ] 컨트롤러 패키지를 대상으로 하는 `@Pointcut`을 작성했다
* [ ] `@Around`에서 시작 로그와 종료 로그를 출력했다
* [ ] `proceed()`로 실제 대상 메서드를 실행했다
* [ ] `build.gradle`에 H2 테스트 런타임 의존성을 추가했다
* [ ] `src/test/resources/application.yaml`에 테스트 DB 설정을 작성했다
* [ ] `MemberServiceTest`를 작성했다
* [ ] Mockito로 리포지토리와 매퍼를 Mock 처리했다
* [ ] 로그인 성공과 실패를 검증했다
* [ ] 회원가입 성공과 중복 예외를 검증했다
* [ ] `MemberRepositoryTest`를 작성했다
* [ ] `@DataJpaTest`로 쿼리 메서드를 검증했다
* [ ] `MemberApiControllerTest`를 작성했다
* [ ] `@WebMvcTest`와 MockMvc로 HTTP 요청과 응답을 검증했다
* [ ] `./gradlew test` 실행 결과가 성공인지 확인했다
