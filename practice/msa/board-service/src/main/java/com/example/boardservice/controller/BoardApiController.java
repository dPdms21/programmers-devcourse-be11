package com.example.boardservice.controller;

import com.example.boardservice.domain.entity.Board;
import com.example.boardservice.dto.BoardListItemResponseDto;
import com.example.boardservice.dto.BoardSearchRequestDto;
import com.example.boardservice.dto.BoardWithCommentsResponseDto;
import com.example.boardservice.mapper.BoardMapper;
import com.example.boardservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {
    private final BoardService boardService;
    private final BoardMapper boardMapper;

    @GetMapping("/search")
    public Page<BoardListItemResponseDto> searchBoards(
            @ModelAttribute BoardSearchRequestDto dto,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return boardService.searchBoards(dto, PageRequest.of(page - 1, size));
    }

    @GetMapping("/{id}/with-comments")
    public BoardWithCommentsResponseDto getBoardWithComments(@PathVariable("id") long id) {
        Board board = boardService.getBoardWithComments(id);
        return boardMapper.toBoardWithCommentsResponseDto(board);
    }
}
