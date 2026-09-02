package com.example.webservice.client;

import com.example.webservice.dto.BoardPageResponseDto;
import com.example.webservice.dto.BoardSearchRequestDto;
import com.example.webservice.dto.BoardWithCommentsResponseDto;
import com.example.webservice.dto.CommentWriteRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// 헤더를 파라미터로 받는 이유
// Feign은 서블릿 요청과 무관한 새 HTTP 요청을 만들기 때문에,
// 브라우저 web-service에 보낸 Authorization/Cookie 헤더가 자동으로 따라가지 않음
// 컨트롤러가 받은 값을 명시적으로 넘겨줘야 auth-service까지 전달됨

@FeignClient(value = "board-service", url = "${edge-service.url:http://localhost:8000}")
public interface BoardClient {
    // @SpringQueryMap
    // DTO 필드를 ?title=...&userId=... 쿼리스트링으로 펼쳐줌 (null 필드는 생략)
    @GetMapping("/api/boards/search")
    BoardPageResponseDto searchBoards(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @SpringQueryMap BoardSearchRequestDto condition,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    @GetMapping("/api/boards/{id}/with-comments")
    BoardWithCommentsResponseDto getBoardWithComments(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable("id") long id
    );

    @PostMapping("/api/boards/{boardId}/comments")
    void addComment(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable long boardId,
            @RequestBody CommentWriteRequestDto requestDto
    );

    @PostMapping(value = "/api/boards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void saveBoard(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("userId") String userId,
            @RequestPart(value = "file", required = false) MultipartFile file
    );
}
