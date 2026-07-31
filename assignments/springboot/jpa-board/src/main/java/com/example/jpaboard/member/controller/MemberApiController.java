package com.example.jpaboard.member.controller;

import com.example.jpaboard.auth.dto.TokenPair;
import com.example.jpaboard.auth.config.security.CustomUserDetails;
import com.example.jpaboard.member.domain.entity.Member;
import com.example.jpaboard.member.dto.*;
import com.example.jpaboard.member.service.MemberService;
import com.example.jpaboard.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/join")
    public MemberJoinResponseDto join(@RequestBody MemberJoinRequestDto request) {
        memberService.join(request);

        return new MemberJoinResponseDto("/members/login");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginTokenResponseDto> login(
            @ModelAttribute LoginRequestDto request
    ) {
        TokenPair tokenPair = tokenService.login(
                request.getUserId(),
                request.getPassword()
        );

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refreshToken", tokenPair.refreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new LoginTokenResponseDto(tokenPair.accessToken()));
    }

    @GetMapping("/info")
    public MemberInfoResponseDto info(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();

        return new MemberInfoResponseDto(
                member.getUserId(),
                member.getUserName(),
                member.getRole().name()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie expiredRefreshTokenCookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        expiredRefreshTokenCookie.toString()
                )
                .build();
    }
}
