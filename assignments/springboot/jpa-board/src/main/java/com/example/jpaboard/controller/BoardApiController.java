package com.example.jpaboard.controller;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.dto.*;
import com.example.jpaboard.service.BoardService;
import com.example.jpaboard.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;
    private final FileService fileService;

    @GetMapping
    public BoardListResponseDto getBoardList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<Board> boards = boardService.getBoardList(page, size);
        int totalBoards = boardService.getTotalBoards();

        int totalPages = (int) Math.ceil((double) totalBoards / size);
        boolean last = page >= totalPages;

        return BoardListResponseDto.builder()
                .boards(boards)
                .last(last)
                .totalPages(totalPages)
                .build();
    }

    @PostMapping
    public void saveArticle(@ModelAttribute BoardWriteRequestDto request) {
        boardService.saveArticle(
                request.getUserId(),
                request.getTitle(),
                request.getContent(),
                request.getFile()
        );
    }

    @GetMapping("/{id}")
    public BoardDetailResponseDto getBoardDetail(@PathVariable Long id) {
        Board board = boardService.getBoardDetail(id);

        return BoardDetailResponseDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .created(board.getCreated())
                .userId(board.getUserId())
                .filePath(board.getFilePath())
                .build();
    }

    @GetMapping("/file/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileService.downloadFile(fileName);
        String encoded = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encoded)
                .body(resource);
    }

    @PutMapping("/{id}")
    public void updateArticle(@PathVariable Long id, @ModelAttribute BoardUpdateRequestDto request) {
        boardService.updateArticle(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id, @RequestBody BoardDeleteRequestDto request) {
        boardService.deleteArticle(id, request);
    }
}
