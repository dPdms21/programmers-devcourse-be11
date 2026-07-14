package com.example.jpaboard.controller;

import com.example.jpaboard.dto.CommentWriteRequestDto;
import com.example.jpaboard.service.CommentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API", description = "게시글 댓글 작성 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments")
public class CommentApiController {
    private final CommentService commentService;

    @PostMapping
    public void addComment(
            @Parameter(description = "댓글을 달 게시글 id", example = "1")
            @PathVariable long boardId,
            @RequestBody CommentWriteRequestDto dto
    ) {
        commentService.addComment(boardId, dto);
    }
}
