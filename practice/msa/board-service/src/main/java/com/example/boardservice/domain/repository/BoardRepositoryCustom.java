package com.example.boardservice.domain.repository;

import com.example.boardservice.dto.BoardListItemResponseDto;
import com.example.boardservice.dto.BoardSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    Page<BoardListItemResponseDto> searchBoards(BoardSearchRequestDto condition, Pageable pageable);
}
