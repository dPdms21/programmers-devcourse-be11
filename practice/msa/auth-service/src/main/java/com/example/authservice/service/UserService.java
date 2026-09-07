package com.example.authservice.service;

import com.example.authservice.config.client.BoardClient;
import com.example.authservice.config.security.CustomUserDetails;
import com.example.authservice.domain.entity.Role;
import com.example.authservice.domain.entity.User;
import com.example.authservice.domain.repository.UserRepository;
import com.example.authservice.dto.*;
import com.example.authservice.exception.DuplicateUserIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final BoardClient boardClient;

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        if (userRepository.existsByUserId(signUpRequestDto.getUserId())) {
            throw new DuplicateUserIdException("[회원가입] 이미 사용중인 아이디입니다.");
        }

        User user = signUpRequestDto.toUser(passwordEncoder.encode(signUpRequestDto.getPassword()));

        userRepository.save(user);
    }

    @Transactional
    public SignInResponseDto login(SignInRequestDto signInRequestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequestDto.getUserId(), signInRequestDto.getPassword())
        );

        User user = ((CustomUserDetails) authenticate.getPrincipal()).getUser();

        TokenService.TokenPair tokenPair = tokenService.issueToken(user);

        return SignInResponseDto.builder()
                .loggedIn(true)
                .message("로그인 성공")
                .url("/")
                .accessToken(tokenPair.accessToken())
                .refreshToken(tokenPair.refreshToken())
                .userId(user.getUserId())
                .userName(user.getName())
                .build();
    }

    public List<UserNameResponseDto> getUserNames(List<String> userIds) {
        return userRepository.findByUserIdIn(userIds).stream()
                .map( user -> UserNameResponseDto.builder()
                        .userId(user.getUserId())
                        .userName(user.getName())
                        .build())
                .toList();
    }

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
                .loggedIn(true)
                .message("가입이 완료되었습니다.")
                .url("/")
                .accessToken(tokenPair.accessToken())
                .refreshToken(tokenPair.refreshToken())
                .userId(user.getUserId())
                .userName(user.getName())
                .build();
    }

    // 회원탈퇴
    // 탈퇴는 두 서비스의 커밋이 필요한 분산 작업
    // auth: 계정 상태 변경 / board: 그 사용자의 글/댓글 삭제
    // 서로 다른 DB여서 @Transactional 하나로 묶을 수 없으므로,
    // "로컬 커밋들의 연쇄 + 실패 시 보상"으로 전체를 원자적'처럼' 만듦 => Saga패턴

    // 설계
    // 커밋 1: ACTIVE -> WITHDRAWING (보상 가능한 준비 단계 먼저)
    // 호출: board 글/댓글 삭제
    // 커밋 2: WITHDRAWING -> WITHDRAWN
    // 보상: board 실패 시 WITHDRAWING -> ACTIVE

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public WithDrawResponseDto withDraw(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 커밋 1 : 탈퇴 진행중 마킹
        userRepository.save(user.startWithdrawal());

        try {
            // 참여자 호출: board가 자기 로컬트랜잭션으로 글/댓글을 지움
            boardClient.deleteUserContents(userId);
        } catch (Exception e) {
            // 보상: 이미 커밋된 상태 변경을 반대 연산으로 되돌림
            userRepository.save(user.cancelWithdrawal());
            log.error("[탈퇴 saga 보상] board 정리 실패로 계정 상태 원복. userId: {}", userId, e);
            throw new IllegalStateException("탈퇴 처리에 실패했습니다. 잠시 수 다시 시도해주세요.");
        }

        // 커밋 2: 탈퇴 확정
        userRepository.save(user.completeWithdrawal());
        log.info("[탈퇴 saga 완료] userId: {}", userId);

        return WithDrawResponseDto.builder()
                .message("탈퇴가 완료되었습니다.")
                .url("/users/login")
                .build();
    }
}
