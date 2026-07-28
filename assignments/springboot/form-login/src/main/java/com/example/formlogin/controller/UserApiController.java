package com.example.formlogin.controller;

import com.example.formlogin.dto.SignUpRequestDto;
import com.example.formlogin.dto.SignUpResponseDto;
import com.example.formlogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;

    @PostMapping("/join")
    public SignUpResponseDto signUp(@RequestBody SignUpRequestDto request) {
        userService.signUp(request);

        return new SignUpResponseDto("/users/login");
    }
}
