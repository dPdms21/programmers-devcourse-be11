package com.example.webservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentWriteRequestDto {
    private String userId;
    private String content;
}
