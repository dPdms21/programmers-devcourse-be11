package com.example.authservice.config.oauth2;

import com.example.authservice.config.jwt.JwtProperties;
import com.example.authservice.config.jwt.TokenProvider;
import com.example.authservice.service.TokenService;
import com.example.authservice.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    @Value("${web-service.url}")
    private String webServiceUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        String targetUrl;

        if (principal.isRegistered()) {
            // 기존회원
            TokenService.TokenPair tokenPair = tokenService.issueToken(principal.getUser());
            CookieUtil.addCookie(
                    response,
                    CookieUtil.REFRESH_TOKEN_COOKIE,
                    tokenPair.refreshToken(),
                    (int) jwtProperties.getRefreshTokenValidity().toSeconds()
            );
            targetUrl = webServiceUrl + "/";
        } else {
            // 미가입
            // 10분짜리 "가입 토큰"을 발급해 가입 동의 페이지로 보냄
            String signupToken = tokenProvider.createSignupToken(principal.getProvider(), principal.getUserInfo());

            targetUrl = UriComponentsBuilder.fromUriString(webServiceUrl + "/users/oauth-join")
                    .queryParam("signupToken", signupToken)
                    .build()
                    .toUriString();
        }

        if (response.isCommitted()) {
            log.debug("Response has already been committed");

            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
