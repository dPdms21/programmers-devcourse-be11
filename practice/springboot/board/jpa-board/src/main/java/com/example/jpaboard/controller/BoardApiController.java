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
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {
    private final BoardService boardService;
    private final FileService fileService;

    @GetMapping
    public BoardListResponseDto getBoardList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 게시글 목록
        List<Board> boards = boardService.getBoardList(page, size);

        // 전체 게시글 수 가져오기
        int totalBoards = boardService.getTotalBoards();

        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalBoards / size);

        // 마지막 페이지 여부
        boolean last = page >= totalPages;

        return BoardListResponseDto.builder()
                .boards(boards)
                .last(last)
                .totalPages(totalPages)
                .build();
    }

    @PostMapping
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

    // ResponseEntity는 HTTP응답의 3가지를 직접 제어하게 해주는 상자
    // [상태코드] + [헤더] + [본문(body)]
    // 그냥 Resource만 리턴하면 파일 내용은 내려가지만,
    // Content-Disposition: attachment 헤더를 붙일 방법이 없음
    // -> 그러면 다운로드가 아니라 브라우저가 파일을 그냥 열어버리고, 저장 파일명도 못 정함
    @GetMapping("/file/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileService.downloadFile(fileName);

        // * 한글 파일명 인코딩
        // HTTP 헤더 값에는 원칙적으로 ASCII만 안전하게 담을 수 있음
        // -> "이력서.pdf"같은 한글/공백을 그대로 넣으면 깨지거나 잘림
        //    그래서 파일명을 URL 인코딩해서 넣음
        // - URLEncoder는 공백을 '+'로 바꾸는데, 파일명에선 '+'가 그대로 보이면 곤란하므로 %20으로 치환
        String encodedFileName = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        // * contentType(MediaType.APPLICATION_OCTET_STREAM) => 힌트
        //   무슨 파일인지 특정하지 않은 순수 바이너리라는 뜻
        //   브라우저가 열 방법을 몰라 저장 쪽으로 기울게 하는 '힌트'일 뿐, 다운로드 확정하지 못함
        //   (확장자 등에 따라 브라우저가 그냥 열어버릴 수도 있음)
        //   * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
        //   attachment는 인라인으로 열지말고 무조건 첨부(다운로드)하라는 확실한 지시
        //   filename*(별표)는 인코딩을 명시하는 최신 문법으로 "저장될 기본 파일명"을 정함
        //   이게 없으면 URL 끝의 UUID 붙음 이름으로 저장돼 버림. 그래서 원본 이름으로 저장되게 넣는 것
        // utf8 뒤에 '': 언어필드 생략 (ex: utf8'ko')
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }

    @PutMapping("/{id}")
    public void updateBoard(@PathVariable long id, @ModelAttribute BoardUpdateRequestDto dto) {
        boardService.updateBoard(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable long id, @RequestBody BoardDeleteRequestDto dto) {
        boardService.deleteBoard(id, dto);
    }
}
