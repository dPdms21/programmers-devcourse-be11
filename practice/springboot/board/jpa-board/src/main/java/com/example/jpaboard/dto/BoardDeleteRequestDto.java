package com.example.jpaboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @RequestBody
// 컨트롤러에서 @RequestBody로 받으면, JSON을 Jackson라이브러리가 이 객체로 바꿈 (역직렬화)
// - Jackson은 setter로 값을 채움

@Getter
@Setter
@NoArgsConstructor
public class BoardDeleteRequestDto {
    @Schema(description = "함께 삭제할 첨부파일 경로(없으면 비움)", example = "3f2a1b_이력서.pdf")
    private String filePath;
}
