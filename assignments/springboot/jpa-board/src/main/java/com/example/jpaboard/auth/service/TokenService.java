package com.example.jpaboard.auth.service;

import com.example.jpaboard.auth.config.jwt.TokenProvider;
import com.example.jpaboard.auth.config.jwt.TokenStatus;
import com.example.jpaboard.member.domain.entity.Member;
import com.example.jpaboard.member.domain.repository.MemberRepository;
import com.example.jpaboard.auth.dto.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public TokenPair login(String userId, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userId, password)
        );

        Member member = memberRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "회원을 찾을 수 없습니다. userId=" + authentication.getName())
                );

        String accessToken = tokenProvider.createAccessToken(member);
        String refreshToken = tokenProvider.createRefreshToken(member);

        return new TokenPair(accessToken, refreshToken);
    }

    public TokenPair refresh(String refreshToken) {
        if (refreshToken == null
                || tokenProvider.validateToken(refreshToken) != TokenStatus.VALID
                || !"REFRESH".equals(tokenProvider.getTokenType(refreshToken))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "유효하지 않은 Refresh Token입니다."
            );
        }

        String userId = tokenProvider.getUserId(refreshToken);

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "회원을 찾을 수 없습니다."
                ));

        String newAccessToken = tokenProvider.createAccessToken(member);
        String newRefreshToken = tokenProvider.createRefreshToken(member);

        return new TokenPair(
                newAccessToken,
                newRefreshToken
        );
    }
}
