package com.example.jpaboard.controller;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.dto.*;
import com.example.jpaboard.mapper.BoardMapper;
import com.example.jpaboard.service.BoardService;
import com.example.jpaboard.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

// * Swagger 어노테이션들
// - @Tag                 : 컨트롤러(그룹) 단위의 설명 - 화면에서 API를 묶는 큰 제목
// - @Operation           : 메서드(API 한 개) 단위의 설명 - 요약(summary)/상세(description)
// - @Parameter           : 파라미터 하나에 대한 설명
// - @ApiResponse(s)      : 이 API가 낼 수 있는 응답(상태코드별)을 문서에 명시
// - @Content / @Schema   : 응답/요청 본문의 "형태(어떤 DTO인지)"를 지정

// * 뷰 컨트롤러(@Controller + 뷰 이름 반환)는 이 설정과 무관하게 원래 문서에 안 나옴
// - springdoc은 @ResponseBody(= @RestController) 핸들러만 문서화 대상으로 삼기 때문
// -(BoardController/MemberController는 "board-list" 같은 뷰 이름을 반환하므로 애초에 제외)

@Tag(name = "게시글 API", description = "게시글 목록/상세 조회, 작성, 수정, 삭제, 첨부파일 다운로드")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {
    private final BoardService boardService;
    private final FileService fileService;
    private final BoardMapper boardMapper;

    @Operation(
            summary = "게시글 목록 조회",
            description = "페이지 단위로 게시글 목록을 조회한다. 목록(boards)과 마지막 페이지 여부(last), 전체 페이지 수(totalPages)를 함께 돌려준다."
    )
    @GetMapping
    public BoardListResponseDto getBoardList(
            @Parameter(description = "조회할 페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "한 페이지에 담을 게시글 수", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        // 게시글 목록
        List<BoardSummaryResponseDto> boards = boardService.getBoardList(page, size)
                .stream()
                .map(boardMapper::toSummaryDto)
                .toList();

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

    // * Swagger에서 "파일 업로드(multipart)"를 제대로 그리게 하는 핵심
    // # 문제: @ModelAttribute + MultipartFile을 그냥 두면, Swagger가 이걸 JSON 본문으로 오해하거나
    //        파일 선택 버튼을 안 그려서 UI에서 테스트가 안 됨
    // # 해결 2가지 (둘을 같이 써야 완성):
    //   (1) 여기 @PostMapping에 consumes = MULTIPART_FORM_DATA_VALUE를 "명시"
    //       -> springdoc이 "아, 이 API는 JSON이 아니라 multipart/form-data구나"를 알고 폼 형태로 그림
    //       -> 덤으로 이 엔드포인트가 multipart 요청만 받도록 더 엄격/정확 (JS는 원래 multipart로 보냄)
    //   (2) DTO의 MultipartFile 필드에 @Schema(type="string", format="binary")를 붙임
    //       -> 그래야 그 칸이 "파일 선택" 버튼으로 렌더링 (BoardWriteRequestDto 참고)
    @Operation(summary = "게시글 작성",
            description = "제목/내용/작성자와 (선택적) 첨부파일을 multipart/form-data로 받아 새 게시글을 저장한다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveBoard(@ModelAttribute BoardWriteRequestDto dto) {
        boardService.saveBoard(dto.getUserId(), dto.getTitle(), dto.getContent(), dto.getFile());
    }

    // @ApiResponses = "이 API가 낼 수 있는 응답들"을 상태코드별로 문서에 나열
    // - 성공(200)만이 아니라 실패(404)도 미리 적어두면, 이 API를 쓰는 사람이 어떤 상황을 대비해야 하는지 한눈에 앎
    // - 404의 응답 본문 형태(schema)를 ErrorResponseDto로 지정하면, 실패 시 무엇이 오는지까지 문서에 드러남
    @Operation(summary = "게시글 상세 조회", description = "id로 게시글 한 건의 상세 내용을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 상세 조회 실패 - 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/{id}")
    public BoardDetailResponseDto getBoardDetail(
            @Parameter(description = "조회할 게시글 id", example = "1")
            @PathVariable long id
    ) {
        Board boardDetail = boardService.getBoardDetail(id);

        return boardMapper.toDetailDto(boardDetail);
    }

    // ResponseEntity는 HTTP 응답의 3가지를 직접 제어하게 해주는 상자
    // [상태코드] + [헤더] + [본문(body)]
    // 그냥 Resource만 리턴하면 파일 내용은 내려가지만,
    // Content-Disposition: attachment 헤더를 붙일 방법이 없음
    // -> 그러면 다운로드가 아니라 브라우저가 파일을 그냥 열어버리고, 저장 파일명도 못 정함
    // 응답이 "파일(바이너리)" 임을 문서에 알려주기
    // - 이 API는 JSON이 아니라 파일 그 자체를 내려줌
    // - 응답 형태를 octet-stream + Schema(format="binary")로 지정하면, 문서에 "다운로드되는 바이너리"로 표시
    @Operation(summary = "첨부파일 다운로드",
            description = "저장된 파일 이름으로 첨부파일을 내려받는다. Content-Disposition: attachment로 브라우저가 다운로드하게 한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파일 다운로드",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "해당 이름의 파일이 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/file/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "서버에 저장된 파일 이름(UUID 포함)", example = "3f2a1b_이력서.pdf")
            @PathVariable String fileName
    ) {
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
        //   attachment는 인라인으로 열지 말고 무조건 첨부(다운로드)하라는 확실한 지시
        //   filename*(별표)는 인코딩을 명시하는 최신 문법으로 "저장될 기본 파일명"을 정함
        //   이게 없으면 URL 끝의 UUID 붙음 이름으로 저장돼 버림. 그래서 원본 이름으로 저장되게 넣는 것
        //   UTF-8 뒤에 '': 언어 필드 생략 (ex: UTF-8'ko')
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }

    @Operation(summary = "게시글 수정",
            description = "경로의 id 게시글을 수정한다. 파일 교체가 가능하도록 multipart/form-data로 받는다.")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateBoard(
            @Parameter(description = "수정할 게시글 id", example = "1")
            @PathVariable long id,
            @ModelAttribute BoardUpdateRequestDto dto
    ) {
        boardService.updateBoard(id, dto);
    }

    @Operation(summary = "게시글 삭제",
            description = "경로의 id 게시글을 삭제한다. 첨부파일 경로(filePath)를 JSON 본문으로 함께 받아 파일도 정리한다.")
    @DeleteMapping("/{id}")
    public void deleteBoard(
            @Parameter(description = "삭제할 게시글 id", example = "1")
            @PathVariable long id,
            @RequestBody BoardDeleteRequestDto dto
    ) {
        boardService.deleteBoard(id, dto);
    }
}
