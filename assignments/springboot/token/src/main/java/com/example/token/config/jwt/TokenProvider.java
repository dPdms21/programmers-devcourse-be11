package com.example.token.config.jwt;

import com.example.token.config.security.CustomUserDetails;
import com.example.token.domain.entity.Role;
import com.example.token.domain.entity.User;
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
}
