package com.example.kotlinboard.dto

import com.example.kotlinboard.domain.entity.Board
import org.springframework.data.domain.Page
import java.time.format.DateTimeFormatter

private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

data class BoardPageResponse(
    val boards: List<BoardListItemResponse>,
    val page: Int,
    val totalPages: Int,
    val totalElements: Long,
    val last: Boolean,
) {
    // static
    companion object {
        fun from(page: Page<Board>): BoardPageResponse = BoardPageResponse(
            boards = page.content.map(BoardListItemResponse::from),
            page = page.number + 1,
            totalPages = page.totalPages,
            totalElements = page.totalElements,
            last = page.isLast,
        )
    }
}

data class BoardListItemResponse(
    val id: Long,
    val title: String,
    val userId: String,
    var created: String,
) {
    companion object {
        fun from(board: Board): BoardListItemResponse = BoardListItemResponse(
            // !! -> null 아님을 보증
            id = board.id!!,
            title = board.title,
            userId = board.userId,
            created = board.created.format(DATE_FORMAT)
        )
    }
}

data class BoardCreateRequest(
    val title: String,
    val content: String,
    val userId: String,
) {
    fun toEntity(): Board = Board(
        title = title,
        content = content,
        userId = userId,
    )
}

data class BoardResponse(
    val id: Long,
    val title: String,
    val content: String,
    val userId: String,
    val created: String,
) {
    companion object {
        fun from(board: Board): BoardResponse = BoardResponse(
            id = board.id!!,
            title = board.title,
            content = board.content,
            userId = board.userId,
            created = board.created.format(DATE_FORMAT)
        )
    }
}

data class BoardUpdateRequest(
    val title: String,
    val content: String,
)