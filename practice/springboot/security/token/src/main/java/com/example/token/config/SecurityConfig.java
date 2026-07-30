package com.example.token.config;

import com.example.token.config.filter.TokenAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

// * JWT(JSON Web Token)
// JWT는 당사자 간에 정보를 JSON 객체로 안전하게 전달하기 위한 토큰 표준
// '.'(점)으로 구분된 세 부분으로 구성

// xxxxx.yyyyy.zzzzz -> Header.Payload.Signature

// 1. Header
// - 토큰의 메타 정보: 서명 알고리즘(alg), 토큰 타입(typ)
// - 예: {"alg" : "HS256", "typ" : "JWT"}
// - 이 JSON을 Base64Url 인코딩한 것이 첫 번째 부분

// 2. Payload
// - 실제 전달할 데이터인 클레임(Claim)들을 담음
// - 주의: 암호화가 아니라 '인코딩'일 뿐 -> 누구나 디코딩해서 볼 수 있으므로 비밀번호 등 민감 정보를 절대 넣으면 안 됨

// 클레임이란?
// - Payload에 담기는 정보 한 조각 (key-value 쌍 하나하나)
// - 토큰이 "이 사용자는 test다", "이 토큰은 10시에 만료된다" 같은 사실을 '주장(claim)'한다는 의미
// 서명이 유효하면 그 주장들을 신뢰할 수 있음

// 클레임의 3가지 종류
// 1) 등록된 클레임(Registered) - RFC 7519 표준에 미리 정의된 이름들
//    sub(주체/사용자 식별자), iss(발급자), exp(만료 시간), iat(발급 시각), aud(사용 대상), jti(토큰 고유 ID)
// -> 이름이 3글자로 짧은 이유는 토큰 크기를 줄이기 위해
// 2) 공개 클레임(Public) - 시스템 간 충돌하지 않도록 공개적으로 정의해서 쓰는 것
//    보통 URI 형태로 이름을 지음. 직접 만들 일은 드묾
// 3) 비공개 클레임(Private) - 서버-클라이언트 간 약속한 커스텀 데이터
//    예: role, nickname,...

// 3. Signature
// - 토큰이 위조되지 않았음을 증명하는 부분
// - HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secretKey)
// - Payload를 조작하면 서명이 일치하지 않아 검증 단계에서 탐지됨
// - 즉 JWT는 '내용을 숨기는' 것이 아니라 '변조를 막는' 기술

// * Spring Security에서의 동작 흐름
// 1) 로그인 성공 -> 서버가 jwt 토큰 생성 -> 클라이언트에게 전달
// 2) 이후 요청 -> 클라이언트가 Authorization: Bearer <토큰> 헤더에 담아 전송
// 3) JWT 필터 -> 토큰의 서명과 만료 시간을 검증
// 4) 검증 통과 -> SecurityContextHolder에 인증 정보 저장 -> 컨트롤러까지 요청 도달

// * 세션 방식과의 차이
// - 세션: 상태를 서버에 저장, 서버 확장 시 세션 공유 필요
// - JWT: 서버 세션 없이 클라이언트가 토큰을 보관하고 요청마다 전달하는 방식

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // [CSRF vs XSS — 토큰 방식에서 위협이 어떻게 바뀌는가]

                // CSRF(Cross-Site Request Forgery):
                //   "브라우저가 쿠키(세션)를 자동으로 실어 보내는" 성질을 악용해,
                //   로그인된 사용자의 브라우저로 의도하지 않은 요청을 보내게 하는 공격
                //   → 우리는 인증을 Authorization 헤더(자동 전송 안 됨)로 하므로 성립하지 않아 끔

                // XSS(Cross-Site Scripting):
                //   공격자가 페이지에 악성 스크립트를 주입해 "사용자의 브라우저에서" 실행시키는 공격
                //   (예: 게시글에 <script> 태그를 심었는데 이스케이프 없이 그대로 렌더링되는 경우)
                //   토큰 방식에서는 이게 더 큰 위협이 됨:
                //   - localStorage는 같은 페이지의 JS라면 누구나 읽을 수 있음
                //     → 주입된 스크립트가 localStorage.getItem('accessToken')으로 토큰을 훔칠 수 있음
                //   - 그래서 이 프로젝트의 방어 설계:
                //     ① access token은 수명을 짧게(2h) → 탈취돼도 피해 시간 제한
                //     ② refresh token(7d)은 HttpOnly 쿠키에 → JS 접근 자체가 불가능해 XSS로 못 훔침
                //   - 근본 방어는 설정이 아니라 코딩 습관: 사용자 입력을 이스케이프 없이 렌더링하지 않기
                //     (Thymeleaf의 th:text는 자동 이스케이프, th:utext는 위험) + CSP 헤더 적용 등
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/users/login",
                                "/users/join",
                                "/", // 페이지(HTML)는 공개, 데이터는 보호 - 브라우저 페이지 이동은 Bearer 헤더를 못 실으므로 페이지 인가는 API가 담당
                                "/admin",
                                "/api/users/login",
                                "/api/users/join",
                                "/api/tokens/refresh",
                                "/css/**",
                                "/js/**",
                                "/access-denied",
                                "/error" // 404 등 에러 포워딩 경로. 막으면 인증 리다이렉트 생김
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter (폼 로그인 필터) 자리 앞에 끼워 넣음
                // 인가 판단은 체인 맨 끝에서 일어나므로,
                // 그 전에 토큰을 검증해 SecurityContext를 채워둬야 "인증된 요청"으로 취급
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 인가 실패의 두 갈래:
                // - 401 (미인증): 누군지 모름 -> authenticationEntryPoint
                // - 403 (권한 부족): 누군지는 알지만 자격 없음 -> accessDeniedHandler
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint())
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 아이디/비밀번호 검증의 진입점
    // form-login에서는 필터가 내부적으로 호출했지만,
    // 토큰 방식에서는 UserSerivce.login()이 직접 호출
    // authentication() -> DaoAuthenticationProvider -> UserDetailService.loadUserByUsername() -> 비밀번호 대조
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix() // "ROLE_" 접두사 자동 부착
                .role("ADMIN").implies("USER")
                .build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return ((request, response, accessDeniedException) -> {
            if (request.getRequestURI().startsWith("/api")) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
            } else {
                response.sendRedirect("/access-denied");
            }
        });
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return ((request, response, authException) -> {
            if (request.getRequestURI().startsWith("/api")) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
            } else {
                response.sendRedirect("/access-denied");
            }
        });
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("status code : " + status + ", message : " + message);
    }
}
