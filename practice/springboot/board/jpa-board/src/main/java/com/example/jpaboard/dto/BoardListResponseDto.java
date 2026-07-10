package com.example.jpaboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "게시글 목록 응답")
public class BoardListResponseDto {
    @Schema(description = "게시글 목록")
    private List<BoardSummaryResponseDto> boards;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;

    @Schema(description = "전체 페이지 수", example = "3")
    private int totalPages;
}