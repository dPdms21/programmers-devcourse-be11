package com.example.oauth2.config.oauth2;

import com.example.oauth2.config.jwt.JwtProperties;
import com.example.oauth2.config.jwt.TokenProvider;
import com.example.oauth2.service.TokenService;
import com.example.oauth2.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        String targetUrl;

        if (principal.isRegistered()) {
            TokenService.TokenPair tokens = tokenService.issueTokens(principal.getUser());
            CookieUtil.addCookie(
                    response,
                    CookieUtil.REFRESH_TOKEN_COOKIE,
                    tokens.refreshToken(),
                    (int) jwtProperties.getRefreshTokenValidity().toSeconds()
            );
            targetUrl = "/";
        } else {
            String signupToken = tokenProvider.createSignupToken(principal.getProvider(), principal.getUserInfo());
            targetUrl = UriComponentsBuilder.fromUriString("/users/oauth-join")
                    .queryParam("signupToken", signupToken)
                    .build()
                    .toUriString();
        }

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Redirecting is not possible.");

            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
