# JPA로 회원가입 만들기 (계층 구조와 예외 공통화)

> Spring Boot로 회원가입 기능을 구현하며 웹 애플리케이션의 계층 구조, JPA 저장 흐름, 예외 공통 처리 방식을 학습한다.
> 회원가입 기능은 컨트롤러, 서비스, 리포지토리 계층을 분리하여 구현하고, JPA를 사용해 회원 정보를 DB에 저장한다.
> 중복 아이디와 같은 예외 상황은 컨트롤러마다 처리하지 않고 공통 예외 처리 클래스로 관리한다.
>
> 각 Step의 힌트는 접혀 있다. 먼저 각 계층의 역할을 생각하며 직접 구현한 뒤, 필요한 경우 힌트를 확인한다.
>
> 이번 과제는 Spring Security를 아직 학습하지 않은 단계이므로 비밀번호를 평문으로 저장한다. 실무에서는 반드시 암호화가 필요하다.

---

## 0. 먼저 알아둘 점

* 이 과제는 Spring Boot 프로젝트가 이미 세팅되어 있다는 전제로 진행한다.
* 제공된 설정과 파일은 다음과 같다.

  * `build.gradle` 의존성: `spring-boot-starter-data-jpa`, `spring-boot-starter-web`, `spring-boot-starter-thymeleaf`, `mysql-connector-j`, `lombok`
  * `application.yaml`의 DB 접속 정보와 JPA 설정
  * 회원가입 화면인 `sign-up.html`
  * 회원가입 요청을 보내는 `signUp.js`
  * 회원가입 폼을 보여주는 뷰 컨트롤러인 `GET /members/join`
* 실행하려면 MySQL 서버가 실행 중이어야 하고, `java_basic` DB가 존재해야 한다.
* `member` 테이블은 Step 1에서 JPA가 엔티티 기준으로 생성하도록 설정한다.
* 작성할 백엔드 계층은 엔티티, 리포지토리, DTO, 매퍼, 서비스, 컨트롤러, 예외 처리다.

프론트엔드의 `signUp.js`는 다음 요청과 응답 형식을 기준으로 동작한다.

* 요청: `POST /api/members/join`
* 요청 본문:

```json id="cgxb1t"
{
  "userId": "...",
  "password": "...",
  "userName": "..."
}
```

* 성공 응답:

```json id="g6k5mm"
{
  "url": "/members/login"
}
```

* 실패 응답:

```json id="vwobpu"
{
  "message": "..."
}
```

중복 아이디인 경우 상태 코드는 `409 Conflict`로 응답한다.

---

## 1. 무엇을 만드는가?

회원가입 폼에 아이디, 비밀번호, 이름을 입력하고 가입 버튼을 누르면 회원 정보가 DB의 `member` 테이블에 저장된다. 가입이 성공하면 로그인 페이지로 이동하고, 이미 존재하는 아이디라면 경고 메시지를 표시한다.

| 주소와 메서드                  | 역할        | 계층       |
| ------------------------ | --------- | -------- |
| `GET /members/join`      | 회원가입 폼 조회 | 뷰 컨트롤러   |
| `POST /api/members/join` | 회원가입 처리   | API 컨트롤러 |

**동작 예시**

```text id="dl9a4l"
[가입 성공]
아이디: newbie
비밀번호: 1234
이름: 새싹

가입 요청
→ member 테이블에 새 회원 저장
→ 로그인 페이지로 이동
```

```text id="038cf2"
[가입 실패]
아이디: hong

가입 요청
→ 이미 존재하는 아이디 확인
→ "이미 존재하는 아이디입니다." 메시지 표시
→ 페이지 이동 없음
```

---

## 2. 학습 목표

| 개념                                             | 학습 위치   |
| ---------------------------------------------- | ------- |
| 컨트롤러, 서비스, 리포지토리 계층으로 역할 분리                    | 전체 Step |
| JPA `@Entity`로 클래스와 테이블 매핑                     | Step 1  |
| `JpaRepository` 상속으로 기본 CRUD 사용                | Step 1  |
| 쿼리 메서드로 중복 아이디 조회                              | Step 1  |
| 요청 DTO와 응답 DTO 분리                              | Step 2  |
| DTO를 엔티티로 변환하는 매퍼 작성                           | Step 2  |
| 서비스 계층에 비즈니스 로직 작성                             | Step 3  |
| `@RestController`와 `@RequestBody`로 JSON API 구현 | Step 4  |
| `@RestControllerAdvice`로 예외 공통 처리              | Step 5  |

---

## 3. 핵심 개념

### (1) 계층 구조

요청은 다음 흐름으로 처리된다.

```text id="chffqb"
브라우저
    ↓ JSON 요청
Controller
    ↓ 서비스 호출
Service
    ↓ 저장 또는 조회 요청
Repository
    ↓ SQL 실행
DB
```

| 계층         | 책임             | 이번 과제에서의 역할         |
| ---------- | -------------- | ------------------- |
| Controller | HTTP 요청과 응답 처리 | 회원가입 요청을 받아 서비스에 전달 |
| Service    | 비즈니스 로직 처리     | 아이디 중복 검사 후 저장 요청   |
| Repository | DB 접근          | 회원 저장과 아이디 존재 여부 조회 |

계층을 분리하면 각 클래스의 책임이 명확해진다. 화면 요청 처리, 비즈니스 판단, DB 접근을 한 클래스에 모두 작성하지 않아도 되므로 수정과 테스트가 쉬워진다.

### (2) `JpaRepository`

```java id="pqdfz7"
public interface MemberRepository extends JpaRepository<Member, Long> {
}
```

`JpaRepository`를 상속하면 직접 구현 클래스를 만들지 않아도 기본 CRUD 기능을 사용할 수 있다.

```text id="7e2moh"
JpaRepository<Member, Long>
```

위 선언은 `Member` 엔티티를 기본키 타입 `Long`으로 관리하는 리포지토리라는 의미다.

`save`, `findById`, `findAll`, `delete`, `count`, `existsById` 같은 기본 메서드는 상속만으로 사용할 수 있다.

조건 조회가 필요한 경우 메서드 이름 규칙으로 쿼리를 만들 수 있다.

```java id="zyohjm"
boolean existsByUserId(String userId);
```

위 메서드는 `userId` 값이 존재하는지 확인하는 쿼리로 해석된다.

### (3) DTO와 Entity 분리

| 구분     | Entity              | DTO                                             |
| ------ | ------------------- | ----------------------------------------------- |
| 역할     | DB 테이블과 매핑          | 요청 또는 응답 데이터 전달                                 |
| 예시     | `Member`            | `MemberJoinRequestDto`, `MemberJoinResponseDto` |
| 포함 데이터 | DB 저장에 필요한 필드       | API에서 필요한 필드                                    |
| 사용 위치  | Repository, Service | Controller, Service                             |

엔티티를 API 요청과 응답에 그대로 사용하면 DB 구조가 외부로 노출될 수 있다. 특히 비밀번호와 같은 민감한 값이 포함될 수 있으므로 DTO를 따로 사용한다.

### (4) 예외 공통 처리

서비스 계층에서 중복 아이디가 발견되면 예외를 던진다.

```java id="6014sa"
throw new DuplicateUserIdException("이미 존재하는 아이디입니다.");
```

컨트롤러는 예외를 직접 `try-catch`로 처리하지 않는다. 대신 `@RestControllerAdvice`가 예외를 가로채 상태 코드와 응답 DTO로 변환한다.

이를 통해 컨트롤러는 성공 흐름에 집중하고, 예외 응답 형식은 한 곳에서 관리할 수 있다.

---

## Step 1. 회원 엔티티와 리포지토리 만들기

DB의 `member` 테이블과 매핑되는 `Member` 엔티티와 저장·조회 기능을 담당하는 `MemberRepository`를 만든다.

* `domain/entity/Member`: `@Entity`로 테이블과 매핑한다.
* `domain/repository/MemberRepository`: `JpaRepository`를 상속한다.
* `existsByUserId(String userId)`: 아이디 중복 검사용 쿼리 메서드다.
* `application.yaml`의 `ddl-auto`를 `update`로 설정하면 엔티티 기준으로 테이블이 생성된다.

<details>
<summary>힌트 보기</summary>

```java id="0v7ee2"
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String userId;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(nullable = false, length = 10)
    private String userName;
}
```

```java id="iuwvm3"
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUserId(String userId);
}
```

필드 이름은 Java 코드에서 `userId`처럼 카멜 케이스로 작성한다. DB 컬럼은 JPA 설정에 따라 `user_id`처럼 스네이크 케이스로 매핑될 수 있다.

</details>

---

## Step 2. 요청 DTO, 응답 DTO, 매퍼 만들기

화면에서 전달된 JSON을 받을 요청 DTO, 성공 응답으로 반환할 응답 DTO, 요청 DTO를 엔티티로 변환하는 매퍼를 만든다.

* `dto/MemberJoinRequestDto`: 회원가입 요청 데이터를 담는다.
* `dto/MemberJoinResponseDto`: 회원가입 성공 후 이동할 URL을 담는다.
* `mapper/MemberMapper`: 요청 DTO를 `Member` 엔티티로 변환한다.

<details>
<summary>힌트 보기</summary>

```java id="xfo6ky"
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberJoinRequestDto {

    private String userId;
    private String password;
    private String userName;
}
```

```java id="5xaf9a"
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberJoinResponseDto {

    private String url;
}
```

```java id="0ysx5d"
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toEntity(MemberJoinRequestDto request) {
        return Member.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .userName(request.getUserName())
                .build();
    }
}
```

`@RequestBody`로 JSON을 DTO에 매핑하려면 기본 생성자와 setter가 필요하다.

매퍼를 분리하면 컨트롤러나 서비스가 DTO를 엔티티로 변환하는 세부 작업을 직접 담당하지 않아도 된다.

</details>

---

## Step 3. 서비스에서 회원가입 로직 만들기

회원가입의 핵심 비즈니스 로직을 `MemberService`에 작성한다.

`join()` 메서드는 아이디 중복 여부를 확인하고, 중복이 아니면 회원 정보를 저장한다. 중복이라면 `DuplicateUserIdException`을 던진다.

* `@Service`: 서비스 계층의 Bean으로 등록한다.
* `@RequiredArgsConstructor`: 필요한 의존성을 생성자 주입으로 받는다.
* `@Transactional`: DB 변경 작업을 하나의 트랜잭션으로 처리한다.

<details>
<summary>힌트 보기</summary>

```java id="1ngl14"
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public void join(MemberJoinRequestDto request) {
        if (memberRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateUserIdException("이미 존재하는 아이디입니다.");
        }

        Member member = memberMapper.toEntity(request);
        memberRepository.save(member);
    }
}
```

`DuplicateUserIdException`은 Step 5에서 작성한다. Step 5까지 구현하면 예외 클래스 관련 오류가 사라진다.

</details>

---

## Step 4. 컨트롤러에서 회원가입 API 만들기

프론트엔드가 호출하는 `POST /api/members/join` API를 작성한다.

JSON 요청 본문을 `@RequestBody`로 받아 서비스에 전달하고, 가입 성공 시 로그인 페이지 URL을 응답한다.

* `@RestController`: JSON 응답을 반환하는 컨트롤러로 등록한다.
* `@RequestMapping("/api/members")`: 공통 API 경로를 지정한다.
* `@PostMapping("/join")`: 회원가입 요청 경로를 지정한다.
* `@RequestBody`: 요청 본문의 JSON을 DTO로 변환한다.

<details>
<summary>힌트 보기</summary>

```java id="bmd4ns"
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/join")
    public MemberJoinResponseDto join(@RequestBody MemberJoinRequestDto request) {
        memberService.join(request);
        return new MemberJoinResponseDto("/members/login");
    }
}
```

`@RestController`는 `@Controller`와 `@ResponseBody`를 합친 역할을 한다. 반환한 DTO는 JSON으로 변환되어 응답 본문에 담긴다.

`@RequestBody`는 요청 본문이 JSON일 때 사용한다.

</details>

---

## Step 5. 예외 공통 처리로 중복 아이디 응답 만들기

Step 3에서 던진 `DuplicateUserIdException`을 `409 Conflict` 상태 코드와 메시지 JSON으로 변환한다.

컨트롤러마다 `try-catch`를 작성하지 않고, 공통 예외 처리 클래스에서 한 번에 처리한다.

* `exception/DuplicateUserIdException`: 중복 아이디 상황을 나타내는 예외다.
* `dto/ErrorResponseDto`: 오류 응답 데이터를 담는다.
* `exception/GlobalExceptionHandler`: 예외를 HTTP 응답으로 변환한다.

<details>
<summary>힌트 보기</summary>

```java id="r8ix1g"
public class DuplicateUserIdException extends RuntimeException {

    public DuplicateUserIdException(String message) {
        super(message);
    }
}
```

```java id="zauy4d"
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private int status;
    private String message;
}
```

```java id="l2w9cl"
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserIdException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateUserId(
            DuplicateUserIdException e
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage()
                ));
    }
}
```

`409 Conflict`는 요청 자체는 이해했지만 현재 서버의 상태와 충돌하여 처리할 수 없다는 의미다. 이미 존재하는 아이디와 충돌하는 회원가입 요청에 적절하다.

상태 코드를 `200 OK`가 아닌 `409 Conflict`로 지정해야 하므로 `ResponseEntity`를 사용한다.

</details>

---

## Step 6. 화면에서 통합 실행 확인하기

모든 계층을 구현한 뒤 실제 화면에서 회원가입 흐름을 확인한다.

1. 애플리케이션을 실행한다.
2. 브라우저에서 `GET /members/join`으로 접속한다.
3. 새 아이디로 회원가입을 진행한다.
4. 로그인 페이지로 이동하면 성공이다.
5. MySQL에서 `SELECT * FROM member;`로 저장 여부를 확인한다.
6. 같은 아이디로 다시 회원가입을 시도한다.
7. `"이미 존재하는 아이디입니다."` 메시지가 표시되고 페이지가 이동하지 않으면 성공이다.

<details>
<summary>잘 안 될 때 체크리스트</summary>

| 증상              | 확인할 내용                                                                                         |
| --------------- | ---------------------------------------------------------------------------------------------- |
| 404 발생          | 컨트롤러 경로가 `POST /api/members/join`인지 확인한다                                                       |
| 415 또는 400 발생   | 요청 DTO에 기본 생성자와 setter가 있는지 확인한다                                                               |
| 중복 요청에서 500 발생  | `@RestControllerAdvice`와 `@ExceptionHandler(DuplicateUserIdException.class)`가 올바르게 작성되었는지 확인한다 |
| DB에 저장되지 않음     | `application.yaml`의 DB 접속 정보와 `ddl-auto: update` 설정을 확인한다                                      |
| 화면 메시지가 표시되지 않음 | 실패 응답이 `409` 상태 코드와 `message` 필드를 포함하는지 확인한다                                                   |

</details>

---

## 완성 체크리스트

* [ ] `Member` 엔티티를 작성했다
* [ ] `MemberRepository`에 `existsByUserId`를 작성했다
* [ ] `MemberJoinRequestDto`와 `MemberJoinResponseDto`를 작성했다
* [ ] `MemberMapper`를 작성했다
* [ ] `MemberService.join()`에서 중복 검증과 저장을 처리했다
* [ ] `MemberApiController`에서 `POST /api/members/join`을 처리했다
* [ ] `DuplicateUserIdException`을 작성했다
* [ ] `GlobalExceptionHandler`에서 중복 아이디 예외를 `409`로 처리했다
* [ ] 화면에서 가입 성공과 중복 실패 흐름을 모두 확인했다
