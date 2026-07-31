package com.example.jpaboard.auth.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}