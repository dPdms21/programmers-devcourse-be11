# Form Login 인증

> Spring Security의 Form Login을 이용해 DB 기반 회원가입, 로그인과 로그아웃을 구현한다.
> 회원가입 시 BCrypt로 비밀번호를 암호화하고, 로그인 성공 후에는 서버 세션과 `JSESSIONID` 쿠키로 인증 상태를 유지한다.

---

## 0. 먼저 알아둘 점

HTTP Basic은 브라우저가 자격 증명을 저장하고 매 요청마다 `Authorization` 헤더를 전송하는 stateless 인증 방식이다.

Form Login은 애플리케이션에서 직접 만든 로그인 화면을 사용하고, 로그인 성공 후 서버 세션에 인증 정보를 저장하는 stateful 인증 방식이다.

이번 과제에서는 다음 기능을 구현한다.

* DB 기반 회원가입
* BCrypt 비밀번호 암호화
* 중복 아이디 검사
* Spring Security Form Login
* 로그인 성공·실패 JSON 응답
* 세션 기반 로그인 유지
* 로그아웃 시 세션과 쿠키 삭제

로그인 요청은 일반 컨트롤러가 아니라 `UsernamePasswordAuthenticationFilter`가 처리한다.

따라서 `POST /users/login`을 처리하는 컨트롤러는 작성하지 않는다.

로그인 필터는 요청 파라미터를 읽으므로 로그인 요청은 JSON이 아닌 `application/x-www-form-urlencoded` 형식으로 전송해야 한다.

---

## 1. 구현 기능

| 요청                     | 역할      | 처리 주체               | 접근    |
| ---------------------- | ------- | ------------------- | ----- |
| `GET /users/join`      | 회원가입 화면 | `UserController`    | 전체 허용 |
| `POST /api/users/join` | 회원가입 처리 | `UserApiController` | 전체 허용 |
| `GET /users/login`     | 로그인 화면  | `UserController`    | 전체 허용 |
| `POST /users/login`    | 로그인 처리  | Spring Security 필터  | 전체 허용 |
| `GET /`                | 홈 화면    | `HomeController`    | 인증 필요 |
| `GET /users/logout`    | 로그아웃    | `LogoutFilter`      | 전체 허용 |

전체 흐름은 다음과 같다.

```text
회원가입
→ 비밀번호 BCrypt 암호화
→ DB 저장
→ 로그인 요청
→ Spring Security 필터 인증
→ 서버 세션 생성
→ JSESSIONID 쿠키 발급
→ 홈 화면 접근
→ 로그아웃
→ 세션 무효화
```

---

## 2. 핵심 개념

### (1) HTTP Basic과 Form Login 비교

| 구분       | HTTP Basic              | Form Login         |
| -------- | ----------------------- | ------------------ |
| 로그인 화면   | 브라우저 기본 인증 창            | 직접 만든 HTML         |
| 자격 증명 전달 | 매 요청 `Authorization` 헤더 | 로그인 요청에 한 번 전달     |
| 인증 상태    | stateless               | stateful           |
| 상태 저장    | 브라우저 자격 증명 캐시           | 서버 세션              |
| 이후 요청    | 아이디와 비밀번호 재전송           | `JSESSIONID` 쿠키 전송 |
| 로그아웃     | 명확한 처리 어려움              | 세션 무효화로 가능         |

HTTP Basic은 브라우저가 인증 상태를 유지한다.

Form Login은 서버 세션이 인증 상태를 유지한다.

### (2) 로그인 인증 흐름

```text
POST /users/login
→ UsernamePasswordAuthenticationFilter
→ AuthenticationManager
→ DaoAuthenticationProvider
→ UserDetailsService.loadUserByUsername()
→ PasswordEncoder.matches()
→ 인증 성공 또는 실패
```

애플리케이션에서는 다음 부분을 구현한다.

* DB에서 사용자를 조회하는 `UserDetailsService`
* DB 사용자를 Spring Security 규격으로 변환하는 `UserDetails`
* 비밀번호 암호화 방식
* 로그인 성공·실패 처리

### (3) 로그인 컨트롤러가 필요 없는 이유

`POST /users/login` 요청은 `UsernamePasswordAuthenticationFilter`가 `DispatcherServlet`보다 먼저 처리한다.

```text
클라이언트
→ Spring Security 필터 체인
→ UsernamePasswordAuthenticationFilter
→ 인증 처리
```

`loginProcessingUrl("/users/login")`은 컨트롤러 경로가 아니라 필터가 가로챌 로그인 요청 URL이다.

### (4) 로그인 요청 형식

로그인 필터는 요청 파라미터를 읽는다.

```text
userId=kim&password=1234
```

따라서 로그인 요청은 form-urlencoded 형식으로 보내야 한다.

```javascript
$.ajax({
    type: 'POST',
    url: '/users/login',
    data: {
        userId: 'kim',
        password: '1234'
    }
});
```

다음처럼 JSON으로 보내면 기본 로그인 필터가 값을 읽지 못한다.

```javascript
data: JSON.stringify(formData),
contentType: 'application/json'
```

회원가입과 로그인 요청 형식은 다음과 같이 다르다.

| 요청   | 처리 주체               | 형식              |
| ---- | ------------------- | --------------- |
| 회원가입 | `@RequestBody` 컨트롤러 | JSON            |
| 로그인  | Spring Security 필터  | form-urlencoded |

### (5) 로그인 파라미터 이름

Spring Security의 기본 사용자 이름 파라미터는 `username`이다.

이번 과제에서는 `userId`를 사용하므로 설정을 변경한다.

```java
.usernameParameter("userId")
.passwordParameter("password")
```

설정과 실제 요청 파라미터 이름이 다르면 인증에 실패한다.

### (6) BCrypt 비밀번호 암호화

회원가입에서는 `encode()`로 평문 비밀번호를 암호화한다.

```java
String encodedPassword =
        passwordEncoder.encode(request.getPassword());
```

DB에는 다음과 같은 BCrypt 해시가 저장된다.

```text
$2a$10$...
```

로그인에서는 저장된 해시를 복호화하지 않는다.

`PasswordEncoder.matches()`가 입력 비밀번호와 저장된 해시가 일치하는지 확인한다.

| 시점   | 메서드         | 역할              |
| ---- | ----------- | --------------- |
| 회원가입 | `encode()`  | 평문 비밀번호를 해시로 변환 |
| 로그인  | `matches()` | 입력값과 저장된 해시 비교  |

### (7) `UserDetailsService`의 역할

`UserDetailsService`는 사용자 아이디로 DB 사용자를 조회한다.

```java
UserDetails loadUserByUsername(String username);
```

비밀번호를 직접 비교하지 않는다.

```text
UserDetailsService
→ 사용자 조회

DaoAuthenticationProvider
→ PasswordEncoder.matches()
→ 비밀번호 비교
```

### (8) 세션 기반 인증

로그인에 성공하면 Spring Security가 인증 정보를 `SecurityContext`에 저장하고, 해당 정보를 서버 세션에 보관한다.

```text
로그인 성공
→ Authentication 생성
→ SecurityContext 저장
→ HttpSession 저장
→ JSESSIONID 발급
```

브라우저는 이후 요청마다 `JSESSIONID` 쿠키를 자동으로 전송한다.

```http
Cookie: JSESSIONID=...
```

서버는 세션 ID를 기준으로 기존 `SecurityContext`를 복원한다.

### (9) 로그아웃

로그아웃 시 서버 세션을 무효화하고 브라우저의 세션 쿠키를 삭제한다.

```java
.logout(logout -> logout
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
)
```

`invalidateHttpSession(true)`는 서버 세션을 제거한다.

`deleteCookies("JSESSIONID")`는 브라우저에 남은 세션 ID 쿠키를 삭제한다.

### (10) CSRF

CSRF는 브라우저가 쿠키를 자동으로 전송하는 성질을 이용하는 공격이다.

Spring Security는 기본적으로 POST 요청에 CSRF 토큰을 검사한다.

이번 과제에서는 인증 흐름에 집중하기 위해 비활성화한다.

```java
.csrf(AbstractHttpConfigurer::disable)
```

실제 세션 기반 서비스에서는 CSRF 토큰을 적용하는 것이 원칙이다.

---

## 3. 프로젝트 준비

### 의존성

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'

    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.mysql:mysql-connector-j'
    annotationProcessor 'org.projectlombok:lombok'
}
```

Spring Boot 3.x에서는 다음 의존성을 사용할 수 있다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
```

### 데이터베이스 설정

```yaml
spring:
  application:
    name: form-login

  datasource:
    url: jdbc:mysql://localhost:3306/java_basic?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 1234
```

### 테이블 생성

```sql
CREATE TABLE user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(20),
    email VARCHAR(50),
    user_id VARCHAR(50),
    password VARCHAR(100),
    role ENUM('ROLE_USER', 'ROLE_ADMIN') DEFAULT 'ROLE_USER',
    PRIMARY KEY (id)
);
```

BCrypt 해시는 60자이므로 `password` 컬럼 길이는 충분히 확보해야 한다.

---

## Step 1. 사용자 엔티티와 리포지토리 작성하기

### `Role`

```java
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
```

### `User`

```java
@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String name;

    @Column(length = 50)
    private String email;

    @Column(length = 50)
    private String userId;

    @Column(length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;
}
```

비밀번호에는 평문이 아니라 BCrypt 해시를 저장한다.

### `UserRepository`

```java
public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
```

`findByUserId()`는 로그인 사용자 조회에 사용한다.

`existsByUserId()`는 회원가입 중복 검사에 사용한다.

---

## Step 2. 회원가입 구현하기

### `SignUpRequestDto`

```java
@Getter
public class SignUpRequestDto {

    private String userId;
    private String password;
    private String userName;

    public User toUser(String encodedPassword) {
        return User.builder()
                .userId(userId)
                .password(encodedPassword)
                .name(userName)
                .build();
    }
}
```

### `SignUpResponseDto`

```java
@Getter
@AllArgsConstructor
public class SignUpResponseDto {

    private String url;
}
```

### `DuplicateUserIdException`

```java
public class DuplicateUserIdException
        extends RuntimeException {

    public DuplicateUserIdException(String message) {
        super(message);
    }
}
```

### `ErrorResponseDto`

```java
@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private int status;
    private String message;
}
```

### `GlobalExceptionHandler`

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserIdException.class)
    public ResponseEntity<ErrorResponseDto>
    duplicateUserIdException(
            DuplicateUserIdException e
    ) {
        log.warn("409 응답 : {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(
                        HttpStatus.CONFLICT.value(),
                        e.getMessage()
                ));
    }
}
```

중복 아이디는 `409 Conflict`로 응답한다.

### `UserService`

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpRequestDto request) {
        if (userRepository.existsByUserId(
                request.getUserId()
        )) {
            throw new DuplicateUserIdException(
                    "이미 사용 중인 아이디입니다."
            );
        }

        String encodedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        User user = request.toUser(encodedPassword);

        userRepository.save(user);
    }
}
```

회원가입 흐름은 다음과 같다.

```text
아이디 중복 확인
→ 비밀번호 BCrypt 암호화
→ User 생성
→ DB 저장
```

### `UserApiController`

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/join")
    public SignUpResponseDto signUp(
            @RequestBody SignUpRequestDto request
    ) {
        userService.signUp(request);

        return new SignUpResponseDto(
                "/users/login"
        );
    }
}
```

회원가입 요청은 JSON으로 전송한다.

```json
{
  "userId": "kim",
  "password": "1234",
  "userName": "김개발"
}
```

---

## Step 3. DB 사용자를 Spring Security에 연결하기

### `CustomUserDetails`

```java
@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    private User user;

    @Override
    public Collection<? extends GrantedAuthority>
    getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        user.getRole().name()
                )
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

`CustomUserDetails`는 애플리케이션의 `User` 엔티티를 Spring Security가 읽을 수 있는 형태로 변환한다.

### `UserDetailService`

```java
@Service
@RequiredArgsConstructor
public class UserDetailService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {
        User user = userRepository
                .findByUserId(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                username + " not found"
                        )
                );

        return CustomUserDetails.builder()
                .user(user)
                .build();
    }
}
```

이 서비스는 DB 사용자 조회만 담당한다.

비밀번호 대조는 `DaoAuthenticationProvider`가 수행한다.

---

## Step 4. Spring Security 설정하기

### `SecurityConfig`

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler
            customAuthenticationSuccessHandler;

    private final CustomAuthenticationFailureHandler
            customAuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/users/join",
                                "/api/users/join",
                                "/css/**",
                                "/js/**"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/users/login")
                        .loginProcessingUrl(
                                "/users/login"
                        )
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .successHandler(
                                customAuthenticationSuccessHandler
                        )
                        .failureHandler(
                                customAuthenticationFailureHandler
                        )
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl(
                                "/users/login"
                        )
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

인가 규칙은 위에서부터 순서대로 적용된다.

구체적인 경로를 먼저 작성하고 `anyRequest()`는 마지막에 작성한다.

```text
/users/join
/api/users/join
/css/**
/js/**
→ permitAll

그 외 요청
→ authenticated
```

`GET /users/login`은 컨트롤러가 로그인 화면을 반환한다.

`POST /users/login`은 로그인 필터가 인증을 처리한다.

---

## Step 5. 로그인 성공·실패 처리하기

### `SignInResponseDto`

```java
@Getter
@Builder
public class SignInResponseDto {

    private boolean isLoggedIn;
    private String url;
    private String userName;
    private String userId;
    private String message;
}
```

### `CustomAuthenticationSuccessHandler`

```java
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomUserDetails userDetails =
                (CustomUserDetails)
                        authentication.getPrincipal();

        User user = userDetails.getUser();

        HttpSession session = request.getSession();
        session.setAttribute(
                "userId",
                user.getUserId()
        );
        session.setAttribute(
                "userName",
                user.getName()
        );

        SignInResponseDto dto =
                SignInResponseDto.builder()
                        .isLoggedIn(true)
                        .message("로그인 성공")
                        .url("/")
                        .userId(user.getUserId())
                        .userName(user.getName())
                        .build();

        response.setStatus(
                HttpServletResponse.SC_OK
        );
        response.setContentType(
                "application/json; charset=utf-8"
        );
        response.getWriter().write(
                objectMapper.writeValueAsString(dto)
        );
    }
}
```

로그인 성공 후 홈 화면에서 사용할 `userId`와 `userName`을 세션에 저장한다.

### `CustomAuthenticationFailureHandler`

```java
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler
        implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        SignInResponseDto dto =
                SignInResponseDto.builder()
                        .isLoggedIn(false)
                        .message(
                                "로그인 실패\n다시 로그인해주세요."
                        )
                        .url("/users/login")
                        .build();

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );
        response.setContentType(
                "application/json; charset=utf-8"
        );
        response.getWriter().write(
                objectMapper.writeValueAsString(dto)
        );
    }
}
```

로그인 실패 시 `401 Unauthorized`와 JSON 응답을 반환한다.

---

## Step 6. 화면과 JavaScript 작성하기

### `HomeController`

```java
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}
```

### `UserController`

```java
@Controller
@RequestMapping("/users")
public class UserController {

    @GetMapping("/join")
    public String join() {
        return "sign-up";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
```

로그인과 로그아웃 처리 메서드는 작성하지 않는다.

### `login.html`

```html
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <script
            src="https://code.jquery.com/jquery-3.7.1.js"
            crossorigin="anonymous">
    </script>
    <script th:src="@{/js/signIn.js}"></script>
</head>
<body>
    <h2>로그인</h2>

    <input
            type="text"
            id="user_id"
            placeholder="아이디를 입력하세요"
            required>

    <input
            type="password"
            id="password"
            placeholder="비밀번호를 입력하세요"
            required>

    <input
            id="signin"
            type="submit"
            value="로그인">

    <a href="/users/join">회원가입</a>
</body>
</html>
```

### `home.html`

```html
<h2 th:text="|${session.userName}님 환영합니다|">
    환영합니다
</h2>

<a href="/users/logout">로그아웃</a>
```

### `signIn.js`

```javascript
$(document).ready(() => {
    $('#signin').click(() => {
        let formData = {
            userId: $('#user_id').val(),
            password: $('#password').val()
        };

        $.ajax({
            type: 'POST',
            url: '/users/login',
            data: formData,
            dataType: 'json',

            success: function(response) {
                alert(response.message);
                window.location.href = response.url;
            },

            error: function(xhr) {
                let response = xhr.responseJSON;

                alert(
                    response && response.message
                        ? response.message
                        : '로그인 중 오류가 발생했습니다.'
                );
            }
        });
    });
});
```

로그인 요청은 JSON으로 변환하지 않고 객체를 그대로 전달한다.

### `signUp.js`

```javascript
$(document).ready(() => {
    $('#signup').click(() => {
        let formData = {
            userId: $('#user_id').val(),
            password: $('#password').val(),
            userName: $('#user_name').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/users/join',
            data: JSON.stringify(formData),
            contentType:
                'application/json; charset=utf-8',
            dataType: 'json',

            success: function(response) {
                alert(
                    '회원가입이 성공했습니다.\n로그인해주세요.'
                );
                window.location.href = response.url;
            },

            error: function() {
                alert(
                    '회원가입 중 오류가 발생했습니다.'
                );
            }
        });
    });
});
```

회원가입은 `@RequestBody`가 처리하므로 JSON으로 전송한다.

---

## Step 7. 세션과 로그아웃 확인하기

로그인 성공 응답에는 `JSESSIONID` 쿠키가 포함된다.

```http
Set-Cookie: JSESSIONID=...
```

이후 요청에서 브라우저가 해당 쿠키를 자동 전송한다.

```http
Cookie: JSESSIONID=...
```

로그아웃하면 다음 처리가 수행된다.

```text
LogoutFilter
→ 서버 세션 무효화
→ SecurityContext 제거
→ JSESSIONID 쿠키 삭제
→ 로그인 화면 이동
```

서버 메모리에 세션을 저장하는 기본 설정에서는 서버 재시작 시 기존 세션이 사라져 로그인 상태도 해제된다.

HTTP Basic은 브라우저가 자격 증명을 다시 전송하므로 서버 재시작 후에도 인증될 수 있지만, Form Login은 서버 세션이 사라지면 로그인 상태가 유지되지 않는다.

---

## 4. 최종 코드 확인

최종 구현은 다음 파일로 구성된다.

```text
config
├── SecurityConfig.java
└── security
    ├── CustomAuthenticationFailureHandler.java
    ├── CustomAuthenticationSuccessHandler.java
    └── CustomUserDetails.java

controller
├── HomeController.java
├── UserApiController.java
└── UserController.java

domain
├── entity
│   ├── Role.java
│   └── User.java
└── repository
    └── UserRepository.java

dto
├── ErrorResponseDto.java
├── SignInResponseDto.java
├── SignUpRequestDto.java
└── SignUpResponseDto.java

exception
├── DuplicateUserIdException.java
└── GlobalExceptionHandler.java

service
├── UserDetailService.java
└── UserService.java
```

핵심 설정은 다음과 같다.

```java
http
        .csrf(AbstractHttpConfigurer::disable)

        .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/users/join",
                        "/api/users/join",
                        "/css/**",
                        "/js/**"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
        )

        .formLogin(form -> form
                .loginPage("/users/login")
                .loginProcessingUrl("/users/login")
                .usernameParameter("userId")
                .passwordParameter("password")
                .successHandler(
                        customAuthenticationSuccessHandler
                )
                .failureHandler(
                        customAuthenticationFailureHandler
                )
                .permitAll()
        )

        .logout(logout -> logout
                .logoutUrl("/users/logout")
                .logoutSuccessUrl("/users/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );
```

회원가입에서는 비밀번호를 암호화한다.

```java
String encodedPassword =
        passwordEncoder.encode(
                request.getPassword()
        );
```

로그인 요청은 form-urlencoded 형식으로 전송한다.

```javascript
$.ajax({
    type: 'POST',
    url: '/users/login',
    data: {
        userId: $('#user_id').val(),
        password: $('#password').val()
    }
});
```

---

## 5. 동작 확인

### 회원가입

```bash
curl -i \
  -H "Content-Type: application/json" \
  -d '{"userId":"kim","password":"1234","userName":"김개발"}' \
  http://localhost:8080/api/users/join
```

성공 응답은 다음과 같다.

```json
{
  "url": "/users/login"
}
```

동일한 아이디로 다시 가입하면 `409 Conflict`를 반환한다.

### 로그인

```bash
curl -i \
  -d "userId=kim&password=1234" \
  http://localhost:8080/users/login
```

성공 응답에는 JSON과 `JSESSIONID` 쿠키가 포함된다.

```http
HTTP/1.1 200
Set-Cookie: JSESSIONID=...
```

```json
{
  "isLoggedIn": true,
  "url": "/",
  "userName": "김개발",
  "userId": "kim",
  "message": "로그인 성공"
}
```

잘못된 비밀번호로 로그인하면 `401 Unauthorized`를 반환한다.

---

## 6. 자주 발생하는 문제

| 증상                      | 원인과 해결                                            |
| ----------------------- | ------------------------------------------------- |
| 아이디와 비밀번호가 맞지만 로그인 실패   | 로그인 요청을 JSON으로 전송했는지 확인하고 form-urlencoded로 변경     |
| 항상 로그인 실패               | `usernameParameter("userId")`와 실제 요청 이름 확인        |
| 회원가입 후 로그인 실패           | DB에 평문 비밀번호가 저장되었는지 확인하고 BCrypt로 다시 저장            |
| 로그인 화면 CSS와 JS가 적용되지 않음 | `/css/**`, `/js/**`에 `permitAll()` 적용             |
| 리다이렉트가 반복됨              | 로그인 화면 자체가 잠겼는지 확인하고 `formLogin().permitAll()` 적용 |
| 로그인 요청이 403             | CSRF 토큰 누락 여부 확인                                  |
| 홈에 `null님` 출력           | 세션의 `userName`과 Thymeleaf 속성 이름 일치 확인             |
| BCrypt 비밀번호가 잘림         | DB 비밀번호 컬럼을 `VARCHAR(100)` 이상으로 설정                |
| 로그인 컨트롤러가 호출되지 않음       | 로그인 필터가 먼저 처리하므로 정상 동작                            |

---

## 7. 학습 체크

* [ ] HTTP Basic과 Form Login의 차이를 설명할 수 있다
* [ ] 회원가입에서 `encode()`를 사용하는 이유를 설명할 수 있다
* [ ] 로그인에서 `matches()`가 수행하는 역할을 설명할 수 있다
* [ ] 로그인 요청을 form-urlencoded로 보내야 하는 이유를 설명할 수 있다
* [ ] `loginProcessingUrl`에 컨트롤러가 필요 없는 이유를 설명할 수 있다
* [ ] `UserDetails`와 `UserDetailsService`의 역할을 설명할 수 있다
* [ ] 비밀번호 비교를 `DaoAuthenticationProvider`가 수행하는 것을 설명할 수 있다
* [ ] `anyRequest()`를 마지막에 작성해야 하는 이유를 설명할 수 있다
* [ ] `JSESSIONID` 쿠키로 인증 상태가 유지되는 흐름을 설명할 수 있다
* [ ] 로그아웃 시 세션 무효화와 쿠키 삭제의 차이를 설명할 수 있다
* [ ] Form Login이 stateful 인증인 이유를 설명할 수 있다
* [ ] CSRF가 쿠키 자동 전송을 이용하는 공격임을 설명할 수 있다

---

## 8. 완성 체크리스트

* [ ] 회원가입 시 비밀번호가 BCrypt 해시로 저장된다
* [ ] 중복 아이디 요청에 409를 반환한다
* [ ] 미인증 상태로 `/` 접근 시 로그인 화면으로 이동한다
* [ ] 로그인 요청을 form-urlencoded로 전송한다
* [ ] 로그인 성공 시 200 JSON을 반환한다
* [ ] 로그인 실패 시 401 JSON을 반환한다
* [ ] 로그인 성공 후 `JSESSIONID` 쿠키가 생성된다
* [ ] 홈 화면에서 세션의 사용자 이름을 출력한다
* [ ] 로그아웃 시 세션이 무효화된다
* [ ] 로그아웃 시 `JSESSIONID` 쿠키가 삭제된다
* [ ] 로그아웃 후 보호된 페이지 접근이 다시 차단된다
* [ ] 서버 재시작 시 로그인 상태가 해제되는 이유를 설명할 수 있다
