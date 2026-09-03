// * 상속 - open, override, super

// 1. 코틀린의 클래스 기본이 '상속 금지'
// 자바에서는 아무 클래스나 extends 할 수 있고, 막으려면 final을 붙여야 함
// 코틀린은 반대. 기본이 final이고, 상속을 허용하려면 open을 붙여야 함

// class Animal {} // 상속 불가
// open class Animal {} // 상속 가능

// 메서드와 프로퍼티도 마찬가지. open을 붙인 것만 자식이 재정의할 수 있음
// -> "상속을 염두에 두고 설계한 것만 상속하라"는 의도

// 2. 상속하기 - 콜론(:)
// class Dog : Animal() {}
// 부모에 주 생성자가 있으면 여기서 부모의 생성자를 호출해야 하므로 괄호가 붙음

// 3. 오버라이딩 - override 키워드
// 부모: open fun sound() {}
// 자식: override fun sound() {} -> override는 생략할 수 없음

// super로 부모의 것을 부를 수 있음
// override fun sound() {
//      super.sound()
//      println("추가 동작")
// }

// 프로퍼티도 오버라이드할 수 있음. 부모에 open val ..로 선언되어 있어야 함

// 4. Any - 모든 클래스의 최상위 부모
// 아무것도 상속하지 않으면 모든 클래스는 자동으로 Any를 상속 (자바의 Object)
// toString(), equals(), hashCode()

open class Animal(val name: String) {
    open fun sound() {
        println("$name: ...")
    }

    fun sleep() {
        println("$name sleep")
    }

    open val legs: Int = 4

    override fun toString(): String {
        return "Animal(name=$name)"
    }
}

class Dog(name: String) : Animal(name) {
    override fun sound() {
        println("$name: 멍멍!")
    }
}

class Cat(name: String) : Animal(name) {
    override fun sound() {
        super.sound()
        println("$name: 야옹!")
    }
}

class Bird(name: String) : Animal(name) {
    override val legs: Int = 2

    override fun sound() {
        println("$name: 짹짹!")
    }
}

// ------------------------------------------------------------
// 예제 1. 상속 기본 - 물려받은 것 사용하기
// ------------------------------------------------------------
fun c_exam1() {
    val dog = Dog("바둑이")

    // 부모에서 물려받은 프로퍼티
    println(dog.name)               // 바둑이
    println(dog.legs)               // 4 (부모의 값 그대로)

    // 부모에서 물려받은 메서드. Dog에는 sleep()이 없지만 쓸 수 있음
    dog.sleep()                     // 바둑이 sleep

    // 자식에서 재정의한 메서드
    dog.sound()                     // 바둑이: 멍멍!
}

// ------------------------------------------------------------
// 예제 2. 오버라이딩과 super
// ------------------------------------------------------------
fun c_exam2() {
    val animal = Animal("동물")
    val dog = Dog("바둑이")
    val cat = Cat("나비")

    // 같은 이름의 메서드지만 클래스마다 동작이 다름
    animal.sound()                  // 동물: ...
    dog.sound()                     // 바둑이: 멍멍!

    // Cat은 super.sound()로 부모 것을 먼저 부름. 두 줄이 출력됨
    cat.sound()                     // 나비: ...  /  나비: 야옹!

    // open이 없는 sleep()은 재정의할 수 없으므로 어느 클래스든 동작이 같음
    dog.sleep()
    cat.sleep()
}

// ------------------------------------------------------------
// 예제 3. 프로퍼티 오버라이드와 toString
// ------------------------------------------------------------
fun c_exam3() {
    val dog = Dog("바둑이")
    val bird = Bird("짹짹이")

    println("${dog.name} 다리: ${dog.legs}개")      // 4개 (부모 값)
    println("${bird.name} 다리: ${bird.legs}개")    // 2개 (재정의한 값)
    bird.sound()                                   // 짹짹이: 짹짹!

    // Animal에서 toString()을 재정의했으므로, 자식들도 그것을 물려받음
    println(dog)                                   // Animal(name=바둑이)
    println(bird)                                  // Animal(name=짹짹이)
    // 재정의하지 않았다면 Dog@1b6d3586 같은 알아보기 힘든 값이 나옴
}

fun main() {
    c_exam1()     // 상속 기본
    c_exam2()     // 오버라이딩과 super
    c_exam3()     // 프로퍼티 오버라이드와 toString
}