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
    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(length = 20)
    private String name;

    @Column(length = 50)
    private String email;

    @Column(length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(length = 20)
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(length = 100)
    private String providerId;

    public User updateProfile(String name) {
        this.name = name;

        return this;
    }
}
