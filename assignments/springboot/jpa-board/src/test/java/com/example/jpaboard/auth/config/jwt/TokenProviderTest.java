package com.example.jpaboard.auth.config.jwt;

import com.example.jpaboard.auth.config.jwt.JwtProperties;
import com.example.jpaboard.auth.config.jwt.TokenProvider;
import com.example.jpaboard.auth.config.jwt.TokenStatus;
import com.example.jpaboard.member.domain.entity.Member;
import com.example.jpaboard.member.domain.entity.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class TokenProviderTest {

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        String secretKey = Base64.getEncoder()
                .encodeToString(
                        "test-secret-key-for-hs512-must-be-at-least-64-bytes-long-1234567890"
                                .getBytes()
                );

        JwtProperties jwtProperties = new JwtProperties(
                "jpa-board-test",
                secretKey,
                60_000L,
                120_000L
        );

        tokenProvider = new TokenProvider(jwtProperties);
    }

    @Test
    void Access_Token을_생성하면_회원_정보와_ACCESS_타입이_저장된다() {
        Member member = createMember();

        String token = tokenProvider.createAccessToken(member);
        Claims claims = tokenProvider.parseClaims(token);

        assertThat(token).isNotBlank();
        assertThat(claims.getIssuer()).isEqualTo("jpa-board-test");
        assertThat(claims.getSubject()).isEqualTo("hong");
        assertThat(claims.get("userName", String.class)).isEqualTo("홍길동");
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_USER");
        assertThat(claims.get("tokenType", String.class)).isEqualTo("ACCESS");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Test
    void Refresh_Token을_생성하면_REFRESH_타입과_사용자_아이디가_저장된다() {
        Member member = createMember();

        String token = tokenProvider.createRefreshToken(member);
        Claims claims = tokenProvider.parseClaims(token);

        assertThat(token).isNotBlank();
        assertThat(claims.getIssuer()).isEqualTo("jpa-board-test");
        assertThat(claims.getSubject()).isEqualTo("hong");
        assertThat(claims.get("tokenType", String.class)).isEqualTo("REFRESH");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Test
    void 정상적인_토큰은_VALID를_반환한다() {
        String token = tokenProvider.createAccessToken(createMember());

        TokenStatus status = tokenProvider.validateToken(token);

        assertThat(status).isEqualTo(TokenStatus.VALID);
    }

    @Test
    void 만료된_토큰은_EXPIRED를_반환한다() throws InterruptedException {
        String secretKey = Base64.getEncoder()
                .encodeToString(
                        "expired-token-test-secret-key-for-hs512-at-least-64-bytes-123456789"
                                .getBytes()
                );

        JwtProperties jwtProperties = new JwtProperties(
                "jpa-board-test",
                secretKey,
                1L,
                1L
        );

        TokenProvider expiredTokenProvider =
                new TokenProvider(jwtProperties);

        String token =
                expiredTokenProvider.createAccessToken(createMember());

        Thread.sleep(10L);

        TokenStatus status =
                expiredTokenProvider.validateToken(token);

        assertThat(status).isEqualTo(TokenStatus.EXPIRED);
    }

    @Test
    void 변조된_토큰은_INVALID를_반환한다() {
        String token = tokenProvider.createAccessToken(createMember());

        String[] parts = token.split("\\.");

        String tamperedSignature =
                parts[2].charAt(0) == 'a'
                        ? "b" + parts[2].substring(1)
                        : "a" + parts[2].substring(1);

        String tamperedToken =
                parts[0] + "." +
                        parts[1] + "." +
                        tamperedSignature;

        TokenStatus status =
                tokenProvider.validateToken(tamperedToken);

        assertThat(status).isEqualTo(TokenStatus.INVALID);
    }

    @Test
    void 토큰에서_사용자_아이디를_추출한다() {
        String token =
                tokenProvider.createAccessToken(createMember());

        String userId = tokenProvider.getUserId(token);

        assertThat(userId).isEqualTo("hong");
    }

    @Test
    void 토큰에서_토큰_타입을_추출한다() {
        Member member = createMember();

        String accessToken =
                tokenProvider.createAccessToken(member);

        String refreshToken =
                tokenProvider.createRefreshToken(member);

        assertThat(tokenProvider.getTokenType(accessToken))
                .isEqualTo("ACCESS");

        assertThat(tokenProvider.getTokenType(refreshToken))
                .isEqualTo("REFRESH");
    }

    private Member createMember() {
        return Member.builder()
                .userId("hong")
                .password("encoded-password")
                .userName("홍길동")
                .role(Role.ROLE_USER)
                .build();
    }
}