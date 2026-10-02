package com.example.slowboard

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Post(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var postId: Long,

    @Column
    var title: String,

    @Column
    var content: String,

    @Column
    var boardId: Long,

    @Column
    var writerId: Long,

    @Column
    var createdAt: LocalDateTime,

    @Column
    var updatedAt: LocalDateTime,
)