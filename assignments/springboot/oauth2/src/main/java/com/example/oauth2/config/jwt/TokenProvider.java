package com.example.oauth2.config.jwt;

import com.example.oauth2.config.oauth2.AuthProvider;
import com.example.oauth2.config.oauth2.OAuth2UserInfo;
import com.example.oauth2.config.security.CustomUserDetails;
import com.example.oauth2.domain.entity.Role;
import com.example.oauth2.domain.entity.User;
import com.example.oauth2.dto.SignupPayloadDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLE = "role";

    private static final String CLAIM_PROVIDER = "provider";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_TYPE = "type";
    private static final String TOKEN_TYPE_SIGNUP = "signup";
    private static final Duration SIGNUP_TOKEN_VALIDITY = Duration.ofMinutes(10);

    private final JwtProperties jwtProperties;

    private SecretKey secretKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(jwtProperties.getSecretKey())
        );

        jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    public String generateToken(User user, Duration validity) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validity.toMillis());

        return makeToken(user, expiration);
    }

    private String makeToken(User user, Date expiration) {
        Date now = new Date();

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiration)
                .subject(user.getUserId())
                .claim(CLAIM_ID, user.getUserId())
                .claim(CLAIM_ROLE, user.getRole().name())
                .claim(CLAIM_NAME, user.getName())
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public TokenStatus validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            log.debug("Token is valid");

            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            log.warn("Token is expired");

            return TokenStatus.EXPIRED;
        } catch (Exception e) {
            log.warn("Token is invalid");

            return TokenStatus.INVALID;
        }
    }

    private Claims getClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public User getTokenDetails(String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        return User.builder()
                .userId(claims.getSubject())
                .name(claims.get(CLAIM_NAME, String.class))
                .role(Role.valueOf(claims.get(CLAIM_ROLE, String.class)))
                .build();
    }

    public Authentication getAuthentication(User user, String token) {
        CustomUserDetails principal = CustomUserDetails.builder()
                .user(user)
                .build();

        return new UsernamePasswordAuthenticationToken(
                principal,
                token,
                principal.getAuthorities()
        );
    }

    public String createSignupToken(AuthProvider provider, OAuth2UserInfo userInfo) {
        Date now = new Date();

        return Jwts.builder()
                .header().type("JWT").and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + SIGNUP_TOKEN_VALIDITY.toMillis()))
                .subject(userInfo.id())
                .claim(CLAIM_NAME, userInfo.name())
                .claim(CLAIM_TYPE, TOKEN_TYPE_SIGNUP)
                .claim(CLAIM_PROVIDER, provider.name())
                .claim(CLAIM_EMAIL, userInfo.email())
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    // 가입 토큰 검증 + 클레임 복원
    public SignupPayloadDto getSignupPayload(String token) {
        Claims claims;

        try {
            claims = getClaims(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 가입 토큰입니다.");
        }

        if (!TOKEN_TYPE_SIGNUP.equals(claims.get(CLAIM_TYPE, String.class))) {
            throw new IllegalArgumentException("가입 토큰이 아닙니다.");
        }

        return new SignupPayloadDto(
                AuthProvider.valueOf(claims.get(CLAIM_PROVIDER, String.class)),
                claims.getSubject(),
                claims.get(CLAIM_EMAIL, String.class),
                claims.get(CLAIM_NAME, String.class)
        );
    }
}
