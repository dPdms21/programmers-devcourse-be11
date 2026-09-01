package com.example.authservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserNameResponseDto {
    private String userId;
    private String userName;
}
