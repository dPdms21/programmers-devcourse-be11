package com.example.oauth2.config.security;

import com.example.oauth2.domain.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(user.getRole().name())
        );
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    // ---- 이하 4개는 "계정 상태" 검사용 ----

    // 계정 자체가 만료되지 않았는가 (휴면 계정 정책 등)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠기지 않았는가 (로그인 연속 실패 잠금 등)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호가 만료되지 않았는가 (주기적 변경 강제 정책 등)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성 상태인가 (탈퇴/이메일 미인증 등)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
