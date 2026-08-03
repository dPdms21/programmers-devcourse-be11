package com.example.oauth2.config.oauth2;

import java.util.Map;

// record란? (Java 16+): 필드 나열만 하면 나머지 상용구를 컴파일러가 다 써주는, 불변 데이터 전용 클래스
// 필드선언, 생성자, getter, equals/hashCode/toString까지 수십줄을 대신 해줌
// record인 이유: 상태는 응답 맵 하나뿐인 불변 값 객체이고,
// 컴포넌트 이름을 attributes로 지으면 인터페이스 attributes()가 자동 구현

//   {
//     "id": 123456789,                          ← 회원번호(숫자). 유일하게 최상위에 있음
//     "kakao_account": {
//       "email": "user@example.com",            ← 동의 항목(비즈 앱)에 따라 아예 없을 수 있음
//       "profile": {
//         "nickname": "홍길동",
//         "profile_image_url": "https://..."
//       }
//     }
//   }

public record KakaoUserInfo(
        Map<String, Object> attributes
) implements OAuth2UserInfo {
    @Override
    public String id() {
        Object id = attributes.get("id");

        return id == null ? null : String.valueOf(id);
    }

    @Override
    public String email() {
        Map<String, Object> kakaoAccount = kakaAccount();

        return kakaoAccount == null ? null : String.valueOf(kakaoAccount.get("email"));
    }

    @Override
    public String name() {
        Map<String, Object> profile = profile();

        return profile == null ? null : String.valueOf(profile.get("nickname"));
    }

    @Override
    public String imageUrl() {
        Map<String, Object> profile = profile();

        return profile == null ? null : String.valueOf(profile.get("profile_image_url"));
    }

    // 중첩 구조 접근을 한 곳에서 모아둠. 캐스팅이 반복되는 것도 여기서만 감수
    private Map<String, Object> kakaAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }

    private Map<String, Object> profile() {
        return (Map<String, Object>) kakaAccount().get("profile");
    }
}