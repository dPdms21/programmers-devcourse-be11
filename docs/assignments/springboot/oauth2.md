# OAuth2 카카오 소셜 로그인

> 카카오 OAuth2 인가 코드 방식을 이용해 소셜 로그인을 구현한다.
> 카카오 인증 결과를 우리 서비스의 회원과 연결하고, 최종적으로 기존 JWT 인증 체계에 합류시킨다.

---

## 0. 먼저 알아둘 점

이번 과제는 기존 JWT 토큰 인증에 카카오 로그인이라는 새로운 인증 진입점을 추가하는 작업이다.

카카오는 사용자가 카카오 회원이라는 사실을 확인하고 사용자 정보를 제공한다.

우리 서비스는 카카오에서 받은 사용자 정보를 기준으로 회원을 조회하거나 가입시킨 뒤, 기존 JWT Access Token과 Refresh Token을 발급한다.

```text
카카오 인증
→ 카카오 사용자 정보 조회
→ 우리 회원과 연결
→ 우리 JWT 발급
→ 이후 요청은 기존 JWT 인증 필터 사용
```

카카오 Access Token과 우리 서비스 JWT는 역할이 다르다.

| 토큰               | 역할                  |
| ---------------- | ------------------- |
| 카카오 Access Token | 카카오 사용자 정보 조회       |
| 우리 Access Token  | 우리 API 요청 인증        |
| 우리 Refresh Token | 우리 Access Token 재발급 |

카카오 Access Token은 OAuth2 로그인 과정에서 사용자 정보를 조회할 때만 사용한다.

로그인이 끝난 뒤 우리 API 인증은 기존 JWT를 사용한다.

이번 과제는 기존 JWT 프로젝트를 기반으로 다음 기능을 추가한다.

* 카카오 OAuth2 로그인
* 기존 소셜 회원 조회
* 미가입 소셜 회원 가입 동의
* 가입용 단기 JWT 발급
* 소셜 가입 완료 후 JWT 발급
* OAuth2 성공·실패 후처리
* 소셜 회원과 자체 회원의 인증 체계 통합

---

## 1. 구현 기능

| 요청                                | 역할                        | 처리 주체                     |
| --------------------------------- | ------------------------- | ------------------------- |
| `GET /oauth2/authorization/kakao` | 카카오 인가 페이지 이동             | Spring Security 필터        |
| `GET /login/oauth2/code/kakao`    | 인가 코드 처리                  | Spring Security 필터        |
| `loadUser()`                      | 카카오 사용자와 우리 회원 연결         | `CustomOAuth2UserService` |
| OAuth2 성공 처리                      | 기존 회원 JWT 발급 또는 가입 페이지 이동 | `OAuth2SuccessHandler`    |
| OAuth2 실패 처리                      | 실패 메시지와 로그인 페이지 이동        | `OAuth2FailureHandler`    |
| `GET /users/oauth-join`           | 소셜 회원가입 동의 화면             | `UserController`          |
| `POST /api/users/oauth-join`      | 소셜 회원가입 확정                | `UserApiController`       |
| 기존 JWT API                        | 정보 조회, 권한 확인, 재발급, 로그아웃   | 기존 코드 재사용                 |

두 OAuth2 URL에는 직접 작성한 컨트롤러가 없다.

```text
/oauth2/authorization/kakao
/login/oauth2/code/kakao
```

두 요청은 `DispatcherServlet`에 도달하기 전에 Spring Security 필터가 처리한다.

---

## 2. 학습 목표

| 개념                        | 단계            |
| ------------------------- | ------------- |
| OAuth2 등장인물과 인가 코드 방식     | 핵심 개념, Step 0 |
| registration과 provider 설정 | Step 0        |
| 소셜 회원 식별 기준               | Step 1        |
| 제공자별 사용자 정보 추상화           | Step 2        |
| `loadUser()`와 회원 연결       | Step 3        |
| OAuth2 성공·실패 핸들러          | Step 4        |
| 가입용 단기 JWT                | Step 5        |
| 기존 JWT 인증 체계와 통합          | Step 5~6      |

---

## 3. 핵심 개념

### (1) OAuth2 등장인물

OAuth2는 사용자의 비밀번호를 제3자 서비스에 전달하지 않고, 특정 자원에 대한 접근 권한을 위임하는 방식이다.

| 역할                   | 의미              | 이번 과제             |
| -------------------- | --------------- | ----------------- |
| Resource Owner       | 자원의 소유자         | 카카오 사용자           |
| Client               | 자원 사용을 요청하는 서비스 | 우리 서비스            |
| Authorization Server | 인가를 처리하는 서버     | `kauth.kakao.com` |
| Resource Server      | 사용자 정보를 제공하는 서버 | `kapi.kakao.com`  |

OAuth2에서 Client는 사용자가 아니라 우리 서비스다.

`client-id`와 `client-secret`도 우리 서비스가 카카오에 등록한 자격 증명이다.

### (2) 인가 코드 방식

인가 코드 방식은 다음 순서로 처리된다.

```text
1. 우리 서비스가 사용자를 카카오 인가 화면으로 이동
2. 사용자가 카카오에서 로그인하고 동의
3. 카카오가 브라우저를 통해 인가 코드를 전달
4. 우리 서버가 인가 코드와 Client Secret으로 카카오 토큰 요청
5. 카카오가 Access Token 발급
6. 우리 서버가 카카오 사용자 정보 조회
```

브라우저를 거치는 1~3단계는 프론트 채널이다.

서버끼리 통신하는 4~6단계는 백 채널이다.

진짜 Access Token을 브라우저에 직접 전달하지 않고, 일회용 인가 코드를 먼저 전달하는 이유는 토큰 노출 위험을 줄이기 위해서다.

인가 코드를 탈취하더라도 Client Secret이 없으면 Access Token으로 교환하기 어렵다.

### (3) state 파라미터

`state`는 OAuth2 요청을 시작할 때 생성하는 임의 값이다.

카카오 콜백에서 동일한 값이 반환되는지 확인하여 요청 위조를 방지한다.

```text
인가 요청 state
→ 카카오 로그인
→ 콜백 state
→ 두 값 비교
```

공격자가 자신의 카카오 인가 코드를 피해자에게 전달해 계정을 잘못 연결시키는 OAuth2 로그인 CSRF를 막는 역할을 한다.

Spring Security가 `state` 생성과 검증을 자동으로 처리한다.

### (4) Spring Security OAuth2 로그인 흐름

```text
GET /oauth2/authorization/kakao
→ OAuth2AuthorizationRequestRedirectFilter
→ state 생성
→ 카카오 인가 화면 이동

GET /login/oauth2/code/kakao
→ OAuth2LoginAuthenticationFilter
→ state 검증
→ 인가 코드와 Access Token 교환
→ 카카오 사용자 정보 조회
→ CustomOAuth2UserService.loadUser()
→ OAuth2SuccessHandler 또는 OAuth2FailureHandler
```

개발자가 직접 구현하는 주요 지점은 다음 두 곳이다.

| 지점                     | 역할                        |
| ---------------------- | ------------------------- |
| `loadUser()`           | 카카오 사용자와 우리 회원 연결         |
| `OAuth2SuccessHandler` | 기존 회원 JWT 발급 또는 가입 페이지 이동 |

### (5) 카카오 Access Token과 우리 JWT

카카오 Access Token은 카카오 Resource Server에서 사용자 정보를 가져오는 데 사용한다.

우리 JWT는 우리 서비스의 API 인증과 권한 처리에 사용한다.

```text
카카오 Access Token
→ 카카오 사용자 정보 조회
→ 사용 종료

우리 JWT
→ 우리 API 인증
→ 사용자 권한 처리
→ Access Token 재발급
```

카카오 로그인이 성공해도 우리 서비스가 직접 JWT를 발급하는 이유는 우리 서비스의 사용자, 권한과 만료 정책을 직접 관리하기 위해서다.

### (6) 소셜 회원 식별 기준

소셜 회원은 이메일이 아니라 다음 조합으로 식별한다.

```text
(provider, providerId)
```

카카오 회원의 경우 다음과 같다.

```text
provider   = KAKAO
providerId = 카카오 회원번호
```

이메일은 사용자가 동의하지 않을 수 있고 변경될 수도 있다.

반면 카카오 회원번호는 해당 제공자 안에서 사용자를 식별하는 값이다.

같은 이메일이라도 카카오와 다른 제공자의 계정은 서로 다른 소셜 회원일 수 있다.

### (7) 명시적 가입 정책

카카오 인증에 성공했다고 곧바로 회원을 저장하지 않는다.

미가입 사용자는 가입 동의 화면으로 이동하고, 사용자가 동의한 시점에 DB에 저장한다.

```text
카카오 인증 성공
→ 우리 DB 회원 조회
→ 미가입
→ 가입 동의 페이지
→ 가입 동의
→ DB 저장
→ 우리 JWT 발급
```

동의 전 사용자 정보를 보관하는 방법은 다음과 같다.

| 방법       | 문제                  |
| -------- | ------------------- |
| 세션 저장    | stateless 구조와 맞지 않음 |
| DB 임시 저장 | 가입하지 않은 계정이 남을 수 있음 |
| 단기 JWT   | 서버 저장 없이 서명으로 변조 방지 |

이번 과제에서는 카카오 사용자 정보를 10분 동안 유효한 가입용 JWT에 저장한다.

```text
type     = signup
provider = KAKAO
sub      = 카카오 회원번호
email    = 카카오 이메일
name     = 카카오 닉네임
```

가입 토큰에는 `type=signup` 클레임을 넣는다.

이를 통해 일반 Access Token과 가입 토큰을 서로 다른 용도로 구분한다.

### (8) OAuth2 성공 응답과 리다이렉트

자체 로그인은 AJAX 요청이므로 응답 body에 Access Token을 반환할 수 있다.

OAuth2 로그인은 브라우저 전체가 리다이렉트되는 흐름이므로 성공 핸들러의 응답 body를 처리할 JavaScript가 없다.

따라서 기존 회원 로그인에서는 Refresh Token만 HttpOnly 쿠키로 저장하고 메인 화면으로 이동한다.

```text
OAuth2 로그인 성공
→ Refresh Token 쿠키 저장
→ "/" 이동
→ 기존 프론트 로직이 Refresh Token으로 Access Token 재발급
```

Access Token을 URL 쿼리 파라미터로 전달하면 브라우저 기록과 로그, Referer 등에 노출될 수 있으므로 사용하지 않는다.

### (9) OAuth2 실패 응답

OAuth2 로그인은 브라우저 이동으로 진행되므로 실패 시 JSON보다 리다이렉트가 적합하다.

```text
OAuth2 실패
→ /users/login?error=실패메시지
→ 로그인 화면에서 오류 출력
```

자체 로그인 AJAX 요청은 상태 코드와 JSON을 사용한다.

OAuth2 브라우저 이동은 리다이렉트와 쿼리 파라미터를 사용한다.

### (10) OAuth2와 세션

JWT 인증은 `SessionCreationPolicy.STATELESS`로 설정한다.

다만 OAuth2 인가 과정에서 `state`와 인가 요청 정보를 잠시 보관하기 위해 기본 구현은 세션을 사용할 수 있다.

이는 로그인 이후 사용자 인증 정보를 세션에 저장하는 것과는 다르다.

OAuth2 핸드셰이크가 끝난 뒤 우리 API 인증은 JWT로 처리한다.

---

## 4. 프로젝트 준비

기존 JWT 프로젝트의 다음 구성을 재사용한다.

* `TokenProvider`
* `TokenAuthenticationFilter`
* `TokenService`
* `CookieUtil`
* Access Token 재발급
* 로그아웃
* `User`
* `Role`
* `CustomUserDetails`
* 자체 회원가입과 로그인

### OAuth2 Client 의존성

```gradle
implementation 'org.springframework.boot:spring-boot-starter-security-oauth2-client'
```

이번 서비스는 카카오의 사용자 정보를 사용하는 OAuth2 Client이므로 `oauth2-client`를 사용한다.

### 카카오 개발자 설정

1. 카카오 개발자 사이트에서 애플리케이션을 생성한다.
2. REST API 키를 확인한다.
3. 카카오 로그인을 활성화한다.
4. Redirect URI를 등록한다.
5. Client Secret을 생성하고 활성화한다.
6. 닉네임 동의 항목을 설정한다.

Redirect URI는 다음 값과 정확히 일치해야 한다.

```text
http://localhost:8080/login/oauth2/code/kakao
```

### OAuth2 설정

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: ${KAKAO_CLIENT_ID}
            client-secret: ${KAKAO_CLIENT_SECRET}
            redirect-uri: "{baseUrl}/login/oauth2/code/kakao"
            authorization-grant-type: authorization_code
            client-authentication-method: client_secret_post
            client-name: Kakao
            scope:
              - profile_nickname

        provider:
          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-name-attribute: id
```

`registration.kakao`의 `kakao`가 registrationId가 된다.

다음 경로의 마지막 값도 registrationId다.

```text
/oauth2/authorization/kakao
/login/oauth2/code/kakao
```

카카오는 Client Secret을 HTTP Basic 헤더가 아니라 POST body로 전달받으므로 다음 설정이 필요하다.

```yaml
client-authentication-method: client_secret_post
```

Client ID와 Client Secret은 공개 저장소에 커밋하지 않는다.

---

## 5. Step by Step

### Step 0. 카카오 OAuth2 설정하기

기존 JWT 프로젝트에 OAuth2 Client 의존성을 추가한다.

카카오 개발자 콘솔에서 REST API 키, Client Secret과 Redirect URI를 설정한다.

`application.yaml`에 registration과 provider 설정을 추가한다.

서버 실행 후 다음 경로에 접속한다.

```text
http://localhost:8080/oauth2/authorization/kakao
```

카카오 로그인 화면으로 이동하면 OAuth2 Redirect Filter가 정상적으로 등록된 상태다.

---

### Step 1. 소셜 회원 필드 추가하기

### `AuthProvider`

```java
public enum AuthProvider {
    LOCAL,
    KAKAO;

    public static AuthProvider from(
            String registrationId
    ) {
        return AuthProvider.valueOf(
                registrationId.toUpperCase()
        );
    }
}
```

자체 가입 회원은 `LOCAL`, 카카오 회원은 `KAKAO`로 저장한다.

### `User`

```java
@Enumerated(EnumType.STRING)
@Column(length = 20)
@Builder.Default
private AuthProvider provider =
        AuthProvider.LOCAL;

@Column(name = "provider_id", length = 100)
private String providerId;

public User updateProfile(String name) {
    this.name = name;
    return this;
}
```

`provider`는 문자열로 저장한다.

기존 자체 회원은 기본값으로 `LOCAL`을 사용한다.

### DB 컬럼 추가

```sql
ALTER TABLE user
ADD COLUMN provider VARCHAR(20)
NOT NULL DEFAULT 'LOCAL';

ALTER TABLE user
ADD COLUMN provider_id VARCHAR(100)
NULL;
```

DB 기본값은 기존 행을 `LOCAL`로 채우는 역할을 한다.

새로운 JPA 엔티티 생성 시 기본값은 `@Builder.Default`가 담당한다.

### `UserRepository`

```java
Optional<User> findByProviderAndProviderId(
        AuthProvider provider,
        String providerId
);
```

소셜 회원은 `provider`와 `providerId` 조합으로 조회한다.

---

### Step 2. 제공자 사용자 정보 추상화하기

### `OAuth2UserInfo`

```java
public interface OAuth2UserInfo {

    Map<String, Object> attributes();

    String id();

    String email();

    String name();

    String imageUrl();
}
```

제공자마다 사용자 정보 응답 형식이 다르므로 공통 인터페이스로 추상화한다.

### `KakaoUserInfo`

```java
public record KakaoUserInfo(
        Map<String, Object> attributes
) implements OAuth2UserInfo {

    @Override
    public String id() {
        Object id = attributes.get("id");

        return id == null
                ? null
                : String.valueOf(id);
    }

    @Override
    public String email() {
        Map<String, Object> account =
                kakaoAccount();

        return account == null
                ? null
                : (String) account.get("email");
    }

    @Override
    public String name() {
        Map<String, Object> profile =
                profile();

        return profile == null
                ? null
                : (String) profile.get(
                        "nickname"
                );
    }

    @Override
    public String imageUrl() {
        Map<String, Object> profile =
                profile();

        return profile == null
                ? null
                : (String) profile.get(
                        "profile_image_url"
                );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> kakaoAccount() {
        return (Map<String, Object>)
                attributes.get("kakao_account");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> profile() {
        Map<String, Object> account =
                kakaoAccount();

        return account == null
                ? null
                : (Map<String, Object>)
                        account.get("profile");
    }
}
```

카카오는 동의하지 않은 항목의 키를 생략할 수 있으므로 중첩 값 접근 시 null을 확인한다.

이 클래스는 값 추출만 담당한다.

이메일 누락 시 로그인 거부와 같은 정책은 상위 서비스에서 판단한다.

### `OAuth2UserInfoFactory`

```java
public final class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
    }

    public static OAuth2UserInfo of(
            AuthProvider provider,
            Map<String, Object> attributes
    ) {
        return switch (provider) {
            case KAKAO ->
                    new KakaoUserInfo(attributes);

            case LOCAL ->
                    throw new IllegalArgumentException(
                            "LOCAL은 OAuth2 제공자가 아닙니다."
                    );
        };
    }
}
```

새로운 제공자를 추가할 때는 해당 제공자의 `OAuth2UserInfo` 구현체와 팩토리 분기를 추가한다.

---

### Step 3. 카카오 사용자와 우리 회원 연결하기

### `CustomOAuth2User`

```java
@Getter
public class CustomOAuth2User
        implements OAuth2User {

    private final User user;
    private final AuthProvider provider;
    private final OAuth2UserInfo userInfo;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    public CustomOAuth2User(
            User user,
            AuthProvider provider,
            OAuth2UserInfo userInfo,
            Map<String, Object> attributes,
            String nameAttributeKey
    ) {
        this.user = user;
        this.provider = provider;
        this.userInfo = userInfo;
        this.attributes = attributes;
        this.nameAttributeKey =
                nameAttributeKey;
    }

    public static CustomOAuth2User unregistered(
            AuthProvider provider,
            OAuth2UserInfo userInfo,
            Map<String, Object> attributes,
            String nameAttributeKey
    ) {
        return new CustomOAuth2User(
                null,
                provider,
                userInfo,
                attributes,
                nameAttributeKey
        );
    }

    public boolean isRegistered() {
        return user != null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority>
    getAuthorities() {
        if (user == null) {
            return List.of(
                    new SimpleGrantedAuthority(
                            "ROLE_GUEST"
                    )
            );
        }

        return List.of(
                new SimpleGrantedAuthority(
                        user.getRole().name()
                )
        );
    }

    @Override
    public String getName() {
        return String.valueOf(
                attributes.get(nameAttributeKey)
        );
    }
}
```

`CustomOAuth2User`는 OAuth2 인증 결과와 우리 `User`를 연결하는 어댑터다.

기존 회원이면 `User`가 존재하고, 미가입 사용자는 `User`가 `null`이다.

### `CustomOAuth2UserService`

```java
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService
        extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(
            OAuth2UserRequest userRequest
    ) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User =
                super.loadUser(userRequest);

        String registrationId =
                userRequest
                        .getClientRegistration()
                        .getRegistrationId();

        String nameAttributeKey =
                userRequest
                        .getClientRegistration()
                        .getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUserNameAttributeName();

        AuthProvider provider =
                AuthProvider.from(
                        registrationId
                );

        OAuth2UserInfo userInfo =
                OAuth2UserInfoFactory.of(
                        provider,
                        oAuth2User.getAttributes()
                );

        if (userInfo.email() == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            "email_not_found"
                    ),
                    "SNS 계정에서 이메일을 가져오지 못했습니다."
            );
        }

        return userRepository
                .findByProviderAndProviderId(
                        provider,
                        userInfo.id()
                )
                .map(existing -> {
                    existing.updateProfile(
                            userInfo.name()
                    );

                    return new CustomOAuth2User(
                            existing,
                            provider,
                            userInfo,
                            oAuth2User.getAttributes(),
                            nameAttributeKey
                    );
                })
                .orElseGet(() ->
                        CustomOAuth2User.unregistered(
                                provider,
                                userInfo,
                                oAuth2User
                                        .getAttributes(),
                                nameAttributeKey
                        )
                );
    }
}
```

`super.loadUser()`는 카카오 User Info API를 호출하고 원시 사용자 정보를 반환한다.

이 메서드는 실제 HTTP 요청을 실행하므로 한 번만 호출한다.

기존 회원이면 닉네임을 갱신하고, 미가입이면 DB에 저장하지 않은 상태로 `CustomOAuth2User`를 반환한다.

카카오 일반 애플리케이션에서 이메일 동의 항목을 사용할 수 없는 경우 실습 중에는 이메일 필수 검증 정책을 조정한다.

`OAuth2AuthenticationException`을 던져야 OAuth2 실패 핸들러로 전달된다.

---

### Step 4. OAuth2 성공·실패 처리하기

### `OAuth2FailureHandler`

```java
@Component
public class OAuth2FailureHandler
        extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        String targetUrl =
                UriComponentsBuilder
                        .fromUriString(
                                "/users/login"
                        )
                        .queryParam(
                                "error",
                                exception
                                        .getLocalizedMessage()
                        )
                        .encode(
                                StandardCharsets.UTF_8
                        )
                        .build()
                        .toUriString();

        getRedirectStrategy()
                .sendRedirect(
                        request,
                        response,
                        targetUrl
                );
    }
}
```

OAuth2 실패는 브라우저 이동 중 발생하므로 JSON 대신 로그인 화면으로 리다이렉트한다.

### `OAuth2SuccessHandler`

```java
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final JwtProperties jwtProperties;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOAuth2User principal =
                (CustomOAuth2User)
                        authentication.getPrincipal();

        String targetUrl;

        if (principal.isRegistered()) {
            TokenService.TokenPair tokens =
                    tokenService.issueTokens(
                            principal.getUser()
                    );

            CookieUtil.addCookie(
                    response,
                    CookieUtil
                            .REFRESH_TOKEN_COOKIE,
                    tokens.refreshToken(),
                    (int) jwtProperties
                            .getRefreshTokenValidity()
                            .toSeconds()
            );

            targetUrl = "/";

        } else {
            String signupToken =
                    tokenProvider
                            .createSignupToken(
                                    principal
                                            .getProvider(),
                                    principal
                                            .getUserInfo()
                            );

            targetUrl =
                    UriComponentsBuilder
                            .fromUriString(
                                    "/users/oauth-join"
                            )
                            .queryParam(
                                    "signupToken",
                                    signupToken
                            )
                            .build()
                            .toUriString();
        }

        if (response.isCommitted()) {
            return;
        }

        getRedirectStrategy()
                .sendRedirect(
                        request,
                        response,
                        targetUrl
                );
    }
}
```

기존 회원이면 우리 JWT Refresh Token을 쿠키에 저장하고 메인 화면으로 이동한다.

미가입 사용자면 가입용 단기 JWT를 발급하고 가입 동의 페이지로 이동한다.

### `SecurityConfig`

```java
.oauth2Login(oauth2 -> oauth2
        .userInfoEndpoint(userInfo ->
                userInfo.userService(
                        customOAuth2UserService
                )
        )
        .successHandler(
                oAuth2SuccessHandler
        )
        .failureHandler(
                oAuth2FailureHandler
        )
)
```

공개 경로에 다음 값을 추가한다.

```java
"/users/oauth-join",
"/api/users/oauth-join"
```

미가입 사용자가 접근하는 경로이므로 일반 JWT 인증을 요구하지 않는다.

---

### Step 5. 가입용 JWT와 소셜 가입 구현하기

### 가입 토큰 생성

`TokenProvider`에 가입 토큰 생성과 해석 기능을 추가한다.

```java
private static final String CLAIM_TYPE =
        "type";

private static final String
        TOKEN_TYPE_SIGNUP = "signup";

private static final String
        CLAIM_PROVIDER = "provider";

private static final String
        CLAIM_EMAIL = "email";

private static final Duration
        SIGNUP_TOKEN_VALIDITY =
        Duration.ofMinutes(10);
```

```java
public String createSignupToken(
        AuthProvider provider,
        OAuth2UserInfo userInfo
) {
    Date now = new Date();

    return Jwts.builder()
            .header()
            .type("JWT")
            .and()
            .issuer(jwtProperties.getIssuer())
            .issuedAt(now)
            .expiration(
                    new Date(
                            now.getTime()
                                    + SIGNUP_TOKEN_VALIDITY
                                    .toMillis()
                    )
            )
            .subject(userInfo.id())
            .claim(
                    CLAIM_TYPE,
                    TOKEN_TYPE_SIGNUP
            )
            .claim(
                    CLAIM_PROVIDER,
                    provider.name()
            )
            .claim(
                    CLAIM_EMAIL,
                    userInfo.email()
            )
            .claim(
                    CLAIM_NAME,
                    userInfo.name()
            )
            .signWith(
                    secretKey,
                    Jwts.SIG.HS512
            )
            .compact();
}
```

가입 토큰은 10분 동안 유효하다.

`type=signup` 클레임으로 일반 Access Token과 구분한다.

### `SignupPayloadDto`

```java
@Getter
@AllArgsConstructor
public class SignupPayloadDto {

    private final AuthProvider provider;
    private final String providerId;
    private final String email;
    private final String name;
}
```

### 가입 토큰 검증

```java
public SignupPayloadDto getSignupPayload(
        String token
) {
    Claims claims;

    try {
        claims = getClaims(token);

    } catch (Exception exception) {
        throw new IllegalArgumentException(
                "유효하지 않거나 만료된 가입 토큰입니다."
        );
    }

    if (
            !TOKEN_TYPE_SIGNUP.equals(
                    claims.get(
                            CLAIM_TYPE,
                            String.class
                    )
            )
    ) {
        throw new IllegalArgumentException(
                "가입 토큰이 아닙니다."
        );
    }

    return new SignupPayloadDto(
            AuthProvider.valueOf(
                    claims.get(
                            CLAIM_PROVIDER,
                            String.class
                    )
            ),
            claims.getSubject(),
            claims.get(
                    CLAIM_EMAIL,
                    String.class
            ),
            claims.get(
                    CLAIM_NAME,
                    String.class
            )
    );
}
```

서명, 만료 시간과 토큰 용도를 검증한다.

Access Token을 가입 API에 전달하면 `type` 검사에서 거부된다.

### `OAuthSignUpRequestDto`

```java
@Getter
public class OAuthSignUpRequestDto {

    private String signupToken;
    private Role role;
}
```

이름과 이메일은 클라이언트가 전송하지 않는다.

서버가 서명된 가입 토큰에서 직접 복원한다.

클라이언트가 이름과 이메일을 전달하면 값을 조작할 수 있기 때문이다.

### `UserService.oauthSignUp()`

```java
public SignInResponseDto oauthSignUp(
        OAuthSignUpRequestDto request
) {
    SignupPayloadDto payload =
            tokenService.getSignupPayload(
                    request.getSignupToken()
            );

    Role role = request.getRole();

    User user = userRepository
            .findByProviderAndProviderId(
                    payload.getProvider(),
                    payload.getProviderId()
            )
            .orElseGet(() ->
                    userRepository.save(
                            User.builder()
                                    .userId(
                                            payload
                                                    .getProvider()
                                                    .name()
                                                    .toLowerCase()
                                            + "_"
                                            + payload
                                                    .getProviderId()
                                    )
                                    .name(
                                            payload.getName()
                                    )
                                    .email(
                                            payload.getEmail()
                                    )
                                    .provider(
                                            payload.getProvider()
                                    )
                                    .providerId(
                                            payload
                                                    .getProviderId()
                                    )
                                    .role(
                                            role != null
                                                    ? role
                                                    : Role
                                                    .ROLE_USER
                                    )
                                    .build()
                    )
            );

    TokenService.TokenPair tokens =
            tokenService.issueTokens(user);

    return SignInResponseDto.builder()
            .isLoggedIn(true)
            .message(
                    "가입이 완료되었습니다."
            )
            .url("/")
            .accessToken(
                    tokens.accessToken()
            )
            .refreshToken(
                    tokens.refreshToken()
            )
            .userId(user.getUserId())
            .userName(user.getName())
            .build();
}
```

이미 가입한 회원이면 기존 회원을 사용하므로 같은 가입 요청이 반복되어도 중복 회원이 생기지 않는다.

소셜 회원의 `userId`는 다음 규칙으로 생성한다.

```text
kakao_{providerId}
```

소셜 회원의 비밀번호는 저장하지 않는다.

따라서 자체 로그인 폼으로는 로그인할 수 없다.

### 뷰 컨트롤러

```java
@GetMapping("/oauth-join")
public String oauthSignUp() {
    return "oauth-join";
}
```

### 가입 확정 API

```java
@PostMapping("/oauth-join")
public SignInResponseDto oauthSignUp(
        @RequestBody
        OAuthSignUpRequestDto requestDto,
        HttpServletResponse response
) {
    SignInResponseDto result =
            userService.oauthSignUp(
                    requestDto
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

소셜 가입 완료 후 자체 로그인과 동일하게 Access Token은 응답 body로, Refresh Token은 HttpOnly 쿠키로 전달한다.

---

### Step 6. 기존 JWT 인증과 통합 확인하기

소셜 로그인 후 발급되는 토큰은 자체 로그인과 동일한 JWT다.

따라서 다음 기능을 그대로 사용할 수 있다.

* 사용자 정보 조회
* USER 권한 API
* ADMIN 권한 API
* Access Token 재발급
* 로그아웃
* 서버 재시작 후 인증 유지

로그인 진입점만 다르고 JWT 발급 이후의 인증 흐름은 동일하다.

```text
자체 로그인 ─┐
             ├→ 우리 JWT 발급
카카오 로그인 ┘
                 → TokenAuthenticationFilter
                 → SecurityContext
                 → 권한 검사
```

---

## 6. 화면과 JavaScript

### 카카오 로그인 버튼

```html
<a
    href="/oauth2/authorization/kakao"
    class="kakao-login-btn">
    카카오 로그인
</a>
```

카카오 로그인은 AJAX가 아니라 일반 링크로 시작한다.

### OAuth2 실패 메시지

```html
<div
    id="oauth-error"
    style="display:none;">
</div>

<script>
    const oauthError =
        new URLSearchParams(
            window.location.search
        ).get('error');

    if (oauthError) {
        document
            .getElementById('oauth-error')
            .textContent = oauthError;

        document
            .getElementById('oauth-error')
            .style.display = 'block';

        history.replaceState(
            null,
            '',
            window.location.pathname
        );
    }
</script>
```

오류 메시지는 `textContent`로 출력하여 HTML로 해석되지 않도록 한다.

### `oauth-join.html`

```html
<div class="signup-container">
    <h2>소셜 회원가입</h2>

    <p>
        카카오 계정 인증이 완료되었습니다.
        아래 정보로 가입하시겠습니까?
    </p>

    <label>닉네임</label>
    <input
        type="text"
        id="oauth_name"
        readonly>

    <label>이메일</label>
    <input
        type="text"
        id="oauth_email"
        readonly>

    <label for="role">권한</label>
    <select id="role">
        <option
            value="ROLE_USER"
            selected>
            일반 사용자
        </option>

        <option value="ROLE_ADMIN">
            관리자
        </option>
    </select>

    <input
        id="oauth-join"
        type="submit"
        value="동의하고 가입하기">

    <a href="/users/login">
        가입하지 않고 돌아가기
    </a>
</div>
```

관리자 역할 선택은 권한 학습용이다.

실제 서비스에서는 사용자가 관리자 권한을 선택할 수 없도록 서버에서 `ROLE_USER`로 고정해야 한다.

### `oauthJoin.js`

```javascript
$(document).ready(() => {
    const signupToken =
        new URLSearchParams(
            window.location.search
        ).get('signupToken');

    if (!signupToken) {
        alert(
            '잘못된 접근입니다. 소셜 로그인부터 진행해주세요.'
        );

        window.location.href =
            '/users/login';

        return;
    }

    history.replaceState(
        null,
        '',
        window.location.pathname
    );

    try {
        const base64 =
            signupToken
                .split('.')[1]
                .replace(/-/g, '+')
                .replace(/_/g, '/');

        const payload = JSON.parse(
            new TextDecoder().decode(
                Uint8Array.from(
                    atob(base64),
                    character =>
                        character.charCodeAt(0)
                )
            )
        );

        $('#oauth_name')
            .val(payload.name ?? '');

        $('#oauth_email')
            .val(payload.email ?? '');

    } catch (error) {
        console.error(
            '토큰 디코딩 실패:',
            error
        );
    }

    $('#oauth-join').click(() => {
        $.ajax({
            type: 'POST',
            url: '/api/users/oauth-join',

            contentType:
                'application/json; charset=utf-8',

            data: JSON.stringify({
                signupToken: signupToken,
                role: $('#role').val()
            }),

            dataType: 'json',

            success: response => {
                localStorage.setItem(
                    'accessToken',
                    response.accessToken
                );

                alert(response.message);

                window.location.href =
                    response.url;
            },

            error: xhr => {
                let response =
                    xhr.responseJSON;

                alert(
                    response
                    && response.message
                        ? response.message
                        : '가입 중 오류가 발생했습니다.'
                );

                window.location.href =
                    '/users/login';
            }
        });
    });
});
```

브라우저에서 가입 토큰의 Payload를 디코딩할 수 있다.

이는 JWT Payload가 암호화된 값이 아니라 인코딩된 값이라는 점을 보여준다.

화면에 표시된 값을 조작하더라도 서버는 가입 토큰의 서명과 클레임을 기준으로 가입 정보를 결정한다.

---

## 7. 동작 확인

### 첫 카카오 로그인

```text
/users/login
→ 카카오 로그인 클릭
→ 카카오 로그인과 동의
→ /users/oauth-join 이동
→ 닉네임과 이메일 표시
→ 동의하고 가입
→ DB 회원 저장
→ 우리 JWT 발급
→ 메인 화면 이동
```

DB에는 다음 형태로 저장된다.

```text
user_id     = kakao_123456789
provider    = KAKAO
provider_id = 123456789
password    = NULL
```

### 재로그인

```text
카카오 로그인
→ provider와 providerId로 기존 회원 조회
→ 닉네임 갱신
→ 가입 동의 없이 JWT 발급
→ 메인 화면 이동
```

### 권한과 기존 기능

소셜 계정으로 다음 API를 호출한다.

```text
GET /api/users/info
GET /api/users/user
GET /api/users/admin
POST /api/tokens/refresh
POST /api/users/logout
```

자체 가입 회원과 동일하게 동작해야 한다.

### 가입 토큰 검증

가입 토큰 Payload를 수정한 뒤 가입 API에 전달한다.

서명이 일치하지 않으므로 서버가 요청을 거부해야 한다.

가입 토큰이 10분 이상 지난 경우에도 가입을 거부해야 한다.

---

## 자주 발생하는 문제

| 증상                         | 원인과 해결                                               |
| -------------------------- | ---------------------------------------------------- |
| `KOE006`                   | 카카오 콘솔 Redirect URI와 YAML의 Redirect URI가 일치하는지 확인    |
| `KOE101` 또는 invalid client | REST API 키를 `client-id`로 사용했는지 확인                    |
| 토큰 교환에서 401                | `client_secret_post` 설정과 Client Secret 활성화 확인        |
| 서버 시작 시 설정 오류              | `user-name-attribute: id`와 YAML 들여쓰기 확인              |
| OAuth2 시작 경로가 404          | OAuth2 Client 의존성과 `.oauth2Login()` 설정 확인            |
| 이메일을 가져오지 못했다는 오류          | 카카오 이메일 동의 항목과 이메일 필수 정책 확인                          |
| `loadUser()` 예외가 500으로 처리됨 | `OAuth2AuthenticationException` 사용 확인                |
| `AuthProvider` 타입 오류       | `java.security.AuthProvider`가 아닌 프로젝트 enum import 확인 |
| 미가입자가 접근 거부됨               | OAuth2 가입 페이지와 API가 `permitAll`인지 확인                 |
| 가입 토큰이 아니라는 오류             | URL의 `signupToken`을 전달하는지 확인                         |
| 가입 토큰 만료 오류                | 10분 만료 또는 토큰 전달 과정 확인                                |
| 닉네임 변경이 DB에 반영되지 않음        | `loadUser()`의 `@Transactional` 확인                    |
| 기존 회원 조회 시 enum 오류         | 기존 데이터의 `provider` 값을 `LOCAL`로 갱신                    |
| 새 소셜 회원의 provider가 null    | `@Builder.Default`와 가입 코드의 provider 설정 확인            |
| 로그인 후 다시 로그인 화면으로 이동       | SuccessHandler의 Refresh Token 쿠키 저장 확인               |
| 로그인할 때마다 새 회원 생성           | `providerId`에 카카오 회원번호를 저장하는지 확인                     |

---

## 6. 학습 체크

* [ ] OAuth2 등장인물 4명을 우리 실습의 실체와 연결할 수 있다
* [ ] 인가 코드 방식에서 토큰 대신 코드를 먼저 전달하는 이유를 설명할 수 있다
* [ ] state 파라미터가 막는 공격과 검증 주체를 설명할 수 있다
* [ ] `/oauth2/authorization/kakao`와 `/login/oauth2/code/kakao`에 컨트롤러가 없는 이유를 설명할 수 있다
* [ ] 개발자가 구현하는 `loadUser()`와 SuccessHandler의 위치와 역할을 설명할 수 있다
* [ ] 카카오 Access Token과 우리 JWT의 용도 차이를 설명할 수 있다
* [ ] 소셜 회원을 이메일이 아니라 `(provider, providerId)`로 식별하는 이유를 설명할 수 있다
* [ ] 가입 동의 전 프로필 보관 방법과 가입 토큰을 선택한 이유를 설명할 수 있다
* [ ] 가입 토큰의 `type` 클레임이 막는 토큰 교차 사용을 설명할 수 있다
* [ ] OAuth2 실패가 JSON이 아니라 리다이렉트로 처리되는 이유를 설명할 수 있다
* [ ] SuccessHandler에서 Access Token을 URL로 전달하지 않는 이유를 설명할 수 있다
* [ ] stateless 설정과 OAuth2 핸드셰이크 중 세션 사용이 모순되지 않는 이유를 설명할 수 있다
* [ ] 새로운 OAuth2 제공자를 추가할 때 변경되는 부분을 구분할 수 있다

## 7. 최종 완성 체크리스트

* [ ] 카카오 로그인 후 첫 방문이면 가입 동의 페이지에 카카오 닉네임이 표시된다
* [ ] 동의하고 가입하면 즉시 로그인되고 DB에 `provider=KAKAO`, `password=NULL` 회원이 생성된다
* [ ] 재로그인 시 가입 동의 페이지 없이 바로 로그인된다
* [ ] 동일한 가입 토큰 요청이 반복되어도 중복 회원이 생성되지 않는다
* [ ] 카카오 동의를 취소하면 로그인 페이지에 실패 메시지가 표시된다
* [ ] 가입 토큰 Payload를 조작하면 서버가 요청을 거부한다
* [ ] 소셜 계정으로 기존 JWT API가 자체 계정과 동일하게 동작한다
* [ ] 소셜 회원의 `userId`와 자체 로그인 폼으로 로그인할 수 없다
* [ ] 기존 자체 가입 회원의 로그인이 계속 정상 동작한다
* [ ] Client ID와 Client Secret을 공개 저장소에 커밋하지 않았다
