package com.example.jpaboard.member.dto;

public record MemberInfoResponseDto(
        String userId,
        String userName,
        String role
) {
}