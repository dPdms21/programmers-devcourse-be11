package com.example.authservice.config.oauth2;

import java.util.Map;

public final class OAuth2UserInfoFactory {
    private OAuth2UserInfoFactory() {}

    public static OAuth2UserInfo of(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case KAKAO -> new KakaoUserInfo(attributes);
            case LOCAL -> throw new IllegalArgumentException("local은 OAuth2 제공자가 아닙니다.");
        };
    }
}
