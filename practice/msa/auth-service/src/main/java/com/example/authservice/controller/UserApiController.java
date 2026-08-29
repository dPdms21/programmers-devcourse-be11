package com.example.authservice.controller;

import com.example.authservice.config.jwt.JwtProperties;
import com.example.authservice.dto.SignInRequestDto;
import com.example.authservice.dto.SignInResponseDto;
import com.example.authservice.dto.SignUpRequestDto;
import com.example.authservice.dto.SignUpResponseDto;
import com.example.authservice.service.UserService;
import com.example.authservice.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
