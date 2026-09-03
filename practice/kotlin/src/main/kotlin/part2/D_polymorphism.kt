package part2

// 다형성 - 업캐스팅, 동적 바인딩, is/as

// 다형성은 "부모 타입 하나로 여러 자식을 똑같이 다루는 것"
// 부르는 쪽은 실제 객체의 구체적인 타입을 몰라도 되고, 각 객체에 맞는 동작이 실행됨

// 1. 업캐스팅 - 자식 객체를 부모 타입에 담기
// val s: Shape = Circle(5.0)
// 부모에 없는 자식만의 기능을 쓸 수 없게 됨
// Shape 타입으로 담는 순간 Circle 고유의 메서드는 보이지 않음

// 2. 동적 바인딩 - 실제로 실행되는 것은 '객체'의 메서드
// val s: Shape = Circle(5.0)
// s.area() // Shape의 area가 아닌 Circle의 area가 실행됨

// 같은 코드 한 줄이 객체에 따라 다르게 동작함. 이것이 다형성의 핵심

// 3. 다형성을 쓰는 이유
// 도형이 늘어날 때마다 when 갈래를 추가하는 대신, 새 클래스만 만들면 됨
//
//   (다형성 없이)              (다형성으로)
//   when (도형종류) {           for (shape in shapes) {
//       "원" -> ...                shape.area()
//       "사각형" -> ...          }
//       "삼각형" -> ...          <- 도형이 늘어도 이 코드는 그대로
//   }

// 4. 다시 자식 타입으로 - is와 as
//   if (shape is Circle) { shape.radius }     // is로 검사하면 스마트 캐스트가 됨
//   val c = shape as Circle                   // 실패하면 예외
//   val c = shape as? Circle                  // 실패하면 null

// 도형의 공통 틀. 자식들이 각자 넓이 구하는 방법을 재정의
open class Shape(val shapeName: String) {
    // 부모는 넓이를 구할 방법이 없으므로 일단 0.0을 돌려줌
    open fun area(): Double = 0.0

    fun describe() {                    // 공통 기능은 부모에 한 번만 작성
        println("${shapeName}의 넓이: ${area()}")
    }
}

class Circle(val radius: Double) : Shape("원") {
    override fun area(): Double = 3.14 * radius * radius

    // Circle에만 있는 메서드
    fun printRadius() {
        println("반지름: $radius")
    }
}

class Rectangle(val width: Double, val height: Double) : Shape("사각형") {
    override fun area(): Double = width * height
}

class Triangle(val base: Double, val height: Double) : Shape("삼각형") {
    override fun area(): Double = base * height / 2
}

// ------------------------------------------------------------
// 예제 1. 업캐스팅 - 부모 타입에 자식 객체 담기
// ------------------------------------------------------------
fun d_exam1() {
    // 변수의 타입은 Shape이지만, 실제로 들어 있는 것은 Circle
    val s: Shape = Circle(5.0)
    println(s.shapeName)            // 원
    s.describe()                    // 원의 넓이: 78.5

    // 부모 타입으로 담으면 자식만의 기능은 보이지 않음
    // s.printRadius()              // 컴파일 에러! Shape에는 printRadius()가 없음
    // s.radius                     // 컴파일 에러!

    // 처음부터 Circle 타입으로 받으면 당연히 쓸 수 있음
    val c = Circle(5.0)
    c.printRadius()                 // 반지름: 5.0
}

// ------------------------------------------------------------
// 예제 2. 동적 바인딩 - 같은 호출, 다른 결과
// ------------------------------------------------------------
fun d_exam2() {
    val s1: Shape = Circle(5.0)
    val s2: Shape = Rectangle(4.0, 3.0)
    val s3: Shape = Triangle(6.0, 4.0)

    // 세 변수 모두 타입은 Shape이지만, 실제 객체에 맞는 area()가 실행됨
    println(s1.area())              // 78.5  Circle의 area()
    println(s2.area())              // 12.0  Rectangle의 area()
    println(s3.area())              // 12.0  Triangle의 area()

    // describe()는 Shape에 한 번만 작성했지만,
    // 그 안에서 부르는 area()는 각 객체의 것이 실행됨
    s1.describe()
    s2.describe()
    s3.describe()
}

// ------------------------------------------------------------
// 예제 3. 목록에 담아 한꺼번에 다루기 - 다형성의 진짜 쓸모
// ------------------------------------------------------------
fun d_exam3() {
    // 서로 다른 클래스의 객체들을 하나의 목록에 담을 수 있음
    val shapes: List<Shape> = listOf(
        Circle(5.0),
        Rectangle(4.0, 3.0),
        Triangle(6.0, 4.0),
        Circle(1.0)
    )

    // 무엇이 들었는지 신경 쓰지 않고 똑같이 다룸
    for (shape in shapes) {
        shape.describe()
    }

    // 넓이의 합 구하기
    var total = 0.0

    for (shape in shapes) {
        total += shape.area()
    }
    println("전체 넓이의 합: $total")

    // 도형을 하나 더 추가하고 싶다면? 새 클래스를 만들고 목록에 넣기만 하면 됨
    // 위 for문은 한 글자도 고칠 필요가 없음
}

// ------------------------------------------------------------
// 예제 4. is와 as - 다시 자식 타입으로 다루기
// ------------------------------------------------------------
fun d_exam4() {
    val shapes: List<Shape> = listOf(Circle(5.0), Rectangle(4.0, 3.0), Circle(2.0))

    for (shape in shapes) {
        // is로 검사하면 그 블록 안에서는 Circle로 자동 인식됨 (스마트 캐스트)
        if (shape is Circle) {
            println("원 발견! 반지름 ${shape.radius}")
            shape.printRadius()     // Circle의 고유 메서드도 쓸 수 있음
        } else {
            println("${shape.shapeName}은(는) 원이 아닙니다.")
        }
    }

    // when으로 쓰면 더 깔끔
    for (shape in shapes) {
        val info = when (shape) {
            is Circle -> "원 (반지름 ${shape.radius})"
            is Rectangle -> "사각형 (${shape.width} x ${shape.height})"
            is Triangle -> "삼각형 (밑변 ${shape.base})"
            else -> "알 수 없는 도형"
        }

        println(info)
    }

    // as?로 안전하게 변환하기
    val first: Shape = shapes[0]
    val circle: Circle? = first as? Circle
    println(circle?.radius)         // 5.0

    val second: Shape = shapes[1]
    val notCircle: Circle? = second as? Circle
    println(notCircle?.radius)      // null (변환에 실패해도 예외가 나지 않음)
}

fun main() {
    d_exam1()     // 업캐스팅
    d_exam2()     // 동적 바인딩
    d_exam3()     // 목록에 담아 한꺼번에 다루기
    d_exam4()     // is와 as
}