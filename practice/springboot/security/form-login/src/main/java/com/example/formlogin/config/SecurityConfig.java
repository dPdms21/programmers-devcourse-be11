package com.example.formlogin.config;

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
// 인증 안 된 사용자가 보호된 페이지에 접근하면, 로그인 페이지로(/login)로 리다이렉트됨
// 이 처리는 AuthenticationEntryPoint(폼 로그인용 구현체 LoginUrlAuthenticationEntryPoint)가 담당
// 2. 사용자가 폼에 입력하고 제출
// 3. UsernamePasswordAuthenticationFilter가 가로챔
// 4. 인증 검증 (공통 흐름)
// AuthenticationManager (ProviderManager)
//   → DaoAuthenticationProvider
//        ├─ UserDetailsService (사용자 조회)
//        └─ PasswordEncoder (비밀번호 대조)
// 5. 인증 성공 -> 세션 생성

// * 인증(Authentication) 흐름
// 로그인처럼 "너 누구냐"를 확인하는 과정

// * 인가(Authorization) 흐름
// "인증된 사용자가 이 자원에 접근할 권한이 있냐"를 판단하는 과정

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers(
                                "/users/join",
                                "/api/users/join",
                                "/css/**",
                                "/js/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
