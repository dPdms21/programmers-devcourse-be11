# HTTP Basic 인증

> Spring Security의 필터 체인에 HTTP Basic 인증을 적용한다.
> 인증되지 않은 요청에 대한 401 응답과 `WWW-Authenticate` 헤더를 확인하고, `Authorization` 헤더를 이용한 인증 흐름을 구현한다.
> `SecurityFilterChain`, `UserDetailsService`, `PasswordEncoder`를 설정하고 HTTP Basic의 stateless 특성과 브라우저 자격 증명 캐싱을 확인한다.

---

## 0. 먼저 알아둘 점

HTTP Basic 과제에서는 새로운 기능을 많이 구현하기보다 요청이 Spring Security의 필터 체인을 통과하는 과정을 관찰하고 이해하는 것이 중요하다.

구현할 주요 요소는 다음과 같다.

* `GET /hello` 요청을 처리하는 컨트롤러
* HTTP Basic 인증을 설정하는 `SecurityFilterChain`
* 인메모리 사용자를 관리하는 `UserDetailsService`
* 비밀번호를 비교하는 `PasswordEncoder`

`spring-boot-starter-security` 의존성을 추가하면 별도의 보안 설정이 없어도 모든 요청에 인증이 적용된다.

기본 설정에서는 사용자 이름으로 `user`를 사용하며, 애플리케이션을 실행할 때마다 임시 비밀번호가 새로 생성된다.

```text
Using generated security password: ...
```

자동 생성된 비밀번호는 현재 실행 중인 애플리케이션에서만 유효하다. 애플리케이션을 재시작하면 새로운 비밀번호가 생성되므로 이전 비밀번호로는 인증할 수 없다.

HTTP Basic은 사용자 이름과 비밀번호를 Base64로 인코딩하여 요청 헤더에 포함한다. Base64는 암호화 방식이 아니므로 실제 환경에서는 반드시 HTTPS와 함께 사용해야 한다.

---

## 1. 무엇을 만드는가?

`GET /hello` API를 만들고 HTTP Basic 인증을 적용한다.

완성된 애플리케이션은 다음과 같이 동작한다.

| 접근 방법             | 결과                                  |
| ----------------- | ----------------------------------- |
| 인증 없이 `/hello` 요청 | `401 Unauthorized`                  |
| 인증 없이 curl 요청     | `401`과 `WWW-Authenticate: Basic` 응답 |
| 올바른 계정으로 요청       | `200 OK`, `Hello World!` 응답         |
| 브라우저에서 최초 접근      | 사용자 이름과 비밀번호 입력 창 표시                |
| 브라우저에서 인증 후 재접근   | 저장된 자격 증명을 자동으로 전송하여 바로 접근          |

전체 요청 흐름은 다음과 같다.

```text
클라이언트 요청
→ Spring Security 필터 체인
→ HTTP Basic 인증 처리
→ 인증 성공
→ DispatcherServlet
→ HelloApiController
```

인증에 실패하면 요청은 컨트롤러까지 전달되지 않고 Spring Security 필터에서 401 응답으로 종료된다.

---

## 2. 학습 목표

| 개념                             | 학습 단계  |
| ------------------------------ | ------ |
| Spring Security 기본 자동 설정       | Step 1 |
| HTTP Basic의 401 인증 요청 흐름       | Step 2 |
| Base64 인코딩과 `Authorization` 헤더 | Step 2 |
| `SecurityFilterChain` 설정       | Step 3 |
| `UserDetailsService` 사용자 등록    | Step 4 |
| `PasswordEncoder`를 이용한 비밀번호 비교 | Step 4 |
| HTTP Basic의 stateless 특성       | Step 5 |
| 브라우저의 자격 증명 캐싱                 | Step 5 |

---

## 3. 핵심 개념

### (1) Spring Security 필터 체인

Spring Security는 서블릿 필터를 기반으로 동작한다.

클라이언트 요청은 컨트롤러에 도달하기 전에 여러 보안 필터를 순서대로 통과한다.

```text
요청
→ DelegatingFilterProxy
→ FilterChainProxy
→ SecurityFilterChain
→ BasicAuthenticationFilter
→ DispatcherServlet
→ Controller
```

각 요소의 역할은 다음과 같다.

| 요소                          | 역할                                  |
| --------------------------- | ----------------------------------- |
| `DelegatingFilterProxy`     | 서블릿 컨테이너와 Spring 빈으로 등록된 필터를 연결     |
| `FilterChainProxy`          | Spring Security의 필터 체인을 관리          |
| `SecurityFilterChain`       | 요청에 적용할 보안 필터 목록과 보안 규칙 정의          |
| `BasicAuthenticationFilter` | `Authorization: Basic` 헤더를 읽고 인증 처리 |
| `DispatcherServlet`         | 인증을 통과한 요청을 컨트롤러로 전달                |

`http.httpBasic()`을 설정하면 `BasicAuthenticationFilter`가 보안 필터 체인에 포함된다.

인증 정보가 없거나 올바르지 않으면 필터에서 요청을 중단하고 401 응답을 반환한다. 따라서 컨트롤러 내부에서 사용자 이름이나 비밀번호를 직접 확인하지 않는다.

### (2) HTTP Basic 인증 흐름

HTTP Basic 인증은 다음 순서로 진행된다.

```text
1. 클라이언트가 인증 정보 없이 보호된 자원을 요청한다.

2. 서버가 다음 응답을 반환한다.
   401 Unauthorized
   WWW-Authenticate: Basic realm="Realm"

3. 클라이언트가 username:password를 Base64로 인코딩한다.

4. 인코딩한 값을 Authorization 헤더에 담아 다시 요청한다.
   Authorization: Basic dXNlcjoxMjM0NQ==

5. 서버가 사용자 정보를 조회하고 비밀번호를 검증한다.

6. 인증에 성공하면 보호된 자원을 반환한다.
```

Spring Security에서는 `BasicAuthenticationFilter`가 `Authorization` 헤더를 읽어 인증 정보를 추출한다.

인증되지 않은 요청에 401 응답을 만드는 역할은 `BasicAuthenticationEntryPoint`가 담당한다.

### (3) `WWW-Authenticate` 헤더

HTTP Basic 인증이 필요한 서버는 인증되지 않은 요청에 다음 헤더를 반환한다.

```http
WWW-Authenticate: Basic realm="Realm"
```

`WWW-Authenticate`는 클라이언트에게 해당 자원에 Basic 인증이 필요하다는 사실을 알린다.

브라우저는 이 헤더를 확인하면 사용자 이름과 비밀번호를 입력받는 인증 창을 표시한다.

`realm`은 인증 영역을 구분하는 값이다. 브라우저는 사이트와 realm을 기준으로 인증 정보를 저장하고 재사용할 수 있다.

### (4) `Authorization` 헤더

클라이언트는 사용자 이름과 비밀번호를 다음 형식으로 연결한다.

```text
user:12345
```

이를 Base64로 인코딩하면 다음과 같은 값이 생성된다.

```text
dXNlcjoxMjM0NQ==
```

인코딩한 값은 요청 헤더에 포함한다.

```http
Authorization: Basic dXNlcjoxMjM0NQ==
```

서버는 헤더 값을 디코딩한 뒤 사용자 이름과 비밀번호를 분리하여 인증을 수행한다.

### (5) Base64와 HTTPS

Base64는 데이터를 다른 문자 형식으로 표현하는 인코딩 방식이다. 비밀번호를 보호하는 암호화 방식이 아니다.

다음 명령으로 Base64 값을 원래 문자열로 쉽게 복원할 수 있다.

```bash
echo dXNlcjoxMjM0NQ== | base64 -d
```

실행 결과는 다음과 같다.

```text
user:12345
```

평문 HTTP에서 HTTP Basic을 사용하면 네트워크 요청을 확인할 수 있는 사람이 사용자 이름과 비밀번호를 복원할 수 있다.

따라서 HTTP Basic 인증을 실제 서비스에서 사용할 때는 TLS를 적용한 HTTPS 통신이 필요하다.

### (6) HTTP Basic과 stateless

HTTP Basic은 서버 세션을 이용하여 로그인 상태를 유지하지 않는다.

클라이언트는 보호된 자원을 요청할 때마다 `Authorization` 헤더를 전송한다. 서버는 각 요청에서 인증 정보를 다시 확인한다.

```text
첫 번째 요청
Authorization: Basic ...
→ 인증 수행
→ 응답

두 번째 요청
Authorization: Basic ...
→ 인증 다시 수행
→ 응답
```

서버는 이전 요청의 로그인 상태를 기억할 필요가 없다. 이러한 방식을 stateless 인증이라고 한다.

다만 브라우저는 한 번 성공한 HTTP Basic 자격 증명을 내부적으로 저장하고 이후 요청마다 자동으로 헤더를 추가할 수 있다. 이 때문에 사용자는 로그인 상태가 유지되는 것처럼 느낄 수 있다.

---

## 4. 프로젝트 준비

### 의존성

`build.gradle`에 Spring Security와 Spring Web MVC 의존성을 추가한다.

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
}
```

Spring Boot 3.x 프로젝트에서는 다음 의존성을 사용할 수 있다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
```

### 파일 구조

주요 파일 구조는 다음과 같다.

```text
src/main/java
└── com.example.spring.httpbasic
    ├── HttpBasicApplication.java
    ├── config
    │   └── SecurityConfig.java
    └── controller
        └── HelloApiController.java
```

패키지 구조는 프로젝트 설정에 따라 달라질 수 있다.

---

## Step 1. 보호할 API 작성하기

`GET /hello` 요청에 `Hello World!`를 반환하는 컨트롤러를 작성한다.

```java
package com.example.spring.httpbasic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloApiController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
```

이 단계에서는 별도의 `SecurityConfig`를 작성하지 않는다.

`spring-boot-starter-security` 의존성이 존재하므로 Spring Boot의 기본 보안 자동 설정이 적용된다.

애플리케이션을 실행한 뒤 브라우저에서 다음 주소로 접근한다.

```text
http://localhost:8080/hello
```

기본 보안 설정에 따라 인증되지 않은 사용자는 `/hello`에 바로 접근할 수 없다.

---

## Step 2. 기본 보안 자동 설정 확인하기

Spring Security 의존성만 추가한 상태에서는 Spring Boot가 기본 사용자를 자동으로 생성한다.

애플리케이션 실행 로그에서 다음 내용을 확인한다.

```text
Using generated security password: 생성된_비밀번호
```

기본 사용자 이름은 다음과 같다.

```text
user
```

브라우저에서 `/hello`에 접근한 뒤 다음 정보를 사용해 로그인한다.

```text
사용자 이름: user
비밀번호: 실행 로그에 출력된 임시 비밀번호
```

로그인에 성공하면 다음 응답을 확인할 수 있다.

```text
Hello World!
```

애플리케이션을 재시작하면 자동 생성된 비밀번호가 변경된다.

```text
첫 번째 실행: Using generated security password: aaa...
두 번째 실행: Using generated security password: bbb...
```

두 번째 실행에서는 첫 번째 실행에서 사용한 비밀번호로 인증할 수 없다. 현재 실행 로그에 출력된 새로운 비밀번호를 사용해야 한다.

### 기본 로그인 방식

Spring Boot의 기본 보안 자동 설정은 Form Login과 HTTP Basic을 함께 활성화한다.

브라우저에서 HTML 응답을 요청하면 로그인 페이지로 이동할 수 있다.

```text
GET /hello
→ 302 Redirect
→ GET /login
→ 기본 로그인 페이지
```

curl과 같이 HTML 로그인 화면을 처리하지 않는 클라이언트에서는 401과 HTTP Basic 인증 헤더를 확인할 수 있다.

```bash
curl -i http://localhost:8080/hello
```

응답 예시는 다음과 같다.

```http
HTTP/1.1 401
WWW-Authenticate: Basic realm="Realm"
```

동일한 서버에서도 클라이언트의 요청 특성과 `Accept` 헤더에 따라 Form Login 또는 HTTP Basic 방식으로 인증을 요청할 수 있다.

---

## Step 3. curl로 HTTP Basic 흐름 확인하기

### 인증 없이 요청하기

다음 명령으로 인증 정보 없이 `/hello`를 요청한다.

```bash
curl -i http://localhost:8080/hello
```

응답에서 상태 코드와 `WWW-Authenticate` 헤더를 확인한다.

```http
HTTP/1.1 401
WWW-Authenticate: Basic realm="Realm", charset="UTF-8"
```

401은 요청한 자원에 접근하기 위한 인증 정보가 없거나 올바르지 않음을 의미한다.

### 인증 정보 인코딩하기

사용자 이름과 비밀번호를 콜론으로 연결한다.

```text
user:현재_비밀번호
```

Base64로 인코딩한다.

```bash
echo -n "user:현재_비밀번호" | base64
```

`-n`을 생략하면 문자열 끝에 개행 문자가 포함될 수 있다. 개행까지 함께 인코딩되면 서버가 올바른 비밀번호로 인식하지 못할 수 있다.

Windows PowerShell에서는 다음과 같이 인코딩할 수 있다.

```powershell
[Convert]::ToBase64String(
    [Text.Encoding]::UTF8.GetBytes("user:현재_비밀번호")
)
```

### `Authorization` 헤더로 요청하기

생성한 Base64 값을 `Authorization` 헤더에 포함한다.

```bash
curl -i \
  -H "Authorization: Basic 인코딩한_값" \
  http://localhost:8080/hello
```

인증에 성공하면 다음과 같은 응답을 확인할 수 있다.

```http
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8

Hello World!
```

### `curl -u` 사용하기

curl의 `-u` 옵션을 사용하면 사용자 이름과 비밀번호를 직접 Base64로 인코딩하지 않아도 된다.

```bash
curl -i -u user:현재_비밀번호 http://localhost:8080/hello
```

`curl -u`는 내부적으로 다음 헤더를 생성하여 요청한다.

```http
Authorization: Basic Base64(user:현재_비밀번호)
```

---

## Step 4. `SecurityFilterChain` 설정하기

Spring Boot의 기본 보안 설정을 대신할 `SecurityFilterChain`을 작성한다.

```java
package com.example.spring.httpbasic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated()
                );

        return http.build();
    }
}
```

각 설정의 의미는 다음과 같다.

| 설정                    | 의미                           |
| --------------------- | ---------------------------- |
| `@Configuration`      | 해당 클래스가 Spring 설정 클래스임을 나타냄  |
| `@EnableWebSecurity`  | Spring Security 웹 보안 설정 활성화  |
| `SecurityFilterChain` | HTTP 요청에 적용할 보안 필터와 인가 규칙 정의 |
| `httpBasic()`         | HTTP Basic 인증 활성화            |
| `anyRequest()`        | 모든 HTTP 요청을 대상으로 지정          |
| `authenticated()`     | 인증된 사용자만 요청 허용               |
| `http.build()`        | 설정한 보안 필터 체인 생성              |

`SecurityFilterChain` 빈을 직접 등록하면 Spring Boot의 기본 보안 필터 체인 대신 작성한 설정이 사용된다.

현재 설정에는 `formLogin()`이 없고 `httpBasic()`만 존재한다. 따라서 브라우저로 `/hello`에 접근하면 기본 로그인 페이지가 아니라 HTTP Basic 인증 창이 표시된다.

기존 브라우저 창에 인증 정보가 저장되어 있다면 인증 창이 나타나지 않을 수 있다. 이 경우 시크릿 창에서 다시 확인한다.

이 단계에서는 별도의 사용자를 등록하지 않았으므로 사용자 이름은 여전히 `user`이고, 비밀번호는 애플리케이션 실행 시 자동 생성된 값을 사용한다.

---

## Step 5. 인메모리 사용자 등록하기

자동 생성 계정 대신 애플리케이션에서 직접 사용할 계정을 등록한다.

`SecurityConfig`에 `UserDetailsService` 빈을 추가한다.

```java
package com.example.spring.httpbasic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager =
                new InMemoryUserDetailsManager();

        UserDetails user = User.withUsername("user")
                .password("12345")
                .authorities("USER")
                .build();

        manager.createUser(user);

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
```

### `UserDetails`

`UserDetails`는 Spring Security가 인증에 사용할 사용자 정보를 표현한다.

```java
UserDetails user = User.withUsername("user")
        .password("12345")
        .authorities("USER")
        .build();
```

등록한 사용자 정보는 다음과 같다.

| 항목     | 값       |
| ------ | ------- |
| 사용자 이름 | `user`  |
| 비밀번호   | `12345` |
| 권한     | `USER`  |

### `UserDetailsService`

`UserDetailsService`는 사용자 이름을 기준으로 인증에 필요한 사용자 정보를 조회하는 역할을 한다.

이 과제에서는 DB 대신 메모리에 사용자를 저장하는 `InMemoryUserDetailsManager`를 사용한다.

```java
InMemoryUserDetailsManager manager =
        new InMemoryUserDetailsManager();

manager.createUser(user);
```

`InMemoryUserDetailsManager`는 `UserDetailsService`를 구현하고 있으므로 그대로 빈으로 반환할 수 있다.

애플리케이션이 종료되면 메모리에 저장된 사용자도 사라진다. 다만 애플리케이션을 다시 실행할 때 같은 설정 코드가 실행되므로 `user` 계정이 다시 등록된다.

### `PasswordEncoder`

Spring Security는 저장된 비밀번호와 요청으로 전달된 비밀번호를 직접 비교하지 않고 `PasswordEncoder`를 통해 비교한다.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
}
```

`NoOpPasswordEncoder`는 비밀번호를 변환하지 않고 평문 그대로 비교한다.

```text
입력 비밀번호: 12345
저장 비밀번호: 12345
→ 문자열 그대로 비교
```

이 방식은 비밀번호가 평문으로 저장되므로 실제 서비스에서는 사용하지 않는다. 이 과제에서는 인증 흐름을 확인하기 위한 학습 목적으로만 사용한다.

실제 서비스에서는 `BCryptPasswordEncoder`와 같은 단방향 해시 기반 인코더를 사용해야 한다.

### 사용자 등록 후 확인

애플리케이션을 재시작한 뒤 로그를 확인한다.

`UserDetailsService`를 직접 등록했으므로 다음 자동 생성 비밀번호 메시지가 더 이상 나타나지 않는다.

```text
Using generated security password: ...
```

다음 명령으로 등록한 계정을 확인한다.

```bash
curl -i -u user:12345 http://localhost:8080/hello
```

인증에 성공하면 다음 응답을 반환한다.

```http
HTTP/1.1 200

Hello World!
```

애플리케이션을 재시작해도 설정 코드에서 동일한 사용자가 다시 생성되므로 같은 계정으로 인증할 수 있다.

---

## Step 6. 인증 처리 흐름 확인하기

`user`와 `12345`를 사용한 요청의 내부 흐름은 다음과 같다.

```text
1. 클라이언트가 Authorization 헤더를 전송한다.

2. BasicAuthenticationFilter가 헤더를 확인한다.

3. 헤더에서 Base64 값을 추출한다.

4. 값을 디코딩하여 사용자 이름과 비밀번호를 분리한다.

5. UserDetailsService에서 user 계정을 조회한다.

6. PasswordEncoder가 입력 비밀번호와 저장 비밀번호를 비교한다.

7. 인증에 성공하면 Authentication 객체를 생성한다.

8. 인증 정보를 SecurityContext에 저장한다.

9. 요청을 다음 필터로 전달한다.

10. DispatcherServlet이 HelloApiController를 호출한다.
```

`UserDetailsService`는 사용자 정보를 조회하는 역할을 하고, 비밀번호 비교는 인증 처리 과정에서 `PasswordEncoder`를 통해 수행된다.

따라서 `UserDetailsService` 내부에서 비밀번호를 직접 비교하지 않는다.

---

## Step 7. 브라우저 자격 증명 캐싱 확인하기

HTTP Basic 인증에 성공한 브라우저는 사용자 이름과 비밀번호를 일정 기간 저장할 수 있다.

일반 브라우저 창에서 다음 주소에 접근한다.

```text
http://localhost:8080/hello
```

인증 창에서 다음 정보를 입력한다.

```text
사용자 이름: user
비밀번호: 12345
```

인증에 성공한 뒤 같은 주소에 다시 접근하면 인증 창이 나타나지 않고 바로 응답을 확인할 수 있다.

```text
Hello World!
```

브라우저 개발자 도구의 Network 탭에서 `/hello` 요청을 선택하고 Request Headers를 확인한다.

```http
Authorization: Basic dXNlcjoxMjM0NQ==
```

브라우저가 이전 인증 정보를 저장한 뒤 다음 요청부터 `Authorization` 헤더를 자동으로 추가한 것이다.

서버의 보안 설정이 해제된 것이 아니라 브라우저가 인증 정보를 매 요청마다 자동으로 전송하고 있는 상태다.

curl로 인증 정보 없이 요청하면 여전히 401 응답을 확인할 수 있다.

```bash
curl -i http://localhost:8080/hello
```

```http
HTTP/1.1 401
WWW-Authenticate: Basic realm="Realm", charset="UTF-8"
```

---

## Step 8. 서버 재시작과 인증 상태 확인하기

브라우저에서 HTTP Basic 인증을 완료한 뒤 서버를 재시작한다.

서버 재시작 후 브라우저에서 `/hello`를 다시 요청해도 바로 접근될 수 있다.

이는 서버가 로그인 상태를 저장하고 있기 때문이 아니다.

```text
서버
→ 이전 로그인 세션을 저장하지 않음
→ 재시작 후 user/12345 계정을 다시 생성

브라우저
→ 기존 Basic 자격 증명을 저장
→ 재요청 시 Authorization 헤더 자동 전송
```

서버는 브라우저가 다시 보낸 사용자 이름과 비밀번호를 새로운 요청에서 재검증한다.

따라서 서버가 재시작되어도 브라우저가 같은 자격 증명을 계속 전송하고 계정 정보가 동일하다면 인증에 성공한다.

---

## Step 9. HTTP Basic 로그아웃 이해하기

세션 인증에서는 서버가 세션을 삭제하면 로그인 상태를 종료할 수 있다.

```text
POST /logout
→ 서버 세션 삭제
→ 이후 요청은 미인증 상태
```

HTTP Basic은 서버 세션을 사용하지 않는다. 브라우저가 매 요청마다 사용자 이름과 비밀번호를 다시 전송한다.

```text
요청
Authorization: Basic ...
→ 매번 인증 성공
```

서버가 이전 인증 정보를 삭제하더라도 브라우저가 다음 요청에 같은 헤더를 보내면 다시 인증된다.

이 때문에 HTTP Basic에서는 일반적인 세션 로그아웃과 같은 명확한 로그아웃 처리가 어렵다.

다시 인증 창을 확인하는 방법은 다음과 같다.

* 시크릿 창에서 접근한다.
* 브라우저를 완전히 종료한 뒤 다시 실행한다.
* 브라우저의 사이트 인증 정보를 삭제한다.
* 서버의 Basic 인증 realm을 변경한다.
* 다른 브라우저를 사용한다.

브라우저마다 HTTP Basic 자격 증명 저장 방식과 유지 기간은 다를 수 있다.

---

## 5. 최종 코드

### `HelloApiController.java`

```java
package com.example.spring.httpbasic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloApiController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
```

### `SecurityConfig.java`

```java
package com.example.spring.httpbasic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager =
                new InMemoryUserDetailsManager();

        UserDetails user = User.withUsername("user")
                .password("12345")
                .authorities("USER")
                .build();

        manager.createUser(user);

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
```

### 실행 결과

인증 정보 없이 요청한다.

```bash
curl -i http://localhost:8080/hello
```

```http
HTTP/1.1 401
WWW-Authenticate: Basic realm="Realm", charset="UTF-8"
```

올바른 계정으로 요청한다.

```bash
curl -i -u user:12345 http://localhost:8080/hello
```

```http
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8

Hello World!
```

잘못된 비밀번호로 요청한다.

```bash
curl -i -u user:wrong-password http://localhost:8080/hello
```

```http
HTTP/1.1 401
WWW-Authenticate: Basic realm="Realm", charset="UTF-8"
```

---

## 6. 자주 발생하는 문제

### 자동 생성 비밀번호로 인증되지 않는 경우

자동 생성 비밀번호는 애플리케이션을 실행할 때마다 변경된다.

현재 실행 중인 애플리케이션의 로그에서 비밀번호를 다시 확인해야 한다.

```text
Using generated security password: ...
```

브라우저가 이전 비밀번호를 자동 완성하고 있을 수도 있으므로 시크릿 창에서 다시 확인한다.

### 로그인 페이지가 나타나는 경우

별도의 `SecurityFilterChain`을 작성하지 않은 기본 설정에서는 Form Login과 HTTP Basic이 함께 활성화된다.

브라우저 요청에서는 Form Login 페이지가 선택될 수 있다.

HTTP Basic 인증 창만 확인하려면 다음 설정을 직접 등록한다.

```java
http
        .httpBasic(Customizer.withDefaults())
        .authorizeHttpRequests(auth ->
                auth.anyRequest().authenticated()
        );
```

### 인증 창 없이 바로 접근되는 경우

브라우저가 이전에 성공한 HTTP Basic 자격 증명을 저장하고 있을 수 있다.

Network 탭에서 다음 헤더가 자동으로 포함되는지 확인한다.

```http
Authorization: Basic ...
```

시크릿 창이나 다른 브라우저에서 접근하면 인증 정보가 없는 상태를 다시 확인할 수 있다.

### `There is no PasswordEncoder mapped for the id "null"`

평문 비밀번호를 등록했지만 이를 처리할 `PasswordEncoder`가 없을 때 발생할 수 있다.

학습용으로 다음 빈을 등록한다.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
}
```

또는 비밀번호 앞에 `{noop}` 접두사를 사용할 수 있다.

```java
User.withUsername("user")
        .password("{noop}12345")
        .authorities("USER")
        .build();
```

두 방식을 동시에 사용할 필요는 없다.

### 서버 재시작 후에도 인증이 유지되는 경우

HTTP Basic은 서버 세션을 이용하지 않는다.

브라우저가 저장한 `Authorization` 헤더를 계속 전송하고, 서버에 같은 사용자 계정이 다시 생성되므로 인증에 성공하는 것이다.

서버에 이전 로그인 세션이 남아 있는 것은 아니다.

### `Whitelabel Error Page`와 status 999가 나타나는 경우

status 999는 실제 HTTP 표준 오류 코드가 아니라 Spring Boot 오류 페이지 처리 과정에서 나타날 수 있는 내부 placeholder 값이다.

이전 오류 페이지의 `/error` 주소를 새로고침하거나, 인증 후 다시 `/error` 경로로 이동할 때 나타날 수 있다.

주소창에 `/hello`를 직접 입력하여 인증 결과를 확인한다.

### 8080 포트를 사용할 수 없는 경우

다음 오류가 나타나면 이전 애플리케이션 인스턴스가 실행 중인지 확인한다.

```text
Port 8080 was already in use
```

기존 프로세스를 종료한 뒤 애플리케이션을 다시 실행한다.

---

## 7. 학습 체크

* [ ] 요청이 Spring Security 필터 체인을 거쳐 컨트롤러로 전달되는 흐름을 설명할 수 있다
* [ ] `DelegatingFilterProxy`와 `FilterChainProxy`의 역할을 구분할 수 있다
* [ ] `BasicAuthenticationFilter`가 처리하는 요청 헤더를 설명할 수 있다
* [ ] 인증되지 않은 요청에 401과 `WWW-Authenticate` 헤더가 반환되는 이유를 설명할 수 있다
* [ ] `username:password`를 Base64로 인코딩하여 `Authorization` 헤더에 포함할 수 있다
* [ ] Base64가 암호화가 아닌 이유를 설명할 수 있다
* [ ] HTTP Basic에서 HTTPS가 필요한 이유를 설명할 수 있다
* [ ] `SecurityFilterChain`을 직접 등록하면 기본 보안 필터 체인이 대체되는 것을 이해한다
* [ ] `UserDetailsService`와 `PasswordEncoder`의 역할을 구분할 수 있다
* [ ] HTTP Basic이 서버 세션을 사용하지 않는 stateless 인증임을 설명할 수 있다
* [ ] 브라우저가 HTTP Basic 자격 증명을 자동으로 전송하는 것을 확인할 수 있다
* [ ] HTTP Basic에서 일반적인 로그아웃 처리가 어려운 이유를 설명할 수 있다

---

## 8. 완성 체크리스트

* [ ] `GET /hello` API를 작성했다
* [ ] Spring Security 의존성 추가 후 기본 보안 동작을 확인했다
* [ ] 자동 생성 비밀번호가 재시작할 때마다 변경되는 것을 확인했다
* [ ] 인증 정보 없이 요청하여 401 응답을 확인했다
* [ ] `WWW-Authenticate: Basic` 헤더를 확인했다
* [ ] 사용자 이름과 비밀번호를 직접 Base64로 인코딩했다
* [ ] 직접 작성한 `Authorization` 헤더로 인증에 성공했다
* [ ] `curl -u`를 사용해 인증에 성공했다
* [ ] HTTP Basic만 활성화한 `SecurityFilterChain`을 작성했다
* [ ] `InMemoryUserDetailsManager`에 사용자를 등록했다
* [ ] `PasswordEncoder`를 등록했다
* [ ] `user`와 `12345`로 인증에 성공했다
* [ ] 브라우저 Network 탭에서 `Authorization` 헤더를 확인했다
* [ ] 서버 재시작 후 브라우저 인증이 유지되는 이유를 확인했다
* [ ] 시크릿 창에서 인증 정보가 없는 상태를 다시 확인했다
