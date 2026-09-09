package part3

import kotlin.system.measureTimeMillis

// * 시퀀스 - 필요할 때만 계산하기

// 시퀀스는 중간 리스트를 만들지 않고, 원소 하나가 모든 단계를 통과하게 만듦
// 원소가 몇 개 안되면 상관없지만, 수십만 개라면 리스트를 몇 번씩 새로 만드는 것이 낭비

// 1. 즉시 계산과 지연 계산
// [List - 즉시 계산]
// list.filter { } -> 전부 걸러서 리스트를 만듦
// .map { }        -> 그 리스트를 전부 변환해서 또 리스트를 만듦
// .first()        -> 그중 첫 번째를 꺼냄
// 단계별로 끝까지 다 처리. 첫 번째 하나만 필요해도 전부 계산

// [Sequence - 지연 계산]
// 원소 하나가 filter -> map을 통과하고, 그다음 원소가 다시 filter -> map을 통과
// first()가 답을 얻는 순간 나머지 원소는 아예 건드리지도 않음

// 2. 중간 연산과 최종 연산
// [중간 연산] 시퀀스를 돌려줌. 이 시점에는 아무것도 계산하지 않음
// map { it * 10 }    원소 변환 [3, 1, 4] -> [30, 10, 40]
// filter { it > 3 }  조건에 맞는 것만 남김 [3, 1, 4] -> [4]
// take (2)           앞에서 n개만 가져오고 거기서 멈춤 [3, 1, 4] -> [3, 1]
// drop (2)           앞에서 n개를 버리고 나머지를 흘려보냄 [3, 1, 4] -> [4]
// distinct ()        중복 제거. 처음 나온 순서를 지킴 [3, 1, 3] -> [3, 1]
// sorted ()          정렬 [3, 1, 4] -> [1, 3, 4]
// flatMap { }        변환한 뒤 한 겹 펼침 (평탄화)
// ["a b", "c"] -> map [[a, b], [c]]
//              -> flatMap [a, b, c]
// onEach { }         들여다보기만 하고 원소를 흘려보냄

// [최종 연산] 시퀀스가 아닌 것을 돌려줌. 이때 전체가 실행됨
// toList() / toSet() 결과를 모아 리스트나 집합으로 만듦. 가장 많이 사용됨
// first() / find { } 조건에 맞는 첫 원소를 꺼냄 -> 찾는 즉시 멈춤
// count()            개수를 셈
// sum() / sumOf { }  합계를 냄
// forEach { }        하나씩 꺼내 쓰고 끝남
// any { } / all { }  하나라도 맞는가 / 전부 맞는가 -> 판정이 나면 즉시 멈춤
// maxOrNull()        최댓값 전부 봐야 하므로 중간에 멈추지 못함
// * 최종 연산을 부르지 않으면 중간 연산은 한 줄도 실행되지 않음. 시퀀스의 메커니즘

// 3. 만들기
// list.asSequence() 이미 있는 컬렉션에서 사용
// sequence(1, 2, 3) 값을 나열하기
// generateSequence(1) { it + 1 } 규칙으로 무한히 만들기
// (1): 시작값
// 1 -> 1+1=2, 2+1=3, 3+1=4,... 끝없이 이어짐
// 끝이 없으므로 take(n) 같은 것으로 반드시 끊어줘야함
// sequence{ yield(1); yield(2) } 직접 하나씩 내어 주기

// 4. 언제 쓰나 / 언제 쓰지 않나
// 쓰면 좋을 때
// - 원소가 아주 많음 (수만 개 이상)
// - 중간 연산 단계가 여러 개
// - first, find, take처럼 일부만 필요해서 중간에 멈출 수 있음
// - 끝이 없는 데이터를 다룸

// 쓰지 않는 것이 나을 때
// - 원소가 적음. 시퀀스는 원소마다 함수 호출이 늘어나 오히려 느림
// - 단계가 하나뿐. list.map { } 한 번이면 그냥 리스트가 나음
// - sorted()처럼 어차피 전부 모아야 하는 연산이 중간에 들어감

// * 기본은 List. 느려서 문제가 되는 곳에서만 시퀀스를 씀

fun e_isEven(n: Int): Boolean {
    println(" filter($n)")

    return n % 2 == 0
}

fun e_double(n: Int): Int {
    println(" map($n)")

    return n * 2
}

// 1. 실행 순서가 다음
fun e_exam1() {
    val nums = listOf(1, 2, 3, 4, 5, 6)
    println("[List]")
    val r1 = nums.filter { e_isEven(it) }.map { e_double(it) }
    println(r1)
    // filter(1)
    // filter(2)
    // filter(3)
    // filter(4)
    // filter(5)
    // filter(6)
    // map(2)
    // map(4)
    // map(6)
    // filter를 6번 다하고 그 다음 map을 3번 실행

    println("[Sequence]")
    val r2 = nums.asSequence().filter { e_isEven(it) }.map { e_double(it) }.toList()
    println(r2)
    //  filter(1)
    // filter(2) -> map(2)
    // filter(3)
    // filter(4) -> map(4)
    // filter(5)
    // filter(6) -> map(6)
    // 원소 하나가 파이프를 끝까지 통과하고, 그 다음 원소가 들어감

    // 결과는 똑같음. 다른 것은 '언제 무엇을 계산하는가'뿐
}

// 2. 최종 연산이 없으면 실행되지 않음
fun e_exam2() {
    val nums = listOf(1, 2, 3, 4, 5, 6)

    println("[중간 연산만]")
    val seq = nums.asSequence().filter { e_isEven(it) }.map { e_double(it) }
    println("여기까지 아무것도 출력되지 않음")
    println(seq)

    println("[최종 연산을 부르면]")
    println(seq.toList())

    println("[다시 사용]")
    println(seq.count())
}

// 3. 중간에 멈출 수 있음
fun e_exam3() {
    val nums = (1..1_000_000).toList()

    // 100만 개 중에서 "1000으로 나누어떨어지는 첫 번째 수의 두 배"를 구함
    // 3-1 List: 100만 개를 전부 걸러 낸 뒤 그것을 전부 변환하고 그중 첫 번째를 꺼냄
    val t = measureTimeMillis {
        val a = nums.filter { it % 1000 == 0 }.map { it * 2 }.first()
        println(a)
    }
    println(" List: ${t}ms ")

    // 3-2 Sequence: 1000까지만 검사하고 바로 끝남. 나머지 999,000개는 보지도 않음
    val st = measureTimeMillis {
        val b = nums.asSequence().filter { it % 1000 == 0 }.map { it * 2 }.first()
        println(b)
    }
    println(" Sequence : ${st}ms ")

    // take(n)도 필요한 만큼만 계산하고 멈춤
    println(
        // [7, 14, 21, 28, 35]
        nums.asSequence().filter { it % 7 == 0 }.take(5).toList()
    )

    // 원소가 적으면 시퀀스가 손해
    val small = listOf(1, 2, 3, 4, 5)
    println(
        small.filter { it > 2}.map { it * 2 } // 이쪽이 나음
    )
    println(
        small.asSequence().filter { it % 2 == 0 }.map{ it * 2 }.toList()
    )
}

// 4. 끝이 없는 시퀀스
fun e_exam4() {
    // 4-1 generateSequence(시작값) { 다음값 }
    val naturals = generateSequence(1) { it + 1 }
    println( naturals.take(10).toList() )

    // 2의 거듭제곱
    println(
        generateSequence (1) { it * 2 }.take(10).toList()
    )
    // 1 -> 1*2, 2*2, 4*2 ,...

    // 4-2 조건을 붙여 끝내기 -> 다음 값으로 null을 돌려주면 시퀀스가 끝남
    val underThousand = generateSequence(1) { if (it * 3 < 1000 ) it * 3 else null }
    println(underThousand.toList())

    // 4-3 무한 시퀀스에 filter
    println(
        generateSequence (1) { it + 1 }.filter { it % 3 == 0 }.take(5).toList()
    )

    // 4-4 sequence { }로 직접 하나씩 내어 주기
    // yield를 만나면 값을 하나 내어 주고 그 자리에서 멈춰 있다가 다음 값을 요구하면 이어서 실행
    val custom = sequence {
        println("첫 번째 값 준비")
        yield(1)

        println("두 번째 값 준비")
        yield(2)

        println("나머지를 한꺼번에 준비")
        yieldAll(listOf(3, 4, 5))
    }

    println(
        custom.take(2).toList()
    )
}

// 5. 시퀀스가 손해인 경우
data class E_Student(val name: String, val age: Int)

val e_students = listOf(
    E_Student("User1", 21),
    E_Student("User2", 22),
    E_Student("User3", 23),
    E_Student("User4", 24),
    E_Student("User5", 25),
)

fun e_exam5() {
    // 틀린 코드는 아니지만 시퀀스로 얻는 것이 거의 없음
    val result = e_students.asSequence()
        .sortedByDescending { it.age }
        .map { it.name }
        .take(3)
        .toList()
}

fun main() {
    e_exam5()
}