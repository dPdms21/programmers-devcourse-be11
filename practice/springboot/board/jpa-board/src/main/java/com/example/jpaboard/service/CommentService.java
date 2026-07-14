package com.example.jpaboard.service;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.domain.entity.Comment;
import com.example.jpaboard.domain.repository.BoardRepository;
import com.example.jpaboard.domain.repository.CommentRepository;
import com.example.jpaboard.dto.CommentDeleteRequestDto;
import com.example.jpaboard.dto.CommentUpdateRequestDto;
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

    @Transactional
    public void updateComment(Long boardId, Long commentId, CommentUpdateRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없음 id = " + commentId));

        validateCommentBoard(boardId, comment);
        validateCommentWriter(dto.getUserId(), comment);

        comment.updateContent(dto.getContent());
    }

    @Transactional
    public void deleteComment(Long boardId, Long commentId, CommentDeleteRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없음 id = " + commentId));

        validateCommentBoard(boardId, comment);
        validateCommentWriter(dto.getUserId(), comment);

        commentRepository.delete(comment);
    }

    private void validateCommentBoard(Long boardId, Comment comment) {
        if (!comment.getBoard().getId().equals(boardId)) {
            throw new IllegalArgumentException("게시글과 댓글이 일치하지 않음");
        }
    }

    private void validateCommentWriter(String userId, Comment comment) {
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글 작성자만 수정/삭제할 수 있음");
        }
    }
}
