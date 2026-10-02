package com.example.slowboard

import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService,
) {

    @GetMapping
    fun getPostsByBoard(
        @RequestParam boardId: Long,
        @RequestParam page: Int,
        @RequestParam size: Int,
    ): Page<Post> = postService.getPostsByBoard(boardId, page, size)
}