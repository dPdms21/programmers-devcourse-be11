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

// * OAuth2 로그인 성공 직후의 처리기
// 여기까지 오면 "SNS 인증"은 이미 끝난 상태 (state 검증/토큰 교환/user-info 조회 완료)
// 그런데 세션을 쓰지 않으므로 (STATELESS), SNS 인증 결과를 "서비스의 신분증"인
// 자체 JWT(access/refresh)로 바꿔서 클라이언트에게 건네주는 것이 이 클래스의 역할

// 왜 SNS가 준 토큰을 그대로 안 쓰고 우리 JWT를 새로 발급하나?
// - 제공자(카카오) 토큰에 의존하지 않고, 우리 권한(Role)/만료정책을 통제하기 위해
// - 이후 모든 API 인증을 "제공자와 무관한 단일 규격"으로 통일하기 위해

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
        // 인증 성공시 principal은 CustomOAuth2UserService가 반환했던 CustomOAuth2User
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        String targetUrl;

        if (principal.isRegistered()) {
            // 기존 회원
            TokenService.TokenPair tokens = tokenService.issueToken(principal.getUser());
            CookieUtil.addCookie(
                    response,
                    CookieUtil.REFRESH_TOKEN_COOKIE,
                    tokens.refreshToken(),
                    (int) jwtProperties.getRefreshTokenValidity().toSeconds()
            );
            targetUrl = "/";
        } else {
            // 미가입
            // 10분짜리 "가입 토큰"을 발급해 가입 동의 페이지로 보냄
            String signupToken = tokenProvider.createSignupToken(principal.getProvider(), principal.getUserInfo());

            targetUrl = UriComponentsBuilder.fromUriString("/users/oauth-join")
                    .queryParam("signupToken", signupToken)
                    .build()
                    .toUriString();
        }

        // 이미 응답이 커밋되었다면 리다이렉트가 불가능하므로 방어적으로 빠져나감
        if (response.isCommitted()) {
            log.debug("Response has already been committed. Redirecting is not possible.");

            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
