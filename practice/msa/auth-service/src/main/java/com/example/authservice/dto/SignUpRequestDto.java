package com.example.authservice.dto;

import com.example.authservice.domain.entity.Role;
import com.example.authservice.domain.entity.User;
import lombok.Getter;

@Getter
public class SignUpRequestDto {
    private String userId;
    private String password;
    private String userName;
    private Role role;

    public User toUser(String encodedPassword) {
        return User.builder()
                .userId(userId)
                .password(encodedPassword)
                .name(userName)
                .role(role != null ? role : Role.ROLE_USER)
                .build();
    }
}
