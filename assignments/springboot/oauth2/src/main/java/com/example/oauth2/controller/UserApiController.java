package com.example.oauth2.controller;

import com.example.oauth2.config.jwt.JwtProperties;
import com.example.oauth2.config.security.CustomUserDetails;
import com.example.oauth2.domain.entity.User;
import com.example.oauth2.dto.*;
import com.example.oauth2.service.UserService;
import com.example.oauth2.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;
    private final JwtProperties jwtProperties;

    @PostMapping("/join")
    public SignUpResponseDto signUp(
            @RequestBody SignUpRequestDto requestDto
    ) {
        userService.signUp(requestDto);

        return SignUpResponseDto.builder()
                .url("/users/login")
                .build();
    }

    @PostMapping("/login")
    public SignInResponseDto signIn(
            @RequestBody SignInRequestDto requestDto,
            HttpServletResponse response
    ) {
        SignInResponseDto result = userService.signIn(
                requestDto.getUserId(),
                requestDto.getPassword()
        );

        CookieUtil.addCookie(
                response,
                CookieUtil.REFRESH_TOKEN_COOKIE,
                result.getRefreshToken(),
                (int) jwtProperties.getRefreshTokenValidity().toSeconds()
        );

        result.setRefreshToken(null);

        return result;
    }

    @PostMapping("/oauth-join")
    public SignInResponseDto oauthJoin(
            @RequestBody OAuthSignUpRequestDto requestDto,
            HttpServletResponse response
    ) {
        SignInResponseDto signInResponseDto = userService.oauthSignUp(requestDto);

        CookieUtil.addCookie(
                response,
                CookieUtil.REFRESH_TOKEN_COOKIE,
                signInResponseDto.getRefreshToken(),
                (int) jwtProperties.getRefreshTokenValidity().toSeconds()
        );

        signInResponseDto.setRefreshToken(null);

        return signInResponseDto;
    }

    @GetMapping("/info")
    public UserInfoResponseDto getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();

        return UserInfoResponseDto.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .role(user.getRole())
                .build();
    }

    @PostMapping("/logout")
    public LogoutResponseDto logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        CookieUtil.deleteCookie(
                request,
                response,
                CookieUtil.REFRESH_TOKEN_COOKIE
        );

        return LogoutResponseDto.builder()
                .message("로그아웃 되었습니다.")
                .url("/users/login")
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public AuthorityResponseDto authorityUser() {
        return AuthorityResponseDto.builder()
                .message("일반 사용자 권한이 필요한 API입니다.")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public AuthorityResponseDto authorityAdmin() {
        return AuthorityResponseDto.builder()
                .message("관리자 권한이 필요한 API입니다.")
                .build();
    }
}
