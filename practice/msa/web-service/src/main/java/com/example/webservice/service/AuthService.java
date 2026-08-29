package com.example.webservice.service;

import com.example.webservice.client.AuthClient;
import com.example.webservice.dto.SignInRequestDto;
import com.example.webservice.dto.SignInResponseDto;
import com.example.webservice.dto.SignUpRequestDto;
import com.example.webservice.dto.SignUpResponseDto;
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
}