package com.example.oauth2.service;

import com.example.oauth2.config.security.CustomUserDetails;
import com.example.oauth2.domain.entity.Role;
import com.example.oauth2.domain.entity.User;
import com.example.oauth2.domain.repository.UserRepository;
import com.example.oauth2.dto.OAuthSignUpRequestDto;
import com.example.oauth2.dto.SignInResponseDto;
import com.example.oauth2.dto.SignUpRequestDto;
import com.example.oauth2.dto.SignupPayloadDto;
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
    public void signUp(SignUpRequestDto request) {
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateUserIdException("이미 사용 중인 아이디");
        }

        User user = request.toUser(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public SignInResponseDto signIn(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

        TokenService.TokenPair tokens = tokenService.issueTokens(user);

        return SignInResponseDto.builder()
                .isLoggedIn(true)
                .message("로그인 성공")
                .url("/")
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .userId(user.getUserId())
                .userName(user.getName())
                .build();
    }

    @Transactional
    public SignInResponseDto oauthSignUp(OAuthSignUpRequestDto requestDto) {
        SignupPayloadDto payload = tokenService.getSignupPayload(requestDto.getSignupToken());
        Role role = requestDto.getRole();

        User user = userRepository.findByProviderAndProviderId(
                        payload.getProvider(),
                        payload.getProviderId()
                )
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

        TokenService.TokenPair tokenPair = tokenService.issueTokens(user);

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
