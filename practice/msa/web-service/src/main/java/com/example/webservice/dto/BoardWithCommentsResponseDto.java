package com.example.webservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardWithCommentsResponseDto {
    private String title;

    private String content;

    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created;

    private String filePath;

    private List<CommentResponseDto> comments;
}
