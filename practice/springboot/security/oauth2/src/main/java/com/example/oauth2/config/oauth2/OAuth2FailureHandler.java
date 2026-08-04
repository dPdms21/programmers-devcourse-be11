package com.example.oauth2.config.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// OAuth2 로그인의 실패 경로 처리기
// loadUser() 사이에서 문제가 생긴 경우
// - 사용자가 카카오 동의 화면에서 "취소" 또는 권한 거부
// - state 불일치/인가코드 만료/토큰 교환 실패
// - CustomOAuth2UserService가 던진 OAuth2AuthenticationException

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        // 실패 사유를 ?error= 쿼리파라미터로 실어 로그인 페이지로 보냄
        String targetUrl = UriComponentsBuilder.fromUriString("/users/login")
                .queryParam("error", exception.getLocalizedMessage())
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
