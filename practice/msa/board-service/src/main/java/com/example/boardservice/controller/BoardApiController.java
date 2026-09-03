package com.example.boardservice.controller;

import com.example.boardservice.domain.entity.Board;
import com.example.boardservice.dto.*;
import com.example.boardservice.mapper.BoardMapper;
import com.example.boardservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveBoard(@ModelAttribute BoardWriteRequestDto dto) {
        boardService.saveBoard(dto.getUserId(), dto.getTitle(), dto.getContent(), dto.getFile());
    }

    @GetMapping("/{id}")
    public BoardDetailResponseDto getBoardDetail(@PathVariable long id) {
        Board boardDetail = boardService.getBoardDetail(id);

        return BoardDetailResponseDto.builder()
                .title(boardDetail.getTitle())
                .content(boardDetail.getContent())
                .filePath(boardDetail.getFilePath())
                .created(boardDetail.getCreated())
                .userId(boardDetail.getUserId())
                .build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateBoard(
            @PathVariable long id,
            @ModelAttribute BoardUpdateRequestDto dto
    ) {
        boardService.updateBoard(id, dto);
    }
}
