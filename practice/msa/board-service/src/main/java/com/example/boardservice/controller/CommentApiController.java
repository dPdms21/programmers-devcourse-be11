package com.example.boardservice.controller;

import com.example.boardservice.dto.CommentWriteRequestDto;
import com.example.boardservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments")
public class CommentApiController {
    private final CommentService commentService;

    @PostMapping
    public void addComment(
            @PathVariable Long boardId,
            @RequestBody CommentWriteRequestDto dto
    ) {
        commentService.addComment(boardId, dto);
    }
}
