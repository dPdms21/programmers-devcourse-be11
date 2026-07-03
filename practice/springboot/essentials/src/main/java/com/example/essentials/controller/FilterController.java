package com.example.essentials.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// * 디스패처 서블릿 (DispatcherServlet)
// 스프링 MVC가 처리하는 HTTP 요청은 DispatcherServlet을 거침
// 그래서 이 서블릿을 '프론트 컨트롤러'라고 부름 (프론트 컨트롤러 패턴)
// 개발자가 만들지 않아도 스프링부트가 자동 구성으로 미리 등록해줌

// 요청 하나가 들어와 응답이 나가기까지의 흐름
// 1. 브라우저 요청이 (필터를 지나) DispatcherServlet에 도착
// 2. HandlerMapping을 통해 요청 URL을 처리할 핸들러를 찾음
// 3. HandlerAdapter를 통해 실제 컨트롤러 메서드를 호출
// 이때 파라미터(@RequestParam, HttpSession 등)를 알맞게 만들어 넣어 줌
// 4. 컨트롤러가 값을 반환하면, 그 반환값을 어떻게 처리할지 갈림
//    - @Controller + 뷰 이름 -> ViewResolver가 templates/이름.html을 찾음
//    - @RestController/@ResponseBody -> HttpMessageConverter가 데이터(문자열/JSON)로 변환
// 5. 최종 결과(HTML 또는 데이터)를 응답으로 만들어 브라우저에 돌려줌
// 핵심은 @GetMapping 메서드만 작성하면 그 앞뒤의 '분배와 변환'은
// DispatcherServlet이 정해진 순서대로 대신 처리해 준다는 점
// 아래의 @Controller / @RestController 차이도, 결국 4번 단계에서
// DispatcherServlet이 반환값을 뷰로 볼지 데이터로 볼지를 가르는 이야기

// * @Controller와 @RestController의 차이
// 둘 다 웹 요청을 받아 처리하는 컨트롤러지만, 메서드가 반환하는 String을 '어떻게 해석하느냐'가 다름
// - @Controller: 반환하는 String을 '뷰(view)의 이름'으로 봄
//   그래서 SessionController, CookieController처럼 HTML 페이지를 보여 줄 때 사용
// - @RestController: 반환하는 String(또는 객체)을 '데이터 그 자체'로 봄
//   return "Hello World!"는 뷰를 찾지 않고 그 글자를 그대로 응답 본문에 보여 줄 때 사용
//   객체를 반환하면 JSON으로 변환해줌. 그래서 REST API를 만들 때 씀
// - 사실 @RestController는 @Controller + @ResponseBody를 합친 것
//   @Controller에서도 메서드에 @ResponseBody를 붙이면 데이터를 그대로 반환할 수 있음
// 즉 @RestController는 "이 클래스의 모든 메서드는 데이터를 반환한다"는 선언인 셈

// - 정리: 화면(HTML)을 보여 주려면 @Controller,
//   데이터(JSON/문자열)를 내려 주려면 @RestController를 씀

// * 필터 - Dispatcher Servlet 앞단에서 실행
// 요청이 Dispatcher Servlet에 '도착하기도 전에' 먼저 거치는 관문이 필터
// 서블릿 컨테이너(톰캣) 수준에서 동작하며, 스프링 MVC의 바깥에 위치
// 즉 필터는 '요청이 들어올 때'와 '응답이 나갈 때'를 모두 가로챌 수 있음
// [하는 일]
// - 모든 요청에 공통으로 필요한 처리를 컨트롤러보다 먼저 해치움
//   예: 인증/인가 검사, 요청 로깅, 문자 인코딩(UTF-8) 설정, CORS 처리 등
// - 문지기라서, 통과시키지 않고 여기서 바로 응답을 돌려보내며 막을 수도 있음
//   (예: 로그인 안 된 요청을 컨트롤러까지 보내지 않고 필터에서 차단함)
// [만드는 법]
// - jakarta.servlet.Filter 를 구현하고 doFilter() 안에 로직을 작성
// - doFilter() 안에서 chain.doFilter(request, response) 를 호출해야
//   '다음 단계(다음 필터 또는 DispatcherServlet)'로 요청이 넘어감
//   이 호출을 하지 않으면 요청은 여기서 멈춤

@RestController
public class FilterController {
    @GetMapping("/hello")
    public String hello() {
        System.out.println("hello");

        return "Hello World!";
    }

    @GetMapping("/api/data")
    public String data() {
        return "data";
    }
}
