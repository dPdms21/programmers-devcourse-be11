package com.example.boardservice.config.filter;

import com.example.boardservice.config.jwt.TokenProvider;
import com.example.boardservice.config.jwt.TokenStatus;
import com.example.boardservice.config.security.CustomUserDetails;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.debug("requestURI: {}", requestURI);

        String token = resolveToken(request);
        log.debug("token: {}", token);

        if (token != null) {
            TokenStatus status = tokenProvider.validateToken(token);
            log.debug("Token status: {}", status);
            if (status == TokenStatus.VALID) {
                CustomUserDetails userDetails = tokenProvider.getTokenDetails(token);

                Authentication authentication = tokenProvider.getAuthentication(userDetails, token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (status == TokenStatus.EXPIRED) {
                log.warn("{}, Token is expired", requestURI);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더에서 JWT토큰 추출
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
