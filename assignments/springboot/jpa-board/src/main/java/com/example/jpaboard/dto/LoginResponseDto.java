package com.example.jpaboard.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginResponseDto {
    private boolean loggedIn;
    private String url;
    private String message;

    public static LoginResponseDto success() {
        return new LoginResponseDto(true, "/", "로그인 성공");
    }

    public static LoginResponseDto fail() {
        return new LoginResponseDto(false, null, "아이디 또는 비밀번호 불일치");
    }
}
