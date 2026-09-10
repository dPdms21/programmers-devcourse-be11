package com.example.kotlinboard.controller

import com.example.kotlinboard.dto.BoardCreateRequest
import com.example.kotlinboard.dto.BoardPageResponse
import com.example.kotlinboard.dto.BoardResponse
import com.example.kotlinboard.dto.BoardUpdateRequest
import com.example.kotlinboard.service.BoardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/boards")
class BoardApiController(private val boardService: BoardService) {
    @GetMapping
    fun getBoards(
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int,
        @RequestParam(value = "keyword", required = false) keyword: String?
    ): BoardPageResponse = BoardPageResponse.from(boardService.getBoards(page, size, keyword))

    @PostMapping
    fun createBoard(@RequestBody request: BoardCreateRequest) {
        boardService.createBoard(request)
    }

    @GetMapping("/{id}")
    fun getBoard(@PathVariable("id") id: Long): ResponseEntity<BoardResponse> {
        val board = boardService.getBoard(id) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(BoardResponse.from(board))
    }

    @PutMapping("/{id}")
    fun updateBoard(
        @PathVariable("id") id: Long,
        @RequestBody request: BoardUpdateRequest
    ) {
        boardService.updateBoard(id, request)
    }

    @DeleteMapping("/{id}")
    fun deleteBoard(@PathVariable("id") id: Long) {
        boardService.deleteBoard(id)
    }
}