package com.example.token.config.filter;

import com.example.token.config.jwt.TokenProvider;
import com.example.token.config.jwt.TokenStatus;
import com.example.token.domain.entity.User;
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

// * JWT 인증 필터 - 모든 요청마다 한 번 실행 (OncePerRequestFilter)
// 이 필터는 "요청에 실려온 토큰"에서 인증 상태를 복원
// 1) Authorization: Bearer <token> 헤더에서 토큰 추출
// 2) 서명/만료 검증 (TokenProvider)
// 3) 유효하면 클레임으로 User를 복원해서 SecurityContext에 등록
// -> 이 요청을 처리하는 동안만 "인증된 사용자"가 됨 (STATELESS 응답 후 컨텍스트는 버려짐)
// 4) 토큰이 없거나 무효면 인증 없이 통과 -> 보호된 경로라면 체인 끝의 AuthorizationFilter가 거부 (401)

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

                // * principal이란
                // Authentication 객체는 세 가지로 구성:
                // - principal   : "인증된 주체가 누구인가" - 사용자를 대표하는 객체 (보통 UserDetails)
                // - credentials : 인증에 사용한 증명 수단 (비밀번호, 토큰 등. 인증 후에 보통 지움)
                // - authorities : 부여된 권한 목록
                // 즉 principal은 "이 요청의 주인"이고, 시큐리티 어디서든
                // SecurityContextHolder...getAuthentication().getPrincipal()로 꺼낼 수 있음
                // 우리는 principal 자리에 CustomUserDetails를 넣음
                // 그래서 @AuthenticationPrincipal CustomUserDetails로 바로 주입받아
                // getUser()로 도메인 User까지 접근할 수 있음
                Authentication authentication = tokenProvider.getAuthentication(user, token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else if (status == TokenStatus.EXPIRED) {
                log.warn("{}, Token is expired", requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                return;
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
