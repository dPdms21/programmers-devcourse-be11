package com.example.jpaboard.service;

import com.example.jpaboard.domain.entity.Member;
import com.example.jpaboard.domain.repository.MemberRepository;
import com.example.jpaboard.dto.LoginRequestDto;
import com.example.jpaboard.dto.MemberJoinRequestDto;
import com.example.jpaboard.mapper.MemberMapper;
import com.example.jpaboard.exception.DuplicateUserIdException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public void join(MemberJoinRequestDto request) {
        if (memberRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateUserIdException("이미 존재하는 아이디");
        }

        Member member = memberMapper.toEntity(request);
        memberRepository.save(member);
    }

    public Optional<Member> login(LoginRequestDto request) {
        return memberRepository.findByUserId(request.getUsername())
                .filter(member -> member.getPassword().equals(request.getPassword()));
    }
}
