package com.example.oauth2.dto;

import com.example.oauth2.domain.entity.Role;
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
