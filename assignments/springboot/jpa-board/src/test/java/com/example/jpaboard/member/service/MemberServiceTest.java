package com.example.jpaboard.member.service;

import com.example.jpaboard.member.domain.entity.Member;
import com.example.jpaboard.member.domain.entity.Role;
import com.example.jpaboard.member.domain.repository.MemberRepository;
import com.example.jpaboard.member.dto.MemberJoinRequestDto;
import com.example.jpaboard.global.exception.DuplicateUserIdException;
import com.example.jpaboard.member.mapper.MemberMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberMapper memberMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    MemberService memberService;

    @Test
    void join_성공() {
        MemberJoinRequestDto request = new MemberJoinRequestDto();
        request.setUserId("hong");
        request.setPassword("1234");
        request.setUserName("홍길동");

        String encodedPassword = "encoded-password";

        Member member = Member.builder()
                .userId("hong")
                .password(encodedPassword)
                .userName("홍길동")
                .role(Role.ROLE_USER)
                .build();

        given(memberRepository.existsByUserId("hong"))
                .willReturn(false);

        given(passwordEncoder.encode("1234"))
                .willReturn(encodedPassword);

        given(memberMapper.toEntity(request, encodedPassword))
                .willReturn(member);

        memberService.join(request);

        verify(memberRepository).existsByUserId("hong");
        verify(passwordEncoder).encode("1234");
        verify(memberMapper).toEntity(request, encodedPassword);
        verify(memberRepository).save(member);
    }

    @Test
    void join_중복된_아이디면_예외가_발생한다() {
        MemberJoinRequestDto request = new MemberJoinRequestDto();
        request.setUserId("hong");
        request.setPassword("1234");
        request.setUserName("홍길동");

        given(memberRepository.existsByUserId("hong"))
                .willReturn(true);

        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(DuplicateUserIdException.class);

        verify(memberRepository).existsByUserId("hong");
        verify(passwordEncoder, never()).encode(any());
        verify(memberMapper, never()).toEntity(any(), any());
        verify(memberRepository, never()).save(any());
    }
}