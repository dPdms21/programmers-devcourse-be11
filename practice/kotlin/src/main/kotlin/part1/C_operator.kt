package part1

// * 산술 연산자: +  -  *  /  %
//   더하기, 빼기, 곱하기, 나누기, 나머지를 구함
//   정수끼리 나누면 소수점 이하가 잘리고 정수만 남는다는 점을 주의해야 함

// * 대입 연산자: =  +=  -=  *=  /=  %=
//   오른쪽 값을 왼쪽 변수에 넣음. +=, -=처럼 산술 연산자와 결합한 것을 복합 대입 연산자라고 함
//   값을 바꾸는 것이므로 반드시 var로 선언한 변수에만 쓸 수 있음

// * 증가 연산자(++), 감소 연산자(--)
//   변수의 값을 1 늘리거나 1 줄임
//   변수 앞에 붙이면(전위) 값을 먼저 바꾸고 사용하고, 뒤에 붙이면(후위) 값을 먼저 쓰고 나중에 바꿈

// * 비교 연산자: >  <  >=  <=  ==  !=
//   두 값을 비교해 그 결과를 true 또는 false(Boolean)로 돌려줌

// * 논리 연산자: &&(그리고) ||(또는) !(부정)
//   Boolean 값을 조합해 다시 하나의 Boolean을 만듦
//   &&, ||는 결과가 이미 정해지면 오른쪽을 아예 계산하지 않음. 이를 '단락 평가'라고 함

fun main() {
    // 1. 산술 연산자   +  -  *  /  %
    val a = 7
    val b = 3

    println("$a + $b = ${a + b}")       // 10   더하기
    println("$a - $b = ${a - b}")       // 4    빼기
    println("$a * $b = ${a * b}")       // 21   곱하기
    println("$a / $b = ${a / b}")       // 2    나누기 (소수점 이하 버림!)
    println("$a % $b = ${a % b}")       // 1    나머지

    // 주의! 정수끼리 나누면 결과도 정수. 2.333...이 아니라 2가 됨
    // 소수점까지 구하려면 피연산자 중 하나가 실수여야 함
    println(7 / 2)                      // 3     Int / Int -> Int
    println(7.0 / 2)                    // 3.5   Double / Int -> Double
    println(7 / 2.0)                    // 3.5
    println(a.toDouble() / b)           // 2.3333333333333335

    // % (나머지)는 짝수/홀수 판별, 배수 판별에 자주 씀
    println("10 은 짝수인가? ${10 % 2 == 0}")   // true
    println("11 은 짝수인가? ${11 % 2 == 0}")   // false

    // 0으로 나누기
    // println(7 / 0)                   // 실행 중 ArithmeticException! (정수는 예외 발생)
    println(7.0 / 0)                    // Infinity   (실수는 예외 대신 무한대)

    // 문자열에서의 +는 '이어 붙이기'가 됨. 같은 기호라도 자료형에 따라 동작이 다름
    println("코틀린" + "공부")            // 코틀린공부
    println("점수: " + 100)              // 점수: 100

    // 2. 대입 연산자   =  +=  -=  *=  /=  %=
    var num = 10
    println("시작: $num")

    num = 20            // =: 오른쪽 값을 그대로 넣음
    println("num = 20      -> $num")

    num += 5            // num = num + 5와 같음
    println("num += 5      -> $num")     // 25

    num -= 10           // num = num - 10
    println("num -= 10     -> $num")     // 15

    num *= 2            // num = num * 2
    println("num *= 2      -> $num")     // 30

    num /= 4            // num = num / 4  (정수 나눗셈이라 7.5가 아니라 7)
    println("num /= 4      -> $num")     // 7

    num %= 3            // num = num % 3
    println("num %= 3      -> $num")     // 1

    // val은 값을 바꿀 수 없으므로 복합 대입 연산자도 쓸 수 없음
    val fixed = 10
    // fixed += 1       // 컴파일 에러! Val cannot be reassigned

    // 문자열에도 +=를 쓸 수 있음
    var text = "안녕"
    text += "하세요"
    println(text)                        // 안녕하세요

    // 3. 증가 연산자(++) / 감소 연산자(--)
    var count = 10

    count++             // count = count + 1과 같음
    println("count++ 후: $count")        // 11

    count--             // count = count - 1과 같음
    println("count-- 후: $count")        // 10

    // 3-1. 전위(++변수): 값을 먼저 1 늘리고, 그 늘어난 값을 사용
    var x = 10
    println("++x 의 결과: ${++x}")        // 11  (먼저 11로 만들고 사용)
    println("사용 후 x  : $x")            // 11

    // 3-2. 후위(변수++): 지금 값을 먼저 사용하고, 그 다음에 1 늘림
    var y = 10
    println("y++ 의 결과: ${y++}")        // 10  (10을 먼저 쓰고 나서 11로 바뀜)
    println("사용 후 y  : $y")            // 11

    // 결과만 놓고 보면 x와 y는 똑같이 11이지만,
    // '그 줄에서 사용된 값'이 11이냐 10이냐가 다름. 이것이 전위와 후위의 차이

    // 4. 비교 연산자   >  <  >=  <=  ==  !=
    val p = 10
    val q = 20

    println("$p > $q  -> ${p > q}")      // false
    println("$p < $q  -> ${p < q}")      // true
    println("$p >= 10 -> ${p >= 10}")    // true
    println("$p <= 9  -> ${p <= 9}")     // false
    println("$p == $q -> ${p == q}")     // false  값이 같은가?
    println("$p != $q -> ${p != q}")     // true   값이 다른가?

    // 비교 결과는 Boolean이므로 변수에 담아둘 수 있음
    val isBigger: Boolean = q > p
    println("q 가 더 큰가? $isBigger")     // true

    // 문자열도 ==로 내용을 비교 (자바의 equals()에 해당)
    println("kotlin" == "kotlin")        // true
    println("kotlin" == "Kotlin")        // false  대소문자를 구분

    // 참고) ==는 값 비교, ===는 같은 객체인지(주소) 비교

    // ------------------------------------------------------------
    // 5. 논리 연산자   &&(AND)  ||(OR)  !(NOT)
    // ------------------------------------------------------------
    val t = true
    val f = false

    // &&: 둘 다 true여야 true
    println("true  && true  -> ${t && t}")      // true
    println("true  && false -> ${t && f}")      // false
    println("false && false -> ${f && f}")      // false

    // ||: 하나라도 true면 true
    println("true  || false -> ${t || f}")      // true
    println("false || false -> ${f || f}")      // false

    // !: true와 false를 뒤집음
    println("!true  -> ${!t}")                  // false
    println("!false -> ${!f}")                  // true

    // 비교 연산자와 조합해서 조건을 여러 개 묶는 것이 실제 사용법
    val age = 25
    val hasTicket = true

    println("성인이면서 표가 있는가? ${age >= 19 && hasTicket}")           // true
    println("미성년이거나 표가 없는가? ${age < 19 || !hasTicket}")          // false
    println("20대인가? ${age >= 20 && age <= 29}")                        // true

    // 5-1. 단락 평가 (Short-circuit)
    // &&는 왼쪽이 false면 오른쪽을 아예 계산하지 않음 (이미 false로 확정)
    // ||는 왼쪽이 true면 오른쪽을 아예 계산하지 않음 (이미 true로 확정)
    var check = 0

    val r1 = false && (check++ > 0)      // 왼쪽이 false -> 오른쪽 실행 안 됨
    println("r1 = $r1, check = $check")  // r1 = false, check = 0  (증가하지 않음!)

    val r2 = true || (check++ > 0)       // 왼쪽이 true -> 오른쪽 실행 안 됨
    println("r2 = $r2, check = $check")  // r2 = true, check = 0

    val r3 = true && (check++ > -1)      // 왼쪽이 true -> 오른쪽까지 실행됨
    println("r3 = $r3, check = $check")  // r3 = true, check = 1  (증가!)

    // 6. 연산자 우선순위
    // 높음  ->  낮음
    //   1) ++  --  !            (단항)
    //   2) *  /  %
    //   3) +  -
    //   4) >  <  >=  <=
    //   5) ==  !=
    //   6) &&
    //   7) ||
    //   8) =  +=  -=  *=  /=  %=
    println(2 + 3 * 4)                   // 14   (곱하기가 먼저)
    println((2 + 3) * 4)                 // 20   괄호로 순서를 바꿀 수 있음
    println(1 + 2 > 2 && 3 > 1)          // true (산술 -> 비교 -> 논리 순서로 계산)
}