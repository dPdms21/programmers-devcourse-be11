# 세션 + 쿠키로 개인화 대시보드 만들기 (로그인은 세션, 취향은 쿠키)

> **세션**과 **쿠키**를 한 화면에서 함께 사용한다. 두 방식의 역할이 왜 다른지 코드를 통해 확인하는 것이 목표다.
>
> * **세션** = 로그인 상태와 사용자 정보 → 민감하고 일시적으로 필요 → **서버**에 저장
> * **쿠키** = 테마와 마지막 방문 시각 → 민감하지 않고 장기간 유지 → **브라우저**에 저장
>
> 화면은 Thymeleaf로 간단하게 구성한다. 디자인보다 어떤 정보를 어디에 저장하는지에 집중한다.
>
> 각 Step의 힌트는 접혀 있다. 먼저 해당 정보가 세션과 쿠키 중 어디에 적합한지 고민하고, 필요한 경우 힌트를 펼쳐 확인한다.

---

## 0. 먼저 알아둘 점

* 프로그램을 완성하면 로그인 후 대시보드가 열리고, **마지막 방문 시각**과 사용자가 선택한 **밝은 테마 또는 어두운 테마**가 표시된다. 로그아웃하면 로그인 정보는 사라지지만 테마와 방문 기록은 그대로 남는다. 이 차이가 이번 과제의 핵심이다.
* 로그인하지 않은 사용자가 대시보드 주소에 직접 접근하면 로그인 페이지로 리다이렉트한다. 이를 통해 세션 기반 접근 제어를 구현한다.
* Spring Boot 3에서는 Servlet 관련 클래스를 `jakarta.servlet.http.*`에서 import한다. Spring Boot 2에서는 `javax.servlet.http.*`를 사용한다.
* 쿠키 값에는 공백이나 콜론과 같은 문자를 그대로 저장하기 어렵다. 따라서 마지막 방문 시각은 **밀리초 단위의 숫자**로 저장하고, 화면에 출력할 때 사람이 읽을 수 있는 형식으로 변환한다.

---

## 1. 무엇을 만드는가?

| 화면과 주소                | 역할                                 | 저장 방식                         |
| --------------------- | ---------------------------------- | ----------------------------- |
| `GET /login`          | 로그인 폼 표시                           | 이미 로그인 상태라면 대시보드로 이동          |
| `POST /login`         | 이름을 세션에 저장                         | 세션                            |
| `GET /dashboard`      | 로그인한 사용자에게 환영 문구, 마지막 방문 시각, 테마 표시 | 세션 기반 접근 제어 + 쿠키 기반 방문 기록과 테마 |
| `GET /theme?mode=...` | 선택한 테마 저장                          | 쿠키                            |
| `GET /logout`         | 세션 무효화                             | 세션                            |

**대시보드 화면 예시**

```text
환영합니다, kim 님!
마지막 방문: 2026-06-24 08:43:15
현재 테마: dark   [밝게] [어둡게]
[로그아웃]
```

---

## 2. 학습 목표

| 개념                           | 학습 위치                                     |
| ---------------------------- | ----------------------------------------- |
| 세션에 값 저장 및 조회                | Step 1 (`LoginController`)                |
| 세션 기반 접근 제어                  | Step 2 (`DashboardController`)            |
| 쿠키 저장과 `@CookieValue`를 통한 조회 | Step 3 (`DashboardController`)            |
| 쿠키를 이용한 사용자 테마 유지            | Step 4 (`DashboardController`, Thymeleaf) |
| 세션 무효화와 세션·쿠키 차이 확인          | Step 5 (`LoginController`)                |

---

## 3. 핵심 개념

### (1) 세션과 쿠키 비교

| 구분           | 세션(Session)             | 쿠키(Cookie)                       |
| ------------ | ----------------------- | -------------------------------- |
| 저장 위치        | **서버**                  | **브라우저**                         |
| 브라우저가 보관하는 값 | `JSESSIONID`와 같은 세션 식별자 | 실제 이름과 값                         |
| 수명           | 브라우저 종료 또는 세션 타임아웃까지    | `maxAge`로 직접 설정                  |
| 보안           | 실제 값이 서버에 있어 상대적으로 안전   | 브라우저에 값이 노출되므로 민감 정보 저장에 부적합     |
| 적합한 용도       | 로그인 상태와 같은 민감하고 일시적인 정보 | 테마와 최근 본 상품처럼 민감하지 않고 유지가 필요한 정보 |

### (2) 쿠키 읽기: `getCookies()`와 `@CookieValue`

Servlet 방식에서는 요청에 포함된 모든 쿠키를 가져온 뒤 원하는 쿠키를 직접 찾는다.

```java
Cookie[] cookies = request.getCookies();

for (Cookie cookie : cookies) {
    if ("theme".equals(cookie.getName())) {
        // theme 쿠키 처리
    }
}
```

Spring MVC에서는 `@CookieValue`를 사용해 원하는 쿠키를 직접 전달받을 수 있다.

```java
@CookieValue(value = "theme", defaultValue = "light")
String theme
```

`theme` 쿠키가 있으면 해당 값이 전달되고, 없으면 기본값인 `light`가 전달된다.

```text
로그인 상태 = 세션에 저장
테마와 방문 기록 = 쿠키에 저장
세션이 없으면 리다이렉트로 접근 제한
```

---

## 4. 파일 구조와 준비물

| 파일                         | 역할                           |
| -------------------------- | ---------------------------- |
| `LoginController.java`     | 로그인과 로그아웃 처리                 |
| `DashboardController.java` | 대시보드, 접근 제어, 방문 기록과 테마 쿠키 처리 |
| `templates/login.html`     | 로그인 폼                        |
| `templates/dashboard.html` | 사용자 대시보드                     |

**Spring Boot 의존성**

```gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
```

---

## 5. Step by Step

### Step 1. 로그인 — 이름을 세션에 저장하기 (`LoginController`, `login.html`)

**목표**: 로그인 폼으로 전달받은 이름을 세션에 저장하고 대시보드로 이동한다.

**할 일**

1. `GET /login`에서 로그인 폼인 `login.html`을 반환한다.
2. 이미 세션에 `username`이 있으면 대시보드로 리다이렉트한다.
3. `POST /login`에서 요청 파라미터로 받은 `username`을 세션에 저장한다.
4. 저장 후 `redirect:/dashboard`를 반환한다.
5. `login.html`에 이름 입력 필드와 제출 버튼을 작성한다.

<details>
<summary>힌트 보기</summary>

```java
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session.getAttribute("username") != null) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            HttpSession session
    ) {
        session.setAttribute("username", username);
        return "redirect:/dashboard";
    }
}
```

```html
<!-- templates/login.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
</head>
<body>
    <h1>로그인</h1>

    <form th:action="@{/login}" method="post">
        <input
                type="text"
                name="username"
                placeholder="이름을 입력하세요"
                required
        >
        <button type="submit">로그인</button>
    </form>
</body>
</html>
```

`redirect:`를 반환하면 로그인 정보를 저장한 뒤 브라우저가 `GET /dashboard`를 다시 요청한다.

POST 요청의 결과 화면을 바로 반환하지 않으므로 새로고침 시 폼이 다시 전송되는 문제를 방지할 수 있다. 이를 PRG(Post-Redirect-Get) 패턴이라고 한다.

</details>

**확인**: 이름을 입력하고 로그인했을 때 `/dashboard`로 이동하면 성공이다. 아직 대시보드를 구현하지 않은 상태에서는 오류가 발생할 수 있다.

---

### Step 2. 대시보드 — 로그인하지 않은 사용자 차단하기 (`DashboardController`)

**목표**: 세션에 로그인 정보가 있는 사용자만 대시보드에 접근할 수 있도록 한다.

**할 일**

1. `GET /dashboard`에서 세션에 저장된 `username`을 조회한다.
2. `username`이 없으면 `redirect:/login`을 반환한다.
3. `username`이 있으면 모델에 담고 `dashboard` 화면을 반환한다.

<details>
<summary>힌트 보기</summary>

```java
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model
    ) {
        String username =
                (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", username);
        return "dashboard";
    }
}
```

세션은 로그인이라는 민감하고 일시적인 상태를 서버에 저장한다. 따라서 서버는 세션에 `username`이 존재하는지 확인해 사용자의 접근 여부를 결정할 수 있다.

쿠키에 단순히 `loggedIn=true`와 같은 값을 저장하면 사용자가 값을 직접 조작할 수 있으므로 로그인 상태를 저장하는 방식으로는 적절하지 않다.

</details>

**확인**: 로그인 후 대시보드에 접근할 수 있어야 한다. 로그아웃 상태에서 `/dashboard`에 직접 접근하면 로그인 페이지로 이동해야 한다.

---

### Step 3. 쿠키로 마지막 방문 시각 기억하기 (`DashboardController`)

**목표**: 대시보드에 접근할 때 이전 방문 시각을 쿠키에서 읽어 출력하고, 현재 방문 시각으로 쿠키를 갱신한다.

**할 일**

1. `@CookieValue(value = "lastVisit", required = false)`로 이전 방문 시각을 전달받는다.
2. 첫 방문이라 쿠키가 없으면 `null`이 전달된다.
3. 쿠키가 있으면 밀리초 값을 사람이 읽을 수 있는 날짜와 시간 형식으로 변환한다.
4. 변환한 값을 모델에 담는다.
5. 현재 시각을 밀리초 문자열로 변환해 `lastVisit` 쿠키에 저장한다.
6. 쿠키를 응답에 추가한다.

<details>
<summary>힌트 보기</summary>

```java
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

@GetMapping("/dashboard")
public String dashboard(
        HttpSession session,
        @CookieValue(
                value = "lastVisit",
                required = false
        ) String lastVisit,
        HttpServletResponse response,
        Model model
) {
    String username =
            (String) session.getAttribute("username");

    if (username == null) {
        return "redirect:/login";
    }

    model.addAttribute("username", username);

    if (lastVisit != null) {
        long millis = Long.parseLong(lastVisit);

        String readable =
                Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .format(FMT);

        model.addAttribute("lastVisit", readable);
    }

    Cookie visit = new Cookie(
            "lastVisit",
            String.valueOf(System.currentTimeMillis())
    );

    visit.setMaxAge(30 * 24 * 60 * 60);
    visit.setPath("/");
    visit.setHttpOnly(true);

    response.addCookie(visit);

    return "dashboard";
}
```

쿠키 값에는 공백이나 콜론이 포함된 날짜 문자열을 그대로 저장하기 어렵다.

따라서 저장할 때는 `System.currentTimeMillis()`로 구한 밀리초 값을 문자열로 저장하고, 출력할 때 `Instant`와 `DateTimeFormatter`를 사용해 변환한다.

</details>

**확인**: 대시보드를 두 번 열었을 때 첫 방문에는 마지막 방문 시각이 표시되지 않고, 두 번째 방문부터 이전 방문 시각이 표시되어야 한다.

브라우저 개발자 도구의 Application 또는 Storage 메뉴에서 `lastVisit` 쿠키를 확인한다.

---

### Step 4. 쿠키로 테마 유지하기 (`DashboardController`, `dashboard.html`)

**목표**: 사용자가 선택한 테마를 쿠키에 저장하고, 이후에도 동일한 테마가 적용되도록 한다.

**할 일**

1. `GET /theme`에서 요청 파라미터인 `mode`를 전달받는다.
2. `mode`가 `light` 또는 `dark`인지 확인한다.
3. 선택한 값을 `theme` 쿠키에 저장한다.
4. 쿠키 저장 후 `redirect:/dashboard`를 반환한다.
5. 대시보드에서는 `@CookieValue`로 현재 테마를 읽는다.
6. 쿠키가 없으면 기본값으로 `light`를 사용한다.
7. 모델에 테마를 담고 Thymeleaf에서 `<body>` 클래스에 적용한다.

<details>
<summary>힌트 보기</summary>

```java
@GetMapping("/theme")
public String setTheme(
        @RequestParam String mode,
        HttpServletResponse response
) {
    String value =
            "dark".equals(mode) ? "dark" : "light";

    Cookie theme = new Cookie("theme", value);

    theme.setMaxAge(30 * 24 * 60 * 60);
    theme.setPath("/");

    response.addCookie(theme);

    return "redirect:/dashboard";
}
```

`dashboard()` 메서드의 파라미터에 현재 테마 쿠키를 추가한다.

```java
@CookieValue(
        value = "theme",
        defaultValue = "light"
) String theme
```

모델에도 테마 값을 저장한다.

```java
model.addAttribute("theme", theme);
```

```html
<!-- templates/dashboard.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>대시보드</title>

    <style>
        body.light {
            background: #ffffff;
            color: #222222;
        }

        body.dark {
            background: #222222;
            color: #eeeeee;
        }

        a {
            color: #3b82f6;
        }
    </style>
</head>

<body th:classappend="${theme}">

    <h1 th:text="|환영합니다, ${username} 님!|">
        환영합니다
    </h1>

    <p
            th:if="${lastVisit != null}"
            th:text="|마지막 방문: ${lastVisit}|"
    >
        마지막 방문
    </p>

    <p th:if="${lastVisit == null}">
        첫 방문을 환영합니다!
    </p>

    <p>
        현재 테마:
        <b th:text="${theme}">light</b>
    </p>

    <a th:href="@{/theme(mode='light')}">밝게</a>
    |
    <a th:href="@{/theme(mode='dark')}">어둡게</a>

    <hr>

    <a th:href="@{/logout}">로그아웃</a>
</body>
</html>
```

`th:classappend="${theme}"`는 현재 테마 값을 `<body>`의 클래스에 추가한다.

예를 들어 `theme` 값이 `dark`라면 다음과 같이 렌더링된다.

```html
<body class="dark">
```

`th:if`는 마지막 방문 시각의 존재 여부에 따라 서로 다른 문구를 출력한다.

</details>

**확인**: 어두운 테마를 선택하면 화면이 어두워지고 새로고침 후에도 상태가 유지되어야 한다.

브라우저 개발자 도구에서 `theme=dark` 쿠키가 저장되었는지 확인한다.

---

### Step 5. 로그아웃과 세션·쿠키 차이 확인하기 (`LoginController`)

**목표**: 로그아웃할 때 세션만 무효화하고, 쿠키에 저장된 테마와 방문 기록은 유지되는지 확인한다.

**할 일**

1. `GET /logout`에서 `session.invalidate()`를 호출한다.
2. 세션 무효화 후 `redirect:/login`을 반환한다.
3. 로그아웃 후 다시 로그인했을 때 테마와 마지막 방문 시각이 남아 있는지 확인한다.

<details>
<summary>힌트 보기</summary>

```java
@GetMapping("/logout")
public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/login";
}
```

`invalidate()`는 서버에 저장된 세션만 무효화한다.

브라우저에 저장된 `theme`, `lastVisit` 쿠키는 삭제하지 않으므로 다시 로그인해도 테마와 방문 기록이 유지된다.

</details>

**확인**

1. 테마를 어둡게 변경한다.
2. 로그아웃한다.
3. `/dashboard`에 직접 접근한다.
4. 세션이 없으므로 로그인 페이지로 이동하는지 확인한다.
5. 다시 로그인한다.
6. 어두운 테마와 이전 방문 시각이 유지되는지 확인한다.

로그인 정보가 사라지고 테마와 방문 기록이 유지되는 이유는 로그인 정보는 서버의 세션에 저장되고, 테마와 방문 기록은 브라우저의 쿠키에 저장되기 때문이다.

---

## 6. 학습 체크

* [ ] 세션과 쿠키의 저장 위치, 수명, 용도 차이를 설명할 수 있다
* [ ] 로그인 상태를 세션에 저장하고 테마를 쿠키에 저장하는 이유를 설명할 수 있다
* [ ] 세션 정보가 없을 때 리다이렉트를 통해 접근을 제한할 수 있다
* [ ] `@CookieValue`가 `getCookies()`와 반복문을 통한 쿠키 탐색을 대신한다는 것을 설명할 수 있다
* [ ] `maxAge`와 쿠키 삭제 방식의 관계를 설명할 수 있다
* [ ] `session.invalidate()` 이후에도 쿠키가 유지되는 이유를 설명할 수 있다

## 7. 최종 완성 체크리스트

* [ ] 로그인하면 대시보드가 열린다
* [ ] 로그아웃 상태에서 `/dashboard`에 직접 접근하면 로그인 페이지로 이동한다
* [ ] 두 번째 방문부터 마지막 방문 시각이 표시된다
* [ ] 테마를 변경하면 새로고침 후에도 유지된다
* [ ] 브라우저 개발자 도구에서 `theme`, `lastVisit` 쿠키를 확인할 수 있다
* [ ] 로그아웃 후 다시 로그인해도 테마와 방문 기록이 유지된다
