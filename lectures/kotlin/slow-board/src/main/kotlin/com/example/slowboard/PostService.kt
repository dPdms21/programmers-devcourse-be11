package com.example.slowboard

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
) {

    fun getPostsByBoard(boardId: Long, page: Int, size: Int): Page<Post> {
        val pageRequest = PageRequest.of(
            /* pageNumber = */ page,
            /* pageSize = */ size,
            /* sort = */ Sort.by(Sort.Direction.DESC, "createdAt"),
        )

        return postRepository.findByBoardId(boardId, pageRequest)
    }
}