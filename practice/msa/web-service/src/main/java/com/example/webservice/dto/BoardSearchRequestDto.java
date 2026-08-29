package com.example.webservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BoardSearchRequestDto {
    private String title;
    private String userId;
    private LocalDate from;
    private LocalDate to;
}
