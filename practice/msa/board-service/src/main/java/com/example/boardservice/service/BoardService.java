package com.example.boardservice.service;

import com.example.boardservice.domain.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;

    public void searchBoards() {
        // searchBoards 게시글들 가져오기

        // boardRepository에서 가져온 ID추려서 auth-service로 요청해서 userName들 받아오기
    }
}
