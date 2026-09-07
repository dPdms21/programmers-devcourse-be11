package com.example.webservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 생성자 두 개는 Feign 응답 역직렬화용 — 이유는 SignInResponseDto 주석 참고
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseDto {
    private String message;
    private String url;
}
