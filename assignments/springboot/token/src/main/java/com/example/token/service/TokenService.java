package com.example.token.service;

import com.example.token.config.jwt.JwtProperties;
import com.example.token.config.jwt.TokenProvider;
import com.example.token.config.jwt.TokenStatus;
import com.example.token.domain.entity.User;
import com.example.token.dto.RefreshTokenResponseDto;
import com.example.token.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    public record TokenPair(
            String accessToken,
            String refreshToken
    ) {

    }

    public TokenPair issueTokens(User user) {
        String accessToken = tokenProvider.generateToken(
                user,
                jwtProperties.getAccessTokenValidity()
        );

        String refreshToken = tokenProvider.generateToken(
                user,
                jwtProperties.getRefreshTokenValidity()
        );

        return new TokenPair(accessToken, refreshToken);
    }

    public RefreshTokenResponseDto refreshToken(Cookie[] cookies) {
        String refreshToken = getRefreshTokenFromCookies(cookies);

        if (refreshToken != null
                && tokenProvider.validateToken(refreshToken) == TokenStatus.VALID) {
            User user = tokenProvider.getTokenDetails(refreshToken);
            TokenPair tokens = issueTokens(user);

            return RefreshTokenResponseDto.builder()
                    .validated(true)
                    .accessToken(tokens.accessToken())
                    .refreshToken(tokens.refreshToken())
                    .build();
        }

        return RefreshTokenResponseDto.builder()
                .validated(false)
                .build();
    }

    private String getRefreshTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (CookieUtil.REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
