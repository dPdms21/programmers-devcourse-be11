package com.example.oauth2.dto;

import com.example.oauth2.config.oauth2.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupPayloadDto {
    private final AuthProvider provider;
    private final String providerId; // SNS 회원번호 (토큰의 sub 클레임에서 복원)
    private final String email;
    private final String name;
}
