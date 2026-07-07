package com.example.jpaboard.service;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.domain.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;

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
}
