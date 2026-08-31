package com.example.boardservice.config.jwt;

import com.example.boardservice.config.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;

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
    private void init() {
        // 키와 파서는 불변이므로 요청마다 새로 만들지 않고 한 번만 생성해 재사용
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecretKey()));
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    public TokenStatus validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            log.debug("Token is valid");

            return TokenStatus.VALID;
        }
        catch (ExpiredJwtException e) {
            log.warn("Token is expired");

            return TokenStatus.EXPIRED;
        }
        catch (Exception e) {
            log.warn("Token is invalid");

            return TokenStatus.INVALID;
        }
    }

    // 클레임을 principal(CustomUserDetails)로 복원 — DB 조회 없이 토큰만으로.
    // auth-service는 User 엔티티로 복원하지만, 계정은 auth 소유라 board에는 그 엔티티가 없음
    // 검증자에게 필요한 건 "토큰이 말하는 신원" 그 자체이므로 클레임 값을 그대로 담음
    public CustomUserDetails getTokenDetails(String token) {
        Claims claims = getClaims(token);

        return CustomUserDetails.builder()
                .id(claims.get(CLAIM_ID, Long.class))
                .userId(claims.getSubject())
                .userName(claims.get(CLAIM_NAME, String.class))
                .role(claims.get(CLAIM_ROLE, String.class))
                .build();
    }

    private Claims getClaims(String token) {
        return jwtParser
                .parseSignedClaims(token)
                .getPayload();
    }

    // 복원된 principal로 인증 정보를 만드는 메서드
    public Authentication getAuthentication(CustomUserDetails principal, String token) {
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }
}
