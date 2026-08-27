package com.example.webservice.controller;

import com.example.webservice.dto.SignUpRequestDto;
import com.example.webservice.dto.SignUpResponseDto;
import com.example.webservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthApiController {
    private final AuthService authService;

    @PostMapping("/join")
    public SignUpResponseDto join(@RequestBody SignUpRequestDto signUpRequestDto) {
        return authService.signUp(signUpRequestDto);
    }
}
