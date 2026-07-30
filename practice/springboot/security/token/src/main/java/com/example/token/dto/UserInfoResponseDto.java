package com.example.token.dto;

import com.example.token.domain.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {
    private long id;
    private String userId;
    private String userName;
    private Role role;
}
