package com.example.slowboard

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PostDataInit(
    private val postRepository: PostRepository,
) : CommandLineRunner {

    /*
     * OOM(OutOfMemoryError) 발생
     * 원인: List가 모든 Post 객체를 계속 참조해 reachable 객체가 증가하고,
     * GC가 해당 객체들을 회수하지 못해 Heap 메모리가 부족해짐

    override fun run(vararg args: String) {
        println("=============== 대량 데이터 삽입 시작 =============== ")

        val posts = mutableListOf<Post>()

        for (i in 1..10_000_000) {
            val post = Post(
                postId = 0,
                title = "title $i",
                content = "content $i",
                boardId = 1L,
                writerId = 1L,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            posts.add(post)
        }

        postRepository.saveAll(posts)

        println("=============== 대량 데이터 삽입 종료 =============== ")
    }
    */

    /*
     * 해결: 일정 개수(batchSize) 단위로 저장한 뒤 List를 비움
     * 처리 완료된 Post 객체에 대한 참조를 제거해 reachable 객체 수를 제한하고,
     * GC가 더 이상 참조되지 않는 객체를 회수할 수 있도록 함
     */
    override fun run(vararg args: String) {
        println("=============== 대량 데이터 삽입 시작 =============== ")

        val posts = mutableListOf<Post>()
        val batchSize = 50_000

        for (i in 1..10_000_000) {
            val post = Post(
                postId = 0,
                title = "title $i",
                content = "content $i",
                boardId = 1L,
                writerId = 1L,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            posts.add(post)

            if (posts.size >= batchSize) {
                postRepository.saveAll(posts)
                posts.clear()
            }
        }

        println("=============== 대량 데이터 삽입 종료 =============== ")
    }
}