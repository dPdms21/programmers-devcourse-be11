package com.example.boardservice.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class ServiceTokenFilter extends OncePerRequestFilter {
    public static final String SERVICE_TOKEN_HEADER = "X-Service-Token";

    private static final String INTERNAL_API_PREFIX = "/api/boards/internal/";

    @Value("${service.token}")
    private String serviceToken;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getRequestURI().startsWith(INTERNAL_API_PREFIX)) {
            String token = request.getHeader(SERVICE_TOKEN_HEADER);

            if (!serviceToken.equals(token)) {
                log.warn("[내부 API 차단] 서비스 토큰 불일치: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("status code: 401, message: 서비스 간 인증이 필요합니다.");

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
