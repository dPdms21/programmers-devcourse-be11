package coroutinepractice

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun fetchName(id: Int): String {
    delay(500)
    // Thread.sleep(500)

    return "상품$id"
}
// suspend 빼면 'Compilation error. See log for more details' 에러남
// delay() 자체가 suspend 함수라서, 일반 함수 안에서는 바로 호출 불가


suspend fun fetchPrice(id: Int): Int {
    delay(500)

    return id * 1000
}
// Thread.sleep이 되는 이유는 Thread.sleep는 자바 함수이기에 suspend와 상관없음
// sleep은 스레드를 멈추고, delay는 코루틴만 잠깐 멈춤

fun ex2() = runBlocking {
    println("A")

    launch {
        delay(1000)
        println("B")
    }

    println("C")

    launch {
        delay(500)
        println("D")
    }
}
// A -> C (-> D) -> B
// delay()에서 스레드를 점유하지 않고 양보해서 다른 코루틴이 실행됨
// runBlocking을 지우면 launch를 실행할 코루틴 스코프가 없어지기 때문에 컴파일이 안 됨

fun ex3() = runBlocking {
    val time = measureTimeMillis {
        val a = fetchName(1)
        val b = fetchName(2)
        val c = fetchName(3)
        println("$a / $b / $c")
    }
    println("걸린 시간: ${time}ms")
}
// 상품1 / 상품2 / 상품3
// 걸린 시간: 1574ms

fun ex4() = runBlocking {
    val time = measureTimeMillis {
        val a = async { fetchName(1) }
        val b = async { fetchName(2) }
        val c = async { fetchName(3) }
        println("${a.await()} / ${b.await()} / ${c.await()}")
    }
    println("걸린 시간: ${time}ms")
}
// 상품1 / 상품2 / 상품3
// 걸린 시간: 579ms

fun ex4_2() = runBlocking {
    val time = measureTimeMillis {
        val a = async { fetchName(1) }.await()
        val b = async { fetchName(2) }.await()
        val c = async { fetchName(3) }.await()
        println("${a} / ${b} / ${c}")
    }
    println("걸린 시간: ${time}ms")
}
// 상품1 / 상품2 / 상품3
// 걸린 시간: 1587ms
// async가 동시 작업을 시작하고, await는 그 결과를 기다린다.
// async 직후 바로 await하면 동시성이 사라질 수 있다.

fun ex5() = runBlocking {
    val time = measureTimeMillis {
        val names = (1..10).map { id -> async { fetchName(id) } }
            .awaitAll()
        println(names)
    }
    println("걸린 시간: ${time}ms")
}
// [상품1, 상품2, 상품3, 상품4, 상품5, 상품6, 상품7, 상품8, 상품9, 상품10]
// 걸린 시간: 579ms

/* suspend fun fetchProduct(id: Int): String {
    val name = async { fetchName(id) }      // ✗ 에러
    val price = async { fetchPrice(id) }
    return "${name.await()} (${price.await()}원)"
} */

suspend fun fetchProduct(id: Int): String = coroutineScope {
    val name = async { fetchName(id) }
    val price = async { fetchPrice(id) }
    "${name.await()} (${price.await()}원)"
}

fun ex6() = runBlocking {
    val time = measureTimeMillis { println(fetchProduct(1)) }
    println("걸린 시간: ${time}ms")
}
// 상품1 (1000원)
// 걸린 시간: 645ms
// 마지막 줄 없으면 'Compilation error. See log for more details' 에러
// coroutineScope { } 안에서 마지막에 적힌 식이 그 블록의 반환값이 되는데 반환값이 없어서 에러

fun ex7() = runBlocking {
    val job = launch {
        repeat(10) { i ->
            println("  작업 중 $i")
            delay(200)
            // Thread.sleep(200)
        }
        println("  이 줄은 실행되지 않는다")
    }

    delay(500)
    job.cancelAndJoin()
    println("취소 완료")
}
// 0ms에 작업 중 0이 바로 출력되고, 200ms와 400ms에 1, 2가 출력됨
// 500ms에 취소되어 작업 중 0, 1, 2까지만 출력

suspend fun fetchSlow(): String {
    delay(3000)
    return "느린 응답"
}

fun ex7_2() = runBlocking {
    val result = withTimeoutOrNull(1000) {
        fetchSlow()
    }
    println("결과: $result")

    val ok = withTimeoutOrNull(1000) {
        fetchName(1)
    }
    println("여유 있을 때: $ok")
}
// 제한 시간이 1초인데 fetchSlow()는 3초가 걸리므로 1초 뒤 취소되고 null 반환
// fetchName()은 0.5초 안에 완료되어 정상 결과 반환

/* ex8() */
// fetchName에 Thread.sleep(500)로 수정 후 ex5() 실행
// [상품1, 상품2, 상품3, 상품4, 상품5, 상품6, 상품7, 상품8, 상품9, 상품10]
// 걸린 시간: 5206ms
// delay는 대기 중 스레드를 양보하지만 Thread.sleep은 스레드를 점유하므로
// async를 여러 개 실행해도 동시성이 제대로 활용되지 않아 약 5초가 걸림

// ex7()을 Thread.sleep(200)로 수정하면
//  작업 중 0
//  작업 중 1
//  작업 중 2
//  작업 중 3
//  작업 중 4
//  작업 중 5
//  작업 중 6
//  작업 중 7
//  작업 중 8
//  작업 중 9
//  이 줄은 실행되지 않는다
// 취소 완료
// 코루틴 취소는 스레드를 강제로 끊는 것이 아니다. 코루틴 코드가 취소 상태를 확인해야 실제로 멈춤

fun ex9() = runBlocking {
    try {
        coroutineScope {
            launch {
                delay(1000)
                println("  느린 작업 완료")
            }
            launch {
                delay(100)
                throw RuntimeException("일부러 낸 오류")
            }
        }
    } catch (e: Exception) {
        println("예외를 받았다: ${e.message}")
    }
}
// 예외를 받았다: 일부러 낸 오류
// coroutineScope 안의 자식 하나가 실패하면 그 실패가 부모 스코프로 전파되고, 같은 스코프의 다른 자식들도 취소

fun main() {
    ex9()
}