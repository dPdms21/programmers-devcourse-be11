package com.example.oauth2.config.filter;

import com.example.oauth2.config.jwt.TokenProvider;
import com.example.oauth2.config.jwt.TokenStatus;
import com.example.oauth2.domain.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 검증 로직
        String requestURI = request.getRequestURI();
        log.info("requestURI: {}", requestURI);

        String token = resolveToken(request);

        if (token != null) {
            TokenStatus status = tokenProvider.validateToken(token);
            log.debug("Token status: {}", status);

            if (status == TokenStatus.VALID) {
                User user = tokenProvider.getTokenDetails(token);

                Authentication authentication = tokenProvider.getAuthentication(user, token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else if (status == TokenStatus.EXPIRED) {
                // 1) /api/users/info <- 401
                // 2) /api/tokens/refresh(access token 만료 상태) <- 401
                log.warn("{}, Token is expired", requestURI);
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더에서 JWT 토큰 추출
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
