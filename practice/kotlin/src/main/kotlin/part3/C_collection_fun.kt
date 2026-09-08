package part3

// * 컬렉션 함수 - 순회, 집계, 변환, 정렬, 그룹화

// 1. 자바 스트림과 무엇이 다른가
//   [자바]                                               [코틀린]
//   list.stream().filter(..).map(..).collect(toList())  list.filter { }.map { }
//   list.stream().count()                               list.count()
//   list.stream().mapToInt(X::getA).sum()               list.sumOf { it.a }
//   list.stream().anyMatch(..)                          list.any { }
//   list.stream().findFirst()      -> Optional<T>       list.firstOrNull()   -> T?
//   list.stream().max(cmp).get()                        list.maxByOrNull { }
//   Collectors.groupingBy(..)                           list.groupBy { }
//   Collectors.partitioningBy(..)                       list.partition { }
//   Collectors.joining(", ")                            list.joinToString(", ")
//   Collectors.toMap(k, v)                              list.associate { k to v }
//
//   차이 세 가지
//   (1) stream()과 collect()가 없음. 컬렉션에 확장 함수로 직접 붙어 있고 결과가 바로 List
//   (2) Optional이 없음. 그 자리를 T?와 OrNull 계열 함수가 대신
//   (3) 스트림은 지연 계산이라 중간 컬렉션을 만들지 않지만, 코틀린 컬렉션 함수는
//       단계마다 새 리스트를 하나씩 만듦. 자바 스트림에 해당하는 것은 시퀀스 -> D_sequence

// 2. 이름 규칙 세 가지
//   (1) OrNull       비어 있거나 못 찾았을 때 예외 대신 null을 돌려줌. Optional 자리
//                    max() / maxOrNull(),  first() / firstOrNull()
//                    확신이 없으면 OrNull을 씀
//
//   (2) By 와 Of     By는 '원소'를, Of는 '계산한 값'을 돌려줌
//                    maxByOrNull { it.score } -> Student
//                    maxOfOrNull { it.score } -> Int
//
//   (3) ed 접미사    ed가 붙으면 새것을 만들고, 없으면 원본을 고침 (MutableList만)
//                   sorted() / sort(),  reversed() / reverse(),  shuffled() / shuffle()

data class Student(
    val name: String,
    val grade: Int,         // 학년
    val score: Int,         // 점수
    val club: String        // 동아리
)

val c_students = listOf(
    Student("김철수", 1, 85, "축구"),
    Student("이영희", 2, 92, "독서"),
    Student("박민수", 1, 78, "축구"),
    Student("최지우", 3, 95, "독서"),
    Student("정하늘", 2, 64, "밴드"),
    Student("강바다", 3, 88, "밴드")
)

val c_scores = listOf(85, 92, 78, 95, 64, 88)

// 정렬 기준을 클래스가 직접 갖는 경우 - 자바의 Comparable과 같음
data class CProduct(val name: String, val price: Int) : Comparable<CProduct> {
    override fun compareTo(other: CProduct): Int = price - other.price
}

// ------------------------------------------------------------
// 예제 1. 순회와 이어 붙이기
// ------------------------------------------------------------
fun c_exam1() {
    // stream() 없이 바로 부르고, collect() 없이 바로 List가 나옴
    println(c_students.filter { it.grade >= 2 }.map { it.name })

    // forEach. 이름을 생략하면 it이 됨
    c_scores.forEach { print("$it ") }
    println()
    c_students.forEachIndexed { i, s -> print("$i:${s.name} ") }
    println()

    // forEach 안에서는 break / continue를 쓸 수 없음  <- 자바 스트림과 같은 제약
    // 중간에 멈춰야 하면 for문이나 first { } / takeWhile { }을 씀

    // onEach는 forEach와 같은 일을 하면서 컬렉션 자신을 돌려줌. 중간에 끼워 보기 좋음
    println(c_scores.onEach { print("확인: $it ") }.filter { it >= 85 })

    // 이어 붙이기 - 읽는 순서와 실행 순서가 같음
    val result = c_students
        .filter { it.grade >= 2 }
        .filter { it.score >= 80 }
        .sortedByDescending { it.score }
        .map { it.name }
    println(result)                                 // [최지우, 이영희, 강바다]

    // 순서가 중요. filter를 앞에 두면 map이 처리할 개수가 줄어듦
    // 그리고 단계마다 새 리스트가 하나씩 만들어짐 (여기서는 4개) -> D_sequence
}

// ------------------------------------------------------------
// 예제 2. 집계
// ------------------------------------------------------------
fun c_exam2() {
    // 2-1. 개수와 합계
    println(c_scores.count { it >= 85 })                // 4    조건에 맞는 것만
    println(c_scores.sum())                             // 502
    println(c_students.sumOf { it.score })              // 502
    // sum()은 숫자 컬렉션에만 있음. 객체에서 뽑아 더하려면 sumOf를 씀
    // println(c_students.sum())                        // 컴파일 에러!

    // 2-2. 평균은 항상 Double
    println(c_scores.average())                         // 83.666...
    println("%.1f".format(c_scores.average()))  // 83.7

    // 2-3. 최대/최소 - By와 Of를 구분
    println(c_scores.maxOrNull())                       // 95
    println(c_students.maxByOrNull { it.score }?.name)  // 최지우   원소를 돌려줌
    println(c_students.maxOfOrNull { it.score })        // 95       값을 돌려줌

    // 여러 기준으로 비교하기
    println(c_students.maxWithOrNull(compareBy<Student>({ it.grade }, { it.score }))?.name)

    // 2-4. 판단 - 자바의 anyMatch / allMatch / noneMatch
    println(c_students.any { it.score >= 90 })          // true
    println(c_students.all { it.score >= 60 })          // true
    println(c_students.none { it.score < 50 })          // true

    // 빈 컬렉션에서 all은 true <- 자바와 같지만 자주 걸림
    println(emptyList<Int>().all { it > 100 })          // true !!  "어긋나는 것이 없음"
    val list = emptyList<Int>()
    println(list.isNotEmpty() && list.all { it > 100 }) // false    이렇게 물어야 함

    // 2-5. 빈 컬렉션에서의 결과
    println(emptyList<Int>().sum())                     // 0
    println(emptyList<Int>().maxOrNull())               // null
    println(emptyList<Int>().average())                 // NaN !!   평균만 예외적

    // 2-6. fold와 reduce - 자바의 reduce와 같음
    val nums = listOf(1, 2, 3, 4)
    println(nums.fold(1) { acc, n -> acc * n })         // 24   시작값을 줌
    println(nums.reduce { acc, n -> acc + n })          // 10   첫 원소가 시작값
    // 차이) fold는 자료형을 바꿀 수 있고 비어 있어도 안전. reduce는 비면 예외가 남
    println(emptyList<Int>().fold(0) { a, b -> a + b }) // 0
    println(nums.runningFold(0) { acc, n -> acc + n })  // [0, 1, 3, 6, 10]  중간 과정
    // sum / max처럼 이름 있는 함수를 먼저 찾아보고, 없을 때만 fold를 씀

    // 2-7. joinToString - Collectors.joining보다 할 수 있는 것이 많음
    println(c_scores.joinToString(", ", "[", "]"))
    println(c_students.joinToString(" / ") { it.name })                  // 변환까지 한 번에
    println(c_students.joinToString(limit = 3, truncated = "...") { it.name })
}

// ------------------------------------------------------------
// 예제 3. 찾기와 null
// ------------------------------------------------------------
fun c_exam3() {
    // find는 firstOrNull의 다른 이름. 둘은 완전히 같은 함수
    println(c_students.find { it.club == "독서" }?.name)         // 이영희
    println(c_students.find { it.club == "댄스" })               // null
    println(c_students.findLast { it.club == "독서" }?.name)     // 최지우

    // 자바 Optional을 쓰던 자리가 이렇게 바뀜
    //   opt.orElse(x)          -> ?: x
    //   opt.map(f).orElse(y)   -> ?.let(f) ?: y
    println(c_students.find { it.name == "없는사람" }?.name ?: "찾지 못함")

    // OrNull이 없는 버전은 예외를 던짐. 없을 리 없다고 확신할 때만 씀
    println(c_students.first { it.grade == 3 }.name)            // 최지우
    // c_students.first { it.grade == 9 }                       // NoSuchElementException

    // 위치 찾기 - 없으면 -1 (자바와 같음)
    println(c_students.indexOfFirst { it.score >= 90 })         // 1
    println(c_scores.indexOf(999))                             // -1

    // single - "반드시 하나여야 한다"는 규칙을 코드로 표현. 0개나 2개 이상이면 예외
    println(c_students.single { it.name == "최지우" }.name)      // 최지우
    println(c_students.singleOrNull { it.grade == 1 })          // null   둘이라서

    // 찾은 뒤 null 검사를 하면 그 안에서는 스마트 캐스트가 걸림
    val m = c_students.find { it.grade == 3 }
    if (m != null) println(m.name)      // m이 Student?가 아니라 Student로 취급됨
}

// ------------------------------------------------------------
// 예제 4. 변환과 필터
// ------------------------------------------------------------
fun c_exam4() {
    // 4-1. map은 개수를 유지하며 원소를 바꾸고, filter는 개수를 줄임
    println(c_students.map { it.name })
    println(c_scores.filter { it >= 85 })
    println(c_scores.filterNot { it >= 85 })                    // 반대
    println(c_scores.filterIndexed { i, _ -> i % 2 == 0 })      // 번호까지 보고

    // 조건에 맞는 것이 없으면 빈 리스트. null이 아니라서 뒤에 무엇을 붙여도 안전
    println(c_students.filter { it.score > 100 }.map { it.name })    // []

    // 4-2. null 걸러 내기 - 자바에는 없는 조합
    val raw = listOf("1", "2", "사과", "4")
    println(raw.map { it.toIntOrNull() })                       // [1, 2, null, 4]  List<Int?>
    println(raw.mapNotNull { it.toIntOrNull() })                // [1, 2, 4]        List<Int>
    println(raw.map { it.toIntOrNull() }.filterNotNull().sum()) // 7   같은 결과

    // 4-3. 자료형으로 거르기 - 자바의 filter(instanceof) + cast를 한 번에 함
    val mixed: List<Any> = listOf(1, "둘", 3.0, 4)
    println(mixed.filterIsInstance<Int>().sum())                // 5   결과가 List<Int>

    // 4-4. flatMap - 원소 하나가 여러 개로 늘어날 때
    val sentences = listOf("코틀린 배우기", "컬렉션 재밌음")
    println(sentences.map { it.split(" ") })                    // [[..], [..]]  리스트의 리스트
    println(sentences.flatMap { it.split(" ") })                // [코틀린, 배우기, ...]  펼쳐짐

    val table = listOf(listOf(1, 2), listOf(3, 4, 5))
    println(table.flatten())                                    // [1, 2, 3, 4, 5]
}

// ------------------------------------------------------------
// 예제 5. 나누기와 묶기
// ------------------------------------------------------------
fun c_exam5() {
    // 5-1. partition - 자바의 partitioningBy. 목록을 한 번만 훑고 조건도 한 번만 적음
    val (pass, fail) = c_students.partition { it.score >= 80 }
    println("합격 ${pass.map { it.name }} / 불합격 ${fail.map { it.name }}")

    // 5-2. take / drop - 개수가 모자라도 예외가 나지 않음 (subList와 다름)
    val nums = listOf(1, 2, 3, 4, 5, 6, 7)
    println("${nums.take(3)} ${nums.takeLast(3)} ${nums.drop(5)} ${nums.take(100)}")

    // takeWhile은 조건이 깨지는 순간 멈춤. filter는 끝까지 훑음  <- 자주 헷갈림
    val mixed = listOf(1, 2, 9, 3, 4)
    println(mixed.takeWhile { it < 5 })                     // [1, 2]        9에서 멈췄음
    println(mixed.filter { it < 5 })                        // [1, 2, 3, 4]  끝까지 봤음

    // 5-3. chunked / windowed - 자바에는 없음
    println((1..10).toList().chunked(3))                    // 3개씩 잘라 묶기
    println((1..6).toList().windowed(3))                    // 겹치며 미끄러지듯 자르기
    println(listOf(20, 22, 25, 24).windowed(3) { it.average() })     // 3일 이동 평균
    println(listOf(20, 22, 25, 24).zipWithNext { a, b -> b - a })    // 전날 대비 변화

    // 5-4. 중복 제거
    println(listOf(1, 2, 2, 3).distinct())
    println(c_students.distinctBy { it.grade }.map { it.name })      // 학년마다 첫 한 명

    // 5-5. zip - 두 리스트를 짝지음
    val names = listOf("김철수", "이영희", "박민수")
    val scores = listOf(85, 92, 78)
    println(names.zip(scores).toMap())                      // {김철수=85, ...}
    println(names.zip(scores) { n, s -> "$n:$s" })
    println(names.zip(listOf(1, 2)))                        // 짧은 쪽에 맞춰 잘림. 예외가 없으니 주의
}

// ------------------------------------------------------------
// 예제 6. 정렬
// ------------------------------------------------------------
fun c_exam6() {
    val nums = listOf(3, 1, 4, 1, 5)

    println(nums.sorted())                                  // [1, 1, 3, 4, 5]
    println(nums.sortedDescending())
    println(nums)                                           // 원본은 그대로

    // ed 규칙. 원본을 고치는 것은 MutableList에만 있음 (자바의 List.sort 자리)
    val m = mutableListOf(3, 1, 2)
    m.sort()
    println(m)                                              // [1, 2, 3]  원본이 바뀌었음

    // reversed는 정렬이 아니라 순서 뒤집기
    println(nums.reversed())                                // [5, 1, 4, 1, 3]

    // 6-1. 기준을 정해서
    println(c_students.sortedByDescending { it.score }.map { it.name })
    println(listOf("apple", "kiwi", "banana").sortedBy { it.length })
    println(listOf("banana", "Apple").sortedBy { it.lowercase() })   // 대소문자 무시

    // 6-2. 여러 기준 - 앞의 기준이 같을 때만 뒤를 봄 (자바의 comparing().thenComparing())
    c_students.sortedWith(compareBy<Student> { it.grade }.thenByDescending { it.score })
        .forEach { println("${it.grade}학년 ${it.name} ${it.score}점") }

    // 전부 오름차순이면 compareBy에 기준을 나열해도 됨
    println(c_students.sortedWith(compareBy<Student>({ it.grade }, { it.name })).map { it.name })

    // 6-3. 정렬은 안정적. 기준값이 같으면 원래 순서가 유지
    // 그래서 정렬을 나눠서 여러 번 해도 결과가 맞음 (나중에 한 정렬이 큰 기준이 됨)
    println(c_students.sortedBy { it.name }.sortedBy { it.grade }.map { it.name })

    // 6-4. 클래스가 정렬 기준을 직접 갖는 경우 - Comparable
    val products = listOf(CProduct("키보드", 30000), CProduct("마우스", 15000))
    println(products.sorted().map { it.name })              // [마우스, 키보드]
    println(products.max().name)                            // 키보드   비교할 줄 알아서 max도 됨
    println(products[0] > products[1])                      // true    부등호도 쓸 수 있음
    // "이 클래스의 자연스러운 순서"가 하나로 정해질 때만 Comparable을 구현
    // 그렇지 않으면 sortedBy / sortedWith로 기준을 밖에서 넘김
}

// ------------------------------------------------------------
// 예제 7. 그룹화
// ------------------------------------------------------------
fun c_exam7() {
    // 7-1. groupBy - 자바의 Collectors.groupingBy. 결과는 Map<키, List<원소>>
    val byGrade = c_students.groupBy { it.grade }
    for ((grade, list) in byGrade) println("${grade}학년: ${list.map { it.name }}")

    // 묶으면서 원소를 바꾸기 - 두 번째 람다가 값 변환
    println(c_students.groupBy({ it.club }, { it.name }))
    // {축구=[김철수, 박민수], 독서=[이영희, 최지우], 밴드=[정하늘, 강바다]}

    // 계산한 값으로 묶기
    println(c_students.groupBy { it.score / 10 * 10 }.mapValues { it.value.size })

    // 묶은 뒤 통계 내기 - 자바의 groupingBy(.., averagingInt(..)) 자리
    println(c_students.groupBy { it.grade }
        .mapValues { (_, l) -> l.map { it.score }.average() })      // {1=81.5, 2=78.0, 3=91.5}

    println(c_students.groupBy { it.grade }
        .mapValues { (_, l) -> l.maxByOrNull { it.score }?.name })  // 학년별 1등

    // 7-2. 개수만 필요하면 groupingBy().eachCount()가 가벼움. 리스트를 만들지 않음
    println(c_students.groupingBy { it.club }.eachCount())          // {축구=2, 독서=2, 밴드=2}
    println("hello".groupingBy { it }.eachCount())                  // {h=1, e=1, l=2, o=1}

    // 7-3. associate 3형제 - 자바의 Collectors.toMap 자리
    println(c_students.associateBy { it.name }["최지우"]?.score)     // 95   원소에서 키를 뽑음
    println(listOf("apple", "kiwi").associateWith { it.length })    // {apple=5, kiwi=4}  원소가 키
    println(c_students.associate { it.name to it.club })            // 둘 다 직접 지정
    println(c_students.associateBy({ it.name }, { it.score }))      // 키와 값을 따로

    // 키가 겹치면 마지막 것만 남음  <- 가장 자주 하는 실수
    println(c_students.associateBy { it.grade }.mapValues { it.value.name })
    // {1=박민수, 2=정하늘, 3=강바다}   학년마다 한 명씩만 남았음!
    // 여러 명을 담아야 하면 groupBy를 써야 함

    // associateBy로 만든 Map은 '찾기'를 빠르게 만드는 데 씀
    //   [before] c_students.find { it.name == x }   매번 목록 전체를 훑음
    //   [after]  byName[x]                          바로 찾는다

    // 7-4. Map을 정렬하려면 List로 바꿔서 함
    c_students.groupBy { it.club }
        .mapValues { (_, l) -> l.map { it.score }.average() }
        .toList()
        .sortedByDescending { (_, avg) -> avg }
        .forEach { (club, avg) -> println("$club: ${"%.1f".format(avg)}점") }
}

fun main() {
    c_exam1()     // 순회와 이어 붙이기
    c_exam2()     // 집계
    c_exam3()     // 찾기와 null
    c_exam4()     // 변환과 필터
    c_exam5()     // 나누기와 묶기
    c_exam6()     // 정렬
    c_exam7()     // 그룹화
}