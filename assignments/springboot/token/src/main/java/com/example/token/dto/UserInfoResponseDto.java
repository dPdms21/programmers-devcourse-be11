package com.example.token.dto;

import com.example.token.domain.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {
    private String userId;
    private String userName;
    private Role role;
}