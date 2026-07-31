package com.example.jpaboard.dto;

public record MemberInfoResponseDto(
        String userId,
        String userName,
        String role
) {
}