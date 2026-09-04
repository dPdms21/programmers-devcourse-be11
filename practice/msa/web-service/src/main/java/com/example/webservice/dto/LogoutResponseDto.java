package com.example.webservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 생성자 두 개는 Feign 응답 역직렬화용 — 이유는 SignInResponseDto 주석 참고
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponseDto {
    String message;
    String url;
}
