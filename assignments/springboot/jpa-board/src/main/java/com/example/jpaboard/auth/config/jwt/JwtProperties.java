package com.example.jpaboard.auth.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String issuer,
        String secretKey,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {
}
