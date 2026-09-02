package part2

// 1. 클래스와 객체
// 2. 프로퍼티 - 클래스 안에 선언한 변수
// 3. 메서드

// 자바와의 차이점
// - new 키워드가 없음
// - getter/setter를 직접 쓰지 않음
// - 계산해서 돌려주는 값도 프로퍼티로 만듦
// - 접근 제어자를 생략하면 public
// - 한 파일에 클래스를 여러 개 둘 수 있고, 파일 이름과 클래스 이름이 달라도 됨

// 1. 프로퍼티와 메서드를 가진 클래스
// getter/setter를 직접 만들지 않음. 프로퍼티를 생성하면 자동으로 생김
// 프로퍼티는 선언할 때 초깃값이 있어야 함
// 나중에 넣어야 하면 lateinit 또는 null 허용 자료형(?)을 씀
class Person {
    var name: String = "홍길동"
    var age: Int = 0

    fun introduce() {
        println("안녕하세요, 저는 ${name}이고 ${age}살 입니다.")
    }
}

// 2. 커스텀 getter - 값을 저장하지 않고 읽을 때마다 계산하는 프로퍼티
class Member {
    var name: String = "이름없음"
    var age: Int = 0

    val isAdult: Boolean
        get() = age >= 19 // 저장 공간이 없고 getter만 있음
}

// 1. 객체 생성과 프로퍼티 접근
fun a_exam1() {
    val person = Person()
    println(person.name)

    person.name = "홍길순" // setter가 호출됨
    person.age = 20
    println("${person.name} is ${person.age}") // getter가 호출됨
}

fun a_exam2() {
    val member = Member()
    member.name = "홍길동"
    member.age = 20

    val member2 = Member()
    member2.name = "홍길순"
    member2.age = 15

    // isAdult는 저장된 값이 아니라, 읽을 때마다 age로 계산된 결과
    println("${member.name} 성인? ${member.isAdult}")
    println("${member2.name} 성인? ${member2.isAdult}")

    // isAdult는 getter만 있으므로 값을 직접 넣을 수 없음
//    member2.isAdult = false // 컴파일 에러
}

fun main() {
    a_exam1()
    a_exam2()
}