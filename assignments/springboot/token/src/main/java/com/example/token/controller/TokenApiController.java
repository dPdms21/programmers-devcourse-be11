package com.example.token.controller;

import com.example.token.config.jwt.JwtProperties;
import com.example.token.dto.ErrorResponseDto;
import com.example.token.dto.RefreshTokenResponseDto;
import com.example.token.service.TokenService;
import com.example.token.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokens")
public class TokenApiController {

    private final TokenService tokenService;
    private final JwtProperties jwtProperties;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        RefreshTokenResponseDto result =
                tokenService.refreshToken(request.getCookies());

        if (result.isValidated()) {
            CookieUtil.addCookie(
                    response,
                    CookieUtil.REFRESH_TOKEN_COOKIE,
                    result.getRefreshToken(),
                    (int) jwtProperties.getRefreshTokenValidity().toSeconds()
            );

            result.setRefreshToken(null);

            return ResponseEntity.ok(result);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto(
                        HttpStatus.UNAUTHORIZED.value(),
                        "리프레시 토큰이 만료되었습니다."
                ));
    }
}