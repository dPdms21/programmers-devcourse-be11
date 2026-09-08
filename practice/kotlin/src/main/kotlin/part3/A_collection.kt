package part3

// * 컬렉션 - List, Set, Map

// 1. 자바와 차이점
//   [자바]                                    [코틀린]
//   new ArrayList<>()                      -> mutableListOf()      new가 없음
//   Arrays.asList(1, 2, 3)                 -> listOf(1, 2, 3)
//   list.get(0) / list.set(0, x)           -> list[0] / list[0] = x
//   map.get(k) / map.put(k, v)             -> map[k] / map[k] = v
//   list.size() / map.size()               -> list.size            프로퍼티라 괄호가 없음
//   list.contains(x)                       -> x in list
//   map.containsKey(k)                     -> k in map
//   for (Map.Entry<K,V> e : m.entrySet())  -> for ((k, v) in m)    구조 분해
//   list.stream().filter(..).collect(..)   -> list.filter { .. }   stream()이 필요 없음
//   Collections.unmodifiableList(list)     -> 자료형을 List로 적으면 끝
//
// * 가장 큰 차이는 마지막 줄. 코틀린은 '읽기 전용'을 자료형으로 나눠 놓았음

// 2. 읽기 전용과 가변
//   자바의 List는 하나뿐이라, 어떤 리스트를 받든 add를 부를 수 있었음
//   막으려면 unmodifiableList로 감싸야 했고, 그래도 add를 부르는 것은 컴파일이 됐음
//   (실행 중에 UnsupportedOperationException이 나서야 알게 됨)
//
//   코틀린은 자료형 자체가 두 갈래
//     List<T>          꺼내 볼 수만 있음. add/remove가 아예 존재하지 않음
//     MutableList<T>   넣고 빼고 바꿀 수 있음 (MutableList는 List의 자식)
//     Set / MutableSet,  Map / MutableMap도 같은 구조
//
//   함수 매개변수를 List로 적으면 "이 함수는 목록을 건드리지 않는다"를 컴파일러가 보장
//
//   [만드는 함수]
//     읽기 전용   listOf()          setOf()         mapOf()
//     가변        mutableListOf()  mutableSetOf()  mutableMapOf()
//     자바 구현체  arrayListOf()    hashSetOf() / linkedSetOf() / sortedSetOf()
//                                 hashMapOf() / linkedMapOf() / sortedMapOf()
//
//   * mutableListOf는 내부적으로 ArrayList를, mutableSetOf/mutableMapOf는
//     LinkedHashSet/LinkedHashMap을 만듦. 그래서 넣은 순서가 유지됨
//     특별한 이유가 없으면 mutable~Of를 쓰면 됨

// 3. 읽기 전용은 '불변'이 아님
//   MutableList를 List 자리에 넣으면 '읽기 전용 창'이 하나 생길 뿐, 원본은 그대로 살아 있음
//   원본을 가진 쪽이 값을 바꾸면 그 창으로도 바뀐 값이 보임
//   정말 바뀌지 않게 하려면 toList()로 복사본을 만들어 넘겨야 함

// 4. null을 대하는 방식
//   자바는 없는 값을 물으면 대체로 null이 나왔고, 그것을 확인하는 것은 개발자 몫
//   코틀린은 반환 자료형에 ?가 붙어 있어 확인하지 않으면 컴파일이 되지 않음
//     map["없는키"]        -> V?      null이 나옴
//     list.getOrNull(99)  -> T?      예외 대신 null
//     list[99]            -> T       없으면 예외 (자바와 같음)
//   "없을 때 무엇을 돌려줄지"를 고르는 함수들이 짝으로 준비되어 있음
//     getOrNull / getOrElse { } / getOrDefault / getOrPut { } / first / firstOrNull

val a_prices = mapOf("아메리카노" to 4500, "카페라떼" to 5000, "바닐라라떼" to 5500)

class APoint(val x: Int, val y: Int) {
    override fun toString() = "($x, $y)"
}

// 1. 자바와 달라지는 표기
fun a_exam1() {
    val list = mutableListOf("사과", "바나나")
    list.add("포도")
    println(list[0])
    list[0] = "체리"
    println(list.size)
    println("포도" in list) // contains
    println(list)

    // ==가 내용을 비교. 자바의 equals에 해당
    println(listOf(1, 2) == listOf(1, 2)) // true
    println(arrayOf(1, 2) == arrayOf(1, 2)) // false 배열은 여전히 주소를 비교

    println(arrayOf(1, 2))
    println(listOf(1, 2))

    // 만드는 방법들
    println(emptyList<Int>())                         // [] 비어 있으면 자료형을 적어 줌
    println(listOfNotNull(1, null, 3))     // [1, 3]   null을 걸러 내며 만들기
    println(List(5) { it * it })                // [0, 1, 4, 9, 16]
    println((1..5).toList())                    // [1, 2, 3, 4, 5]
    println(arrayOf(1, 2, 2).toSet())                 // [1, 2] 변환하며 중복 제거
}

// 2. 읽기 전용과 가변
fun a_exam2() {
    val readOnly = listOf(1, 2, 3)
//    readOnly.add(4)
//    readOnly[0] = 100

    val mutable = mutableListOf(1, 2, 3)
    mutable.add(4)
    mutable[0] = 100
    println(mutable)

    // val: 변수 이름이 다른 객체를 가리키지 못하게 막음
    // List: 그 객체의 내용을 바꾸지 못하게 막음
    val a = mutableListOf(1, 2, 3)
    a.add(4)
//    a = mutableListOf(1, 2, 3)

    // 읽기 전용은 불변이 아님
    val origin = mutableListOf(1, 2, 3)
    val view: List<Int> = origin        // 읽기 전용 '창'을 낸 것뿐
    origin.add(4)
    println(view)                       // [1, 2, 3, 4] 같이 바뀜
    println(view === origin)            // true         같은 객체

    val copy = origin.toList()          // 이 순간의 내용을 복사한 새 리스트
    origin.add(5)
    println("$origin / $copy")          // [1,2,3,4,5] / [1,2,3,4]

    // 2-3. 안에서는 가변으로 만들고, 밖으로는 읽기 전용으로 내보내는 것이 정석
    fun makeList(): List<Int> {
        val result = mutableListOf<Int>()
        for (i in 1..3) result.add(i)
        return result.toList()          // toList()를 빼면 원본과 이어져 있음
    }
    println(makeList())                 // [1, 2, 3]
}

// 3. List
fun a_exam3() {
    val fruits = listOf("사과", "바나나", "포도", "딸기")

    // 3-1. 꺼내기 - 없을 때 무엇을 할지 고름
    println(fruits[0])                          // 사과
    // println(fruits[10])                      // IndexOutOfBoundsException
    println(fruits.getOrNull(10))               // null
    println(fruits.getOrElse(10) { "없음" })     // 없음
    println(fruits.first())                     // 사과   비어 있으면 예외
    println(fruits.firstOrNull())               // 사과   비어 있으면 null
    println(fruits.lastIndex)                   // 3
    println(fruits.subList(1, 3))               // [바나나, 포도]

    // 3-2. 넣고 빼기
    val list = mutableListOf("사과", "바나나", "포도")
    list.add(0, "딸기")                          // 그 자리에 끼워 넣기
    list.addAll(listOf("참외", "수박"))
    println(list.remove("참외"))                 // true   '값'으로 지움
    println(list.removeAt(0))                     // 딸기   '자리'로 지움
    list.removeAll { it.length == 3 }           // 조건으로 한꺼번에 (자바의 removeIf)
    println(list)                               // [사과, 포도, 수박]

    // 주의) 숫자 리스트에서 remove(1)은 "값 1"이지 "1번 칸"이 아님
    val nums = mutableListOf(10, 20, 30)
    println(nums.remove(1))                     // false  값 1은 없음
    println(nums.removeAt(1))                     // 20     1번 칸

    // 3-3. 순회 - 셋 다 같은 일을 함
    for (f in fruits) print("$f ")
    println()

    fruits.forEach { print("$it ") }
    println()

    fruits.forEachIndexed { i, f -> print("$i:$f ") }
    println()

    // 3-4. 출력
    println(fruits.joinToString(", "))                  // 사과, 바나나, 포도, 딸기
    println(fruits.joinToString(" | ", "<", ">"))       // <사과 | 바나나 | 포도 | 딸기>

    // 3-5. 리스트 안의 리스트. 행마다 길이가 달라도 됨
    val table = listOf(listOf(1, 2, 3), listOf(4, 5))
    println(table[0][2])                        // 3
    println(table.flatten())                    // [1, 2, 3, 4, 5]
}

// 4. Set
fun a_exam4() {
    println(setOf(1, 2, 3, 2, 1))               // [1, 2, 3]   중복이 사라짐
    // println(setOf(1, 2)[0])                  // 컴파일 에러! Set은 인덱스가 없음

    val set = mutableSetOf("사과", "바나나")

    // add는 실제로 들어갔는지를 돌려줌. "처음 보는 값인가"를 한 줄로 확인할 수 있음
    println(set.add("포도"))                     // true
    println(set.add("사과"))                     // false  이미 있어서 무시됐음

    val seen = mutableSetOf<String>()
    for (n in listOf("A", "B", "A")) println("$n : ${if (seen.add(n)) "처음" else "중복"}")

    // 4-1. 중복 제거
    val names = listOf("김철수", "이영희", "김철수")
    println(names.toSet())                      // [김철수, 이영희]   결과가 Set
    println(names.distinct())                   // [김철수, 이영희]   결과가 List

    // 4-2. 집합 연산 - 중위 함수라 점 없이 씀
    val a = setOf(1, 2, 3, 4)
    val b = setOf(3, 4, 5)
    println(a union b)                          // [1, 2, 3, 4, 5]
    println(a intersect b)                      // [3, 4]
    println(a subtract b)                       // [1, 2]
    println(a + b)                              // union과 같음
    println(a - b)                              // subtract와 같음

    // 4-3. Set을 쓰는 진짜 이유는 contains가 빠르다는 것
    // List는 처음부터 끝까지 비교하고, Set은 hashCode로 위치를 바로 계산
    val big = List(200_000) { it }
    val bigSet = big.toSet()
    var t = System.currentTimeMillis()
    repeat(1000) { big.contains(199_999) }
    println("List: ${System.currentTimeMillis() - t}ms")
    t = System.currentTimeMillis()
    repeat(1000) { bigSet.contains(199_999) }
    println("Set : ${System.currentTimeMillis() - t}ms")

    // 그래서 걸러 낼 조건 목록은 미리 Set으로 만들어 둠
    val banned = listOf("A", "B").toSet()
    println(listOf("A", "C", "D").filter { it !in banned })      // [C, D]
}

// 5. Map
fun a_exam5() {
    // 5-1 순회하는 도중에 목록을 바꾸면 안 됨 (자바와 같은 ConcurrentModificationException)
    //   for (n in list) { if (n % 2 == 0) list.remove(n) }      <- 예외!
    val a = mutableListOf(1, 2, 3, 4, 5)
    a.removeAll { it % 2 == 0 }                     // 조건으로 한 번에 지움
    println(a)                                      // [1, 3, 5]
    println(listOf(1, 2, 3, 4, 5).filter { it % 2 != 0 })    // 또는 새로 걸러 냄

    // 5-2. +=는 상황에 따라 다르게 동작
    var v1 = listOf(1, 2)               // var + 읽기 전용 -> 새 리스트를 만들어 가리킴
    v1 += 3
    val v2 = mutableListOf(1, 2)        // val + 가변      -> add가 불림. 원본이 바뀜
    v2 += 3
    println("$v1 / $v2")                // [1, 2, 3] / [1, 2, 3]   결과는 같아 보이지만 다름

    // 5-3. toList()는 얕은 복사. 리스트는 새로 생기지만 안의 객체는 원본과 같음
    val inner = mutableListOf(1, 2)
    val outerCopy = mutableListOf(inner).toMutableList()
    inner.add(3)
    println(outerCopy)                              // [[1, 2, 3]]   같이 바뀜

    // 5-4. 만든 클래스는 Set / Map 키로 쓰면 이상하게 동작  <- 가장 중요
    val p1 = APoint(1, 2)
    val p2 = APoint(1, 2)                  // 내용이 완전히 같은 점

    println(p1 == p2)                               // false !!
    println(setOf(p1, p2).size)                     // 2 !!   중복 제거가 안 됐음

    val m = mutableMapOf<APoint, String>()
    m[APoint(1, 2)] = "값"
    println(m[APoint(1, 2)])                        // null !!  방금 넣었는데 못 찾음

    // 자바와 똑같은 이유. equals/hashCode를 만들지 않으면 주소를 비교
}

fun main() {
    a_exam1()
    a_exam2()
    a_exam3()
    a_exam4()
    a_exam5()
}