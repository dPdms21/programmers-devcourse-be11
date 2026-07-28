package com.example.formlogin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponseDto {
    private boolean isLoggedIn;
    private String url;
    private String userName;
    private String userId;
    private String message;
}
