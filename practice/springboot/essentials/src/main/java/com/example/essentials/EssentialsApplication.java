package com.example.essentials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// * 웹 서버(Web server), WAS, 톰캣(Tomcat)이란?

// [웹 서버]
// '웹 서버'는 브라우저의 요청을 받아 이미 만들어져 있는 정적인 파일(html, css, 이미지 등..)을 그대로 전달해 주는 서버
// 대표적인 웹 서버로 아파치(Apache HTTP Server)와 Nginx가 있음
// - 정적 파일 전달, HTTPS 처리, 요청 분배(로드 밸런싱) 같은 일은 잘하지만,
//   자바 코드를 직접 실행해 결과를 만들어 내지는 못함

// [WAS(Web Application Server)]
// 사용자가 브라우저로 요청을 보내면, 그 요청을 받아 우리가 작성한 자바 코드를 실행하고
// 그 결과(HTML, JSON 등)를 다시 응답으로 돌려 주는 '실행 환경'이 바로 WAS
// - 즉 웹 서버가 못 하는 '동적인' 처리를 요청마다 프로그램을 돌려 만들어 냄
// - 요청/응답 처리, 스레드 관리, 세션 관리 같은 서버 공통 기능을 대신 맡아 줌
// - 실무에서는 아파치(웹 서버)를 앞단에 두어 정적 요소를 처리하고,
//   동적 요청만 뒷단의 WAS로 넘기는 구조를 함께 쓰기도 함

// [TOMCAT은 그런 WAS 중 가장 널리 쓰이는 오픈소스 구현체]
// 자바의 서블릿(Servlet) 규격을 따르며, 스프링 MVC도 결국 이 서블릿 위에서 동작
// - 톰캣은 정적 파일도 어느 정도 다룰 수 있어, 규모가 작으면 웹 서버 없이 톰캣만으로도 서비스가 가능
// - 원래는 톰캣을 따로 설치하고 그 안에 우리 애플리케이션(WAR)을 넣어 실행
// - 스프링부트는 이 톰캣을 라이브러리 형태로 애플리케이션 '안에' 내장
//   그래서 별도 설치 없이 main() 실행만으로 서버가 함께 떠서 요청을 받을 수 있음

// * HTTP 프로토콜이란
// HTTP(HyperText Transfer Protocol)는 브라우저(클라이언트)와 서버가 서로 데이터를 주고받을 때 지키는 '약속(규칙)'
// - 통신은 항상 '요청(Request) -> 응답(Response)' 한 쌍으로 이루어짐
// 클라이언트가 요청을 보내면 서버가 그에 대한 응답을 돌려 주는 식
// - 요청에는 '무엇을'(URL), '어떻게'(메서드), 부가 정보(헤더), 본문(body)이 담김
// - 응답에는 처리 결과를 나타내는 상태코드(200 성공, 404 없음, 500 서버 오류 등)와 실제 데이터(HTML, JSON 등)가 담김
// - HTTP는 '무상태(stateless)' 프로토콜이라, 각 요청은 서로를 기억하지 못함
//   그래서 로그인 상태 유지 등을 위해 세션이나 토큰 같은 보조 장치를 함께 사용

// * HTTP 메서드 - GET, POST, PUT, DELETE
// 메서드는 '이 요청으로 무엇을 하고 싶은지'를 나타내는 동사
// 데이터의 생성/조회/수정/삭제(CRUD)와 자연스럽게 짝지어짐
// - GET: 데이터를 '조회'할 때 씀
//        서버의 상태를 바꾸지 않으며, 값은 주로 URL 뒤 쿼리 스트링에 담음
// - POST: 데이터를 '새로 생성'할 때 씀
//         보낼 내용을 요청 본문(body)에 담으며, 서버의 상태를 바꿈
// - PUT: 기존 데이터를 '수정(전체 교체)'할 때 씀
//        같은 요청을 여러 번 보내도 결과가 같은 '멱등성'을 가짐
// - DELETE: 데이터를 '삭제'할 때 씀
// - 스프링에서는 @GetMapping, @PostMapping, @PutMapping, @DeleteMapping 으로
//   각 메서드 요청을 컨트롤러의 특정 메서드에 연결

// * 멱등성
// 멱등성은 '같은 요청을 한 번 보내든 여러 번 보내든, 서버의 최종 상태가 똑같이 유지되는 성질'을 말함
// '여러 번 눌러도 결과가 그대로'
// - GET: 멱등함. 여러 번 조회해도 데이터가 바뀌지 않음
// - PUT: 멱등함. 예를 들어 회원 3번의 이름을 '홍길동'으로 바꿔라 를 10번 보내도, 결과는 언제나 '이름이 홍길동'인 하나의 상태
// - DELETE: 멱등함. 예를 들어 회원 3번을 삭제하라 -> 여러 번 보내도 3번이 없는 상태는 동일
// - POST: 멱등하지 '않음'. "회원을 새로 등록하라" 10번 보내면 10명 생겨 버림. 보낼 때마다 상태가 계속 달라짐
// 왜 중요한가?
// 네트워크 오류로 응답을 못 받아 요청을 '재시도'하는 상황이 생겼을 때,
// 이때 멱등한 요청은 여러 번 가도 안전하지만,
// 멱등하지 않은 POST는 중복 처리(예: 결제 두 번)가 될 수 있어 주의해야 함

// * 스프링부트 애플리케이션의 시작점
// [@SpringBootApplication]
// 이 어노테이션 하나는 사실 세 가지 어노테이션을 합쳐 놓은 것
// - @SpringBootConfiguration: 이 클래스 자체가 설정 클래스임을 알리며, 내부의 @Bean 정의를 스프링 컨테이너에 등록하게 됨
// - @ComponentScan: 이 클래스가 위치한 패키지(여기서는 com.example.essentials)를
//   기준으로 하위 패키지를 훑으며 @Component, @Service, @Repository, @Controller 등이 붙은 빈(Bean)들을 찾아 등록
// - @EnableAutoConfiguration: '자동 구성'을 켜는 핵심 스위치

// * 실행 메커니즘 - main()이 호출된 뒤 벌어지는 일
// 1. SpringApplication.run()이 호출되면 가장 먼저 ApplicationContext인 Spring 컨테이너를 생성
// 2. 웹 관련 클래스가 클래스패스에 있는지 확인해 웹 애플리케이션 타입 (Servlet, Reactive, None)을 스스로 판단
// 3. @ComponentScan이 우리가 직접 작성한 빈들을 먼저 스캔해 등록
// 4. 그 다음 @EnableAutoConfiguration이 자동 구성 후보들을 불러옴

// * 자동 구성의 순서와 원리
// 1. 스프링부트는 META-INF/spring/....AutoConfiguration.imports 파일에
//    나열된 수많은 자동 구성 후보 클래스 목록을 읽어 들임
// 2. 각 후보는 @ConditionalOnClass, @ConditionalOnMissingBean,
//    @ConditionalOnProperty 같은 '조건(Condition)'을 달고 있음
//    이 조건이 충족될 때만 해당 구성이 실제로 적용
//    예를 들어 클래스패스에 톰캣이 있으면 내장 톰캣이 자동으로 구성되는 식
// 3. 중요한 점은 '사용자 정의 빈이 우선'이라는 것
//    개발자가 직접 정의한 빈이 있으면 @ConditionalOnMissingBean 조건이 충족되지 않아
//    해당 자동 구성 빈은 등록되지 않음
// 4. 구성 간 순서가 필요한 경우 @AutoConfiguration(before/after),
//    @AutoConfigureOrder 등으로 상대적 순서를 조정

@SpringBootApplication
public class EssentialsApplication {
    // 애플리케이션의 진입 메서드
    public static void main(String[] args) {
        SpringApplication.run(EssentialsApplication.class, args);
    }
}
