package com.example.oauth2.service;

import com.example.oauth2.config.security.CustomUserDetails;
import com.example.oauth2.domain.entity.Role;
import com.example.oauth2.domain.entity.User;
import com.example.oauth2.domain.repository.UserRepository;
import com.example.oauth2.dto.*;
import com.example.oauth2.exception.DuplicateUserIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Transactional
    public void signUp(SignUpRequestDto requestDto) {
        if (userRepository.existsByUserId(requestDto.getUserId())) {
            throw new DuplicateUserIdException("[회원가입] 이미 사용중인 아이디입니다.");
        }

        User user = requestDto.toUser(passwordEncoder.encode(requestDto.getPassword()));

        userRepository.save(user);
    }

    @Transactional
    public SignInResponseDto login(SignInRequestDto requestDto) {
        // form-login에서는 필터가 하던 아이디/비밀번호 검증을 직접 호출
        // 실패하면 AuthenticationException이 던져짐
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUserId(), requestDto.getPassword())
        );

        User user = ((CustomUserDetails) authenticate.getPrincipal()).getUser();

        TokenService.TokenPair tokenPair = tokenService.issueToken(user);

        return SignInResponseDto.builder()
                .isLoggedIn(true)
                .message("로그인 성공")
                .url("/")
                .accessToken(tokenPair.accessToken())
                .refreshToken(tokenPair.refreshToken())
                .userName(user.getName())
                .userId(user.getUserId())
                .build();
    }

    @Transactional
    public SignInResponseDto oauthSignUp(OAuthSignUpRequestDto requestDto) {
        SignupPayloadDto payload = tokenService.getSignupPayload(requestDto.getSignupToken());
        Role role = requestDto.getRole();

        // 이미 가입돼 있으면 그대로 로그인 처리 (멱등)
        // 뒤로가기/새로고침으로 같은 토큰이 두 번 제출돼도 중복 가입이 생기지 않음
        User user = userRepository.findByProviderIdAndProvider(payload.getProviderId(), payload.getProvider())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .userId(payload.getProvider().name().toLowerCase() + "_" + payload.getProviderId())
                                .name(payload.getName())
                                .email(payload.getEmail())
                                .provider(payload.getProvider())
                                .providerId(payload.getProviderId())
                                .role(role != null ? role : Role.ROLE_USER)
                                .build()
                ));

        TokenService.TokenPair tokenPair = tokenService.issueToken(user);

        return SignInResponseDto.builder()
                .isLoggedIn(true)
                .message("가입이 완료되었습니다.")
                .url("/")
                .accessToken(tokenPair.accessToken())
                .refreshToken(tokenPair.refreshToken())
                .userId(user.getUserId())
                .userName(user.getName())
                .build();
    }
}
