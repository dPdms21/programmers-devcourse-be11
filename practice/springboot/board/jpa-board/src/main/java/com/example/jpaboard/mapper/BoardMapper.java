package com.example.jpaboard.mapper;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.dto.BoardDetailResponseDto;
import com.example.jpaboard.dto.BoardSummaryResponseDto;
import org.springframework.stereotype.Component;

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

    public BoardDetailResponseDto toDetailDto(Board board) {
        return BoardDetailResponseDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .filePath(board.getFilePath())
                .created(board.getCreated())
                .userId(board.getUserId())
                .build();
    }
}