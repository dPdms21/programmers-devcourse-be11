package com.example.jpaboard.mapper;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.domain.entity.Comment;
import com.example.jpaboard.dto.BoardSummaryResponseDto;
import com.example.jpaboard.dto.BoardWithCommentsResponseDto;
import com.example.jpaboard.dto.CommentResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardMapper {
    public BoardSummaryResponseDto toSummaryDto(Board board) {
        return BoardSummaryResponseDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .userId(board.getUserId())
                .filePath(board.getFilePath())
                .created(board.getCreated())
                .build();
    }

    public BoardWithCommentsResponseDto toBoardWithCommentsResponseDto(Board board) {
        List<CommentResponseDto> comments = board.getComments().stream()
                .map(this::toCommentDto)
                .toList();

        return BoardWithCommentsResponseDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUserId())
                .created(board.getCreated())
                .filePath(board.getFilePath())
                .comments(comments)
                .build();
    }

    public CommentResponseDto toCommentDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .created(comment.getCreated())
                .build();
    }
}