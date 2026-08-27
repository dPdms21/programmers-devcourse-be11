package com.example.authservice.service;

import com.example.authservice.domain.entity.User;
import com.example.authservice.domain.repository.UserRepository;
import com.example.authservice.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {

        User user = signUpRequestDto.toUser(passwordEncoder.encode(signUpRequestDto.getPassword()));

        userRepository.save(user);
    }
}
