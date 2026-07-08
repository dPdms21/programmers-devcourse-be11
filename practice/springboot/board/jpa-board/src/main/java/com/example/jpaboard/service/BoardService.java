package com.example.jpaboard.service;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.domain.repository.BoardRepository;
import com.example.jpaboard.dto.BoardDeleteRequestDto;
import com.example.jpaboard.dto.BoardUpdateRequestDto;
import com.example.jpaboard.exception.BoardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final FileService fileService;

    public List<Board> getBoardList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        // * findAll(pageable).getContent()의 getContent()란?
        //   findAll(pageable)의 반환 타입은 Page<Board>
        //   Page가 제공하는 것들
        // - getContent() -> List<Board>: "이번 페이지의 게시글 목록"
        // - getTotalElements() -> long: 전체 게시글 수
        // - getTotalPages() -> int: 전체 페이지 수
        // - isLast() -> boolean: 마지막 페이지 여부
        // 주의: getContent()의 'content'는 Board 엔티티의 content가 아님
        return boardRepository.findAll(pageable).getContent();
    }

    public int getTotalBoards() {
        return (int) boardRepository.count();
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
                .orElseThrow(() -> new BoardNotFoundException("[BOARD] 게시글 찾을 수 없음 id: " + id));
    }

    @Transactional
    public void updateBoard(long id, BoardUpdateRequestDto dto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("[BOARD] 수정할 게시글 찾을 수 없음 id: " + id));

        String filePath = board.getFilePath();

        if (dto.isFileFlag()) { // 파일 변경이 있었을 경우
            fileService.deleteFile(filePath); // 기존 파일 삭제
            filePath = fileService.storeFile(dto.getFile()); // 새 파일 저장
        }

        board.update(dto.getTitle(), dto.getContent(), filePath);
    }

    @Transactional
    public void deleteBoard(long id, BoardDeleteRequestDto dto) {
        if ( !boardRepository.existsById(id) ) {
            throw new BoardNotFoundException("[BOARD] 삭제할 게시글 찾을 수 없음 id: " + id);
        }

        boardRepository.deleteById(id);
        fileService.deleteFile(dto.getFilePath());
    }
}
