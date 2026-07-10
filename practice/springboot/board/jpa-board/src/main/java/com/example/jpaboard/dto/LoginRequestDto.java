package com.example.jpaboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto {
    @Schema(description = "로그인 아이디", example = "user01")
    private String username;
    @Schema(description = "비밀번호", example = "pass1234")
    private String password;
}
