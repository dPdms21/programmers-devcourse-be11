package com.example.jpaboard.mapper;

import com.example.jpaboard.domain.entity.Member;
import com.example.jpaboard.domain.entity.Role;
import com.example.jpaboard.dto.MemberJoinRequestDto;
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