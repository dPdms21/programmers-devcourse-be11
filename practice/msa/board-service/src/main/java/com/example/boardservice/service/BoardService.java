package com.example.boardservice.service;

import com.example.boardservice.client.AuthClient;
import com.example.boardservice.domain.entity.Board;
import com.example.boardservice.domain.repository.BoardRepository;
import com.example.boardservice.domain.repository.CommentRepository;
import com.example.boardservice.dto.*;
import com.example.boardservice.exception.BoardNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final AuthClient authClient;
    private final FileService fileService;

    // repository는 userId까지만 채워서 돌려줌
    // 페이지에 등장한 userId를 "모아서 한 번" auth에 요청 (벌크)
    // 받은 DTO 목록에서 이름을 찾아 채워 완성
    public Page<BoardListItemResponseDto> searchBoards(BoardSearchRequestDto dto, Pageable pageable) {
        // searchBoards 게시글들 가져오기
        Page<BoardListItemResponseDto> page = boardRepository.searchBoards(dto, pageable);

        // boardRepository에서 가져온 ID를 추려서 auth-service로 요청해서 userName들 받아오기
        List<UserNameResponseDto> userNameResponseDtos = fetchNames(
                page.getContent().stream().map(BoardListItemResponseDto::getUserId).distinct().toList()
        );

        return page.map(item -> new BoardListItemResponseDto(
                item.getId(),
                item.getTitle(),
                item.getUserId(),
                userNameOf(userNameResponseDtos, item.getUserId()),
                item.getCommentCount(),
                item.getCreated()
        ));
    }

    public Board getBoardWithComments(Long boardId) {
        return boardRepository.findWithComments(boardId)
                .orElseThrow(
                        () -> new BoardNotFoundException("게시글을 찾을 수 없습니다. Id = " + boardId)
                );
    }

    // auth가 죽어도 게시판 조회 자체는 살아야 하므로 (부분 실패 허용)
    // 실패 시 빈 목록을 돌려 이름 없이 응답 -> 장애 전파를 끊음
    private List<UserNameResponseDto> fetchNames(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        try {
            return authClient.getUserNames(userIds);
        } catch (Exception e) {
            log.warn("[작성자 이름 조회 실패] auth-service 호출 불가 — userId로 대체 표시. {}", e.getMessage());
            return List.of();
        }
    }

    // DTO 목록에서 해당 userId의 이름을 찾음. 없으면 null
    private String userNameOf(List<UserNameResponseDto> userNames, String userId) {
        return userNames.stream()
                .filter(userName -> userName.getUserId().equals(userId))
                .map(UserNameResponseDto::getUserName)
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void saveBoard(String userId, String title, String content, MultipartFile file) {
        String filePath = fileService.storeFile(file);

        boardRepository.save(
                Board.builder()
                        .userId(userId)
                        .title(title)
                        .content(content)
                        .filePath(filePath)
                        .created(LocalDateTime.now())
                        .build()
        );
    }

    public Board getBoardDetail(long id) {
        return boardRepository.findById(id)
                .orElseThrow(
                        () -> new BoardNotFoundException("[BOARD] 게시글을 찾을 수 없습니다. id = " + id)
                );
    }

    public void updateBoard(long id, BoardUpdateRequestDto dto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(
                        () -> new BoardNotFoundException("[BOARD] 수정할 게시글을 찾을 수 없습니다. id = " + id)
                );

        String filePath = board.getFilePath();

        if (dto.isFileFlag()) {
            fileService.deleteFile(filePath);
            filePath = fileService.storeFile(dto.getFile());
        }

        board.update(dto.getTitle(), dto.getContent(), filePath);
    }

    public void deleteBoard(long id, BoardDeleteRequestDto dto) {
        if (!boardRepository.existsById(id)) {
            throw new BoardNotFoundException("[BOARD] 삭제할 게시글을 찾을 수 없습니다. id = " + id);
        }

        // comment
        commentRepository.deleteByBoardId(id);
        // board
        boardRepository.deleteById(id);
        // file
        fileService.deleteFile(dto.getFilePath());
    }

    public List<BoardAuthorStatsResponseDto> getAuthorStats(long minCount) {
        List<BoardAuthorStatsResponseDto> stats = boardRepository.countBoardsByAuthor(minCount);

        List<UserNameResponseDto> userNames = fetchNames(
                stats.stream().map(BoardAuthorStatsResponseDto::getUserId).distinct().toList()
        );

        return stats.stream()
                .map(item -> new BoardAuthorStatsResponseDto(
                        item.getUserId(),
                        userNameOf(userNames, item.getUserId()),
                        item.getBoardCount()
                ))
                .toList();
    }
}
