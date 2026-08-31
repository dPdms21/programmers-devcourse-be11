package com.example.boardservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class BoardSearchRequestDto {
    private String title;
    private String userId;
    private LocalDate from;
    private LocalDate to;
}
