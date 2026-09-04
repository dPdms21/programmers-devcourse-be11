package com.example.webservice.dto;

import lombok.Getter;

@Getter
public class BoardAuthorStatsResponseDto {
    private final String userId;
    private final String userName;
    private final long boardCount;

    public BoardAuthorStatsResponseDto(String userId, String userName, long boardCount) {
        this.userId = userId;
        this.userName = userName;
        this.boardCount = boardCount;
    }
}
