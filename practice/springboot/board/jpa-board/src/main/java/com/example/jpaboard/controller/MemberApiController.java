package com.example.jpaboard.controller;

import com.example.jpaboard.dto.LoginRequestDto;
import com.example.jpaboard.dto.LoginResponseDto;
import com.example.jpaboard.dto.MemberJoinResponseDto;
import com.example.jpaboard.dto.MemberJoinRequestDto;
import com.example.jpaboard.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/join")
    public MemberJoinResponseDto join(@RequestBody MemberJoinRequestDto dto) {
        memberService.join(dto);
        return new MemberJoinResponseDto("/members/login");
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto dto, HttpSession session) {
        return memberService.login(dto)
                .map(
                        member -> {
                            session.setAttribute("userId", member.getUserId());
                            session.setAttribute("userName", member.getUserName());
                            return LoginResponseDto.success();
                        }
                ).orElseGet(LoginResponseDto::fail);
    }
}
