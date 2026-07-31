package com.example.jpaboard.auth.controller;

import com.example.jpaboard.auth.dto.TokenPair;
import com.example.jpaboard.auth.dto.TokenRefreshResponseDto;
import com.example.jpaboard.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refresh(
            @CookieValue(
                    name = "refreshToken",
                    required = false
            ) String refreshToken
    ) {
        TokenPair tokenPair =
                tokenService.refresh(refreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from(
                        "refreshToken",
                        tokenPair.refreshToken()
                )
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookie.toString()
                )
                .body(
                        new TokenRefreshResponseDto(
                                tokenPair.accessToken()
                        )
                );
    }
}