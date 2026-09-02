package part1

// * for문
//   for (변수 in 범위나_목록) {
//       반복할 코드
//   }
// 이 변수는 자동으로 만들어지는 val여서 반복문 안에서 값을 바꿀 수 없음

// * 범위를 만드는 방법
// 1..5: 1, 2, 3, 4, 5 (끝 값 포함)
// 1 until 5: 1, 2, 3, 4 (끝 값 제외)
// 5 downTo 1: 5, 4, 3, 2, 1 (거꾸로)
// 1..10 step 2: 1, 3, 5, 7, 9 (건너뛰기)

// * while문 - 조건이 참인 동안 계속 반복할 때
//   while (조건) { 반복할 코드 }
//   조건을 먼저 검사하므로, 처음부터 거짓이면 한 번도 실행되지 않음
//   반복을 멈출 수 있도록 조건에 쓰이는 값을 안에서 반드시 바꿔줘야 함
//   (그렇지 않으면 영원히 끝나지 않는 무한 루프가 됨)

// * do ~ while문 - 일단 한 번 실행하고 나서 조건을 검사할 때
//   do { 반복할 코드 } while (조건)
//   조건이 거짓이더라도 최소 한 번은 실행된다는 점이 while과 다름

// * 반복을 제어하는 키워드
//   break    : 반복문을 즉시 빠져나옴
//   continue : 이번 회차만 건너뛰고 다음 회차로 넘어감
//   라벨@    : 중첩된 반복문에서 어느 반복문을 빠져나올지 지정

// 1부터 n까지 더하는 함수
fun sumTo(n: Int): Int {
    var sum = 0

    for (i in 1..n) {
        sum += i
    }

    return sum
}

// 구구단 한 단을 출력하는 함수
fun printGugudan(dan: Int) {
    for (i in 1..9) {
        println("$dan x $i = ${dan * i}")
    }
}

// 팩토리얼(1 x 2 x ... x n)을 구하는 함수
fun factorial(n: Int): Int {
    var result = 1

    for (i in 1..n) {
        result *= i
    }

    return result
}

// ------------------------------------------------------------
// 예제 1. 가장 기본이 되는 for문
// ------------------------------------------------------------
fun g_exam1() {
    // 1부터 5까지 반복. i에 1, 2, 3, 4, 5가 차례로 담김
    for (i in 1..5) {
        println("i = $i")
    }

    // 반복할 코드가 한 줄이면 중괄호를 생략할 수 있음
    for (i in 1..3) println("한 줄 for: $i")

    // 반복문 안에서 계산을 누적할 수 있음
    // 누적할 변수는 반복문 '밖'에 선언해야 함. 안에 선언하면 매번 새로 만들어짐
    var sum = 0

    for (i in 1..10) {
        sum += i
    }

    println("1부터 10까지의 합: $sum")       // 55

    // 반복 변수 i는 val이라 값을 바꿀 수 없음
    for (i in 1..3) {
        // i = 10       // 컴파일 에러! Val cannot be reassigned
        println(i)
    }

    // 준비해 둔 함수로 확인해 보기
    println(sumTo(100))         // 5050
    println(factorial(5))       // 120
}

// ------------------------------------------------------------
// 예제 2. 여러 가지 범위 만들기 - until, downTo, step
// ------------------------------------------------------------
fun g_exam2() {
    // 2-1. ..은 끝 값을 포함
    print("1..5      : ")
    for (i in 1..5) print("$i ")
    println()                                   // 1 2 3 4 5

    // 2-2. until은 끝 값을 포함하지 않음
    print("1 until 5 : ")
    for (i in 1 until 5) print("$i ")
    println()                                   // 1 2 3 4

    // 2-3. downTo는 거꾸로 셈
    print("5 downTo 1: ")
    for (i in 5 downTo 1) print("$i ")
    println()                                   // 5 4 3 2 1

    // 2-4. step은 건너뛰는 간격을 정함
    print("1..10 step 2 : ")
    for (i in 1..10 step 2) print("$i ")
    println()                                   // 1 3 5 7 9

    print("10 downTo 1 step 3 : ")
    for (i in 10 downTo 1 step 3) print("$i ")
    println()                                   // 10 7 4 1

    // 주의! 범위가 거꾸로면(시작 > 끝) 한 번도 실행되지 않음
    for (i in 5..1) println("이 줄은 실행되지 않는다")
    println("(위 반복문은 한 번도 실행되지 않았다)")

    // 문자에도 범위를 쓸 수 있음
    print("a..e : ")
    for (c in 'a'..'e') print("$c ")
    println()                                   // a b c d e
}

// ------------------------------------------------------------
// 예제 3. 목록을 훑는 for문
// ------------------------------------------------------------
fun g_exam3() {
    val fruits = listOf("사과", "바나나", "포도")

    // 목록의 항목이 하나씩 담김. 개수를 세지 않아도 되니 편하고 안전
    for (fruit in fruits) {
        println(fruit)
    }

    // 순서(번호)가 필요하면 indices를 씀. 0부터 마지막 번호까지의 범위를 만들어 줌
    for (i in fruits.indices) {
        println("$i 번째: ${fruits[i]}")
    }

    // 번호와 값이 둘 다 필요하면 withIndex()가 더 깔끔
    // withIndex()는 '번호와 값이 짝지어진 것'을 하나씩 내어 줌
    for ((index, fruit) in fruits.withIndex()) {
        println("$index -> $fruit")
    }

    // 위에서 괄호로 (index, fruit)라고 쓴 것을 '구조 분해 선언'이라고 함
    // 짝지어진 값을 한 번에 여러 변수로 쪼개서 받는 문법
    // 괄호 안에 적은 이름 개수만큼 변수가 만들어지고, 순서대로 값이 담김
    //
    //   for ((index, fruit) in ...)    -> 앞의 것은 index에, 뒤의 것은 fruit에
    //
    // 쪼갠 값 중에 쓰지 않을 것이 있으면 밑줄(_)로 자리만 채움
    for ((_, fruit) in fruits.withIndex()) {
        println("번호는 필요 없고 값만: $fruit")
    }

    // 문자열도 한 글자씩 훑을 수 있다.
    for (c in "코틀린") {
        println(c)
    }
}

// ------------------------------------------------------------
// 예제 4. while문
// ------------------------------------------------------------
fun g_exam4() {
    // 조건이 true인 동안 계속 반복
    var i = 1

    while (i <= 5) {
        println("i = $i")
        i++                     // 이 줄이 없으면 조건이 영원히 참이라 무한 루프가 됨
    }

    // 반복 횟수가 정해지지 않은 일에 어울림
    // 예) 100을 2로 계속 나눠서 1보다 작아질 때까지 몇 번 걸리는지 세기
    var number = 100
    var count = 0

    while (number > 1) {
        number /= 2
        count++
    }

    println("100을 2로 나눠 1이 될 때까지: ${count}번")     // 6번

    // 조건이 처음부터 거짓이면 한 번도 실행되지 않음
    var x = 10

    while (x < 5) {
        println("이 줄은 실행되지 않는다")
        x++
    }

    println("(위 while은 한 번도 실행되지 않았다)")
}

// ------------------------------------------------------------
// 예제 5. do ~ while문
// ------------------------------------------------------------
fun g_exam5() {
    // 일단 한 번 실행하고 나서 조건을 검사
    var i = 1

    do {
        println("i = $i")
        i++
    } while (i <= 5)

    // while과의 차이 - 조건이 처음부터 거짓일 때
    var x = 10

    // while: 조건을 먼저 검사하므로 한 번도 실행되지 않음
    while (x < 5) {
        println("while 안 - 실행되지 않음")
    }

    // do ~ while: 일단 한 번은 실행
    do {
        println("do ~ while 안 - 조건이 거짓이어도 한 번은 실행된다")
    } while (x < 5)

    // "최소 한 번은 반드시 해야 하는 일"에 어울림
    // 예) 사용자에게 값을 입력받고, 잘못 입력했으면 다시 받기
}

// ------------------------------------------------------------
// 예제 6. break와 continue
// ------------------------------------------------------------
fun g_exam6() {
    // 6-1. break: 반복문을 즉시 빠져나옴
    print("break  : ")

    for (i in 1..10) {
        if (i > 5) break            // 6이 되는 순간 반복을 멈춤
        print("$i ")
    }

    println()                       // 1 2 3 4 5

    // 6-2. continue: 이번 회차만 건너뛰고 다음으로 넘어감
    print("continue: ")

    for (i in 1..10) {
        if (i % 2 == 1) continue    // 홀수면 건너뜀
        print("$i ")
    }

    println()                       // 2 4 6 8 10

    // 활용 예) 1부터 100까지 더하다가 합이 100을 넘으면 멈추기
    var sum = 0
    var last = 0

    for (i in 1..100) {
        sum += i

        if (sum > 100) {
            last = i
            break
        }
    }

    println("합이 100을 넘은 시점: ${last}까지 더했을 때, 합은 $sum")     // 14, 105

    // while문에서도 똑같이 쓸 수 있음
    var n = 0

    while (true) {                  // 조건이 항상 참이지만 break로 빠져나옴
        n++

        if (n >= 3) break
    }

    println("n = $n")               // 3
}

// ------------------------------------------------------------
// 예제 7. 중첩 반복문과 라벨
// ------------------------------------------------------------
fun g_exam7() {
    // 반복문 안에 반복문을 넣을 수 있음. 구구단이 대표적인 예
    for (dan in 2..4) {
        for (i in 1..3) {
            print("$dan x $i = ${dan * i}   ")
        }

        println()                   // 안쪽 반복이 끝날 때마다 줄바꿈
    }

    // 준비해 둔 함수로 한 단만 출력해 보기
    printGugudan(7)

    // 7-1. 중첩 반복문에서의 break
    // 그냥 break를 쓰면 '자기가 속한 안쪽 반복문'만 빠져나옴
    println("--- 그냥 break ---")

    for (i in 1..3) {
        for (j in 1..3) {
            if (j == 2) break       // 안쪽 for만 빠져나옴
            println("i = $i, j = $j")
        }
    }
    // 바깥 반복은 계속되므로 i = 1, 2, 3 모두 출력

    // 7-2. 라벨을 쓰면 바깥 반복문까지 한 번에 빠져나올 수 있음
    // 반복문 앞에 '이름@'을 붙이고, break@이름으로 지정
    println("--- 라벨 break ---")

    outer@ for (i in 1..3) {
        for (j in 1..3) {
            if (j == 2) break@outer     // 바깥 for까지 한 번에 빠져나옴
            println("i = $i, j = $j")
        }
    }
    // i = 1, j = 1만 출력되고 전체가 끝남

    // continue에도 라벨을 쓸 수 있음
    println("--- 라벨 continue ---")

    outer@ for (i in 1..3) {
        for (j in 1..3) {
            if (j == 2) continue@outer  // 바깥 for의 다음 회차로 넘어감
            println("i = $i, j = $j")
        }
    }
}

// ------------------------------------------------------------
// 예제 8. repeat - 단순히 n번 반복할 때
// ------------------------------------------------------------
fun g_exam8() {
    // 반복 변수를 쓸 일이 없다면 repeat이 더 간단
    repeat(3) {
        println("안녕하세요!")
    }

    // 필요하면 몇 번째인지도 받을 수 있음 (0부터 시작)
    repeat(3) { index ->
        println("${index + 1}번째 실행")
    }

    // repeat은 사실 반복문이 아니라, E_functional_programming에서 배운 고차 함수
    // 중괄호 안의 코드가 람다식으로 넘어가서 n번 실행되는 것
}

fun main() {
    g_exam1()     // 기본 for문
    g_exam2()     // 범위 만들기 (until, downTo, step)
    g_exam3()     // 목록을 훑는 for문
    g_exam4()     // while문
    g_exam5()     // do ~ while문
    g_exam6()     // break와 continue
    g_exam7()     // 중첩 반복문과 라벨
    g_exam8()     // repeat
}