package com.example.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInResponseDto {
    private boolean isLoggedIn;
    private String url;
    private String userName;
    private String userId;
    private String accessToken;
    private String refreshToken;
    private String message;
}
