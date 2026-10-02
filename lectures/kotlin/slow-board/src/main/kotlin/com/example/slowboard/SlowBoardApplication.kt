package com.example.slowboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SlowBoardApplication

fun main(args: Array<String>) {
    runApplication<SlowBoardApplication>(*args)
}
