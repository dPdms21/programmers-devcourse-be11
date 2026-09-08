package part3

// * data class - 값을 담는 클래스

// 1. 모든 클래스는 Any를 상속
// Any에는 equals(), hashCode(), toString()이 있음
// Any가 만들어 둔 기본 동작
// - equals(other): this === other '같은 객체인가(주소 비교)'
// - hashCode(): 객체마다 다른 번호
// - toString(): 클래스이름@주소
// 그래서 내용이 똑같은 객체 두 개를 만들어도 ==는 false
// Set과 Map은 hashcode로 칸을 정하고 equals로 확인하므로 중복 제거도 되지 않음

// 2. data class가 자동으로 만들어 주는 것
// class 앞에 data를 붙이면, 주 생성자에 선언한 프로퍼티를 기준으로 다섯 가지가 생김
//     equals()        모든 프로퍼티가 같으면 true
//     hashCode()      모든 프로퍼티로 계산한 번호 (내용이 같으면 번호도 같음)
//     toString()      BMember(name=김철수, age=20)
//     copy()          일부만 바꾼 새 객체를 만듦
//     component1(), component2(), ...   구조 분해에 쓰임

// 3. 특징
//   - 주 생성자에 프로퍼티가 최소 하나는 있어야 함
//   - 주 생성자의 프로퍼티는 val 또는 var여야 함
//   - '주 생성자에 선언한 것'만 equals/hashCode/toString/copy에 들어감
//     클래스 본문(중괄호 안)에 선언한 프로퍼티는 무시됨
//   - var 프로퍼티를 Set이나 Map 키로 쓰면 위험
//     넣은 뒤에 값을 바꾸면 hashCode가 달라져서 다시는 찾지 못함
//     -> 키로 쓸 data class는 val로만 만드는 것이 안전

// 4. 언제 쓰나
//   "값을 담아 나르는 것이 주된 일"인 클래스에 씀. 회원, 상품, 주문, 좌표, 응답 결과 ...
//   반대로 동작이 중심인 클래스(MemberManager, MemberApp 같은)는 보통 클래스로 둠

// 보통 클래스
class BPlainMember(val name: String, val email: String)

// data class
data class BMember(val name: String, val email: String, var age: Int = 0)

// 본문에 선언한 프로퍼티는 equals가 들어가지 않음
data class BBook(val title: String) {
    var price: Int = 0
}

// 1. 보통 클래스와 data class
fun b_exam1() {
    val p1 = BPlainMember("김철수", "kim@a.com")
    val p2 = BPlainMember("김철수", "kim@a.com")

    println(p1 == p2)
    println(p1)

    val d1 = BMember("김철수", "kim@a.com")
    val d2 = BMember("김철수", "kim@a.com")

    println(d1 == d2)
    println(d1)
    println(d1.hashCode() == d2.hashCode())

    println(d1 == d2)  // equals 값비교
    println(d1 === d2) // 주소비교

    val d3 = d2
    println(d3 === d2) // true
}

// 2. data class와 컬렉션
fun b_exam2() {
    val list = listOf(
        BMember("김철수", "kim@a.com"),
        BMember("이영희", "lee@a.com"),
        BMember("김철수", "kim@a.com")       // 위와 내용이 같은 회원
    )

    // 2-1. contains / indexOf가 '내용'으로 동작
    println(list.contains(BMember("이영희", "lee@a.com")))      // true
    println(list.indexOf(BMember("김철수", "kim@a.com")))       // 0
    println(BMember("이영희", "lee@a.com") in list)             // true
    // 보통 클래스였다면 전부 false / -1이 나옴

    // 2-2. 중복 제거가 됨
    println(list.distinct().size)               // 2
    println(list.toSet().size)                  // 2

    // 2-3. Map의 키로 쓸 수 있음
    val visits = mutableMapOf<BMember, Int>()
    visits[BMember("김철수", "kim@a.com")] = 5
    println(visits[BMember("김철수", "kim@a.com")])              // 5   제대로 찾음

    // 2-4. 리스트끼리 비교도 내용으로 됨
    val a = listOf(BMember("김철수", "kim@a.com"))
    val b = listOf(BMember("김철수", "kim@a.com"))
    println(a == b)                             // true

    // 2-5. 출력이 읽을 만해짐. 디버깅할 때 이것만으로도 큰 이득
    println(list)
}

// 3. copy - 일부만 바꾼 새 객체
fun b_exam3() {
    val origin = BMember("김철수", "kim@a.com", 20)

    // 이름만 바꾼 새 객체
    val renamed = origin.copy(name = "김영수")
    println(origin)                 // BMember(name=김철수, email=kim@a.com, age=20)
    println(renamed)                // BMember(name=김영수, email=kim@a.com, age=20)

    // 인자를 하나도 안 주면 그대로 복사한 새 객체가 나옴
    val cloned = origin.copy()
    println(cloned == origin)       // true    내용은 같고
    println(cloned === origin)      // false   객체는 다름

    // copy는 '고치는' 대신 '새로 만드는' 방식
    // 원본이 그대로 남으므로, 여러 곳에서 같은 객체를 공유해도 안전
    val members = listOf(
        BMember("김철수", "kim@a.com", 20),
        BMember("이영희", "lee@a.com", 30)
    )

    // 모두의 나이를 한 살씩 올린 '새 리스트'를 만듦. 원본은 건드리지 않음
    val aged = members.map { it.copy(age = it.age + 1) }
    println(members)                // 나이 20, 30
    println(aged)                   // 나이 21, 31

    // 주의! copy는 얕은 복사. 안에 든 컬렉션은 원본과 공유
    // (A_collection 예제 5-3의 얕은 복사와 같은 이야기)
}

// 4. 구조분해
fun b_exam4() {
    val member = BMember("김철수", "kim@a.com", 20)

    // 주 생성자에 선언한 순서대로 쪼개짐
    val (name, email, age) = member
    println("$name, $email, $age")

    // 필요 없는 것은 밑줄로 건너뜀
    val (n, _, a) = member
    println("$n, $a")

    // 반복문에서 바로 쪼개기
    val members = listOf(
        BMember("김철수", "kim@a.com", 20),
        BMember("이영희", "lee@a.com", 30)
    )

    for ((n, e, a) in members) {
        println("$n, $a - $e")
    }

    // Pair와 Triple도 같은 원리로 쪼개짐 -> 둘 다 data class로 만들어져 있음
    val pair = "사과" to 1000
    val (fruit, price) = pair
    println("$fruit, $price")

    val tri = Triple(1, "둘", 3.0)
    val (first, second, third) = tri
    println("$first - $second - $third")

    for ((k, v) in mapOf("a" to 1)) println("$k=$v")
}

// 5. 본문에 선언한 프로퍼티는 빠짐
fun b_exam5() {
    val b1 = BBook("코틀린 입문")
    val b2 = BBook("코틀린 입문")

    b1.price = 1000
    b2.price = 2000

    println(b1 == b2)
    println(b1)
    println(setOf(b1, b2).size)

    // 비교에 넣고 싶다면 주 생성자로 올려야 함
}

// 6. var 프로퍼티를 키로 쓰면
fun b_exam6() {
    val member = BMember("김철수", "kim@a.com", 20)
    val set = mutableSetOf(member)

    println(set.contains(member))       // true    잘 찾음

    // 그런데 넣은 뒤에 값을 바꾸면?
    member.age = 21                     // age는 var

    println(set.contains(member))       // false !!   방금 넣은 그 객체인데 못 찾음
    println(set)                        // [BMember(name=김철수, email=kim@a.com, age=21)]
    println(set.size)                   // 1          들어는 있음

    // 왜 이럴까
    //   Set은 넣을 때의 hashCode로 칸을 정해 놓았음
    //   age가 바뀌면서 hashCode가 달라졌고, 이제는 엉뚱한 칸을 찾아보게 됨
    //   지우지도 못함. 사실상 잃어버린 값이 됨 (자바 HashSet에서도 똑같이 생기는 문제)

    // 해결
    //   Set이나 Map 키로 쓸 data class는 프로퍼티를 전부 val로 만듦
    //   값을 바꿔야 하면 copy로 새 객체를 만들어 다시 넣음
    val safe = BMember("이영희", "lee@a.com", 30)
    val set2 = mutableSetOf(safe)
    set2.remove(safe)
    set2.add(safe.copy(age = 31))       // 뺐다가 새로 넣음
    println(set2)
}

// 7. data class를 쓰면 안 되는 자리
fun b_exam7() {
    // 7-1. 값이 아니라 '역할'이 중심인 클래스
    //   MemberManager, MemberApp, ArrayMemberStorage 같은 것은 data class로 만들지 않음
    //   두 관리자가 "내용이 같으니 같은 것"이라고 판단될 이유가 없음

    // 7-2. 상속 관계가 필요한 클래스
    //   data class는 open으로 만들 수 없음. 상속받을 수 없다는 뜻
    //   part-2의 Member(추상 클래스) - VipMember처럼 계층이 필요하면 보통 클래스를 씀

    // 7-3. 프로퍼티가 많고, 그중 일부만 비교 기준일 때
    //   자동 생성되는 equals는 '전부'를 비교. 원하는 것이 아니면 직접 만들어야 함

    // 참고) equals/hashCode를 직접 쓰면 이런 모양. data 한 단어가 이것을 대신
    val m1 = BCustomMember("김철수", "kim@a.com")
    val m2 = BCustomMember("김철수", "kim@a.com")
    println(m1 == m2)                       // true
    println(setOf(m1, m2).size)             // 1
}

// 직접 만든 equals/hashCode - 이메일만 같으면 같은 회원으로 봄
class BCustomMember(val name: String, val email: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true                 // 같은 객체면 볼 것도 없음
        if (other !is BCustomMember) return false       // 자료형이 다르면 다름

        return email == other.email                     // 이메일만 비교
    }

    // equals를 고쳤으면 hashCode도 반드시 함께 고쳐야 함
    // "equals가 true면 hashCode도 같아야 한다"는 것이 규칙
    // 이 규칙을 어기면 Set과 Map이 조용히 잘못 동작 (자바와 같은 계약)
    override fun hashCode(): Int = email.hashCode()

    override fun toString(): String = "BCustomMember($name, $email)"
}

fun main() {
    b_exam1()
    b_exam2()
    b_exam3()
    b_exam4()
    b_exam5()
    b_exam6()
    b_exam7()
}