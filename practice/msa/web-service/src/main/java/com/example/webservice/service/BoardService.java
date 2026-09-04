package com.example.webservice.service;

import com.example.webservice.client.BoardClient;
import com.example.webservice.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardClient boardClient;

    public BoardPageResponseDto searchBoard(String authorization, BoardSearchRequestDto condition, int page, int size) {
        return boardClient.searchBoards(authorization, condition, page, size);
    }

    public BoardWithCommentsResponseDto getBoardWithComments(String authorization, Long id) {
        return boardClient.getBoardWithComments(authorization, id);
    }

    public void saveBoard(String authorization, BoardWriteRequestDto dto) {
        boardClient.saveBoard(
                authorization,
                dto.getTitle(),
                dto.getContent(),
                dto.getUserId(),
                emptyToNull(dto.getFile())
        );
    }

    private MultipartFile emptyToNull(MultipartFile file) {
        return (file == null || file.isEmpty()) ? null : file;
    }

    public BoardDetailResponseDto getBoardDetail(String authorization, long id) {
        return boardClient.getBoardDetail(authorization, id);
    }

    public void updateBoard(String authorization, long id, BoardUpdateRequestDto dto) {
        boardClient.updateBoard(
                authorization,
                id,
                dto.getTitle(),
                dto.getContent(),
                emptyToNull(dto.getFile()),
                String.valueOf(dto.isFileFlag())
        );
    }

    public void deleteBoard(String authorization, long id, BoardDeleteRequestDto dto) {
        boardClient.deleteBoard(authorization, id, dto);
    }

    public ResponseEntity<byte[]> downloadFile(String authorization, String fileName) {
        return boardClient.downloadFile(authorization, fileName);
    }

    public List<BoardAuthorStatsResponseDto> getAuthorStats(String authorization, long minCount) {
        return boardClient.getAuthorStats(authorization, minCount);
    }
}
