package com.example.oauth2.domain.entity;

import com.example.oauth2.config.oauth2.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

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

    // 로그인할 때마다 SNS의 최신 프로필로 갱신
    // 영속 상태 엔티티의 필드를 바꾸면 트랜잭션 커밋 시
    // JPA 변경감지(dirty checking)로 자동 UPDATE 됨 (별도 save 불필요)
    public User updateProfile(String name) {
        this.name = name;
        return this;
    }
}
