package com.example.jpaboard.service;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.domain.entity.Comment;
import com.example.jpaboard.domain.repository.BoardRepository;
import com.example.jpaboard.domain.repository.CommentRepository;
import com.example.jpaboard.dto.CommentWriteRequestDto;
import com.example.jpaboard.exception.BoardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void addComment(Long boardId, CommentWriteRequestDto dto) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없음 id = " + boardId));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .userId(dto.getUserId())
                .board(board)
                .created(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
    }
}
