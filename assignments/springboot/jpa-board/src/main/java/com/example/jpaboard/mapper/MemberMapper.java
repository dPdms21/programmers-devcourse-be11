package com.example.jpaboard.mapper;

import com.example.jpaboard.domain.entity.Member;
import com.example.jpaboard.dto.MemberJoinRequestDto;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {
    public Member toEntity(MemberJoinRequestDto request) {
        return Member.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .userName(request.getUserName())
                .build();
    }
}
