package part1

// * 배열
// 배열(Array)은 같은 자료형의 값 여러 개를 하나의 이름으로 묶어 두는 것

// * 만드는 방법
// arrayOf(1, 2, 3): 값을 직접 나열해서 만들기
// IntArray(5): Int 5칸을 0으로 채워서 만들기
// Array(5) { 0 }: 5칸을 중괄호의 결과로 채워서 만들기
// arrayOfNulls<String>(3): 3칸을 null로 채워서 만들기

// * val로 선언해도 안의 값은 바꿀 수 있음
// val arr = arrayOf(1, 2, 3)
// arr[0] = 100 // 가능
// arr = arrayOf(4) // 불가능 (컴파일 에러)

// 배열의 모든 값을 출력하는 함수
fun printAll(arr: IntArray) {
    for (value in arr) {
        print("$value ")
    }

    println()
}

// 배열의 합을 직접 구하는 함수
fun arraySum(arr: IntArray): Int {
    var sum = 0

    for (value in arr) {
        sum += value
    }

    return sum
}

// ------------------------------------------------------------
// 예제 1. 배열 만들고 값 꺼내기
// ------------------------------------------------------------
fun h_exam1() {
    // 값을 직접 나열해서 만들기
    val scores = arrayOf(90, 85, 70, 60, 100)

    // 대괄호 [ ] 안에 인덱스를 넣어 값을 꺼냄. 인덱스는 0부터 시작
    println(scores[0])              // 90   첫 번째
    println(scores[1])              // 85
    println(scores[4])              // 100  다섯 번째(마지막)

    // 칸의 개수
    println("칸 개수: ${scores.size}")           // 5
    println("마지막 인덱스: ${scores.lastIndex}") // 4

    // 마지막 값을 꺼내는 두 가지 방법
    println(scores[scores.size - 1])            // 100
    println(scores[scores.lastIndex])           // 100

    // 주의! 없는 인덱스를 쓰면 실행 중에 예외가 남
    // println(scores[5])           // ArrayIndexOutOfBoundsException!
    // 인덱스가 0부터 시작하므로, 5칸짜리 배열의 마지막은 5가 아니라 4

    // 문자열 배열도 만들 수 있음
    val fruits = arrayOf("사과", "바나나", "포도")
    println(fruits[1])              // 바나나
}

// ------------------------------------------------------------
// 예제 2. 값 바꾸기와 val
// ------------------------------------------------------------
fun h_exam2() {
    val scores = arrayOf(90, 85, 70)

    // 인덱스로 값을 바꿈
    scores[0] = 100
    println(scores[0])              // 100

    scores[2] += 10                 // 원래 값에서 더하기도 됨
    println(scores[2])              // 80

    // val로 선언했는데 왜 바뀔까?
    // val이 막는 것은 'scores라는 이름이 다른 배열을 가리키는 것'
    // 배열 안의 값을 바꾸는 것은 막지 않음
    // scores = arrayOf(1, 2, 3)    // 컴파일 에러! Val cannot be reassigned

    printAll(scores.toIntArray())   // 100 85 80
}

// ------------------------------------------------------------
// 예제 3. 여러 가지 배열 만들기
// ------------------------------------------------------------
fun h_exam3() {
    // 3-1. IntArray(n) - Int 전용 배열. 자동으로 0으로 채워짐
    val arr1 = IntArray(5)
    printAll(arr1)                  // 0 0 0 0 0

    // 값을 하나씩 채워 넣기
    for (i in arr1.indices) {
        arr1[i] = (i + 1) * 10
    }

    printAll(arr1)                  // 10 20 30 40 50

    // 3-2. IntArray(n) { 식 } - 인덱스를 받아 각 칸을 채움
    // 중괄호 안에서 it은 인덱스(0, 1, 2...)를 가리킴
    val arr2 = IntArray(5) { it + 1 }
    printAll(arr2)                  // 1 2 3 4 5

    val arr3 = IntArray(5) { it * it }
    printAll(arr3)                  // 0 1 4 9 16

    // 3-3. Array(n) { 식 } - 어떤 자료형이든 만들 수 있음
    val names = Array(3) { "학생${it + 1}" }

    for (name in names) print("$name ")
    println()                       // 학생1 학생2 학생3

    // 3-4. 다른 자료형 전용 배열도 있음
    val doubles = DoubleArray(3)    // 0.0으로 채워짐
    val booleans = BooleanArray(3)  // false로 채워짐
    println("${doubles[0]} / ${booleans[0]}")
}

// ------------------------------------------------------------
// 예제 4. 반복문으로 배열 훑기
// ------------------------------------------------------------
fun h_exam4() {
    val scores = intArrayOf(90, 85, 70, 60, 100)

    // 4-1. 값만 필요할 때
    for (score in scores) {
        print("$score ")
    }

    println()

    // 4-2. 인덱스가 필요할 때 - indices는 0..lastIndex 범위를 만들어 줌
    for (i in scores.indices) {
        println("${i + 1}번 학생: ${scores[i]}점")
    }

    // 4-3. 인덱스와 값이 둘 다 필요할 때 - 구조 분해 (G_loop에서 배운 것)
    for ((index, score) in scores.withIndex()) {
        println("[$index] $score")
    }

    // 4-4. 반복문으로 계산하기
    println("합계: ${arraySum(scores)}")

    // 가장 큰 값 직접 찾기
    var max = scores[0]

    for (score in scores) {
        if (score > max) {
            max = score
        }
    }

    println("최고점: $max")
}

// ------------------------------------------------------------
// 예제 5. 배열이 제공하는 기능들
// ------------------------------------------------------------
fun h_exam5() {
    val scores = intArrayOf(90, 85, 70, 60, 100)

    // 직접 반복문을 짜지 않아도 되는 기능들이 이미 준비되어 있음
    println("합계: ${scores.sum()}")
    println("평균: ${scores.average()}")
    println("최고: ${scores.max()}")
    println("최저: ${scores.min()}")
    println("개수: ${scores.size}")

    // 찾기
    println("70점이 있는가? ${scores.contains(70)}")
    println("70점의 위치: ${scores.indexOf(70)}")     // 2
    println("55점의 위치: ${scores.indexOf(55)}")     // -1  (없으면 -1)

    // 정렬 - 원본은 그대로 두고 정렬된 결과를 새로 돌려줌
    val sorted = scores.sortedArray()
    printAll(sorted)                                 // 60 70 85 90 100
    printAll(scores)                                 // 원본은 그대로

    // 뒤집기
    printAll(scores.reversedArray())                 // 100 60 70 85 90

    // 보기 좋게 한 줄로 출력하기
    println(scores.joinToString(", "))               // 90, 85, 70, 60, 100

    // 배열은 println으로 그냥 찍으면 내용이 안 나옴
    println(scores)                                  // [I@1b6d3586 같은 값
    // 내용을 보려면 joinToString()이나 반복문을 씀
}

// ------------------------------------------------------------
// 예제 6. 2차원 배열 - 배열 안의 배열
// ------------------------------------------------------------
fun h_exam6() {
    // 3행 4열짜리 표. 배열의 각 칸에 또 다른 배열이 들어 있음
    val table = Array(3) { IntArray(4) }

    // [행][열]로 접근
    table[0][0] = 1
    table[1][2] = 5
    table[2][3] = 9

    for (row in table) {
        for (value in row) {
            print("$value ")
        }
        println()
    }
    // 1 0 0 0
    // 0 0 5 0
    // 0 0 0 9

    // 구구단 표 만들기
    val gugudan = Array(3) { dan -> IntArray(3) { i -> (dan + 2) * (i + 1) } }
    for (row in gugudan) {
        println(row.joinToString("  "))
    }
    // 2  4  6
    // 3  6  9
    // 4  8  12
}

// ------------------------------------------------------------
// 예제 7. 배열과 리스트
// ------------------------------------------------------------
fun h_exam7() {
    // G_loop에서 썼던 listOf가 리스트. 배열과 비슷하지만 성격이 다름
    val arr = arrayOf(1, 2, 3)
    val list = listOf(1, 2, 3)

    // 꺼내는 방법은 같음
    println("${arr[0]} / ${list[0]}")
    println("${arr.size} / ${list.size}")

    // 차이 1) listOf로 만든 리스트는 값을 바꿀 수 없음 (읽기 전용)
    arr[0] = 100                    // 가능
    // list[0] = 100                // 컴파일 에러! 읽기 전용

    // 차이 2) 리스트는 내용을 그대로 출력해줌
    println(arr)                    // [Ljava.lang.Integer;@... 같은 값
    println(list)                   // [1, 2, 3]

    // 차이 3) mutableListOf로 만들면 칸을 늘리고 줄일 수 있음
    // 배열은 한 번 만들면 크기를 바꿀 수 없음
    val mutable = mutableListOf(1, 2, 3)
    mutable.add(4)                  // 칸이 늘어남
    mutable.remove(1)               // 값을 뺌
    println(mutable)                // [2, 3, 4]

    // 정리
    //   배열(Array): 크기 고정, 값 변경 가능. 크기가 정해진 데이터에 적합
    //   리스트(List): listOf는 읽기 전용, mutableListOf는 크기까지 변경 가능
    // 실무에서는 리스트를 훨씬 많이 씀. 배열은 크기가 고정된 경우에 씀
}

fun main() {
    h_exam1()     // 배열 만들고 값 꺼내기
    h_exam2()     // 값 바꾸기와 val
    h_exam3()     // 여러 가지 배열 만들기
    h_exam4()     // 반복문으로 배열 훑기
    h_exam5()     // 배열이 제공하는 기능들
    h_exam6()     // 2차원 배열
    h_exam7()     // 배열과 리스트
}