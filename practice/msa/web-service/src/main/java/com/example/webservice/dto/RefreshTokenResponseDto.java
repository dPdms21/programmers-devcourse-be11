package com.example.webservice.dto;

import lombok.*;

// 생성자 두 개는 Feign 응답 역직렬화용 — 이유는 SignInResponseDto 주석 참고
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDto {
    private boolean validated;
    private String accessToken;
    private String refreshToken;
}
