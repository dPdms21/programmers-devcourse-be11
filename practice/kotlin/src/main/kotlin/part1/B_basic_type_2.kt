package part1

// * 자료형 검사하고 변환하기

// 코틀린에서는 null인 상태인 변수를 허용하려면 물음표(?) 기호를 사용해서 선언해야 함
// 코틀린은 변수에 아예 null을 허용하지 않아 NPE 문제를 미리 방지할 수 있음

// 세이프 콜과 엘비스 연산자를 활용해 null을 허용한 변수를 더 안전하게 사용하기

// 자료형 비교하고 검사하고 변환하기
// 코틀린에서는 자료형이 서로 다른 변수를 같은 자료형으로 만들어야 연산할 수 있음

fun main() {
    // 1. null을 허용하지 않는 변수 vs 허용하는 변수
    // 기본 자료형(String, Int, ...)에는 null을 넣을 수 없음
    val name: String = "홍길동"
//    val name: String = null // 컴파일 에러

    // 자료형 뒤에 물음표(?)를 붙이면 그때부터 null을 넣을 수 있음
    val nickName: String? = null
    println("이름: $name, 별명: $nickName")

    // 2. 세이프 콜(?.) - null이면 실행하지 않고 그냥 null을 반환
    val str1: String? = "Kotlin"
    val str2: String? = null

    println(str1?.length)
    println(str2?.length) // null이니 실행을 건너뛰고 null을 반환

    // 세이프 콜은 이어서 붙일 수도 있음. 중간에 하나라도 null이면 전체가 null
    println(str2?.uppercase()?.length)

    // 3. 엘비스 연산자(?:) - null이면 이 기본값을 대신 사용
    val length1 = str1?.length ?: 0
    val length2 = str2?.length ?: 0
    println("length1: $length1, length2: $length2")

    val display = nickName ?: "별명 없음"
    println(display)

    val input1: String? = "홍길동"
    val input2: String? = null
    println("안녕하세요, ${input1 ?: "익명"}님!")
    println("안녕하세요, ${input2 ?: "익명"}님!")

    // 4. !!: non null 단언 - null이 아님을 보장
    // 실제로 null이면 그 자리에서 NPE가 발생
    // -> 꼭 필요한 경우가 아니면 ?.와 ?:를 사용할 것
    val sure: String = "확실히 값이 있다."
    println(sure!!.length)

    // 5. 자료형 비교하기 - == (값 비교), === (참조 비교)
    val s1: String = "hello"
    val s2: String = "hello"
    val s3: String = StringBuilder("hel").append("lo").toString()

    println(s1 == s2)
    println(s1 === s2)
    println(s1 == s3)
    println(s1 === s3)

    // 자바에서는 equals()를 써야 했지만, 코틀린은 ==가 곧 equals()가 됨
    // 자바의 == (주소비교)에 해당하는 것이 코틀린의 ===

    // 6. 자료형 검사하기 - is / !is - is는 해당 값의 자료형을 묻고 결과로 true/false를 반환
    val obj: Any = "나는 문자열이다." // Any는 모든 자료형의 최상위 (자바의 Object)
    println("== 6 ==")
    println(obj is String)
    println(obj is Int)
    println(obj !is Int)

    // 결과가 Boolean이므로 변수에 담아둘 수도 있음
    val isString: Boolean = obj is String
    println("문자열인가? $isString")

    val num: Any = 100
    println(num is Int)
    println(num is String)

    // is로 검사한 뒤에는 코틀린이 그 값을 해당 자료형으로 알아서 취급해줌
    // 이것을 '스마트 캐스트'라고 함

    // 7. 자료형 변환하기 - as / as?
    val any: Any = "문자열입니다"
    val casted: String = any as String
    println(casted.length)

    // 변환할 수 없는 자료형이면 as는 예외를 던짐
//    val fail: Int = any as Int // ClassCastException

    // as?를 쓰면 실패했을 때 예외 대신 null을 돌려줌 (안전한 형 변환)
    val safe: Int? = any as? Int
    println(safe)

    val number: Int = any as? Int ?: -1
    println(number)

    // 8. 숫자 자료형 변환 - 코틀린은 자동으로 바꿔주지 않음
    val intVal: Int = 100
//    val longVal: Long = intVal // 컴파일 에러. Int를 Long에 그냥 못 넣음
    val longVal: Long = intVal.toLong()
    println(longVal)

    // 자바는 작은 타입 -> 큰 타입을 알아서 바꿔줬지만 (암묵적 형 변환)
    // 코틀린은 실수를 막기 위해 개발자가 명시적으로 변환하도록 강제

    val d: Double = 3.99
    println(d.toInt())          // 3   - 소수점 이하는 버림 (반올림 아님!)
    println(d.toFloat())        // 3.99
    println(intVal.toDouble())  // 100.0
    println(intVal.toString())  // "100"  문자열로
    println("123".toInt() + 1)  // 124    문자열을 숫자로

    // 변환할 수 없는 문자열이면 예외가 남. toIntOrNull()은 null을 돌려줌
//    println("abc".toInt()) // NumberFormatException
    println("abc".toIntOrNull())
    println("abc".toIntOrNull() ?: 0)

    // 단, 서로 다른 숫자 자료형끼리의 '연산'은 가능. 결과는 더 큰 자료형이 됨
    val sum = intVal + longVal
    println("$sum / ${sum::class.simpleName}")
}