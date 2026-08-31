package com.example.boardservice.config;

import com.example.boardservice.config.filter.TokenAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/boards/file/download/**",
                                // 서버 간 내부 API
                                "/api/boards/internal/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error" // 에러 포워딩 경로. 막으면 401 응답이 다시 401을 만드는 루프가 발생
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // JWT filter 적용
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // exceptionHandling 적용
                // - 401 (미인증): authenticationEntryPoint
                // - 403 (권한 부족): accessDeniedHandler
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                );

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return ((request, response, authException) ->
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다."));
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return ((request, response, authException) ->
                sendError(response, HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다."));
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("status code: " + status + ", message: " + message);
    }
}
