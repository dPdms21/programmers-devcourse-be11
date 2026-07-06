package com.example.jpaboard.controller;

import com.example.jpaboard.dto.MemberJoinRequestDto;
import com.example.jpaboard.dto.MemberJoinResponseDto;
import com.example.jpaboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/join")
    public MemberJoinResponseDto join(@RequestBody MemberJoinRequestDto request) {
        memberService.join(request);

        return new MemberJoinResponseDto("/members/login");
    }
}
