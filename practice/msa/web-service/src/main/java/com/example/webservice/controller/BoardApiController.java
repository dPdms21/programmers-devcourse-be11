package com.example.webservice.controller;

import com.example.webservice.dto.*;
import com.example.webservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public BoardDetailResponseDto getBoardDetail(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable long id
    ) {
        return boardService.getBoardDetail(authorization, id);
    }

    @PutMapping("/{id}")
    public void updateBoard(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable long id,
            @ModelAttribute BoardUpdateRequestDto dto
    ) {
        boardService.updateBoard(authorization, id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteBoard(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable long id,
            @RequestBody BoardDeleteRequestDto dto
    ) {
        boardService.deleteBoard(authorization, id, dto);
    }

    @GetMapping("/file/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable String fileName
    ) {
        return boardService.downloadFile(authorization, fileName);
    }

    @GetMapping("/stats/authors")
    public List<BoardAuthorStatsResponseDto> getAuthorStats(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam(defaultValue = "1") long minCount
    ) {
        return boardService.getAuthorStats(authorization, minCount);
    }
}
