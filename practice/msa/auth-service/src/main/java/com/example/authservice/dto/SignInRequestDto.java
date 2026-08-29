package com.example.authservice.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignInRequestDto {
    private String userId;
    private String password;
}
