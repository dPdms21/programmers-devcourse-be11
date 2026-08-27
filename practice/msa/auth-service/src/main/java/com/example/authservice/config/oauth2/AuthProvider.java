package com.example.authservice.config.oauth2;

public enum AuthProvider {
    LOCAL,
    KAKAO;

    // "kakao" -> AuthProvider.KAKAO
    public static AuthProvider from(String registrationId) {
        return AuthProvider.valueOf(registrationId.toUpperCase());
    }
}
