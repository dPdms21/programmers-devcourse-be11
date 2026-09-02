package com.example.webservice.controller;

import com.example.webservice.dto.CommentWriteRequestDto;
import com.example.webservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments")
public class CommentApiController {
    private final CommentService commentService;

    @PostMapping
    public void addComment(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable long boardId,
            @RequestBody CommentWriteRequestDto dto
    ) {
        commentService.addComment(authorization, boardId, dto);
    }
}
