package com.example.jpaboard.service;

import com.example.jpaboard.domain.entity.Member;
import com.example.jpaboard.domain.repository.MemberRepository;
import com.example.jpaboard.dto.LoginRequestDto;
import com.example.jpaboard.dto.MemberJoinRequestDto;
import com.example.jpaboard.exception.DuplicateUserIdException;
import com.example.jpaboard.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
// 이 클래스의 모든 메서드에 기본 적용됨
// - readOnly = true의 효과
// "이 트랜잭션은 데이터를 변경하지 않는다."고 JPA에게 알려줌 -> 조회 최적화
// Hibernate가 변경 감지를 위한 불필요한 작업을 줄여 메모리/성능에 유리
// Insert/Update/Delete가 필요한 메서드는 @Transactional을 다시 붙여 readOnly = false로 실행
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public void join(MemberJoinRequestDto dto) {
        // 아이디 중복 체크
        if (memberRepository.existsByUserId(dto.getUserId())) {
            // 예외 공통화
            throw new DuplicateUserIdException("[회원가입] 이미 존재하는 아이디");
        }

        memberRepository.save(memberMapper.toEntity(dto));
    }

    public Optional<Member> login(LoginRequestDto dto) {
        return memberRepository.findByUserId(dto.getUsername())
                .filter(member -> member.getPassword().equals(dto.getPassword()));
    }
}
