package part1

// 조건문 - when
// when은 하나의 값을 여러 경우와 비교해서, 맞는 갈래 하나만 실행하는 조건문

// * 기본 문법
//   when (비교할 값) {
//       경우1 -> 실행할 코드
//       경우2 -> 실행할 코드
//       else -> 어디에도 해당하지 않을 때 실행할 코드
//   }
//   위에서부터 차례로 비교하다가 처음으로 맞는 갈래 하나만 실행하고 빠져나옴

// * 자바의 switch와 다른 점
//   - break가 필요 없음. 자바처럼 아래 갈래로 흘러내리지 (fall-through) 않음.
//   - 값을 만들어 내는 표현식. 그래서 결과를 변수에 바로 담을 수 있음
//   - 비교할 수 있는 대상에 제한이 거의 없음. 정수, 문자열, 범위, 자료형, 조건식 모두 가능
//   - 인자 없이 when { }만 써서 if ~ else if를 대신할 수도 있음

// * when이 비교할 수 있는 것들
//   값       :  1 -> ...            같은 값인지 (== 비교)
//   여러 값   :  1, 2, 3 -> ...      쉼표로 나열하면 그중 하나라도 맞으면 실행
//   범위     :  in 1..9 -> ...      범위 안에 들어 있는지
//   자료형    :  is String -> ...    그 자료형인지 (스마트 캐스트도 된다)
//   조건식    :  score >= 90 -> ...  인자 없는 when 에서 Boolean 조건을 직접 씀

// * 값으로 쓸 때는 else가 반드시 있어야 함
//   어떤 갈래에도 걸리지 않으면 돌려줄 값이 없어지기 때문 (if 표현식과 같은 이유)

fun getGradeWhen(score: Int): String = when (score) {
    in 90..100 -> "A"
    in 80..89 -> "B"
    in 70..79 -> "C"
    else -> "F"
}

// 숫자를 요일 이름으로 바꾸는 함수
fun getDayName(day: Int): String = when (day) {
    1 -> "월요일"
    2 -> "화요일"
    3 -> "수요일"
    4 -> "목요일"
    5 -> "금요일"
    6, 7 -> "주말"          // 쉼표로 여러 값을 한 갈래에 묶을 수 있음
    else -> "잘못된 요일"
}

// 자료형에 따라 다르게 처리하는 함수
fun describe(value: Any): String = when (value) {
    is Int -> "정수입니다. 1을 더하면 ${value + 1}"
    is Double -> "실수입니다. 2를 곱하면 ${value * 2}"
    is String -> "문자열입니다. 길이는 ${value.length}"
    is Boolean -> "논리값입니다. 뒤집으면 ${!value}"
    else -> "알 수 없는 자료형입니다"
}

// ------------------------------------------------------------
// 예제 1. 가장 기본이 되는 when
// ------------------------------------------------------------
fun f2_exam1() {
    val number = 3

    when (number) {
        1 -> println("하나")
        2 -> println("둘")
        3 -> println("셋")          // 여기가 실행됨
        else -> println("모르는 숫자")
    }

    // 맞는 갈래 하나만 실행하고 끝남. 자바처럼 break를 쓰지 않아도 됨

    // 문자열도 비교할 수 있음
    val fruit = "사과"

    when (fruit) {
        "사과" -> println("빨간색")
        "바나나" -> println("노란색")
        "포도" -> println("보라색")
        else -> println("모르는 과일")
    }

    // 실행할 코드가 여러 줄이면 중괄호로 묶음
    val menu = 2

    when (menu) {
        1 -> println("주문하기를 선택했습니다")
        2 -> {
            println("장바구니를 선택했습니다")
            println("장바구니를 여는 중...")
        }
        else -> println("잘못된 선택입니다")
    }
}

// ------------------------------------------------------------
// 예제 2. 여러 값을 한 갈래로 묶기, 범위로 비교하기
// ------------------------------------------------------------
fun f2_exam2() {
    // 2-1. 쉼표로 여러 값 나열하기
    val day = 6

    when (day) {
        1, 2, 3, 4, 5 -> println("평일입니다")
        6, 7 -> println("주말입니다")           // 6이므로 여기
        else -> println("잘못된 값입니다")
    }

    println(getDayName(1))      // 월요일
    println(getDayName(7))      // 주말
    println(getDayName(9))      // 잘못된 요일

    // 2-2. in으로 범위 비교하기
    val score = 85

    when (score) {
        in 90..100 -> println("A 학점")
        in 80..89 -> println("B 학점")          // 85이므로 여기
        in 70..79 -> println("C 학점")
        else -> println("F 학점")
    }

    // !in으로 '범위 밖'인지도 검사할 수 있음
    val age = 25

    when (age) {
        in 0..18 -> println("미성년자")
        !in 0..64 -> println("65세 이상")
        else -> println("성인")                 // 25이므로 여기
    }

    // if ~ else if로 쓰면 이렇게 길어짐. when이 훨씬 읽기 좋음
    // if (score in 90..100) { ... } else if (score in 80..89) { ... } else ...
}

// ------------------------------------------------------------
// 예제 3. when 은 표현식 - 값을 만들어 냄
// ------------------------------------------------------------
fun f2_exam3() {
    val number = 2

    // when의 결과를 변수에 그대로 담을 수 있음
    val name = when (number) {
        1 -> "하나"
        2 -> "둘"
        3 -> "셋"
        else -> "모름"
    }

    println(name)                   // 둘

    // 값으로 쓸 때는 else가 반드시 있어야 함
    // val bad = when (number) {    // 컴파일 에러! 'when' expression must be exhaustive
    //     1 -> "하나"
    //     2 -> "둘"
    // }
    // number가 3이면 담을 값이 없어지기 때문

    // 함수의 반환값으로 쓰면 아주 간결해짐 (준비 구역의 함수들)
    println(getGradeWhen(95))       // A
    println(getGradeWhen(85))       // B
    println(getGradeWhen(50))       // F

    // 문자열 템플릿 안에서도 쓸 수 있음
    println("오늘은 ${getDayName(3)}입니다")

    // 갈래를 중괄호로 묶었다면, 그 블록의 '마지막 줄'이 값이 됨 (if, 람다식과 같은 규칙)
    val message = when (number) {
        1 -> "첫 번째"
        2 -> {
            println("  2번 갈래를 처리하는 중...")
            "두 번째"               // 이 값이 message에 들어감
        }
        else -> "그 외"
    }

    println(message)                // 두 번째
}

// ------------------------------------------------------------
// 예제 4. 인자 없는 when - if ~ else if를 대신
// ------------------------------------------------------------
fun f2_exam4() {
    val score = 85

    // when 뒤에 괄호를 쓰지 않으면, 각 갈래에 Boolean 조건을 직접 쓸 수 있음
    // 처음으로 true가 되는 갈래 하나만 실행됨
    when {
        score >= 90 -> println("A 학점")
        score >= 80 -> println("B 학점")        // 여기
        score >= 70 -> println("C 학점")
        else -> println("F 학점")
    }

    // 서로 다른 변수를 조건에 섞어 쓸 수 있는 것이 인자 없는 when의 장점
    val age = 25
    val hasTicket = true

    when {
        age < 19 -> println("미성년자는 입장할 수 없습니다")
        !hasTicket -> println("표가 없습니다")
        age >= 19 && hasTicket -> println("입장 완료")     // 여기
        else -> println("확인이 필요합니다")
    }

    // 정리
    // when (값) { ... }  : 하나의 값을 여러 경우와 비교할 때
    // when { ... }       : 서로 다른 조건들을 차례로 검사할 때 (if ~ else if 대체)
}

// ------------------------------------------------------------
// 예제 5. 자료형 검사와 스마트 캐스트
// ------------------------------------------------------------
fun f2_exam5() {
    // is로 자료형을 검사하면, 그 갈래 안에서는 해당 자료형으로 자동 인식됨 (스마트 캐스트)
    println(describe(10))           // 정수입니다. 1을 더하면 11
    println(describe(1.5))          // 실수입니다. 2를 곱하면 3.0
    println(describe("코틀린"))      // 문자열입니다. 길이는 3
    println(describe(true))         // 논리값입니다. 뒤집으면 false
    println(describe('A'))          // 알 수 없는 자료형입니다

    // 직접 써 보면 이런 모습
    val value: Any = "안녕하세요"
    when (value) {
        is Int -> println("정수: ${value + 1}")
        is String -> println("문자열: ${value.uppercase()}")   // String으로 자동 인식
        else -> println("그 외")
    }

    // null도 하나의 갈래로 다룰 수 있음
    val text: String? = null
    when (text) {
        null -> println("값이 없습니다")
        "" -> println("빈 문자열입니다")
        else -> println("값: $text")
    }
}

// ------------------------------------------------------------
// 예제 6. when에서 변수 선언하기
// ------------------------------------------------------------
fun f2_exam6() {
    // when의 괄호 안에서 변수를 선언하면, 그 변수는 when 안에서만 쓸 수 있음
    // 값을 만드는 계산이 길 때 유용
    when (val length = "코틀린 프로그래밍".length) {
        in 0..5 -> println("짧다 (길이 $length)")
        in 6..15 -> println("보통이다 (길이 $length)")      // 여기
        else -> println("길다 (길이 $length)")
    }
    // println(length)      // 컴파일 에러! when 밖에서는 쓸 수 없음

    // 함수 호출 결과를 바로 검사할 때도 편리
    when (val grade = getGradeWhen(95)) {
        "A" -> println("최고 학점입니다: $grade")
        "B", "C" -> println("무난한 학점입니다: $grade")
        else -> println("아쉬운 학점입니다: $grade")
    }
}

fun main() {
    f2_exam1()     // 기본 when
    f2_exam2()     // 여러 값 묶기, 범위(in)로 비교하기
    f2_exam3()     // when은 표현식 (값을 만듦)
    f2_exam4()     // 인자 없는 when (if ~ else if 대체)
    f2_exam5()     // 자료형 검사와 스마트 캐스트
    f2_exam6()     // when에서 변수 선언하기
}