package com.example.authservice.domain.entity;

import com.example.authservice.config.oauth2.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String name;

    @Column(length = 50)
    private String email;

    @Column(length = 50)
    private String userId;

    @Column(length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(length = 100)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    // saga
    private LocalDateTime statusUpdatedAt;

    public User updateProfile(String name) {
        this.name = name;

        return this;
    }

    // ==== 탈퇴 saga 상태 전이 — setter 대신 의도가 드러나는 도메인 메서드 ====
    public User startWithdrawal() {
        this.status = UserStatus.WITHDRAWING;
        this.statusUpdatedAt = LocalDateTime.now();

        return this;
    }

    public User completeWithdrawal() {
        this.status = UserStatus.WITHDRAWN;
        this.statusUpdatedAt = LocalDateTime.now();

        return this;
    }

    // 보상(compensation): 참여자(board) 실패 시 이미 커밋한 상태 변경을 "반대 연산"으로 되돌림
    public User cancelWithdrawal() {
        this.status = UserStatus.ACTIVE;
        this.statusUpdatedAt = LocalDateTime.now();

        return this;
    }
}
