package com.example.webservice.service;

import com.example.webservice.client.BoardClient;
import com.example.webservice.dto.CommentWriteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final BoardClient boardClient;

    public void addComment(
            String authorization,
            long boardId,
            CommentWriteRequestDto requestDto
    ) {
        boardClient.addComment(authorization, boardId, requestDto);
    }
}
