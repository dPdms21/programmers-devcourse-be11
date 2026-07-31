package com.example.jpaboard.board.service;

import com.example.jpaboard.board.domain.entity.Board;
import com.example.jpaboard.member.domain.entity.Role;
import com.example.jpaboard.board.domain.repository.BoardRepository;
import com.example.jpaboard.board.dto.BoardUpdateRequestDto;
import com.example.jpaboard.global.exception.BoardNotFoundException;
import com.example.jpaboard.global.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
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
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("id").descending());

        return boardRepository.findAll(pageable).getContent();
    }

    public int getTotalBoards() {
        return (int) boardRepository.count();
    }

    public Board getBoardDetail(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없음 id=" + id));
    }

    @Transactional
    public void saveArticle(String userId, String title, String content, MultipartFile file) {
        String filePath = fileService.storeFile(file);

        Board board = Board.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .filePath(filePath)
                .created(LocalDateTime.now())
                .build();

        boardRepository.save(board);
    }

    @Transactional
    public void updateArticle(
            Long id,
            BoardUpdateRequestDto request,
            String loginUserId,
            Role role
    ) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() ->
                        new BoardNotFoundException("게시글을 찾을 수 없음 id=" + id)
                );

        validateAuthorOrAdmin(board, loginUserId, role);

        String filePath = board.getFilePath();

        if (request.isFileFlag()) {
            fileService.deleteFile(board.getFilePath());
            filePath = fileService.storeFile(request.getFile());
        }

        board.update(
                request.getTitle(),
                request.getContent(),
                filePath
        );
    }

    @Transactional
    public void deleteArticle(
            Long id,
            String loginUserId,
            Role role
    ) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() ->
                        new BoardNotFoundException("게시글을 찾을 수 없음 id=" + id)
                );

        validateAuthorOrAdmin(board, loginUserId, role);

        boardRepository.delete(board);
        fileService.deleteFile(board.getFilePath());
    }

    private void validateAuthorOrAdmin(
            Board board,
            String loginUserId,
            Role role
    ) {
        boolean author = board.getUserId().equals(loginUserId);
        boolean admin = role == Role.ROLE_ADMIN;

        if (!author && !admin) {
            throw new AccessDeniedException("게시글을 수정하거나 삭제할 권한이 없습니다.");
        }
    }
}
