package com.example.token.controller;

import com.example.token.dto.SignUpRequestDto;
import com.example.token.dto.SignUpResponseDto;
import com.example.token.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;

    @PostMapping("/join")
    public SignUpResponseDto join(@RequestBody SignUpRequestDto requestDto) {
        userService.signUp(requestDto);

        return SignUpResponseDto.builder()
                .url("/users/login")
                .build();
    }
}
