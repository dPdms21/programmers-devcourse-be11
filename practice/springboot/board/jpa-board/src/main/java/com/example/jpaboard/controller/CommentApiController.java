package com.example.jpaboard.controller;

import com.example.jpaboard.dto.CommentDeleteRequestDto;
import com.example.jpaboard.dto.CommentUpdateRequestDto;
import com.example.jpaboard.dto.CommentWriteRequestDto;
import com.example.jpaboard.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API", description = "게시글 댓글 작성, 수정, 삭제 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments")
public class CommentApiController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시글 id에 해당하는 게시글에 댓글을 작성한다.")
    @PostMapping
    public void addComment(
            @Parameter(description = "댓글을 달 게시글 id", example = "1")
            @PathVariable long boardId,
            @RequestBody CommentWriteRequestDto dto
    ) {
        commentService.addComment(boardId, dto);
    }

    @Operation(summary = "댓글 수정", description = "게시글 id와 댓글 id에 해당하는 댓글 내용을 수정한다.")
    @PutMapping("/{commentId}")
    public void updateComment(
            @Parameter(description = "댓글이 달린 게시글 id", example = "1")
            @PathVariable long boardId,
            @Parameter(description = "수정할 댓글 id", example = "1")
            @PathVariable long commentId,
            @RequestBody CommentUpdateRequestDto dto
    ) {
        commentService.updateComment(boardId, commentId, dto);
    }

    @Operation(summary = "댓글 삭제", description = "게시글 id와 댓글 id에 해당하는 댓글을 삭제한다.")
    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @Parameter(description = "댓글이 달린 게시글 id", example = "1")
            @PathVariable long boardId,
            @Parameter(description = "삭제할 댓글 id", example = "1")
            @PathVariable long commentId,
            @RequestBody CommentDeleteRequestDto dto
    ) {
        commentService.deleteComment(boardId, commentId, dto);
    }

}
