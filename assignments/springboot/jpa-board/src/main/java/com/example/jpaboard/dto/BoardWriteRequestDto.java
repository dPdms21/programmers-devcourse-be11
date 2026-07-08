package com.example.jpaboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class BoardWriteRequestDto {
    private String title;
    private String content;
    private String userId;
    private MultipartFile file;
}
