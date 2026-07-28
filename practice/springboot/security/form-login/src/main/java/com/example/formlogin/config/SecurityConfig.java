package com.example.formlogin.config;

import com.example.formlogin.config.security.CustomAuthenticationFailureHandler;
import com.example.formlogin.config.security.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// * 폼 로그인이란?
// 개발자가 직접 만든 HTML 로그인 화면(폼)을 통해 아이디/비밀번호를 받아 인증하는 방식
// HTTP Basic이 브라우저 기본 팝업 + 헤더 방식이었다면, 폼 로그인은 우리가 디자인한 페이지 + 세션 기반이라는 점이 가장 큰 차이
// 사람이 사용하는 일반적인 웹 애플리케이션의 표준 방식

// * 전체 동작 흐름
// 로그인부터 그 이후 흐름까지의 과정
// 1. 보호된 자원 접근 -> 로그인 페이지로 리다이렉트
// 인증되지 않은 사용자가 보호된 페이지에 접근하면 로그인 페이지(/login)로 리다이렉트됨
// 이 처리는 AuthenticationEntryPoint(폼 로그인용 구현체 LoginUrlAuthenticationEntryPoint)가 담당
// 2. 사용자가 폼에 입력하고 제출
// 3. UsernamePasswordAuthenticationFilter가 가로챔
// 4. 인증 검증 (공통 흐름)
// AuthenticationManager (ProviderManager)
//   → DaoAuthenticationProvider
//        ├─ UserDetailsService (사용자 조회)
//        └─ PasswordEncoder (비밀번호 대조)
// 5. 인증 성공 -> 세션 생성
// 인증에 성공하면, 인증된 Authentication을 SecurityContext에 담고 이걸 HttpSession에 저장
// 그리고 서버는 세션 ID를 JSESSIONID라는 쿠키로 브라우저에 내려줌
// 성공 후 처리는 AuthenticationSuccessHandler가 담당 (기본적으로 원래 가려던 페이지 또는 지정된 페이지로 리다이렉트)
// 실패 시에는 AuthenticationFailureHandler가 담당 (보통 /login?error로 되돌림)
// 6. 이후 요청 -> 쿠키로 인증 유지
// 한 번 로그인하면 그 다음 요청부터는 아이디/비밀번호를 다시 보내지 않음
// 브라우저가 자동으로 JSESSIONID 쿠키를 실어 보내고, 서버는 이 쿠키로 세션을 찾아 저장해둔 SecurityContext를 복원 (이 복원은 SecurityContextHolderFilter가 담당)
// 즉 상태 유지(stateful) 방식. HTTP Basic은 매 요청 헤더를 보내는 무상태였던 것과 차이가 있음

// * 인증(Authentication) 흐름
// 로그인처럼 "너 누구냐"를 확인하는 과정

// * 인가(Authorization) 흐름
// "인증된 사용자가 이 자원에 접근할 권한이 있냐"를 판단하는 과정

// * 핵심 컴포넌트
// - SecurityFilterChain: 어떤 필터들을 어떤 순서로 태울지 정의
// - AuthenticationManager / AuthenticationProvider: 인증 검증
// - UserDetailsService / UserDetails: 사용자 정보 조회
// - PasswordEncoder: 비밀번호 검증
// - SecurityContextHolder: 인증 결과 보관
// - AuthorizationManager: 접근 권한 판단

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF(Cross-Site Request Forgery, 사이트 간 요청 위조)
                // 로그인된 사용자의 브라우저를 속여, 사용자가 의도하지 않은 요청을 우리 서버로 보내게 만드는 공격
                // 공격이 성립하는 이유 — 브라우저는 요청이 "어느 사이트에서 시작됐든" 대상 서버의 쿠키(JSESSIONID)를 자동으로 붙임
                //   1. 사용자가 우리 사이트에 로그인한 상태 (JSESSIONID 쿠키 보유)
                //   2. 공격자 페이지(evil.com) 방문 → 숨겨진 form이 우리 서버로 POST 자동 제출 (비밀번호 변경, 송금 등 상태 변경 요청)
                //   3. 브라우저가 JSESSIONID를 자동으로 실어 보냄 → 서버는 정상 사용자의 요청과 구분 불가

                // Spring Security의 방어 (기본 켜짐): 세션마다 예측 불가능한 CSRF 토큰을 발급하고,
                // POST/PUT/DELETE 요청은 이 토큰을 함께 보내야만 허용
                // 공격자 사이트는 Same-Origin Policy 때문에 페이지에 심어진 토큰을 읽을 수 없어 요청이 403으로 거부
                // (로그아웃도 POST + 토큰을 요구하는 이유 = 강제 로그아웃 공격 방지)

                // 여기서 끈 이유: 키면 signUp.js/signIn.js AJAX에 토큰을 실어야 해서 (X-CSRF-TOKEN 헤더 등)
                // 인증 흐름 학습에 집중하기 위해 비활성화. 실서비스(세션 방식)라면 켜고 토큰을 내려주는 방식 권장
                .csrf(AbstractHttpConfigurer::disable)
                // 인가 규칙 - URL별 접근 제어. AuthorizationFilter가 이 규칙대로 판단
                // 규칙은 "위에서부터" 순서대로 매칭되므로, 구체적인 경로를 먼저 쓰고 anyRequest()는 항상 마지막에 둠
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스(css/js)를 막으면 회원가입 페이지가 깨진 채로 렌더링되므로 함께 열어줌
                        .requestMatchers(
                                "/users/join",
                                "/api/users/join",
                                "/css/**",
                                "/js/**"
                        ).permitAll() // 인증 없이 접근 허용
                        .anyRequest().authenticated()
                )
                // 폼 로그인 설정 - UsernamePasswordAuthenticationFilter를 활성화하고 동작을 커스터마이징
                .formLogin(
                        form -> form
                                // GET /users/login -> 로그인 화면 (login.html)
                                // 지정하지 않으면 시큐리티가 기본 제공하는 /login페이지가 뜸
                                .loginPage("/users/login")
                                // POST /users/login -> 이 URL로 오는 로그인 제출을 UsernamePasswordAuthenticationFilter가 가로챔
                                // 컨트롤러를 만들 필요가 없음. - 요청이 DispatcherServlet에 도달하기 전에 필터가 처리
                                .loginProcessingUrl("/users/login")
                                // 필터가 요청 본문에서 아이디/비밀번호를 꺼낼 때 사용할 파라미터 이름
                                // 기본값은 username/password인데, 폼은 userId라는 이름으로 보내므로 맞춰줌
                                .usernameParameter("userId")
                                .passwordParameter("password")
                                // 인증 성공
                                .successHandler(customAuthenticationSuccessHandler)
                                // 인증 실패
                                .failureHandler(customAuthenticationFailureHandler)
                                .permitAll()
                )
                // 로그아웃 설정 - LogoutFilter가 처리. 역시 컨트롤러가 필요없음
                .logout(
                        logout -> logout
                                // 이 URL로 오는 요청을 LogoutFilter가 가로챔
                                .logoutUrl("/users/logout")
                                // 로그아웃 완료된 후 이동될 곳
                                .logoutSuccessUrl("/users/login")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                // 로그아웃 URL도 인증 여부와 무관하게 접근 허용
                                .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
