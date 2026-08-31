package com.example.webservice.service;

import com.example.webservice.client.AuthClient;
import com.example.webservice.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthClient authClient;

    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        return authClient.join(signUpRequestDto);
    }

    public ResponseEntity<SignInResponseDto> signIn(SignInRequestDto signInRequestDto) {
        return authClient.login(signInRequestDto);
    }

    public UserInfoResponseDto getUserInfo(String authorization) {
        return  authClient.getUserInfo(authorization);
    }
}