# JPA 게시판 2 - 로그인 만들기

> JPA 기반 게시판 과제에서 회원가입으로 저장한 회원 정보를 사용해 로그인 기능을 구현한다. 아이디와 비밀번호가 일치하면 서버 세션에 로그인 정보를 저장하고, 게시판 목록 페이지로 이동한다. 이 과정에서 값이 있을 수도 있고 없을 수도 있는 조회 결과를 `Optional`로 처리하고, 로그인 상태를 세션으로 유지하는 흐름을 학습한다.
>
> 이번 과제는 Spring Security를 아직 학습하지 않은 단계이므로 비밀번호를 평문으로 비교한다. 실무에서는 반드시 암호화된 값끼리 비교해야 한다.

---

## 0. 먼저 알아둘 점

이 과제는 1편 회원가입 기능이 완료되었다는 전제로 진행한다.

이미 작성되어 있어야 하는 파일은 다음과 같다.

* `Member` 엔티티
* `MemberRepository`
* `MemberService`
* `MemberApiController`

제공된 화면과 컨트롤러는 다음과 같다.

* 로그인 화면인 `sign-in.html`
* 로그인 요청을 보내는 `signIn.js`
* 로그인 폼을 보여주는 뷰 컨트롤러인 `GET /members/login`
* 로그인 성공 후 이동할 목록 페이지 컨트롤러인 `GET /`

이번 단계에서 작성할 백엔드 요소는 다음과 같다.

* 리포지토리의 회원 조회 메서드
* 로그인 요청 DTO
* 로그인 응답 DTO
* 세션 키 상수
* 서비스의 로그인 판정 로직
* 컨트롤러의 로그인 API와 세션 저장 로직

프론트엔드의 `signIn.js`는 다음 요청과 응답 형식을 기준으로 동작한다.

* 요청: `POST /api/members/login`
* 요청 형식: `application/json`
* 요청 본문:

```json
{
  "username": "...",
  "password": "..."
}
```

* 응답 본문:

```json
{
  "successed": true,
  "url": "/",
  "message": "..."
}
```

`successed` 값이 `true`이면 프론트엔드는 `url`에 담긴 `/` 경로로 이동한다. `successed` 값이 `false`이면 페이지를 이동하지 않고 메시지만 표시한다.

---

## 1. 무엇을 만드는가?

로그인 폼에 아이디와 비밀번호를 입력하고 로그인 버튼을 누르면, 서버가 회원 정보를 조회한다. 아이디와 비밀번호가 일치하면 세션에 로그인 정보를 저장하고 게시판 목록 페이지로 이동한다. 일치하지 않으면 안내 메시지를 표시하고 현재 페이지에 머문다.

| 주소와 메서드                   | 역할                  | 계층       |
| ------------------------- | ------------------- | -------- |
| `GET /members/login`      | 로그인 폼 조회            | 뷰 컨트롤러   |
| `POST /api/members/login` | 로그인 검증과 세션 저장       | API 컨트롤러 |
| `GET /`                   | 로그인한 사용자의 게시판 목록 조회 | 뷰 컨트롤러   |

동작 예시는 다음과 같다.

```text
[로그인 성공]
아이디: hong
비밀번호: 1234

로그인 요청
→ DB에서 hong 회원 조회
→ 비밀번호 일치 확인
→ 서버 세션에 userId=hong, userName=홍길동 저장
→ 게시판 목록 페이지로 이동
→ 목록 상단에 "홍길동님 환영합니다" 표시

[로그인 실패]
아이디: hong
비밀번호: 9999

로그인 요청
→ DB에서 hong 회원 조회
→ 비밀번호 불일치 확인
→ "아이디 또는 비밀번호가 일치하지 않습니다." 메시지 표시
→ 페이지 이동 없음
```

---

## 2. 학습 목표

| 개념                                      | 학습 위치  |
| --------------------------------------- | ------ |
| 쿼리 메서드로 1건 조회하고 `Optional` 반환           | Step 1 |
| 요청 DTO와 응답 DTO 분리                       | Step 2 |
| 정적 팩토리 메서드로 성공·실패 응답 생성                 | Step 2 |
| 세션 키를 상수로 관리                            | Step 3 |
| `Optional.filter()`로 로그인 성공·실패 판정       | Step 4 |
| `HttpSession`에 로그인 상태 저장                | Step 5 |
| `Optional.map()`과 `orElseGet()`으로 응답 분기 | Step 5 |
| 세션 기반 로그인 상태 확인                         | Step 6 |

---

## 3. 핵심 개념

### (1) 세션

HTTP는 기본적으로 상태를 저장하지 않는다. 같은 브라우저에서 여러 번 요청하더라도 서버는 별도의 장치가 없으면 이전 요청에서 로그인했는지 알 수 없다.

로그인 상태를 유지하려면 서버가 사용자 정보를 기억해야 한다. 이때 사용하는 방식이 세션이다.

```text
로그인 성공
→ 서버가 세션을 생성
→ 세션에 userId, userName 저장
→ 브라우저에는 JSESSIONID 전달

다음 요청
→ 브라우저가 JSESSIONID를 함께 전송
→ 서버가 JSESSIONID로 세션 조회
→ 로그인한 사용자 정보 확인
```

| 구분 | 저장 위치 | 브라우저가 가지는 값  | 용도                   |
| -- | ----- | ------------ | -------------------- |
| 세션 | 서버    | `JSESSIONID` | 로그인 상태와 같은 민감한 임시 정보 |

세션에 저장된 실제 사용자 정보는 서버에 있고, 브라우저는 세션을 찾기 위한 식별자만 가진다. 따라서 로그인 상태처럼 외부에 직접 노출되면 안 되는 정보를 관리할 때 사용한다.

### (2) Optional

로그인에서는 아이디에 해당하는 회원이 존재할 수도 있고, 존재하지 않을 수도 있다. 조회 결과가 없을 수 있는 상황을 `null`로 처리하면 `NullPointerException`이 발생하기 쉽다.

`Optional`은 값이 있을 수도 있고 없을 수도 있는 결과를 명시적으로 표현한다.

| 상태                    | 의미    |
| --------------------- | ----- |
| `Optional.of(member)` | 값이 있음 |
| `Optional.empty()`    | 값이 없음 |

자주 사용하는 메서드는 다음과 같다.

| 메서드             | 역할                               |
| --------------- | -------------------------------- |
| `filter(조건)`    | 값이 있고 조건이 참이면 유지하고, 조건이 거짓이면 비운다 |
| `map(변환)`       | 값이 있으면 다른 값으로 변환한다               |
| `orElseGet(함수)` | 값이 없을 때만 대체값을 생성한다               |

### (3) `orElse`와 `orElseGet`

`orElse`와 `orElseGet`은 값이 없을 때 대체값을 반환한다는 점은 같지만, 실행 시점이 다르다.

```java
.orElse(LoginResponseDto.fail())
.orElseGet(LoginResponseDto::fail)
```

`orElse()`는 값이 있어도 인자로 전달된 메서드가 먼저 실행된다. 반면 `orElseGet()`은 값이 없을 때만 전달된 함수를 실행한다.

로그인에 성공한 경우 실패 응답 객체를 만들 필요가 없으므로 `orElseGet(LoginResponseDto::fail)`을 사용하는 것이 적절하다.

`LoginResponseDto::fail`은 메서드를 즉시 실행하는 것이 아니라, 값이 없을 때 실행할 메서드 정보를 넘기는 메서드 참조다.

### (4) 정적 팩토리 메서드

정적 팩토리 메서드는 생성자를 직접 호출하지 않고 의미 있는 이름의 메서드로 객체를 생성하는 방식이다.

```java
LoginResponseDto.success();
LoginResponseDto.fail();
```

다음 코드처럼 생성자에 값을 직접 나열하면 각 인자가 무엇을 의미하는지 한눈에 파악하기 어렵다.

```java
new LoginResponseDto(true, "/", "로그인에 성공했습니다.");
```

반면 `success()`와 `fail()`처럼 이름을 가진 생성 메서드를 사용하면 성공 응답인지 실패 응답인지 명확하게 드러난다.

---

## Step 1. 아이디로 회원 조회하기

로그인하려면 입력된 아이디에 해당하는 회원을 DB에서 조회해야 한다. `MemberRepository`에 아이디 기준 조회 메서드를 추가한다.

작성할 내용은 다음과 같다.

* `MemberRepository`에 `findByUserId(String userId)` 추가
* 조회 결과가 없을 수 있으므로 반환 타입은 `Optional<Member>` 사용

힌트는 다음과 같다.

```java
// domain/repository/MemberRepository.java
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUserId(String userId);

    Optional<Member> findByUserId(String userId);
}
```

`findByUserId`는 Spring Data JPA의 쿼리 메서드 규칙을 사용한다. `find`는 조회, `By`는 조건 시작, `UserId`는 엔티티의 `userId` 필드를 의미한다.

따라서 위 메서드는 다음 조건 조회로 해석된다.

```sql
SELECT *
FROM member
WHERE user_id = ?
```

필드 이름은 DB 컬럼명이 아니라 엔티티의 필드명인 `userId`를 기준으로 작성한다.

---

## Step 2. 로그인 요청 DTO와 응답 DTO 만들기

로그인 요청으로 전달되는 아이디와 비밀번호를 담을 요청 DTO를 만든다. 로그인 결과를 프론트엔드에 전달할 응답 DTO도 만든다.

작성할 내용은 다음과 같다.

* `dto/LoginRequestDto`: 로그인 요청 데이터 저장
* `dto/LoginResponseDto`: 로그인 성공·실패 결과 저장
* `LoginResponseDto.success()`: 성공 응답 생성
* `LoginResponseDto.fail()`: 실패 응답 생성

힌트는 다음과 같다.

```java
// dto/LoginRequestDto.java
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto {

    private String username;
    private String password;
}
```

프론트엔드는 JSON의 키를 `username`, `password`로 전달한다. 따라서 요청 DTO의 필드명도 이에 맞춘다.

`@RequestBody`로 JSON을 DTO에 매핑하려면 Jackson이 객체를 생성하고 값을 주입할 수 있어야 한다. 이를 위해 기본 생성자와 setter가 필요하다.

```java
// dto/LoginResponseDto.java
@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private boolean successed;
    private String url;
    private String message;

    public static LoginResponseDto success() {
        return new LoginResponseDto(true, "/", "로그인에 성공했습니다.");
    }

    public static LoginResponseDto fail() {
        return new LoginResponseDto(false, null, "아이디 또는 비밀번호가 일치하지 않습니다.");
    }
}
```

`successed` 필드는 프론트엔드의 `signIn.js`가 성공과 실패를 구분할 때 사용하는 값이다. 필드명이 다르면 프론트엔드가 응답을 제대로 해석하지 못하므로 기존 약속을 지켜야 한다.

로그인 성공 시에는 게시판 목록으로 이동해야 하므로 `url`에 `/`를 담는다. 실패 시에는 페이지 이동이 없어야 하므로 `url`을 `null`로 둔다.

---

## Step 3. 세션 키 상수 만들기

세션에 값을 저장할 때 문자열 키를 직접 사용하면 오타로 인한 버그가 발생할 수 있다.

예를 들어 저장할 때는 `"userName"`을 사용하고, 꺼낼 때는 `"username"`을 사용하면 세션에 값이 있어도 조회 결과는 `null`이 된다.

이를 방지하기 위해 세션 키를 상수로 모은다.

작성할 내용은 다음과 같다.

* `constant/SessionConst` 클래스 생성
* `USER_ID`, `USER_NAME` 상수 선언
* 상수 클래스이므로 `private` 생성자로 인스턴스 생성을 막음

힌트는 다음과 같다.

```java
// constant/SessionConst.java
public class SessionConst {

    private SessionConst() {
    }

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
}
```

로그인 컨트롤러는 이 상수를 사용해 세션에 값을 저장한다. 목록 컨트롤러도 같은 상수를 사용해 세션 값을 조회해야 한다.

저장하는 쪽과 꺼내는 쪽이 같은 상수를 사용하면 문자열 오타로 인한 세션 조회 오류를 줄일 수 있다.

---

## Step 4. 서비스에서 로그인 판정 로직 만들기

`MemberService`에 로그인 판정 로직을 작성한다. 서비스는 아이디로 회원을 조회하고, 비밀번호가 일치하면 회원 정보를 반환한다. 아이디가 없거나 비밀번호가 다르면 빈 `Optional`을 반환한다.

작성할 내용은 다음과 같다.

* `login(LoginRequestDto request)` 메서드 추가
* 반환 타입은 `Optional<Member>`
* `findByUserId()` 결과에 `filter()`로 비밀번호 일치 조건 적용
* 조회 전용 로직이므로 `@Transactional(readOnly = true)` 사용

힌트는 다음과 같다.

```java
// service/MemberService.java
@Transactional(readOnly = true)
public Optional<Member> login(LoginRequestDto request) {
    return memberRepository.findByUserId(request.getUsername())
            .filter(member -> member.getPassword().equals(request.getPassword()));
}
```

처리 흐름은 다음과 같다.

```text
아이디로 회원 조회
→ 회원이 없으면 Optional.empty()
→ 회원이 있으면 비밀번호 비교
→ 비밀번호가 같으면 Optional<Member> 유지
→ 비밀번호가 다르면 Optional.empty()
```

`filter()`는 값이 있을 때만 조건을 검사한다. 조건이 참이면 기존 값을 유지하고, 조건이 거짓이면 빈 `Optional`로 만든다.

따라서 아이디가 존재하고 비밀번호까지 일치하는 경우에만 로그인 성공으로 판단할 수 있다.

이번 단계에서는 Spring Security를 사용하지 않으므로 비밀번호를 평문으로 비교한다. 실무에서는 평문 비밀번호를 저장하거나 비교하지 않고, 암호화된 값을 기준으로 검증해야 한다.

---

## Step 5. 컨트롤러에서 세션 저장과 응답 처리하기

프론트엔드가 호출하는 `POST /api/members/login` API를 작성한다. 서비스의 로그인 결과가 있으면 세션에 사용자 정보를 저장하고 성공 응답을 반환한다. 결과가 없으면 실패 응답을 반환한다.

작성할 내용은 다음과 같다.

* `MemberApiController`에 `login()` 메서드 추가
* `@PostMapping("/login")` 사용
* `@RequestBody LoginRequestDto`로 JSON 요청 받기
* `HttpSession`으로 세션 접근
* 로그인 성공 시 `SessionConst.USER_ID`, `SessionConst.USER_NAME` 저장
* `Optional.map()`과 `orElseGet()`으로 성공·실패 응답 분기

힌트는 다음과 같다.

```java
// controller/MemberApiController.java
@PostMapping("/login")
public LoginResponseDto login(@RequestBody LoginRequestDto request, HttpSession session) {
    return memberService.login(request)
            .map(member -> {
                session.setAttribute(SessionConst.USER_ID, member.getUserId());
                session.setAttribute(SessionConst.USER_NAME, member.getUserName());

                return LoginResponseDto.success();
            })
            .orElseGet(LoginResponseDto::fail);
}
```

`@RequestBody`는 요청 본문의 JSON을 DTO로 변환한다. 프론트엔드가 `application/json` 형식으로 요청을 보내므로 `@ModelAttribute`가 아니라 `@RequestBody`를 사용한다.

`HttpSession`은 컨트롤러 메서드 파라미터로 선언하면 Spring이 현재 요청의 세션 객체를 전달한다. 세션에는 `setAttribute(키, 값)`으로 값을 저장한다.

`map()`은 `Optional`에 값이 있을 때만 실행된다. 따라서 로그인에 성공한 경우에만 세션 저장 코드가 실행된다.

`orElseGet()`은 `Optional`이 비어 있을 때만 실행된다. 따라서 로그인 실패 시에만 `LoginResponseDto.fail()`이 호출된다.

---

## Step 6. 화면에서 통합 실행 확인하기

모든 계층을 구현한 뒤 실제 화면에서 로그인 흐름을 확인한다.

1. 애플리케이션을 실행한다.
2. 브라우저에서 `GET /members/login`으로 접속한다.
3. 1편 회원가입에서 저장한 아이디와 비밀번호로 로그인한다.
4. 게시판 목록 페이지인 `/`로 이동하면 성공이다.
5. 목록 상단에 로그인한 사용자의 이름이 표시되는지 확인한다.
6. 비밀번호를 틀리게 입력한다.
7. `"아이디 또는 비밀번호가 일치하지 않습니다."` 메시지가 표시되고 페이지가 이동하지 않으면 성공이다.
8. 로그인하지 않은 상태로 `/`에 직접 접근했을 때 로그인 페이지로 이동하는지 확인한다.

잘 안 될 때 확인할 내용은 다음과 같다.

| 증상                  | 확인할 내용                                       |
| ------------------- | -------------------------------------------- |
| 로그인이 항상 실패함         | `username`, `password` 필드명이 JSON 키와 같은지 확인한다 |
| `400` 또는 `415` 발생   | 로그인 요청이 JSON이므로 `@RequestBody`를 사용했는지 확인한다   |
| 이동은 되지만 이름이 표시되지 않음 | 세션에 `SessionConst.USER_NAME`으로 값을 저장했는지 확인한다 |
| 세션 값이 `null`임       | 저장할 때와 꺼낼 때 같은 세션 키 상수를 사용하는지 확인한다           |
| 성공인데 이동하지 않음        | 응답 필드명이 `successed`인지 확인한다                   |
| 실패 분기가 동작하지 않음      | 프론트엔드가 `response.successed`로 분기하는지 확인한다      |

---

## 완성 체크리스트

* [ ] `MemberRepository.findByUserId()`를 작성했다
* [ ] `LoginRequestDto`를 작성했다
* [ ] `LoginResponseDto`에 `success()`와 `fail()`을 작성했다
* [ ] `SessionConst`에 `USER_ID`, `USER_NAME` 상수를 작성했다
* [ ] `MemberService.login()`에서 `Optional.filter()`로 비밀번호를 검증했다
* [ ] `MemberApiController.login()`에서 `@RequestBody`로 로그인 요청을 받았다
* [ ] 로그인 성공 시 `HttpSession`에 사용자 정보를 저장했다
* [ ] `Optional.map()`과 `orElseGet()`으로 성공·실패 응답을 분기했다
* [ ] 화면에서 로그인 성공 시 목록 이동과 환영 문구를 확인했다
* [ ] 화면에서 로그인 실패 시 메시지 표시와 페이지 유지 동작을 확인했다
