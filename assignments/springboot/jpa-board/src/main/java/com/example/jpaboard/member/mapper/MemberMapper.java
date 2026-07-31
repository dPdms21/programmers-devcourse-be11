package com.example.jpaboard.member.mapper;

import com.example.jpaboard.member.domain.entity.Member;
import com.example.jpaboard.member.domain.entity.Role;
import com.example.jpaboard.member.dto.MemberJoinRequestDto;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {
    public Member toEntity(
            MemberJoinRequestDto request,
            String encodedPassword
    ) {
        return Member.builder()
                .userId(request.getUserId())
                .password(encodedPassword)
                .userName(request.getUserName())
                .role(Role.ROLE_USER)
                .build();
    }
}