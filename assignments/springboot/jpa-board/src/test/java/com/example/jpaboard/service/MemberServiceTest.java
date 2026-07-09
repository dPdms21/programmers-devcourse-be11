package com.example.jpaboard.service;

import com.example.jpaboard.domain.entity.Member;
import com.example.jpaboard.domain.repository.MemberRepository;
import com.example.jpaboard.dto.LoginRequestDto;
import com.example.jpaboard.dto.MemberJoinRequestDto;
import com.example.jpaboard.exception.DuplicateUserIdException;
import com.example.jpaboard.mapper.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock MemberRepository memberRepository;
    @Mock MemberMapper memberMapper;
    @InjectMocks MemberService memberService;

    @Test
    void login_성공() {
        Member member = Member.builder()
                .userId("hong")
                .password("1234")
                .userName("홍길동")
                .build();
        given(memberRepository.findByUserId("hong")).willReturn(Optional.of(member));

        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("hong");
        dto.setPassword("1234");

        Optional<Member> result = memberService.login(dto);
        assertThat(result).isPresent();
    }

    @Test
    void join_중복이면_예외() {
        MemberJoinRequestDto dto = new MemberJoinRequestDto();
        dto.setUserId("hong");
        given(memberRepository.existsByUserId("hong")).willReturn(true);

        assertThatThrownBy(() -> memberService.join(dto))
                .isInstanceOf(DuplicateUserIdException.class);
        verify(memberRepository, never()).save(any());
    }
}