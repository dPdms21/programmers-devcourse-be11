package com.example.webservice.controller;

import com.example.webservice.dto.*;
import com.example.webservice.service.AuthService;
import com.example.webservice.util.HeaderRelayUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthApiController {
    private final AuthService authService;

    @PostMapping("/join")
    public SignUpResponseDto join(@RequestBody SignUpRequestDto signUpRequestDto) {
        return authService.signUp(signUpRequestDto);
    }

    @PostMapping("/oauth-join")
    public SignInResponseDto oauthJoin(
            @RequestBody OAuthSignUpRequestDto dto,
            HttpServletResponse response
    ) {
        return HeaderRelayUtil.relaySetCookie(authService.oauthSignUp(dto), response);
    }

    @PostMapping("/login")
    public SignInResponseDto login(
            @RequestBody SignInRequestDto signInRequestDto,
            HttpServletResponse response
    ) {
        return HeaderRelayUtil.relaySetCookie(authService.signIn(signInRequestDto), response);
    }

    @PostMapping("/logout")
    public LogoutResponseDto logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            HttpServletResponse response
    ) {
        return HeaderRelayUtil.relaySetCookie(authService.logout(authorization, cookie), response);
    }

    @DeleteMapping("/me")
    public WithdrawResponseDto withdraw(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            HttpServletResponse response
    ) {
        return HeaderRelayUtil.relaySetCookie(authService.withdraw(authorization, cookie), response);
    }

    @GetMapping("/info")
    public UserInfoResponseDto getUserInfo(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        return authService.getUserInfo(authorization);
    }

}
