package part1

// * val와 var
// - val: 최초로 지정한 변수의 값으로 초기화하고 더 이상 바꿀 수 없는 읽기 전용
// - var: 최초로 지정한 변수의 초깃값이 있더라도 값을 바꿀 수 있음

// * 자료형 추론
// 코틀린은 자료형을 지정하지 않고 변수를 선언하면 변수에 할당된 값을 보고 알아서 자료형을 지정할 수 있음
// 바로 이것을 '자료형을 추론한다'라고 함
// 단, 자료형을 지정하지 않은 변수는 반드시 자료형을 추론할 값을 지정해야 함

// * 코틀린은 참조형 자료형을 사용
// 자바에서 기본형과 참조형을 모두 사용하지만 코틀린에서는 참조형만 사용
// 참조형으로 선언한 변수는 성능 최적화를 위해 코틀린 컴파일러에서 다시 기본형으로 대체됨

fun main() {
    // 1. val - 읽기 전용 (재할당 불가)
    val name = "홍길동"
    println(name)

    // 2. var - 변경 가능 (재할당 가능)
    var age = 20
    age = 21
    println(age)

    // 3. 자료형 추론 - 자료형을 안 써도 값을 보고 컴파일러가 결정
    val a = 10
    val b = 10L
    val c = 10.5
    val d = 10.5f
    val e = 'A'
    val f = "A"
    val g = true

    // 추론된 자료형은 이렇게 알 수 있음
    println(a::class.simpleName)
    println(b::class.simpleName)
    println(c::class.simpleName)
    println(d::class.simpleName)
    println(e::class.simpleName)
    println(f::class.simpleName)
    println(g::class.simpleName)

    // 4. 자료형 직접 지정 - 변수명 뒤에 콜론(:)을 붙여 명시
    val score: Int = 100
    val height: Double = 100.3
    val message: String = "Hello World!"
    val isStudent: Boolean = true

    println("$score / $height / $message / $isStudent")

    val bigScore: Long = 100
    println(bigScore)

    // 5. 자료형을 지정하지 않으면 반드시 초깃값이 있어야 함
    // "var z"만 선언 불가: 컴파일 에러, 추론할 값이 없음
    val z: Int
    z = 5 // 나중에 딱 한 번만 할당 가능
    println(z)

    var w: String
    w = "처음"
    println(w)
    w = "나중" // 계속 바꿀 수 있음
    println(w)

    // 6. $ 기호로 문자열 출력하기 (문자열 템플릿)
    // 문자열 안에 $를 붙이면 변수의 값을 그 자리에 그대로 끼워 넣을 수 있음
    // 자바처럼 +로 일일이 이어 붙이지 않아도 되고, 읽기도 훨씬 편함
    val userName = "홍길동"
    val userAge = 20

    println("이름: $userName, 나이: ${userAge}살")
    // 한글도 '변수명에 쓸 수 있는 글자'라서 $userAge살 이라고 쓰면
    // 컴파일러가 'userAge살'이라는 변수를 찾다가 컴파일 에러를 냄
    // 변수 바로 뒤에 한글이 붙을 때는 반드시 ${변수명}처럼 중괄호로 끊어줘야 함

    // 6-1. ${ } - 표현식(연산, 함수 호출, 프로퍼티 접근)을 넣을 때는 중괄호로 감쌈
    println("내년 나이: ${userAge + 1}살") // 연산
    println("이름 길이: ${userName.length}글자") // 프로퍼티 접근
    println("대문자: ${"kotlin".uppercase()}") // 함수 호출
    println("성인인가? ${userAge >= 19}") // 비교 결과

    // 6-2. $ 기호 자체를 출력하고 싶을 때는 역슬래시로
    val price = 1000
    println("가격은 \$$price 입니다.")

    // 6-3. 여러줄 문자열 안에서도 똑같이 동작
    val profile = """
        === 회원 정보 ===
        이름: $userName
        나이: $userAge
        내년: ${userAge + 1}
    """.trimIndent() // 앞쪽 공통 들여쓰기 제거
    println(profile)
}