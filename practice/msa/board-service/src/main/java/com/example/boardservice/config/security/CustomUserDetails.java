package com.example.boardservice.config.security;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// * auth-service의 CustomUserDetails와 이름·역할은 같지만 구조가 다름
// auth는 DB의 User 엔티티를 감싸는 어댑터였지만, 계정은 auth-service 소유라
// board에는 User 엔티티가 없음. 여기서의 신원 근거는 오직 "검증된 토큰의 클레임"이므로
// 클레임 값(id/userId/userName/role)을 그대로 필드로 가짐

@Getter
@Builder
public class CustomUserDetails implements UserDetails {
    private Long id;         // 계정 PK (auth-service DB 기준)
    private String userId;   // 로그인 아이디 (토큰의 sub)
    private String userName; // 표시 이름
    private String role;     // "ROLE_USER" / "ROLE_ADMIN"

    // 이 사용자가 가진 권한 목록. AuthorizationFilter가 인가 판단할 때 사용한다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    // 토큰 인증에는 비밀번호가 없음 — 비밀번호 대조는 로그인 시점에 auth-service가 이미 끝냈음
    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId;
    }
}
