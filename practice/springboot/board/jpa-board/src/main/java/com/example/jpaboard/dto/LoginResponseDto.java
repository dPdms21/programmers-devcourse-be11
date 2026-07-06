package com.example.jpaboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private boolean loggedIn;
    private String url;
    private String message;

    // 로그인 성공
    public static LoginResponseDto success() {
        return new LoginResponseDto(true, "/", "로그인에 성공했습니다.");
    }

    // 로그인 실패
    public static LoginResponseDto fail() {
        return new LoginResponseDto(false, null, "아이디 또는 비밀번호가 일치하지 않습니다.");
    }
}
