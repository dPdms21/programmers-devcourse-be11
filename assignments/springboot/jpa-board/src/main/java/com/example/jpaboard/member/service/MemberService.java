package com.example.jpaboard.member.service;

import com.example.jpaboard.member.domain.entity.Member;
import com.example.jpaboard.member.domain.repository.MemberRepository;
import com.example.jpaboard.member.dto.MemberJoinRequestDto;
import com.example.jpaboard.global.exception.DuplicateUserIdException;
import com.example.jpaboard.member.mapper.MemberMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(MemberJoinRequestDto request) {
        if (memberRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateUserIdException("이미 존재하는 아이디");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        Member member =
                memberMapper.toEntity(request, encodedPassword);

        memberRepository.save(member);
    }
}