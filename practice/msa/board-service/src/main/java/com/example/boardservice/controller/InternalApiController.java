package com.example.boardservice.controller;

import com.example.boardservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/internal")
public class InternalApiController {
    private final BoardService boardService;

    @DeleteMapping("/users/{userId}")
    public void deleteUserContents(@PathVariable String userId) {
        boardService.deleteUserContents(userId);
    }
}
