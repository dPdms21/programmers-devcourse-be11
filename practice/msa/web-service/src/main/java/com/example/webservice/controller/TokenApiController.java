package com.example.webservice.controller;

import com.example.webservice.dto.RefreshTokenResponseDto;
import com.example.webservice.service.AuthService;
import com.example.webservice.util.HeaderRelayUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokens")
public class TokenApiController {
    private final AuthService authService;

    @PostMapping("/refresh")
    public RefreshTokenResponseDto refreshToken(
            @RequestHeader(value = HttpHeaders.COOKIE, required = false) String cookie,
            HttpServletResponse response
    ) {
        return HeaderRelayUtil.relaySetCookie(authService.refreshToken(cookie), response);
    }
}
