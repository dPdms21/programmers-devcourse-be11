package com.example.boardservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

// @Setter / @NoArgsConstructor
// - @ModelAttribute는 "기본 생성자로 객체를 만든 뒤 setter로 값을 하나씩 채우는" 방식
// - 그래서 응답 DTO들처럼 @Builder만 있으면 안 되고, @Setter / @NoArgsConstructor가 있어야 함

@Getter
@Setter
@NoArgsConstructor
public class BoardWriteRequestDto {
    private String title;
    private String content;
    private String userId;
    private MultipartFile file;
}
