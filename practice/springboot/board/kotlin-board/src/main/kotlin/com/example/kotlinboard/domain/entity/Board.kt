package com.example.kotlinboard.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "board")
class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 200)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(nullable = false, length = 50)
    var userId: String,

    @Column(nullable = false)
    var created: LocalDateTime = LocalDateTime.now(),
) {
    // "게시글 수정" 의도가 드러나는 메서드
    // 하나로 변경 지점을 모으는 편이 추적하기 쉬움
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }
}