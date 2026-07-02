package com.example.essentials.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// * 쿠키(Cookie)란?
// 쿠키는 '브라우저 쪽'에 저장되는 작은 이름-값 데이터 조각
// HTTP는 무상태라 요청끼리 서로를 기억하지 못하는데,
// 쿠키를 쓰면 브라우저가 정보를 들고 다니며 서버에 매번 알려줄 수 있음
// - 서버가 응답할 때 "이 값을 저장해 둬"하고 쿠키를 내려주면(Set-Cookie),
//   브라우저는 그 쿠키를 저장해 둠
// - 이후 브라우저는 쿠키의 도메인과 경로 등 전송 조건이 맞는 요청에 쿠키를 자동으로 함께 보냄

// [세션과 쿠키의 관계 - 헷갈리기 쉬운 부분]
// - 쿠키: 이름-값 형태의 데이터가 브라우저에 저장
//   예: username=hong, theme=dark
// - 세션: 실제 데이터는 '서버'에 있고, 브라우저에는 그 보관함을 여는
//   '열쇠(세션 ID)'만 쿠키로 저장
// - 즉 세션도 그 열쇠를 전달하기 위해 내부적으로 쿠키를 사용
//   둘은 대립 관계가 아니라, 세션이 쿠키를 활용하는 관계
// - 쿠키는 브라우저에 그대로 노출되므로, 비밀번호 같은 민감 정보는 담으면 안 됨

// [참고: 웹 스토리지 - Local Storage / Session Storage]
// 개발자 도구(Application 탭)를 보면 Cookie 옆에 Local Storage, Session Storage가 있음
// 이 둘은 이름만 비슷할 뿐, 쿠키나 서버 세션과는 전혀 다른 '브라우저 전용' 저장소
// - 서버로 자동 전송되지 않으며, 웹 애플리케이션에서는 주로 자바스크립트로 읽고 씀
//   request.getCookies() 같은 방식으로는 서버가 접근할 수 없음
//   (쿠키는 매 요청마다 서버로 자동으로 실려 가지만, 웹 스토리지는 안 감)
// - Local Storage: 브라우저를 껐다 켜도 값이 계속 남음 (직접 지울 때까지 유지)
// - Session Storage: 탭을 닫으면 값이 사라짐. 탭마다 별개로 관리
//   (서버의 HttpSession과는 완전히 다른 것이니 이름에 속으면 안 됨)
// - 자바스크립트 사용 예: localStorage.setItem("키", "값") / localStorage.getItem("키")
// - 정리하면, 서버가 사용자를 기억해야 하면 '쿠키/세션'을,
//   브라우저에서 JS로만 임시 데이터를 다루면 '웹 스토리지'를 씀

@Controller
public class CookieController {
    @GetMapping("/set-cookie")
    public String setCookie() {
        return "set-cookie";
    }

    @PostMapping("/set-cookie")
    public String setCookieExec(
            @RequestParam
            String username,
            HttpServletResponse response,
            Model model
    ) {
        // 쿠키 생성
        Cookie cookie = new Cookie("username", username);

        // 유효기간
        cookie.setMaxAge(7 * 24 * 60 * 60); // 1주일

        // HttpOnly: 자바스크립트(document.cookie)로는 못 읽게 막음. XSS 공격 방어에 씀
        cookie.setHttpOnly(true);

        // Path: 브라우저가 이 쿠키를 어떤 경로 요청에 보낼지 정함
        //       지정한 경로와 그 하위 경로 요청에만 쿠키가 실려 감
        //       "/" 는 모든 경로가 "/"로 시작하므로, 사이트 전체 요청에 항상 보낸다는 뜻
        cookie.setPath("/");

        response.addCookie(cookie);

        model.addAttribute("message", "쿠키 설정 완료");

        return "result-cookie";
    }

    // 쿠키 읽기
    @GetMapping("/get-cookie")
    public String getCookie(
            HttpServletRequest request,
            Model model
    ) {
        Cookie[] cookies = request.getCookies();
        String username = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("username")) {
                    username = cookie.getValue();

                    break;
                }
            }
        }

        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("message", "쿠키에서 사용자 정보를 읽음");
        }
        else {
            model.addAttribute("message", "쿠키 없음");
        }

        return "result-cookie";
    }

    // 쿠키 읽기 (@CookieValue 버전): 위 getCookie()와 결과는 같지만 훨씬 간결함
    // - @CookieValue("username")을 쓰면, 스프링이 그 이름의 쿠키를 알아서 찾아
    //   매개변수 username에 값을 넣어줌 (DispatcherServlet이 파라미터를 만들어 주는 단계)
    // - required = false: 해당 쿠키가 없어도 에러 없이 진행함. 없으면 null이 들어옴
    //   (기본값은 true라, 쿠키가 없으면 400 에러가 나므로 조회용에서는 false로 두는 게 안전)
    // - 없을 때 기본값을 주고 싶으면 defaultValue = "guest" 처럼 지정할 수도 있음
    @GetMapping("/get-cookie2")
    public String getCookieByAnnotation(
            @CookieValue(value = "username", required = false)
            String username,
            Model model
    ) {
        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("message", "쿠키에서 사용자 정보를 읽음");
        }
        else {
            model.addAttribute("message", "쿠키 없음");
        }

        return "result-cookie";
    }

    @GetMapping("/delete-cookie")
    public String deleteCookie(
            HttpServletResponse response,
            Model model
    ) {
        Cookie cookie = new Cookie("username", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        model.addAttribute("message", "쿠키 삭제 완료");

        return "result-cookie";
    }
}
