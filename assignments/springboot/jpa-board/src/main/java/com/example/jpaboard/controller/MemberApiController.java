package com.example.jpaboard.controller;

import com.example.jpaboard.constant.SessionConst;
import com.example.jpaboard.dto.LoginRequestDto;
import com.example.jpaboard.dto.LoginResponseDto;
import com.example.jpaboard.dto.MemberJoinRequestDto;
import com.example.jpaboard.dto.MemberJoinResponseDto;
import com.example.jpaboard.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public LoginResponseDto login(@ModelAttribute LoginRequestDto request, HttpSession session) {
        return memberService.login(request)
                .map(
                        member -> {
                            session.setAttribute(SessionConst.USER_ID, member.getUserId());
                            session.setAttribute(SessionConst.USER_NAME, member.getUserName());

                            return LoginResponseDto.success();
                        }
                )

                .orElseGet(LoginResponseDto::fail);
    }
}
