package com.example.boardservice.service;

import com.example.boardservice.domain.entity.Board;
import com.example.boardservice.domain.entity.Comment;
import com.example.boardservice.domain.repository.BoardRepository;
import com.example.boardservice.domain.repository.CommentRepository;
import com.example.boardservice.dto.CommentWriteRequestDto;
import com.example.boardservice.exception.BoardNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void addComment(Long boardId, CommentWriteRequestDto dto) {
        // 게시글을 먼저 찾음
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found. Id: " + boardId));

        // comment 저장
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .userId(dto.getUserId())
                .board(board)
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment);

        log.info("댓글 등록: commentId: {}", comment.getId());
    }
}
