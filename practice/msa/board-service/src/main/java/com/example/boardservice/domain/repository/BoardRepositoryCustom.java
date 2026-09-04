package com.example.boardservice.domain.repository;

import com.example.boardservice.domain.entity.Board;
import com.example.boardservice.dto.BoardAuthorStatsResponseDto;
import com.example.boardservice.dto.BoardListItemResponseDto;
import com.example.boardservice.dto.BoardSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BoardRepositoryCustom {
    Page<BoardListItemResponseDto> searchBoards(BoardSearchRequestDto condition, Pageable pageable);

    Optional<Board> findWithComments(Long id);

    List<BoardAuthorStatsResponseDto> countBoardsByAuthor(long minCount);
}
