package com.example.formlogin.dto;

import com.example.formlogin.domain.entity.User;
import lombok.Getter;

@Getter
public class SignUpRequestDto {
    private String userId;
    private String password;
    private String userName;

    public User toUser(String encoderPassword) {
        return User.builder()
                .userId(userId)
                .password(encoderPassword)
                .name(userName)
                .build();
    }
}
