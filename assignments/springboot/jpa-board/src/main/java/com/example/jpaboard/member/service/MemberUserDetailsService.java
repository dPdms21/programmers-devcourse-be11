package com.example.jpaboard.member.service;

import com.example.jpaboard.auth.config.security.CustomUserDetails;
import com.example.jpaboard.member.domain.entity.Member;
import com.example.jpaboard.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("회원을 찾을 수 없습니다. userId=" + userId));

        return new CustomUserDetails(member);
    }
}
