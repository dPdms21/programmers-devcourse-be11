package com.example.oauth2.config.oauth2;

import java.util.Map;

// * 제공자마다 제각각인 user-info 응답을 공통 인터페이스로 추상화함
// 문제: 제공자들은 같은 "사용자 정보"라도 JSON 모양과 키 이름이 전부 다름
// 해결: 이 인터페이스가 "우리가 필요한 4가지(id/email/name/imageUrl)"를 꺼내는 공통 창구를 정의하고,
// 제공자별 구현체가 각자의 응답 구조에서 그 값을 찾아 반환

public interface OAuth2UserInfo {
    // 원시 응답 속성
    Map<String, Object> attributes();

    // 제공자가 부여한 변하지 않는 고유 식별자 -> User.providerId로 저장
    String id();

    // 이메일 (우리 User의 필수 값. 없으면 로그인 거부)
    String email();

    // 이름/닉네임
    String name();

    // 프로필 이미지 URL (선택값)
    String imageUrl();
}
