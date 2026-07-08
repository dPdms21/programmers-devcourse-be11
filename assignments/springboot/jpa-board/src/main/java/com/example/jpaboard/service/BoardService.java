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
    public void updateArticle(Long id, BoardUpdateRequestDto request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없음 id=" + id));
        String filePath = board.getFilePath();

        if (request.isFileFlag()) {
            fileService.deleteFile(board.getFilePath());
            filePath = fileService.storeFile(request.getFile());
        }

        board.update(request.getTitle(), request.getContent(), filePath);
    }

    @Transactional
    public void deleteArticle(Long id, BoardDeleteRequestDto request) {
        if (!boardRepository.existsById(id)) {
            throw new BoardNotFoundException("게시글을 찾을 수 없음 id=" + id);
        }

        boardRepository.deleteById(id);
        fileService.deleteFile(request.getFilePath());
    }
}
