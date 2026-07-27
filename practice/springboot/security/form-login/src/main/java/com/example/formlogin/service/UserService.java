package com.example.formlogin.service;

import com.example.formlogin.domain.entity.User;
import com.example.formlogin.domain.repository.UserRepository;
import com.example.formlogin.dto.SignUpRequestDto;
import com.example.formlogin.exception.DuplicateUserIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        if (userRepository.existsByUserId(signUpRequestDto.getUserId())) {
            throw new DuplicateUserIdException("[회원가입] 이미 사용 중인 아이디입니다.");
        }

        User user = signUpRequestDto.toUser(passwordEncoder.encode(signUpRequestDto.getPassword()));

        userRepository.save(user);
    }
}
