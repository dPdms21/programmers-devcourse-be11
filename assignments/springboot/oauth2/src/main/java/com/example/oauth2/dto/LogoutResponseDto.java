package com.example.oauth2.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutResponseDto {
    private String message;
    private String url;
}