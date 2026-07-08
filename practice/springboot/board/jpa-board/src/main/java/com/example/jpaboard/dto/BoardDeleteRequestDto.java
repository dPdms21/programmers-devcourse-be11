package com.example.jpaboard.dto;

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
    private String filePath;
}
