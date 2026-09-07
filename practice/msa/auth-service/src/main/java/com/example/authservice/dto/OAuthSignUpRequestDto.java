package com.example.authservice.dto;

import com.example.authservice.domain.entity.Role;
import lombok.Getter;

@Getter
public class OAuthSignUpRequestDto {
    private String signupToken;
    private Role role;
}
