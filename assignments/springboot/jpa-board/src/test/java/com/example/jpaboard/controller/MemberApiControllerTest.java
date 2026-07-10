package com.example.jpaboard.controller;

import com.example.jpaboard.exception.DuplicateUserIdException;
import com.example.jpaboard.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberApiController.class)
public class MemberApiControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean MemberService memberService;

    @Test
    void join_성공() throws Exception {
        String json = """
                {"userId":"newbie",
                "password":"1234",
                "userName":"새싹"}
                """;
        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("/members/login"));
    }

    @Test
    void join_중복이면_409() throws Exception {
        willThrow(new DuplicateUserIdException("이미 존재하는 아이디"))
                .given(memberService).join(any());
        String json = """
                {"userId":"hong",
                "password":"1234",
                "userName":"홍길동"}
                """;
        mockMvc.perform(post("/api/members/join")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 존재하는 아이디"));
    }
}
