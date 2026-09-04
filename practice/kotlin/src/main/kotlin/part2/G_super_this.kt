package part2

// * this/super

// this  : 자기 자신(이 객체)을 가리킴
// super : 부모 클래스를 가리킴
// 둘 다 "누구의 것인지"가 헷갈릴 때 콕 집어 주는 역할을 함

// 1. this - 프로퍼티와 매개변수 이름이 같을 때
//   class User(name: String) {
//       val name: String
//       init {
//           this.name = name     // this.name은 프로퍼티, 그냥 name은 매개변수
//       }
//   }

// 2. this - 자기 자신을 돌려주기 (메서드 체이닝)
//   fun add(topping: String): Pizza {
//       ...
//       return this          // 자기 자신을 돌려줌
//   }
//   pizza.add("치즈").add("페퍼로니").add("올리브")     <- 점을 이어서 쓸 수 있음

// 3. this - 생성자에서 다른 생성자 부르기
//   constructor(title: String) : this(title, 10000)

// 4. super - 부모의 메서드/프로퍼티 부르기
//   override fun work() {
//       super.work()         // 부모의 work()를 먼저 실행하고
//       println("추가 동작")   // 자식 동작을 덧붙임
//   }

// 5. super - 부모 생성자 부르기
// * 주 생성자가 있을 때: 클래스 선언부에서 부모 생성자를 호출
//   class Manager(name: String) : Employee(name)
//
// * 주 생성자가 없을 때: 부 생성자에서 super(...)로 호출
//   class Intern : Employee {
//       constructor(name: String) : super(name)
//   }

// 6. super<타입> - 어느 부모인지 지정하기
// 인터페이스는 여러 개를 구현할 수 있어서, 같은 이름의 기본 구현이 겹칠 수 있음
// 이때는 꺾쇠로 어느 쪽 것인지 지정해야 함. 지정하지 않으면 컴파일 에러
//   super<Walkable>.move()
//   super<Swimmable>.move()

// 1) 프로퍼티와 매개변수 이름이 같은 경우
class User(name: String, age: Int) {
    val name: String
    val age: Int

    init {
        this.name = name        // 왼쪽은 프로퍼티, 오른쪽은 매개변수
        this.age = age
    }

    fun printInfo() {
        println("$name / ${age}살")
    }
}

// 2) 자기 자신을 돌려주는 메서드 (메서드 체이닝)
class Pizza {
    var toppings: String = ""

    fun add(topping: String): Pizza {
        toppings += "$topping "

        return this             // 자기 자신을 돌려줌
    }

    fun print() {
        println("토핑: $toppings")
    }
}

// 3) super로 부모의 것 부르기
open class Employee(val name: String) {
    open val role: String = "사원"

    open fun work() {
        println("${name}이(가) 일을 함")
    }
}

class Manager(name: String) : Employee(name) {
    override val role: String = "관리자"

    override fun work() {
        super.work()            // 부모의 work()를 먼저 실행
        println("${name}이(가) 팀을 관리")
        println("  자식 역할: $role / 부모의 역할: ${super.role}")
    }
}

// 4) 주 생성자 없이 부 생성자에서 super(...) 부르기
class Intern : Employee {
    constructor(name: String) : super(name) {
        println("  [생성] 인턴 $name 등록")
    }

    override fun work() {
        println("${name}이(가) 일을 배움")
    }
}

// 5) 같은 이름의 기본 구현이 겹치는 인터페이스
interface Walkable {
    fun move() {
        println("걸어서 이동")
    }
}

interface Swimmable {
    fun move() {
        println("헤엄쳐서 이동")
    }
}

class Duck : Walkable, Swimmable {
    // move()가 양쪽에 다 있으므로 반드시 재정의해야 함 (안 하면 컴파일 에러)
    override fun move() {
        super<Walkable>.move()      // 어느 쪽 것인지 지정
        super<Swimmable>.move()
        println("오리는 둘 다 할 수 있습니다.")
    }
}

// ------------------------------------------------------------
// 예제 1. this - 이름이 겹칠 때
// ------------------------------------------------------------
fun g_exam1() {
    val u = User("홍길동", 20)
    u.printInfo()                   // 홍길동 / 20살

    // init에서 this를 빼고 name = name이라고 쓰면
    // 왼쪽 name도 생성자 매개변수로 해석되어 값을 다시 대입할 수 없으므로 컴파일 에러가 발생함

    // 참고) 코틀린에서는 보통 이렇게 씀. this를 쓸 일 자체가 사라짐
    //   class User(val name: String, val age: Int)
}

// ------------------------------------------------------------
// 예제 2. this - 메서드 체이닝
// ------------------------------------------------------------
fun g_exam2() {
    val p = Pizza()

    // add()가 자기 자신을 돌려주므로 점을 이어서 쓸 수 있음
    p.add("치즈").add("페퍼로니").add("올리브")
    p.print()                       // 토핑: 치즈 페퍼로니 올리브

    // 한 줄로도 쓸 수 있음
    Pizza().add("불고기").add("고구마").print()
}

// ------------------------------------------------------------
// 예제 3. super - 부모의 메서드와 프로퍼티
// ------------------------------------------------------------
fun g_exam3() {
    val e = Employee("김사원")
    e.work()                        // 김사원이(가) 일을 함

    val m = Manager("박팀장")
    m.work()
    // 박팀장이(가) 일을 함        <- super.work()로 부른 부모의 동작
    // 박팀장이(가) 팀을 관리
    //   자식 역할: 관리자 / 부모의 역할: 사원

    // 재정의해서 덮어썼어도, super를 쓰면 부모의 원래 값과 동작을 그대로 볼 수 있음
    println(m.role)                 // 관리자 (밖에서는 재정의된 값만 보임)
}

// ------------------------------------------------------------
// 예제 4. super - 부모 생성자 부르기
// ------------------------------------------------------------
fun g_exam4() {
    // Manager는 주 생성자에서 부모 생성자를 부름: Employee(name)
    val m = Manager("박팀장")
    println(m.name)                 // 박팀장 (부모가 만든 프로퍼티)

    // Intern은 주 생성자가 없어서 부 생성자에서 super(name)으로 부름
    val i = Intern("이인턴")
    println(i.name)                 // 이인턴
    i.work()

    // 정리
    //   this(...)  -> 같은 클래스의 다른 생성자
    //   super(...) -> 부모 클래스의 생성자
}

// ------------------------------------------------------------
// 예제 5. super<타입> - 어느 부모인지 지정하기
// ------------------------------------------------------------
fun g_exam5() {
    val duck = Duck()
    duck.move()
    // 걸어서 이동
    // 헤엄쳐서 이동
    // 오리는 둘 다 할 수 있음

    // Walkable과 Swimmable에 똑같이 move()가 있으므로
    // Duck이 재정의하지 않으면 "어느 것을 쓸지 모르겠다"며 컴파일 에러가 남
    // super<Walkable>.move()처럼 꺾쇠로 지정해 주어야 함
}

fun main() {
    g_exam1()     // this - 이름이 겹칠 때
    g_exam2()     // this - 메서드 체이닝
    g_exam3()     // super - 부모의 메서드와 프로퍼티
    g_exam4()     // super - 부모 생성자 부르기
    g_exam5()     // super<타입> - 어느 부모인지 지정하기
}