package com.example.oauth2.service;

import com.example.oauth2.config.oauth2.AuthProvider;
import com.example.oauth2.config.oauth2.CustomOAuth2User;
import com.example.oauth2.config.oauth2.OAuth2UserInfo;
import com.example.oauth2.config.oauth2.OAuth2UserInfoFactory;
import com.example.oauth2.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// * CustomOAuth2UserService - OAuth2 파이프라인에서 개발자가 구현하는 첫 번째 훅 (hook)

// 코드-토큰 교환 -> 사용자 정보 조회 -> *loadUser() 호출 -> Authentication 생성 -> SuccessHandler

// [명시적 가입 정책] - SNS 인증을 통과했다고 곧바로 가입시키지 않음
// - 기존 회원(provider + providerId 일치): 프로필만 최신값으로 갱신하고 로그인 진행
// - 미가입: DB에 저장하지 않고 미가입 principal(CustomOAuth2User.unregistered)을 반환
// -> OAuth2SuccessHandler가 이를 받아 가입 안내로 분기하고,
// 사용자가 동의해야 그때 비로소 계정이 생성됨
// (이메일이 같아도 제공자가 다르면 별개 계정 -> 식별기준: provider + providerId)

// * DefaultOAuth2UserService
// 부모의 loadUser()가 "제공자 user-info 엔드포인트로 HTTP요청을 보내 프로필을 받아오는"
// 코드 전부를 이미 갖고 있음. 통신을 다시 구현할 이유가 없으므로
// super.loadUser()로 결과만 받고, 그 뒤의 "우리 도메인 연결"만 덧붙임

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // access token으로 제공자 user-info를 호출해 원시 속성 맵을 받아옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 어떤 제공자로 로그인했는지: application.yml의 registration 키
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 제공자 응답에서 "사용자 식별자"가 들어있는 속성 키 이름
        // CustomOAuth2User.getName()이 이 키로 식별자를 꺼낼 때 사용
        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 문자열 registrationId -> 우리 enum으로, 원시 속성 맵 -> 제공자별 파서로 감쌈
        AuthProvider provider = AuthProvider.from(registrationId);
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.of(provider, oAuth2User.getAttributes());

        // 이메일은 회원의 기본 식별/연락 정보이자 우리 User의 필수 정보
        // -> 동의항목 미설정 등으로 이메일을 못 받으면 가입 자체가 불가능하므로 로그인을 거부

        // 반드시 OAuth2AuthenticationException으로 던져야 하는 이유
        // loadUser()는 시큐리티 필터(OAuth2LoginAuthenticationFilter) 안에서 호출됨
        // 이 예외 타입이어야 필터가 "인증 실패"로 인식해 OAuth2FailureHandler로 보내줌
        // 다른 RuntimeException을 던지면 처리되지 않은 서버 오류로 새어 나감

        if (userInfo.email() == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("Email is required"),
                    "SNS 계정에서 이메일을 가져오지 못했습니다. 이메일 제공 동의가 필요합니다."
            );
        }

        return userRepository.findByProviderIdAndProvider(userInfo.id(), provider)
                .map(
                        existing -> {
                            // -> 탈퇴 기능이 있다면 (소프트 딜리트) 이 분기에서
                            // 탈퇴 계정 여부를 검사해 로그인을 거부하는 코드가 추가됨

                            // save 불필요
                            existing.updateProfile(userInfo.name());

                            return new CustomOAuth2User(
                                    existing,
                                    provider,
                                    userInfo,
                                    oAuth2User.getAttributes(),
                                    nameAttributeKey
                            );
                        }
                ).orElseGet(
                        () -> CustomOAuth2User.unregistered(
                                provider,
                                userInfo,
                                oAuth2User.getAttributes(),
                                nameAttributeKey
                        )
                );
    }
}
