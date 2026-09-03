package part2

// * 생성자 - 주 생성자, init 블록, 부 생성자

// 1. 주 생성자
// 클래스 이름 바로 뒤에 괄호를 붙여서 씀
// class Person(name: String, age: Int) {...}

// val/var을 붙이면 프로퍼티까지 한 번에 선언됨
// class Person(val name: String, var age: Int) {...}
// -> "자바의 필드 2개 + getter 2개, setter 2개"와 같음

// val/var를 아무것도 붙이지 않으면 프로퍼티가 되지 않고 생성자 안에서만 쓰는 값이 됨

// 2. init 블록
// 객체가 만들어질 때 실행할 코드를 담음. 주 생성자에는 코드를 넣을 자리가 없기 때문
// 값 검사나 초기 처리 같은 일은 init 블록에서 함
/*
class Person(val name: String) {
    init {
        println("${name} 생성됨")
    }
}
*/

// 3. 매개변수 기본값
// 생성자에도 기본값을 줄 수 있음
//      class Book(val title: String, val price: Int = 1000)

// 4. 부 생성자
// constructor 키워드로 생성자를 추가할 수 있음. 여러 개 만들 수 있음
// constructor(title: String) : this(title, 1000)

// 부 생성자는 반드시 this(...)로 주 생성자를 호출해야 함
// 어떤 생성자로 만들어도 init 블록은 항상 실행됨

// 1) 주 생성자로 프로퍼티까지 선언한 형태
class Student(val name: String, var grade: Int) {
    fun printInfo() {
        println("$name / ${grade}학년")
    }
}

// 2) init 블록 + 매개변수 기본값
class Book(val title: String, val price: Int = 10000) {
    init {
        println("[init] $title 객체가 생성되었음")
    }

    fun printInfo() {
        println("$title / ${price}원")
    }
}

// 3) 부 생성자
class Magazine(val title: String, val price: Int) {
    init {
        println("[init] $title / ${price}원")
    }

    constructor(title: String) : this(title, 10000)

    constructor() : this("제목없음", 0)

    fun printInfo() {
        println("$title / ${price}원")
    }
}

// ------------------------------------------------------------
// 예제 1. 주 생성자
// ------------------------------------------------------------
fun b_exam1() {
    // 객체를 만들면서 값까지 정해짐
    val s = Student("홍길동", 3)
    println(s.name)                     // 홍길동
    s.printInfo()                       // 홍길동 / 3학년

    // name은 val이라 바꿀 수 없고, grade는 var이라 바꿀 수 있음
    // s.name = "김철수"                 // 컴파일 에러! Val cannot be reassigned
    s.grade = 4
    s.printInfo()                       // 홍길동 / 4학년

    // 이름 붙인 인자도 그대로 쓸 수 있음
    val s2 = Student(grade = 1, name = "김철수")
    s2.printInfo()                      // 김철수 / 1학년
}

// ------------------------------------------------------------
// 예제 2. init 블록과 기본값
// ------------------------------------------------------------
fun b_exam2() {
    // 객체가 만들어지는 순간 init 블록이 실행됨
    val b1 = Book("코틀린 입문", 25000)
    b1.printInfo()

    // price를 생략하면 기본값 10000이 쓰임
    val b2 = Book("자바 입문")
    b2.printInfo()
}

// ------------------------------------------------------------
// 예제 3. 부 생성자
// ------------------------------------------------------------
fun b_exam3() {
    // 주 생성자로 만들기
    val m1 = Magazine("코틀린 매거진", 8000)
    m1.printInfo()

    // 부 생성자로 만들기 - 제목만 넘기면 가격은 10000이 됨
    val m2 = Magazine("자바 매거진")
    m2.printInfo()

    // 인자가 아예 없는 부 생성자
    val m3 = Magazine()
    m3.printInfo()

    // 부 생성자가 this(...)로 주 생성자를 부르므로 init 블록도 항상 실행됨

    // 참고) 위 Magazine의 부 생성자 두 개는 사실 기본값 하나로 대체할 수 있음
    //   class Magazine(val title: String = "제목없음", val price: Int = 10000)
}

fun main() {
    b_exam1()     // 주 생성자
    b_exam2()     // init 블록과 기본값
    b_exam3()     // 부 생성자
}