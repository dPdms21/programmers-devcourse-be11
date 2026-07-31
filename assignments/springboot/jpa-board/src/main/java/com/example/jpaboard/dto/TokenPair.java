package com.example.jpaboard.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}