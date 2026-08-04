package com.example.oauth2.dto;

import com.example.oauth2.config.oauth2.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupPayloadDto {
    private final AuthProvider provider;
    private final String providerId;
    private final String email;
    private final String name;
}
