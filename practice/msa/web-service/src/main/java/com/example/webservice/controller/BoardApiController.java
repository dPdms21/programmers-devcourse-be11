package com.example.webservice.controller;

import com.example.webservice.dto.BoardPageResponseDto;
import com.example.webservice.dto.BoardSearchRequestDto;
import com.example.webservice.dto.BoardWithCommentsResponseDto;
import com.example.webservice.dto.BoardWriteRequestDto;
import com.example.webservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {
    private final BoardService boardService;

    // Authorization을 required=false로 받는 이유
    // 토큰이 없어도 여기서 거절하지 않고 검증 책임자(board-service)가 판단하게 함
    @GetMapping("/search")
    public BoardPageResponseDto searchBoards(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @ModelAttribute BoardSearchRequestDto boardSearchRequestDto,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return boardService.searchBoard(authorization, boardSearchRequestDto, page, size);
    }

    @GetMapping("/{id}/with-comments")
    public BoardWithCommentsResponseDto getBoardWithComments(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable long id
    ) {
        return boardService.getBoardWithComments(authorization, id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveBoard(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @ModelAttribute BoardWriteRequestDto dto
    ) {
        boardService.saveBoard(authorization, dto);
    }
}
