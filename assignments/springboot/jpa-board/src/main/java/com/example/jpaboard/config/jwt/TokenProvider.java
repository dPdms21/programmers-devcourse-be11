package com.example.jpaboard.config.jwt;

import com.example.jpaboard.domain.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public TokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtProperties.secretKey())
        );
    }

    public String createAccessToken(Member member) {
        Date now = new Date();
        Date expiration = new Date(
                now.getTime() + jwtProperties.accessTokenExpiration()
        );

        return Jwts.builder()
                .issuer(jwtProperties.issuer())
                .subject(member.getUserId())
                .claim("id", member.getId())
                .claim("userName", member.getUserName())
                .claim("role", member.getRole().name())
                .claim("tokenType", "ACCESS")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public String createRefreshToken(Member member) {
        Date now = new Date();
        Date expiration = new Date(
                now.getTime() + jwtProperties.refreshTokenExpiration()
        );

        return Jwts.builder()
                .issuer(jwtProperties.issuer())
                .subject(member.getUserId())
                .claim("tokenType", "REFRESH")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public TokenStatus validateToken(String token) {
        try {
            parseClaims(token);

            return TokenStatus.VALID;
        }
        catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        }
        catch (JwtException | IllegalArgumentException e) {
            return TokenStatus.INVALID;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public String getTokenType(String token) {
        return parseClaims(token).get("tokenType", String.class);
    }
}
