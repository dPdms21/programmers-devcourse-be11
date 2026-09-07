package com.example.authservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WithdrawResponseDto {
    private String message;
    private String url;
}
