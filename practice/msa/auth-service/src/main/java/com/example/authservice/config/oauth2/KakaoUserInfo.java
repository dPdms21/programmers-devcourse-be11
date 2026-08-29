package com.example.authservice.config.oauth2;

import java.util.Map;

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

public record KakaoUserInfo(Map<String, Object> attributes) implements OAuth2UserInfo {
    @Override
    public String id() {
        Object id = attributes.get("id");

        return id == null ? null : String.valueOf(id);
    }

    @Override
    public String email() {
        Map<String, Object> kakaoAccount = kakaoAccount();

        return kakaoAccount == null ? null : String.valueOf(kakaoAccount.get("email"));
    }

    @Override
    public String name() {
        Map<String, Object> nickname = profile();

        return nickname == null ? null : String.valueOf(nickname.get("name"));
    }

    @Override
    public String imageUrl() {
        Map<String, Object> nickname = profile();

        return nickname == null ? null : String.valueOf(nickname.get("profile_image_url"));
    }

    private Map<String, Object> kakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao account");
    }

    private Map<String, Object> profile() {
        return (Map<String, Object>) kakaoAccount().get("profile");
    }
}