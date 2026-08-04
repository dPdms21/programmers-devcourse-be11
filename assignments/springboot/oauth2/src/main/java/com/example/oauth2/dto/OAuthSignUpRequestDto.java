package com.example.oauth2.dto;

import com.example.oauth2.domain.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthSignUpRequestDto {
    private String signupToken;
    private Role role;
}
