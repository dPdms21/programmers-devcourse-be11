package com.example.authservice.controller;

import com.example.authservice.config.jwt.JwtProperties;
import com.example.authservice.config.security.CustomUserDetails;
import com.example.authservice.domain.entity.User;
import com.example.authservice.dto.*;
import com.example.authservice.service.UserService;
import com.example.authservice.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ToString
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;
    private final JwtProperties jwtProperties;

    @PostMapping("/join")
    public SignUpResponseDto join(@RequestBody SignUpRequestDto signUpRequestDto) {
        userService.signUp(signUpRequestDto);

        return SignUpResponseDto.builder()
                .url("/users/login")
                .build();
    }

    @PostMapping("/login")
    public SignInResponseDto login(
            @RequestBody SignInRequestDto signInRequestDto,
            HttpServletResponse response
    ) {
        SignInResponseDto logined = userService.login(signInRequestDto);

        CookieUtil.addCookie(
                response,
                CookieUtil.REFRESH_TOKEN_COOKIE,
                logined.getRefreshToken(),
                (int) jwtProperties.getRefreshTokenValidity().toSeconds()
        );

        logined.setRefreshToken(null);

        return logined;
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

    @GetMapping("/names")
    public List<UserNameResponseDto> getUserNames(@RequestParam List<String> userIds) {
        return userService.getUserNames(userIds);
    }
}
