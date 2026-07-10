package com.example.jpaboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "게시글 목록 응답 항목")
public class BoardSummaryResponseDto {
    @Schema(description = "게시글 id", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "첫 번째 게시글")
    private String title;

    @Schema(description = "작성자 아이디", example = "user01")
    private String userId;

    @Schema(description = "첨부파일 경로", example = "3f2a1b_이력서.pdf")
    private String filePath;

    @Schema(description = "작성일시", example = "2026-07-10 14:30")
    private LocalDateTime created;
}