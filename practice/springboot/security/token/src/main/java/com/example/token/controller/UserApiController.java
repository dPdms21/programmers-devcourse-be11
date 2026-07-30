package com.example.token.controller;

import com.example.token.config.jwt.JwtProperties;
import com.example.token.config.security.CustomUserDetails;
import com.example.token.domain.entity.User;
import com.example.token.dto.*;
import com.example.token.service.UserService;
import com.example.token.util.CookieUtil;
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
    public SignUpResponseDto join(@RequestBody SignUpRequestDto requestDto) {
        userService.signUp(requestDto);

        return SignUpResponseDto.builder()
                .url("/users/login")
                .build();
    }

    @PostMapping("/login")
    public SignInResponseDto login(
            @RequestBody SignInRequestDto requestDto,
            HttpServletResponse response
    ) {
        SignInResponseDto signInResponseDto = userService.login(requestDto);

        CookieUtil.addCookie(
                response,
                CookieUtil.REFRESH_TOKEN_COOKIE,
                signInResponseDto.getRefreshToken(),
                (int) jwtProperties.getRefreshTokenValidity().toSeconds()
        );

        signInResponseDto.setRefreshToken(null);

        return signInResponseDto;
    }

    @PostMapping("/logout")
    public LogoutResponseDto logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        CookieUtil.deleteCookie(request, response, CookieUtil.REFRESH_TOKEN_COOKIE);
        return LogoutResponseDto.builder()
                .message("로그아웃 되었습니다.")
                .url("/users/login")
                .build();
    }

    @GetMapping("/info")
    public UserInfoResponseDto getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return UserInfoResponseDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userName(user.getName())
                .role(user.getRole())
                .build();
    }

    // "hasRole('USER')"는 내부적으로 "ROLE_USER"권한을 찾음 (접두사 자동 부착)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public AuthorityResponseDto authority() {
        return AuthorityResponseDto.builder()
                .message("일반 사용자만 볼 수 있는 권한입니다.")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public AuthorityResponseDto authorityAdmin() {
        return AuthorityResponseDto.builder()
                .message("관리자만 볼 수 있는 권한입니다.")
                .build();
    }
}
