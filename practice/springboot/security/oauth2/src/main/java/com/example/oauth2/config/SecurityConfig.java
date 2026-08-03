package com.example.oauth2.config;

import com.example.oauth2.config.filter.TokenAuthenticationFilter;
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

// * OAuth2 (Open Authorization 2.0)
// OAuth2는 "비밀번호를 넘겨주지 않고 권한을 위임"하기 위한 표준 프레임워크
// OAuth2 사용 방법:
// 사용자가 "카카오에게 직접" 허락받고
// 서비스는 그 허락의 증표(access token)만 받음
// -> 비밀번호는 원래 주인(카카오)만 알고, 위임 범위와 회수가 가능해짐

// 역할(role)
// 1) Resource Owner: 자원의 주인 = 사용자 (카카오 계정의 주인)
// 2) Client: 자원을 쓰고 싶은 제3자 앱 = 서비스
// 3) Authorization Server: 허락(인가)을 발급하는 서버 = kauth.kakao.com
// 4) Resource Server: 실제 자원(프로필 등)을 보관한 서버 = kapi.kakao.com

// 인가 코드 방식 - 표준 흐름
// 1) Client가 사용자를 Authorization Server의 인가 페이지로 리다이렉트
// (client_id, redirect_uri, scope, state를 쿼리로 실어 보냄)
// 2) 사용자가 "카카오 화면에서" 로그인하고 권한 제공에 동의
// 3) Authorization Server가 redirect_uri로 "인가 코드"를 돌려줌 (브라우저 공유)
// 4) Client 서버가 code + client_secret으로 토큰 엔드포인트에 직접 요청 (브라우저 안 거침)
// 5) Authorization Server가 access token 발급
// 6) Client가 그 토큰으로 Resource Server에서 사용자 정보 조회

// Spring Security에서의 동작 흐름 - oauth2Login()
// 위 표준 흐름을 필터 두 개가 나눠서 대신 처리
// 1) OAuth2AuthorizationRequestRedirectFilter
// -> /oauth2/authorization/{registrationId} 요청을 가로채 인가 페이지로 리다이렉트
// 2) OAuth2LoginAuthenticationFilter
// -> /login/oauth2/code/{registrationId}로 돌아온 code를 받아
// "state 검증 -> 토큰 교환 -> 사용자 정보 조회까지 수행"
// 3) 조회된 사용자 정보를 OAuth2UserService.loadUser()에 넘김
// -> 여기서 "제공자의 회원"을 "우리 DB의 회원"으로 연결(없으면 가입)하는 것이 개발자의 몫
// 4) 반환된 OAuth2User로 Authentication을 만들어 SecurityContext에 저장 -> 로그인 완료
// 5) 마지막으로 SuccessHandler 호출 -> 로그인 후처리(JWT 발급)도 개발자의 몫

// 정리하면 개발자가 구현하는 것은 파이프라인의 양 끝 훅(hook) 두 개뿐
// - OAuth2UserService: 제공자 응답 -> 우리 회원 매핑
// - SuccessHandler: 로그인 성공 -> 후처리 (토큰 발급, 리다이렉트 등)
// 나머지는(리다이렉트, state, 코드-토큰 교환, 정보 조회)는 전부 프레임워크가 처리하고,
// 제공자별 차이(엔드포인트 URL, scope 등)는 코드가 아닌 설정 파일의 registration, provider 항목으로 흡수
// 그래서 네이버 같은 새 제공자를 추가해도 Java 코드는 거의 안 바뀜

// * kakao developers 절차 - https://developers.kakao.com/
// 1.kakao developers 접속 -> 로그인
// 2.앱 -> 앱생성(icon까지 등록), 도메인등록(x)
// 3. 앱 > 앱 설정 > 앱 > 일반 -> 개인 개발자 or 사업자 정보등록(개인 사업자 번호 필수)
// 비즈 앱 전환(가입시 이메일을 받아오기 위해 필수) -> 카카오 비즈니스 인증 진행

//=== 카카오 비즈니스 가입 완료 문구 ===
//카카오비즈니스 회원이 되신 것을 환영합니다.
//이제 다양한 자산과 도구를 이용하실 수 있습니다.

//가입 정보는 카카오비즈니스 > 내 정보에서 확인하거나 수정하실 수 있습니다.

//감사합니다.
//====================================

// 4. 앱 > 앱 설정 > 앱 > 플랫폼 키 > REST API 키
//client-id : xxxxxxxxx
//client-secret : xxxxxxxxx

// 5. 앱 > 앱 설정 > 앱 > 플랫폼 키 > REST API 키
// 카카오 로그인 리다이렉트 URI 등록 후 꼭 저장 누르기
// http://localhost:8080/login/oauth2/code/kakao

// 6. 카카오 로그인 활성화
// 앱 > 제품 설정 > 카카오 로그인 > 일반 > 활성화 시키기

// 7. 동의항목
// 앱 > 제품 설정 > 카카오 로그인
// 닉네임, 이메일 설정 변경(상태 : 필수동의)

// ** 프로젝트에서의 OAuth2 로그인 흐름 **

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
