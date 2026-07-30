# JWT 토큰 인증

> JWT Access Token과 Refresh Token을 이용해 stateless 로그인, 인증과 인가를 구현한다.
> 로그인 성공 시 세션 대신 토큰을 발급하고, 이후 요청에서는 필터가 토큰을 검증하여 인증 상태를 복원한다.

---

## 0. 먼저 알아둘 점

Form Login은 로그인 성공 후 서버 세션에 인증 상태를 저장하는 stateful 방식이다.

JWT 토큰 인증은 서버가 로그인 상태를 저장하지 않고, 클라이언트가 요청마다 토큰을 전달하는 stateless 방식이다.

이번 과제에서는 다음 흐름을 구현한다.

```text
로그인
→ Access Token과 Refresh Token 발급
→ Access Token은 응답 body로 전달
→ Refresh Token은 HttpOnly 쿠키로 전달
→ API 요청마다 Bearer Token 전송
→ 필터에서 토큰 검증
→ SecurityContext에 인증 정보 등록
→ 권한에 따라 API 접근
```

Access Token이 만료되면 Refresh Token으로 새로운 토큰을 발급한다.

```text
Access Token 만료
→ API 요청에서 401 응답
→ Refresh Token 쿠키로 재발급 요청
→ 새로운 Access Token과 Refresh Token 발급
→ 기존 요청 재시도
```

Form Login 과제의 다음 구성은 그대로 사용한다.

* `User`
* `Role`
* `UserRepository`
* `CustomUserDetails`
* `UserDetailService`
* 회원가입 처리
* BCrypt 비밀번호 암호화
* 중복 아이디 예외 처리

다음 구성은 사용하지 않는다.

* `CustomAuthenticationSuccessHandler`
* `CustomAuthenticationFailureHandler`
* `formLogin()` 설정
* 세션 기반 로그인 상태 저장

로그인 요청은 `UsernamePasswordAuthenticationFilter`가 아니라 직접 작성한 컨트롤러가 처리한다.

따라서 Form Login과 달리 로그인 정보를 JSON으로 전송한다.

```text
Form Login
→ 필터가 요청 파라미터를 처리
→ form-urlencoded

JWT 로그인
→ 컨트롤러의 @RequestBody가 처리
→ JSON
```

JWT Secret Key가 짧으면 애플리케이션 실행 시 `WeakKeyException`이 발생한다.

HS512를 사용하려면 64바이트 이상의 키가 필요하다.

```bash
openssl rand -base64 64
```

---

## 1. 구현 기능

| 요청                                           | 역할                       | 접근              |
| -------------------------------------------- | ------------------------ | --------------- |
| `POST /api/users/join`                       | 회원가입                     | 전체 허용           |
| `POST /api/users/login`                      | 로그인과 토큰 발급               | 전체 허용           |
| `GET /api/users/info`                        | 로그인 사용자 정보 조회            | Access Token 필요 |
| `POST /api/tokens/refresh`                   | Access·Refresh Token 재발급 | 전체 허용           |
| `POST /api/users/logout`                     | Refresh Token 쿠키 삭제      | 전체 허용           |
| `GET /api/users/user`                        | 일반 사용자 권한 확인             | `ROLE_USER` 이상  |
| `GET /api/users/admin`                       | 관리자 권한 확인                | `ROLE_ADMIN`    |
| `/`, `/admin`, `/users/login`, `/users/join` | HTML 화면                  | 전체 허용           |

---

## 2. 학습 목표

| 개념                               | 단계        |
| -------------------------------- | --------- |
| JWT 구조와 서명의 의미                   | Step 1    |
| 토큰 생성·검증·해석                      | Step 1~2  |
| `AuthenticationManager`를 이용한 로그인 | Step 3    |
| Access Token과 Refresh Token 분리   | Step 3, 5 |
| 인증 필터와 `SecurityContext` 복원      | Step 4    |
| stateless 설정과 401·403 처리         | Step 4    |
| 토큰 재발급과 Refresh Token Rotation   | Step 5    |
| `@PreAuthorize`와 `RoleHierarchy` | Step 6    |

---

## 3. 핵심 개념

### (1) 인증 방식 비교

| 구분       | HTTP Basic | Form Login   | JWT Token       |
| -------- | ---------- | ------------ | --------------- |
| 최초 자격 증명 | 브라우저 인증 창  | Form 파라미터    | JSON            |
| 이후 인증 정보 | ID·비밀번호    | `JSESSIONID` | JWT             |
| 서버 상태    | stateless  | stateful     | stateless       |
| 상태 관리 주체 | 브라우저       | 서버 세션        | 토큰              |
| 서버 재시작   | 인증 가능      | 로그인 해제       | 서명 키가 같으면 인증 가능 |
| 로그아웃     | 명확한 처리 어려움 | 세션 무효화       | 클라이언트 토큰 삭제     |
| 서버 확장    | 쉬움         | 세션 공유 필요     | 쉬움              |

JWT 인증에서는 서버가 사용자별 로그인 세션을 저장하지 않는다.

토큰에 사용자와 권한 정보가 포함되며, 서버는 토큰의 서명을 검증하여 발급한 토큰인지 확인한다.

### (2) JWT 구조

JWT는 점으로 구분된 세 부분으로 구성된다.

```text
Header.Payload.Signature
```

| 영역        | 역할                        |
| --------- | ------------------------- |
| Header    | 토큰 종류와 서명 알고리즘            |
| Payload   | 사용자 정보와 만료 시간 등의 클레임      |
| Signature | Header와 Payload의 변조 여부 검증 |

Payload에는 다음과 같은 정보가 들어갈 수 있다.

```json
{
  "iss": "test@naver.com",
  "sub": "kim",
  "iat": 1785400000,
  "exp": 1785407200,
  "id": 1,
  "role": "ROLE_USER",
  "name": "김개발"
}
```

Payload는 암호화된 값이 아니라 인코딩된 값이다.

누구나 내용을 확인할 수 있으므로 비밀번호와 같은 민감 정보는 넣지 않는다.

### (3) 클레임

클레임은 JWT Payload에 저장하는 정보다.

| 클레임   | 의미      |
| ----- | ------- |
| `iss` | 토큰 발급자  |
| `sub` | 사용자 식별값 |
| `iat` | 발급 시각   |
| `exp` | 만료 시각   |

애플리케이션에서 직접 추가하는 `id`, `role`, `name`은 비공개 클레임이다.

클레임 이름은 생성과 조회에서 동일하게 사용해야 하므로 상수로 관리한다.

```java
private static final String CLAIM_ID = "id";
private static final String CLAIM_ROLE = "role";
private static final String CLAIM_NAME = "name";
```

### (4) JWT 서명

Signature는 Header와 Payload가 변조되지 않았는지 확인한다.

```text
Signature
= HMACSHA512(
    Base64Url(Header) + "." + Base64Url(Payload),
    SecretKey
  )
```

Payload의 `role`을 임의로 `ROLE_ADMIN`으로 변경해도 Secret Key가 없으면 올바른 서명을 다시 만들 수 없다.

JWT는 내용을 숨기는 기술이 아니라 변조 여부를 검증하는 기술이다.

### (5) 로그인과 요청 인증

로그인과 이후 요청은 서로 다른 흐름으로 처리된다.

```text
[로그인]

POST /api/users/login
→ UserApiController
→ UserService.signIn()
→ AuthenticationManager.authenticate()
→ UserDetailsService 사용자 조회
→ PasswordEncoder 비밀번호 검증
→ Access Token과 Refresh Token 발급
```

```text
[이후 요청]

GET /api/users/info
Authorization: Bearer access-token

→ TokenAuthenticationFilter
→ 토큰 검증
→ 클레임에서 User 복원
→ Authentication 생성
→ SecurityContext 등록
→ 인가 확인
→ 컨트롤러 실행
```

로그인할 때는 DB에서 사용자를 조회하고 비밀번호를 검증한다.

이후 요청에서는 DB를 다시 조회하지 않고 토큰의 클레임으로 사용자를 복원한다.

### (6) Access Token과 Refresh Token

| 구분    | Access Token       | Refresh Token     |
| ----- | ------------------ | ----------------- |
| 용도    | API 요청 인증          | 토큰 재발급            |
| 수명    | 짧게 설정              | 길게 설정             |
| 저장 위치 | `localStorage`     | HttpOnly Cookie   |
| 전송 방식 | `Authorization` 헤더 | Cookie            |
| 사용 시점 | 보호된 요청마다           | Access Token 만료 시 |

Access Token은 요청마다 전송되므로 탈취 위험을 줄이기 위해 수명을 짧게 설정한다.

Refresh Token은 재발급 API에서만 사용하고 JavaScript로 읽을 수 없는 HttpOnly 쿠키에 저장한다.

### (7) stateless 인증

JWT 인증에서는 서버에 사용자별 로그인 세션을 저장하지 않는다.

```text
요청
→ JWT 전달
→ 서명과 만료 검증
→ 사용자 정보 복원
```

여러 서버가 같은 Secret Key를 사용하면 어느 서버에서도 토큰을 검증할 수 있다.

반면 이미 발급된 토큰을 즉시 무효화하기는 어렵다.

로그아웃 시 클라이언트의 토큰을 삭제하더라도 기존 Access Token은 만료 전까지 유효할 수 있다.

강제 무효화가 필요하면 DB나 Redis에 토큰 상태를 저장해야 한다.

### (8) 401과 403

| 상태               | 의미                 |
| ---------------- | ------------------ |
| 401 Unauthorized | 인증 정보가 없거나 유효하지 않음 |
| 403 Forbidden    | 인증되었지만 권한이 부족함     |

Spring Security에서는 다음 구성 요소가 처리한다.

| 구성 요소                      | 역할     |
| -------------------------- | ------ |
| `AuthenticationEntryPoint` | 401 처리 |
| `AccessDeniedHandler`      | 403 처리 |

API 요청에는 리다이렉트 대신 상태 코드와 JSON 응답을 반환한다.

### (9) CSRF와 XSS

Access Token을 `Authorization` 헤더로 직접 전송하면 브라우저가 해당 헤더를 자동으로 첨부하지 않는다.

따라서 쿠키 자동 전송을 이용하는 CSRF 위험은 줄어든다.

```java
.csrf(AbstractHttpConfigurer::disable)
```

반면 `localStorage`는 JavaScript로 접근할 수 있어 XSS에 취약하다.

```javascript
localStorage.getItem('accessToken');
```

화면에 사용자 입력을 출력할 때 jQuery의 `.html()` 대신 `.text()`를 사용하고, Thymeleaf에서는 `th:utext` 대신 `th:text`를 사용한다.

Refresh Token은 HttpOnly 쿠키에 저장해 JavaScript에서 읽을 수 없도록 한다.

### (10) HTML 화면과 API 인가

주소창 입력이나 링크 이동에서는 임의의 `Authorization` 헤더를 추가할 수 없다.

따라서 HTML 페이지 자체는 공개하고 실제 데이터 API에 권한 검사를 적용한다.

```text
/admin
→ HTML 화면 반환

GET /api/users/admin
Authorization: Bearer token
→ ROLE_ADMIN 검사
```

화면에서 링크를 숨기는 것은 사용자 편의를 위한 처리이며, 실제 권한 검사는 서버 API에서 수행해야 한다.

---

## 4. 프로젝트 준비

### 의존성

Form Login 과제의 의존성에 JJWT를 추가한다.

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'

    implementation 'io.jsonwebtoken:jjwt-api:0.13.0'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.13.0'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.13.0'

    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.mysql:mysql-connector-j'
    annotationProcessor 'org.projectlombok:lombok'
}
```

### JWT 설정

```yaml
spring:
  application:
    name: token

  datasource:
    url: jdbc:mysql://localhost:3306/java_basic?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 1234

jwt:
  issuer: test@naver.com
  secret_key: ${JWT_SECRET_KEY}
  access-token-validity: 2h
  refresh-token-validity: 7d
```

Secret Key는 코드와 Git 저장소에 직접 작성하지 않는다.

---

## 5. Step by Step

### Step 0. Form Login 프로젝트 기반 준비

Form Login 과제의 사용자, 권한, 리포지토리와 회원가입 코드를 재사용한다.

다음 구성은 제거한다.

* 로그인 성공·실패 핸들러
* `formLogin()` 설정
* 세션 사용자 저장
* Form Login 로그아웃 설정

JJWT 의존성과 JWT 설정값을 추가한다.

### `JwtProperties`

```java
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String issuer;
    private String secretKey;
    private Duration accessTokenValidity;
    private Duration refreshTokenValidity;
}
```

`Duration`을 사용하면 `2h`, `7d`와 같은 값을 Java 시간 타입으로 변환할 수 있다.

---

### Step 1. JWT 생성하기

`TokenProvider`에서 Secret Key를 생성하고 토큰을 발급한다.

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private static final String CLAIM_ID = "id";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_NAME = "name";

    private final JwtProperties jwtProperties;

    private SecretKey secretKey;
    private JwtParser jwtParser;

    @PostConstruct
    private void init() {
        secretKey = Keys.hmacShaKeyFor(
                Base64.getDecoder()
                        .decode(jwtProperties.getSecretKey())
        );

        jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    public String generateToken(
            User user,
            Duration validity
    ) {
        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + validity.toMillis()
        );

        return makeToken(user, expiration);
    }

    private String makeToken(
            User user,
            Date expiration
    ) {
        Date now = new Date();

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiration)
                .subject(user.getUserId())
                .claim(CLAIM_ID, user.getId())
                .claim(
                        CLAIM_ROLE,
                        user.getRole().name()
                )
                .claim(CLAIM_NAME, user.getName())
                .signWith(
                        secretKey,
                        Jwts.SIG.HS512
                )
                .compact();
    }
}
```

Secret Key와 `JwtParser`는 애플리케이션 시작 시 한 번 생성한 뒤 재사용한다.

토큰에는 사용자 비밀번호를 넣지 않는다.

---

### Step 2. JWT 검증과 해석 구현하기

### `TokenStatus`

```java
public enum TokenStatus {
    VALID,
    EXPIRED,
    INVALID
}
```

유효한 토큰, 만료된 토큰과 비정상 토큰을 구분한다.

`TokenProvider`에 검증과 정보 복원 기능을 추가한다.

```java
public TokenStatus validateToken(String token) {
    try {
        jwtParser.parseSignedClaims(token);
        return TokenStatus.VALID;

    } catch (ExpiredJwtException e) {
        log.warn("Token is expired");
        return TokenStatus.EXPIRED;

    } catch (Exception e) {
        log.warn("Token is not valid");
        return TokenStatus.INVALID;
    }
}

public User getTokenDetails(String token) {
    Claims claims = getClaims(token);

    return User.builder()
            .id(claims.get(CLAIM_ID, Long.class))
            .userId(claims.getSubject())
            .name(
                    claims.get(
                            CLAIM_NAME,
                            String.class
                    )
            )
            .role(
                    Role.valueOf(
                            claims.get(
                                    CLAIM_ROLE,
                                    String.class
                            )
                    )
            )
            .build();
}

public Authentication getAuthentication(
        User user,
        String token
) {
    CustomUserDetails principal =
            CustomUserDetails.builder()
                    .user(user)
                    .build();

    return new UsernamePasswordAuthenticationToken(
            principal,
            token,
            principal.getAuthorities()
    );
}

private Claims getClaims(String token) {
    return jwtParser
            .parseSignedClaims(token)
            .getPayload();
}
```

`getTokenDetails()`는 DB를 조회하지 않고 토큰 클레임에서 `User`를 복원한다.

`getAuthentication()`은 복원한 사용자를 Spring Security의 `Authentication`으로 변환한다.

---

### Step 3. 로그인과 토큰 발급 구현하기

### `TokenService`

```java
@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    public record TokenPair(
            String accessToken,
            String refreshToken
    ) {
    }

    public TokenPair issueTokens(User user) {
        String accessToken =
                tokenProvider.generateToken(
                        user,
                        jwtProperties
                                .getAccessTokenValidity()
                );

        String refreshToken =
                tokenProvider.generateToken(
                        user,
                        jwtProperties
                                .getRefreshTokenValidity()
                );

        return new TokenPair(
                accessToken,
                refreshToken
        );
    }
}
```

### `UserService.signIn()`

```java
public SignInResponseDto signIn(
        String username,
        String password
) {
    Authentication authentication =
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            password
                    )
            );

    User user =
            ((CustomUserDetails)
                    authentication.getPrincipal())
                    .getUser();

    TokenService.TokenPair tokens =
            tokenService.issueTokens(user);

    return SignInResponseDto.builder()
            .isLoggedIn(true)
            .message("로그인 성공")
            .url("/")
            .accessToken(tokens.accessToken())
            .refreshToken(tokens.refreshToken())
            .userId(user.getUserId())
            .userName(user.getName())
            .build();
}
```

`AuthenticationManager`는 `UserDetailService`와 `PasswordEncoder`를 이용해 아이디와 비밀번호를 검증한다.

인증에 성공하면 세션을 생성하지 않고 Access Token과 Refresh Token을 발급한다.

### 로그인 응답 DTO

```java
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInResponseDto {

    private boolean isLoggedIn;
    private String url;
    private String userName;
    private String userId;
    private String message;
    private String accessToken;
    private String refreshToken;
}
```

### `CookieUtil`

```java
public class CookieUtil {

    public static final String
            REFRESH_TOKEN_COOKIE = "refreshToken";

    private CookieUtil() {
    }

    public static void addCookie(
            HttpServletResponse response,
            String name,
            String value,
            int maxAge
    ) {
        Cookie cookie = new Cookie(name, value);

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public static void deleteCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String name
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setMaxAge(0);
                cookie.setPath("/");

                response.addCookie(cookie);
            }
        }
    }
}
```

로컬 HTTP 환경에서는 `Secure`를 `false`로 설정하고, 운영 HTTPS 환경에서는 `true`로 설정해야 한다.

### 로그인 API

```java
@PostMapping("/login")
public SignInResponseDto signIn(
        @RequestBody SignInRequestDto requestDto,
        HttpServletResponse response
) {
    SignInResponseDto result =
            userService.signIn(
                    requestDto.getUserId(),
                    requestDto.getPassword()
            );

    CookieUtil.addCookie(
            response,
            CookieUtil.REFRESH_TOKEN_COOKIE,
            result.getRefreshToken(),
            (int) jwtProperties
                    .getRefreshTokenValidity()
                    .toSeconds()
    );

    result.setRefreshToken(null);

    return result;
}
```

Access Token은 응답 body로 전달하고 Refresh Token은 HttpOnly 쿠키로 전달한다.

응답 body에서는 Refresh Token을 제거한다.

---

### Step 4. 인증 필터 구현하기

### `TokenAuthenticationFilter`

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter
        extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            TokenStatus status =
                    tokenProvider.validateToken(token);

            if (status == TokenStatus.VALID) {
                User user =
                        tokenProvider.getTokenDetails(
                                token
                        );

                Authentication authentication =
                        tokenProvider
                                .getAuthentication(
                                        user,
                                        token
                                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );

            } else if (
                    status == TokenStatus.EXPIRED
            ) {
                response.setStatus(
                        HttpServletResponse
                                .SC_UNAUTHORIZED
                );

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(
            HttpServletRequest request
    ) {
        String bearerToken =
                request.getHeader(
                        HttpHeaders.AUTHORIZATION
                );

        if (
                bearerToken != null
                && bearerToken.startsWith("Bearer ")
        ) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
```

토큰이 없으면 필터에서 바로 거부하지 않고 다음 필터로 전달한다.

공개 경로일 수 있기 때문이다.

유효한 토큰이면 사용자와 권한 정보를 복원하여 `SecurityContext`에 등록한다.

만료된 토큰이면 프론트엔드가 재발급을 시도할 수 있도록 401을 반환한다.

### 사용자 정보 API

```java
@GetMapping("/info")
public UserInfoResponseDto getUserInfo(
        @AuthenticationPrincipal
        CustomUserDetails userDetails
) {
    User user = userDetails.getUser();

    return UserInfoResponseDto.builder()
            .id(user.getId())
            .userId(user.getUserId())
            .userName(user.getName())
            .role(user.getRole())
            .build();
}
```

`@AuthenticationPrincipal`은 `SecurityContext`에 저장된 principal을 주입한다.

---

### Step 5. 토큰 재발급과 로그아웃 구현하기

`TokenService`에 Refresh Token 검증과 재발급 기능을 추가한다.

```java
public RefreshTokenResponseDto refreshToken(
        Cookie[] cookies
) {
    String refreshToken =
            getRefreshTokenFromCookies(cookies);

    if (
            refreshToken != null
            && tokenProvider.validateToken(
                    refreshToken
            ) == TokenStatus.VALID
    ) {
        User user =
                tokenProvider.getTokenDetails(
                        refreshToken
                );

        TokenPair tokens = issueTokens(user);

        return RefreshTokenResponseDto.builder()
                .validated(true)
                .accessToken(
                        tokens.accessToken()
                )
                .refreshToken(
                        tokens.refreshToken()
                )
                .build();
    }

    return RefreshTokenResponseDto.builder()
            .validated(false)
            .build();
}

private String getRefreshTokenFromCookies(
        Cookie[] cookies
) {
    if (cookies == null) {
        return null;
    }

    for (Cookie cookie : cookies) {
        if (
                CookieUtil.REFRESH_TOKEN_COOKIE
                        .equals(cookie.getName())
        ) {
            return cookie.getValue();
        }
    }

    return null;
}
```

재발급 시 Access Token과 Refresh Token을 모두 새로 발급한다.

### 재발급 API

```java
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(
        HttpServletRequest request,
        HttpServletResponse response
) {
    RefreshTokenResponseDto result =
            tokenService.refreshToken(
                    request.getCookies()
            );

    if (result.isValidated()) {
        CookieUtil.addCookie(
                response,
                CookieUtil.REFRESH_TOKEN_COOKIE,
                result.getRefreshToken(),
                (int) jwtProperties
                        .getRefreshTokenValidity()
                        .toSeconds()
        );

        result.setRefreshToken(null);

        return ResponseEntity.ok(result);
    }

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                    new ErrorResponseDto(
                            HttpStatus.UNAUTHORIZED
                                    .value(),
                            "리프레시 토큰이 만료되었습니다."
                    )
            );
}
```

`/api/tokens/refresh`는 Access Token이 만료된 상태에서 호출하므로 반드시 `permitAll`이어야 한다.

### 로그아웃 API

```java
@PostMapping("/logout")
public LogoutResponseDto logout(
        HttpServletRequest request,
        HttpServletResponse response
) {
    CookieUtil.deleteCookie(
            request,
            response,
            CookieUtil.REFRESH_TOKEN_COOKIE
    );

    return LogoutResponseDto.builder()
            .message("로그아웃 되었습니다.")
            .url("/users/login")
            .build();
}
```

프론트엔드에서는 Refresh Token 쿠키와 함께 `localStorage`의 Access Token도 삭제한다.

이미 발급된 Access Token 자체는 만료 전까지 유효할 수 있다.

---

### Step 6. 역할 기반 인가 구현하기

메서드 단위 인가를 활성화한다.

```java
@EnableMethodSecurity(prePostEnabled = true)
```

일반 사용자 API에 다음 권한을 적용한다.

```java
@PreAuthorize("hasRole('USER')")
@GetMapping("/user")
public AuthorityResponseDto authority() {
    return AuthorityResponseDto.builder()
            .message(
                    "일반 사용자만 볼 수 있는 권한입니다."
            )
            .build();
}
```

관리자 API에는 다음 권한을 적용한다.

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin")
public AuthorityResponseDto authorityAdmin() {
    return AuthorityResponseDto.builder()
            .message(
                    "관리자만 볼 수 있는 권한입니다."
            )
            .build();
}
```

`hasRole('USER')`는 내부적으로 `ROLE_USER` 권한을 확인한다.

권한 계층을 등록하면 ADMIN이 USER 권한도 포함한다.

```java
@Bean
public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl
            .withDefaultRolePrefix()
            .role("ADMIN")
            .implies("USER")
            .build();
}
```

동작 결과는 다음과 같다.

| 계정           | `/api/users/user` | `/api/users/admin` |
| ------------ | ----------------- | ------------------ |
| `ROLE_USER`  | 200               | 403                |
| `ROLE_ADMIN` | 200               | 200                |

---

## 6. 최종 코드

### `SecurityConfig`

```java
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final TokenAuthenticationFilter
            tokenAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .logout(
                        AbstractHttpConfigurer::disable
                )
                .formLogin(
                        AbstractHttpConfigurer::disable
                )
                .httpBasic(
                        AbstractHttpConfigurer::disable
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy
                                        .STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/admin",
                                "/users/login",
                                "/users/join",
                                "/api/users/join",
                                "/api/users/login",
                                "/api/users/logout",
                                "/api/tokens/refresh",
                                "/css/**",
                                "/js/**",
                                "/access-denied",
                                "/error"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        tokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter
                                .class
                )

                .exceptionHandling(exception ->
                        exception
                                .accessDeniedHandler(
                                        accessDeniedHandler()
                                )
                                .authenticationEntryPoint(
                                        authenticationEntryPoint()
                                )
                );

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl
                .withDefaultRolePrefix()
                .role("ADMIN")
                .implies("USER")
                .build();
    }

    @Bean
    public BCryptPasswordEncoder
    bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager
    authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration
                .getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler
    accessDeniedHandler() {
        return (request, response, exception) -> {
            if (
                    request.getRequestURI()
                            .startsWith("/api/")
            ) {
                sendErrorJson(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        "접근 권한이 없습니다."
                );

                return;
            }

            response.sendRedirect("/access-denied");
        };
    }

    @Bean
    public AuthenticationEntryPoint
    authenticationEntryPoint() {
        return (request, response, exception) -> {
            if (
                    request.getRequestURI()
                            .startsWith("/api/")
            ) {
                sendErrorJson(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "인증이 필요합니다."
                );

                return;
            }

            response.sendRedirect("/access-denied");
        };
    }

    private void sendErrorJson(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType(
                "application/json;charset=UTF-8"
        );

        response.getWriter().write(
                "{\"status\": "
                        + status
                        + ", \"message\": \""
                        + message
                        + "\"}"
        );
    }
}
```

### `common.js`

```javascript
let setupAjax = () => {
    $.ajaxSetup({
        beforeSend: (xhr) => {
            let token =
                localStorage.getItem(
                    'accessToken'
                );

            if (token) {
                xhr.setRequestHeader(
                    'Authorization',
                    'Bearer ' + token
                );
            }
        }
    });
};

let refreshTokens = () => {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'POST',
            url: '/api/tokens/refresh',
            dataType: 'json',
            xhrFields: {
                withCredentials: true
            },

            success: (response) => {
                localStorage.setItem(
                    'accessToken',
                    response.accessToken
                );

                resolve(response);
            },

            error: (xhr) => {
                reject(xhr);
            }
        });
    });
};

let getUserInfo = () => {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'GET',
            url: '/api/users/info',
            dataType: 'json',
            success: resolve,
            error: reject
        });
    });
};

let redirectToLogin = () => {
    alert(
        '로그인이 필요합니다. 다시 로그인해주세요.'
    );

    localStorage.removeItem('accessToken');
    window.location.href = '/users/login';
};
```

### `signIn.js`

```javascript
$.ajax({
    type: 'POST',
    url: '/api/users/login',
    contentType:
        'application/json; charset=utf-8',
    data: JSON.stringify(formData),
    dataType: 'json',

    success: function(response) {
        localStorage.setItem(
            'accessToken',
            response.accessToken
        );

        alert(response.message);
        window.location.href = response.url;
    }
});
```

### 사용자 정보 조회 흐름

```javascript
let loadUserInfo = async () => {
    try {
        if (
            localStorage.getItem(
                'accessToken'
            ) == null
        ) {
            await refreshTokens();
        }

        renderUserInfo(
            await getUserInfo()
        );

    } catch (error) {
        try {
            await refreshTokens();

            renderUserInfo(
                await getUserInfo()
            );

        } catch (refreshError) {
            redirectToLogin();
        }
    }
};
```

---

## 7. 동작 확인

### 로그인

```bash
curl -i \
  -c cookie.txt \
  -H "Content-Type: application/json" \
  -d '{"userId":"kim","password":"1234"}' \
  http://localhost:8080/api/users/login
```

응답 body에는 Access Token이 포함된다.

```json
{
  "isLoggedIn": true,
  "url": "/",
  "userName": "김개발",
  "userId": "kim",
  "message": "로그인 성공",
  "accessToken": "eyJ..."
}
```

응답 헤더에는 Refresh Token 쿠키가 포함된다.

```http
Set-Cookie: refreshToken=eyJ...; Path=/; HttpOnly
```

응답 body에는 Refresh Token이 포함되지 않아야 한다.

### 인증 요청

```bash
curl -i \
  -H "Authorization: Bearer access-token" \
  http://localhost:8080/api/users/info
```

토큰 없이 요청하면 401을 반환한다.

### 토큰 재발급

```bash
curl -i \
  -b cookie.txt \
  -X POST \
  http://localhost:8080/api/tokens/refresh
```

성공하면 새로운 Access Token을 반환하고 Refresh Token 쿠키를 갱신한다.

### 권한 확인

`ROLE_USER` 사용자가 관리자 API를 호출하면 403을 반환한다.

```bash
curl -i \
  -H "Authorization: Bearer access-token" \
  http://localhost:8080/api/users/admin
```

---

## 자주 발생하는 문제

| 증상                             | 원인과 해결                                             |
| ------------------------------ | -------------------------------------------------- |
| `Cannot resolve symbol 'Jwts'` | JJWT 버전과 Gradle 의존성 로드 상태 확인                       |
| `WeakKeyException`             | HS512 Secret Key를 64바이트 이상으로 생성                    |
| 로그인 요청이 실패                     | JWT 로그인 요청을 JSON으로 전송하는지 확인                        |
| 아이디와 비밀번호가 맞지만 실패              | `UserDetailsService` 중복 등록 또는 BCrypt 암호화 누락 확인     |
| Access Token이 있는데 401          | `Bearer ` 접두사, 만료 시간과 Secret Key 확인                |
| 토큰 재발급 실패                      | `/api/tokens/refresh`가 `permitAll`인지 확인            |
| Refresh Token 쿠키가 저장되지 않음      | 로컬 HTTP 환경에서 `Secure=true`인지 확인                    |
| 재발급 후 다시 401                   | 새 Access Token 저장과 기존 요청 재시도 확인                    |
| API 응답으로 HTML이 반환됨             | API의 401·403을 JSON으로 처리                            |
| `/error` 요청 반복                 | `/error`를 `permitAll`에 포함                          |
| 관리자도 USER API에서 403            | `RoleHierarchy` 빈 확인                               |
| 로그아웃 후 기존 Access Token 사용 가능   | JWT는 서버에서 즉시 무효화되지 않아 만료까지 유효                      |
| DB 역할 변경이 바로 반영되지 않음           | 토큰에는 발급 시점의 역할이 저장됨                                |
| 사용자 입력으로 스크립트 실행               | `.html()` 대신 `.text()`, `th:utext` 대신 `th:text` 사용 |

---

## 6. 학습 체크

* [ ] JWT 세 부분(Header/Payload/Signature)의 역할과, payload가 암호화가 아닌 이유를 설명할 수 있다
* [ ] 클레임이 무엇인지, 등록된 클레임(sub/iss/exp/iat)과 비공개 클레임을 구분할 수 있다
* [ ] 서명이 유효한 것이 왜 신뢰의 근거가 되는지 설명할 수 있다
* [ ] 로그인 요청이 Form Login과 달리 왜 JSON인지 설명할 수 있다
* [ ] Access Token과 Refresh Token을 나누는 이유와 저장 위치를 설명할 수 있다
* [ ] XSS와 CSRF 중 localStorage와 HttpOnly 쿠키가 각각 어떤 위험과 관련되는지 설명할 수 있다
* [ ] 필터는 인증을 복원하고 인가 과정이 요청을 거부한다는 역할 분리를 설명할 수 있다
* [ ] 401과 403의 차이와 각각의 처리 지점을 설명할 수 있다
* [ ] principal과 `@AuthenticationPrincipal`의 동작을 설명할 수 있다
* [ ] 서버 재시작 시 Basic, Form Login과 JWT의 인증 상태가 다른 이유를 설명할 수 있다
* [ ] stateless에서 즉시 로그아웃과 권한 회수가 어려운 이유를 설명할 수 있다
* [ ] HTML 화면은 공개하고 실제 인가는 API에서 수행하는 이유를 설명할 수 있다

## 7. 최종 완성 체크리스트

* [ ] 로그인 성공 시 Access Token은 응답 body로, Refresh Token은 HttpOnly 쿠키로 발급된다
* [ ] 응답 body에 Refresh Token이 노출되지 않는다
* [ ] Bearer Token으로 `/api/users/info`가 조회되고, 토큰 없이는 401 JSON이 반환된다
* [ ] JWT Payload가 디코딩되는 것과 Payload 조작 시 서명 검증이 실패하는 것을 확인했다
* [ ] `localStorage`의 Access Token을 삭제해도 Refresh Token 쿠키로 다시 발급된다
* [ ] Refresh Token이 없거나 만료되면 로그인 페이지로 이동한다
* [ ] 로그아웃 시 쿠키와 `localStorage`가 정리되며 기존 Access Token은 만료까지 유효할 수 있다
* [ ] `ROLE_USER`와 `ROLE_ADMIN` 계정이 권한 확인 표대로 동작한다
* [ ] 서버를 재시작해도 JWT 인증이 유지되는 이유를 Form Login과 비교해 설명할 수 있다
