package com.example.authservice.domain.entity;

public enum UserStatus {
    ACTIVE, // 정상
    WITHDRAWING, // 탈퇴 진행중
    WITHDRAWN, // 탈퇴 확정
}
