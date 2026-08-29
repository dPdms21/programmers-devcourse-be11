package com.example.authservice.config.oauth2;

import java.util.Map;

public interface OAuth2UserInfo {
    Map<String, Object> attributes();

    String id();

    String email();

    String name();

    String imageUrl();
}