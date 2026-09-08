# 회원 관리 프로그램 만들기 - Kotlin 컬렉션과 data class

배열 기반 회원 관리 프로그램을 컬렉션 기반 구조로 다시 구현한다.

정원만큼 빈 공간을 미리 만들거나 삭제 시 데이터를 직접 이동하지 않고, `MutableList`와 `data class`, 컬렉션 함수를 활용해 회원 데이터를 관리한다.

선행 과제는 배열 기반 회원 관리 프로그램이며, `List`, `Set`, `Map`, `data class`, `filter`, `find`, `groupBy` 등의 컬렉션 개념을 학습한 상태를 전제로 한다.

---

## 1. 구현 기능

기본적인 회원 관리 기능은 배열 버전과 동일하다.

이번 과제에서는 이름 부분 검색과 이메일 도메인별 통계 기능을 추가한다.

```text
[요금제를 선택하세요]
[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명
> 1

샘플 회원을 넣고 시작할까요? (y/n) > y
4명을 넣었습니다.

[수행할 업무를 선택하세요 - 현재 회원수 : 4/10]
[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)
[4]회원전체조회 [5]회원정보 수정 [6]회원삭제
[7]이름검색 [8]도메인별통계 [9]프로그램 종료
> 7

검색할 이름의 일부를 입력하세요.
> 김

2명을 찾았습니다.
1. [이름] 김철수, [이메일] kim@naver.com, [연락처] 010-1111-1111
2. [이름] 김철수, [이메일] kim2@gmail.com, [연락처] 010-4444-4444
```

도메인별 통계에서는 이메일 도메인별 인원, 이름순 정렬, 동명이인 정보를 출력한다.

```text
[이메일 도메인별]
  naver.com : 2명  (김철수, 이영희)
  gmail.com : 2명  (박민수, 김철수)

[이름순]
  김철수, 김철수, 박민수, 이영희

[이름이 겹치는 회원]
  김철수 : 2명  (kim@naver.com, kim2@gmail.com)
```

배열에서는 여러 조건에 맞는 결과를 별도 배열로 만들기 위해 반복 작업이 필요하지만, 컬렉션에서는 `filter`, `groupBy` 등의 함수를 이용해 간결하게 구현할 수 있다.

---

## 2. 요구사항

| 번호 | 기능        | 설명                                                      |
| -- | --------- | ------------------------------------------------------- |
| 0  | 요금제 선택    | Lite(10명), Basic(20명), Premium(30명) 중 하나를 선택해 정원을 결정한다. |
| 1  | 회원 추가     | 이름, 이메일, 연락처를 입력받아 저장하고 정원 초과와 이메일 중복을 방지한다.            |
| 2  | 회원 조회(메일) | 이메일로 회원 한 명을 조회한다.                                      |
| 3  | 회원 조회(이름) | 이름으로 회원을 검색하고 처음 찾은 한 명을 반환한다.                          |
| 4  | 회원 전체 조회  | 등록된 모든 회원을 출력한다.                                        |
| 5  | 회원 정보 수정  | 이메일로 회원을 찾아 새로운 정보로 변경하고 이메일 중복을 검사한다.                  |
| 6  | 회원 삭제     | 이메일로 회원을 찾아 삭제한다.                                       |
| 7  | 이름 검색     | 이름에 특정 문자열이 포함된 회원을 모두 조회한다.                            |
| 8  | 도메인별 통계   | 이메일 도메인별 인원, 이름순 정렬, 동명이인 정보를 출력한다.                     |
| 9  | 종료        | 프로그램을 종료한다.                                             |

이메일은 회원을 식별하는 고유값으로 사용하며 조회, 수정, 삭제 모두 이메일을 기준으로 처리한다.

---

## 3. 학습 목표

| 개념                        | 활용                                                           |
| ------------------------- | ------------------------------------------------------------ |
| `mutableListOf<Member>()` | 등록된 회원만 저장하는 가변 리스트를 구성한다.                                   |
| `data class`              | `equals()`, `hashCode()`, `toString()`, `copy()` 등을 자동 생성한다. |
| `val`과 `copy()`           | 기존 객체를 직접 변경하지 않고 새로운 객체로 교체한다.                              |
| 커스텀 getter                | `memberCnt`를 별도로 저장하지 않고 `members.size`를 이용해 계산한다.           |
| `find { }`                | 조건에 맞는 회원 한 명을 조회한다.                                         |
| `any { }`                 | 이메일 중복 여부를 검사한다.                                             |
| `filter { }`              | 조건에 맞는 회원 여러 명을 조회한다.                                        |
| `indexOfFirst { }`        | 수정 대상 회원의 위치를 찾는다.                                           |
| `removeAll { }`           | 별도의 데이터 이동 없이 회원을 삭제한다.                                      |
| `toList()`                | 내부 저장소와 분리된 목록을 반환한다.                                        |
| `sortedBy`                | 특정 기준으로 정렬된 새 목록을 만든다.                                       |
| `groupBy`                 | 특정 값을 기준으로 회원을 그룹화한다.                                        |
| `joinToString`            | 여러 값을 문자열 하나로 연결한다.                                          |
| `forEachIndexed`          | 인덱스와 원소를 함께 순회한다.                                            |
| 구조 분해                     | `Map`의 key와 value를 분리해 사용한다.                                 |
| `ifBlank { }`             | 빈 입력 시 기존 값을 유지한다.                                           |

---

## 4. 핵심 개념

### 4.1 배열 기반 구조에서 사라지는 관리 코드

배열 버전에서는 배열의 크기가 고정되어 있기 때문에 여러 부가 작업이 필요했다.

| 배열에서 필요한 처리          | 이유                              | 컬렉션 버전                  |
| -------------------- | ------------------------------- | ----------------------- |
| 정원만큼 빈 객체 또는 문자열 생성  | 배열은 생성 시 크기가 고정된다.              | 등록된 회원만 저장하므로 필요하지 않다.  |
| 삭제 시 뒤 요소 이동         | 중간 요소를 삭제한 뒤 빈 공간을 직접 처리해야 한다.  | `removeAll`이 자동으로 처리한다. |
| `memberCnt` 직접 증가·감소 | 배열은 실제로 사용 중인 요소 수를 별도로 알지 못한다. | `members.size`를 이용한다.   |

컬렉션을 사용하면 데이터 구조 자체가 크기 변경과 요소 이동을 관리하므로 직접 작성해야 하는 상태 관리 코드가 감소한다.

---

### 4.2 `MutableList`

배열에서는 정원만큼 회원 공간을 먼저 생성한다.

```kotlin
private val members =
    Array(totalCnt) {
        Member()
    }
```

컬렉션 버전에서는 빈 리스트로 시작한다.

```kotlin
private val members =
    mutableListOf<Member>()
```

회원 추가, 삭제, 현재 크기 확인은 다음과 같이 처리한다.

```kotlin
members.add(member)

members.removeAll {
    it.email == email
}

members.size
```

`members`가 `val`이어도 리스트 내부 요소는 변경할 수 있다.

```kotlin
val members = mutableListOf<Member>()
```

`val`은 변수 자체가 다른 리스트 객체를 가리키는 것을 막지만, `MutableList` 내부의 데이터를 변경하는 것까지 제한하지는 않는다.

---

### 4.3 `data class`

일반 클래스에서 객체 비교는 기본적으로 객체 참조를 기준으로 이루어진다.

```kotlin
class Member(
    val name: String,
    val email: String,
    val phone: String
)
```

```kotlin
val a =
    Member(
        "김철수",
        "kim@a.com",
        "010-1111-1111"
    )

val b =
    Member(
        "김철수",
        "kim@a.com",
        "010-1111-1111"
    )

println(a == b)
```

일반 클래스에서는 두 객체가 별개의 인스턴스이므로 `false`가 된다.

`data class`를 사용하면 주 생성자 프로퍼티를 기준으로 값 비교가 이루어진다.

```kotlin
data class Member(
    val name: String,
    val email: String,
    val phone: String
)
```

`data class`에서는 다음 기능이 자동으로 생성된다.

| 기능             | 설명                                      |
| -------------- | --------------------------------------- |
| `equals()`     | 주 생성자 프로퍼티를 기준으로 객체의 값을 비교한다.           |
| `hashCode()`   | `Set`, `Map` 등 해시 기반 컬렉션에서 사용할 값을 생성한다. |
| `toString()`   | 객체의 프로퍼티 값을 문자열로 출력한다.                  |
| `copy()`       | 기존 값을 유지하면서 일부 프로퍼티만 변경한 새 객체를 만든다.     |
| `componentN()` | 구조 분해 선언을 사용할 수 있도록 한다.                 |

---

### 4.4 `val`과 `copy()`

이전 회원 관리 프로그램에서는 회원 프로퍼티를 `var`로 선언해 직접 수정할 수 있었다.

```kotlin
val member =
    manager.findByEmail(email)

member.name = readln()
member.email = readln()
```

이 방식은 관리 클래스를 거치지 않고 객체 상태를 변경할 수 있으므로 이메일 중복 검사와 같은 규칙을 우회할 수 있다.

컬렉션 버전에서는 `Member`의 프로퍼티를 `val`로 선언한다.

```kotlin
data class Member(
    val name: String,
    val email: String,
    val phone: String
)
```

따라서 다음과 같은 직접 수정은 허용되지 않는다.

```kotlin
member.name = "김영수"
```

대신 `copy()`를 이용해 새로운 객체를 만든 뒤 리스트의 기존 위치에 교체한다.

```kotlin
members[idx] =
    members[idx].copy(
        name = name,
        email = newEmail,
        phone = phone
    )
```

이를 통해 회원 정보 수정이 `MemberManager.update()`를 거치도록 구성할 수 있고, 수정 과정에서 이메일 중복 검사와 같은 규칙을 함께 적용할 수 있다.

---

### 4.5 컬렉션 함수

컬렉션 함수를 이용하면 반복문과 인덱스 기반 처리를 줄일 수 있다.

| 작업              | 배열 기반 처리                 | 컬렉션 함수                       |
| --------------- | ------------------------ | ---------------------------- |
| 이메일로 한 명 찾기     | 반복문으로 인덱스를 탐색한다.         | `find { it.email == email }` |
| 이메일 중복 확인       | 검색 후 `-1` 여부를 확인한다.      | `any { it.email == email }`  |
| 조건에 맞는 여러 회원 찾기 | 개수를 센 뒤 배열을 새로 만든다.      | `filter { ... }`             |
| 특정 회원 위치 찾기     | 별도의 `findIndex()`를 작성한다. | `indexOfFirst { ... }`       |
| 회원 삭제           | 뒤 요소를 직접 이동한다.           | `removeAll { ... }`          |
| 전체 목록 반환        | 배열 범위를 복사한다.             | `toList()`                   |

컬렉션 함수 이름은 반환 방식이나 동작 특성에 따라 일정한 규칙을 갖는다.

| 규칙       | 의미                          | 예시                               |
| -------- | --------------------------- | -------------------------------- |
| `OrNull` | 결과가 없으면 예외 대신 `null`을 반환한다. | `firstOrNull()`                  |
| `By`     | 조건에 해당하는 원소 자체를 반환한다.       | `maxByOrNull { it.name }`        |
| `Of`     | 원소에서 계산한 값을 반환한다.           | `maxOfOrNull { it.name.length }` |
| `ed`     | 원본을 변경하지 않고 새로운 결과를 만든다.    | `sorted()`                       |

---

### 4.6 `getAll()`과 `toList()`

내부 저장소를 그대로 반환하면 외부 코드와 동일한 컬렉션 객체를 공유하게 된다.

```kotlin
fun getAll(): List<Member> =
    members
```

반환형이 `List<Member>`이므로 외부에서 `add()`나 `remove()`를 직접 호출할 수는 없지만, 내부의 `MutableList`와 같은 객체를 가리킨다.

따라서 이후 내부 리스트가 변경되면 기존에 반환받은 목록에서도 변경 결과가 보일 수 있다.

이를 방지하기 위해 `toList()`로 새로운 리스트를 반환한다.

```kotlin
fun getAll(): List<Member> =
    members.toList()
```

`List<Member>` 반환형은 외부의 직접 수정을 제한하고, `toList()`는 원본 컬렉션과 반환 객체를 분리한다.

`toList()`는 얕은 복사이므로 내부 `Member` 객체 자체는 동일한 참조를 사용한다. 이번 과제의 `Member`는 모든 프로퍼티가 `val`인 불변 객체이므로 내부 객체가 임의로 변경되는 문제를 방지할 수 있다.

---

## 5. 파일 구조

```text
src/main/kotlin/member/
├── Member.kt
├── MemberManager.kt
├── MemberApp.kt
└── Main.kt
```

| 파일                 | 역할                          | 배열 버전 대비                                      |
| ------------------ | --------------------------- | --------------------------------------------- |
| `Member.kt`        | 회원 한 명의 정보를 관리한다.           | 일반 클래스 대신 `data class`, `var` 대신 `val`을 사용한다. |
| `MemberManager.kt` | 회원 저장과 검색, 수정, 삭제 규칙을 담당한다. | `Array` 대신 `MutableList`와 컬렉션 함수를 사용한다.       |
| `MemberApp.kt`     | 사용자 입력과 출력 흐름을 담당한다.        | 기존 구조를 대부분 유지한다.                              |
| `Main.kt`          | 요금제 선택과 객체 생성을 담당한다.        | 샘플 데이터 입력 기능을 추가한다.                           |

저장 구조가 배열에서 리스트로 변경되더라도 `MemberApp`의 역할은 유지된다.

---

## 6. Step by Step

### Step 1. 회원 클래스 (`Member.kt`)

**목표**

회원 한 명을 `data class`로 구현한다.

**구현 내용**

1. `data class Member`를 선언한다.
2. `name`, `email`, `phone`을 모두 `val`로 선언한다.
3. 화면 출력에 사용할 `display` 커스텀 getter를 추가한다.

**힌트**

```kotlin
package member

data class Member(
    val name: String,
    val email: String,
    val phone: String
) {

    val display: String
        get() =
            "[이름] $name, [이메일] $email, [연락처] $phone"
}
```

`data class`가 자동 생성하는 `toString()`은 다음과 같은 형식이다.

```text
Member(name=김철수, email=kim@a.com, phone=010-1111-1111)
```

개발 과정에서 객체 상태를 확인하기에는 유용하지만 사용자 출력 형식과는 다르므로 별도의 `display` 프로퍼티를 사용한다.

배열 버전에서 빈 요소를 채우기 위해 사용했던 빈 생성자는 더 이상 필요하지 않다.

**확인**

```kotlin
fun main() {
    val a =
        Member(
            "김철수",
            "kim@a.com",
            "010-1111-1111"
        )

    val b =
        Member(
            "김철수",
            "kim@a.com",
            "010-1111-1111"
        )

    println(a == b)
    println(setOf(a, b).size)
    println(a)
    println(a.display)
    println(a.copy(name = "김영수"))
}
```

동일한 값을 가진 두 객체의 비교 결과가 `true`인지 확인한다.

---

### Step 2. 저장소 구성 (`MemberManager.kt`)

**목표**

`MutableList`를 이용해 회원 저장소와 정원, 회원 수 정보를 구성한다.

**구현 내용**

1. 생성자로 `planNo`를 받는다.
2. `totalCnt`를 계산한다.
3. `mutableListOf<Member>()`로 회원 저장소를 만든다.
4. 저장소는 `private`으로 제한한다.
5. `memberCnt`와 `isFull`을 커스텀 getter로 만든다.

**힌트**

```kotlin
package member

class MemberManager(
    planNo: Int
) {

    val totalCnt =
        planNo * 10

    private val members =
        mutableListOf<Member>()

    val memberCnt: Int
        get() =
            members.size

    val isFull: Boolean
        get() =
            members.size >= totalCnt
}
```

배열 버전과 달리 회원 수를 별도의 `var`에 저장하지 않는다.

회원 수는 `members.size`로 계산한다.

따라서 회원 추가나 삭제 과정에서 `memberCnt++`, `memberCnt--`를 직접 수행할 필요가 없다.

---

### Step 3. 회원 조회

**목표**

`find`, `filter`, `toList()`를 이용해 회원 검색과 전체 목록 반환을 구현한다.

**힌트**

```kotlin
fun findByEmail(
    email: String
): Member? =
    members.find {
        it.email == email
    }

fun findByName(
    name: String
): Member? =
    members.find {
        it.name == name
    }

fun searchByName(
    keyword: String
): List<Member> =
    members.filter {
        it.name.contains(keyword)
    }

fun getAll(): List<Member> =
    members.toList()
```

`find()`는 조건에 맞는 첫 번째 원소를 반환하고, 찾지 못하면 `null`을 반환한다.

`filter()`는 조건에 맞는 모든 원소를 `List`로 반환한다.

이메일처럼 고유값으로 한 명을 찾는 경우에는 `find()`를 사용하고, 이름처럼 여러 회원이 일치할 수 있는 조건에서는 `filter()`를 활용할 수 있다.

`getAll()`은 내부 `MutableList`를 그대로 반환하지 않고 `toList()`를 이용해 새로운 목록을 반환한다.

---

### Step 4. 회원 추가

**목표**

`any()`를 이용해 이메일 중복 여부를 검사하고 회원을 추가한다.

**힌트**

```kotlin
fun add(
    member: Member
): Boolean {

    if (isFull) {
        return false
    }

    if (
        members.any {
            it.email == member.email
        }
    ) {
        return false
    }

    members.add(member)

    return true
}
```

`any()`는 조건에 해당하는 회원이 하나라도 존재하면 `true`를 반환한다.

회원 객체 자체가 필요하지 않고 중복 여부만 확인하면 되므로 `find()`보다 의도를 직접적으로 표현할 수 있다.

회원 추가 시 배열 위치 계산이나 회원 수 증가 처리는 필요하지 않다.

---

### Step 5. 회원 삭제

**목표**

`removeAll()`을 이용해 회원 삭제를 구현한다.

**힌트**

```kotlin
fun delete(
    email: String
): Boolean =
    members.removeAll {
        it.email == email
    }
```

배열에서는 삭제 대상 이후의 데이터를 직접 한 칸씩 이동해야 했다.

```kotlin
for (
    i in idx until memberCnt - 1
) {
    members[i] =
        members[i + 1]
}
```

컬렉션에서는 `removeAll()`이 요소 삭제와 이후 요소 정리를 처리한다.

`removeAll()`은 조건에 해당하는 요소가 하나 이상 삭제되면 `true`를 반환하므로 그대로 삭제 함수의 반환값으로 사용할 수 있다.

---

### Step 6. 회원 수정

**목표**

`indexOfFirst()`와 `copy()`를 이용해 회원 정보를 수정한다.

**구현 내용**

1. 기존 이메일을 기준으로 회원 위치를 찾는다.
2. 회원이 없으면 `false`를 반환한다.
3. 이메일을 변경하는 경우에만 중복 검사를 수행한다.
4. `copy()`를 이용해 새로운 회원 객체를 생성한다.
5. 기존 리스트 위치의 객체를 새 객체로 교체한다.

**힌트**

```kotlin
fun update(
    email: String,
    name: String,
    newEmail: String,
    phone: String
): Boolean {

    val idx =
        members.indexOfFirst {
            it.email == email
        }

    if (idx == -1) {
        return false
    }

    if (
        newEmail != email &&
        members.any {
            it.email == newEmail
        }
    ) {
        return false
    }

    members[idx] =
        members[idx].copy(
            name = name,
            email = newEmail,
            phone = phone
        )

    return true
}
```

`indexOfFirst()`는 조건에 맞는 첫 번째 원소의 인덱스를 반환하고, 존재하지 않으면 `-1`을 반환한다.

이메일 중복 검사는 기존 이메일과 새 이메일이 다른 경우에만 수행한다.

```kotlin
newEmail != email
```

이 조건이 없으면 이메일은 그대로 두고 다른 값만 수정할 때 자기 자신의 이메일이 중복으로 판단될 수 있다.

`Member` 프로퍼티가 모두 `val`이므로 기존 객체를 직접 변경하지 않고 `copy()`로 새로운 객체를 만들어 해당 위치에 저장한다.

---

### Step 7. 화면 처리 (`MemberApp.kt`)와 실행 (`Main.kt`)

**목표**

기존 회원 관리 화면을 `MemberManager`와 연결하고 수정 기능을 새로운 구조에 맞게 변경한다.

**힌트**

```kotlin
class MemberApp(
    private val manager: MemberManager
) {

    fun start() {
        while (true) {
            when (printMenu()) {
                1 -> addMember()
                2 -> selectByEmail()
                3 -> selectByName()
                4 -> selectAll()
                5 -> updateMember()
                6 -> deleteMember()
                7 -> searchByName()
                8 -> printStatistics()

                9 -> {
                    println(
                        "이용해주셔서 감사합니다."
                    )
                    return
                }

                else ->
                    println(
                        "올바른 번호를 입력하세요."
                    )
            }
        }
    }

    private fun selectAll() {
        val all =
            manager.getAll()

        if (all.isEmpty()) {
            println(
                "등록된 회원이 없습니다."
            )
            return
        }

        all.forEachIndexed {
            i,
            member ->

            println(
                "${i + 1}. ${member.display}"
            )
        }
    }
}
```

전체 조회에서는 `forEachIndexed()`를 이용해 인덱스와 회원 객체를 함께 사용할 수 있다.

회원 수정은 `Member`의 프로퍼티를 직접 변경하지 않고 `MemberManager.update()`를 호출한다.

```kotlin
private fun updateMember() {
    println(
        "수정할 회원의 이메일을 입력하세요."
    )

    val email =
        readln()

    val member =
        manager.findByEmail(email)

    if (member == null) {
        println(
            "찾으시는 회원이 없습니다."
        )
        return
    }

    println(
        "현재 정보 → ${member.display}"
    )

    println(
        "새 이름을 입력하세요. (Enter 만 누르면 유지)"
    )

    val name =
        readln().ifBlank {
            member.name
        }

    println(
        "새 이메일을 입력하세요. (Enter 만 누르면 유지)"
    )

    val newEmail =
        readln().ifBlank {
            member.email
        }

    println(
        "새 연락처를 입력하세요. (Enter 만 누르면 유지)"
    )

    val phone =
        readln().ifBlank {
            member.phone
        }

    if (
        manager.update(
            email,
            name,
            newEmail,
            phone
        )
    ) {
        println(
            "수정이 완료되었습니다."
        )
    } else {
        println(
            "이미 사용 중인 이메일입니다."
        )
    }
}
```

`ifBlank()`를 이용하면 사용자가 빈 문자열을 입력한 경우 기존 값을 유지할 수 있다.

**`Main.kt`**

```kotlin
package member

private fun printPricePlan(): Int {
    println(
        "[요금제를 선택하세요]"
    )

    println(
        "[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명"
    )

    print("> ")

    return readln()
        .toIntOrNull()
        ?: 1
}

private fun askSampleData(
    manager: MemberManager
) {

    print(
        "샘플 회원을 넣고 시작할까요? (y/n) > "
    )

    if (
        readln()
            .lowercase() != "y"
    ) {
        return
    }

    listOf(
        Member(
            "김철수",
            "kim@naver.com",
            "010-1111-1111"
        ),
        Member(
            "이영희",
            "lee@naver.com",
            "010-2222-2222"
        ),
        Member(
            "박민수",
            "park@gmail.com",
            "010-3333-3333"
        ),
        Member(
            "김철수",
            "kim2@gmail.com",
            "010-4444-4444"
        )
    ).forEach {
        manager.add(it)
    }

    println(
        "${manager.memberCnt}명을 넣었습니다."
    )
}

fun main() {
    val planNo =
        printPricePlan()

    val manager =
        MemberManager(planNo)

    askSampleData(manager)

    MemberApp(manager)
        .start()
}
```

샘플 회원은 `listOf()`와 `forEach()`를 이용해 추가한다.

저장 방식이 배열에서 리스트로 변경되었지만 사용자 입력과 출력 구조는 대부분 유지된다.

---

### Step 8. 컬렉션 기반 추가 기능

**목표**

`filter`, `groupBy`, `sortedBy`, `joinToString` 등을 활용해 검색과 통계 기능을 구현한다.

**`MemberManager`**

```kotlin
fun sortedByName(): List<Member> =
    members.sortedBy {
        it.name
    }

fun groupByDomain():
    Map<String, List<Member>> =
    members.groupBy {
        it.email.substringAfter(
            "@",
            "(도메인없음)"
        )
    }

fun duplicatedNames():
    Map<String, List<Member>> =
    members
        .groupBy {
            it.name
        }
        .filter {
            it.value.size > 1
        }
```

`sortedBy()`는 원본 순서를 변경하지 않고 정렬된 새 리스트를 반환한다.

`groupBy()`는 기준값을 key로 하고, 해당 기준에 속하는 회원 목록을 value로 갖는 `Map`을 반환한다.

```text
Map<String, List<Member>>
```

**`MemberApp`**

```kotlin
private fun searchByName() {
    println(
        "검색할 이름의 일부를 입력하세요."
    )

    val keyword =
        readln()

    val found =
        manager.searchByName(
            keyword
        )

    if (found.isEmpty()) {
        println(
            "검색 결과가 없습니다."
        )
        return
    }

    println(
        "${found.size}명을 찾았습니다."
    )

    found.forEachIndexed {
        i,
        member ->

        println(
            "${i + 1}. ${member.display}"
        )
    }
}

private fun printStatistics() {
    if (
        manager.memberCnt == 0
    ) {
        println(
            "등록된 회원이 없습니다."
        )
        return
    }

    println(
        "[이메일 도메인별]"
    )

    manager
        .groupByDomain()
        .forEach {
            (domain, list) ->

            println(
                "  $domain : ${list.size}명  " +
                    "(${list.joinToString(", ") { it.name }})"
            )
        }

    println(
        "[이름순]"
    )

    println(
        "  ${
            manager
                .sortedByName()
                .joinToString(", ") {
                    it.name
                }
        }"
    )

    val duplicated =
        manager.duplicatedNames()

    if (
        duplicated.isNotEmpty()
    ) {
        println(
            "[이름이 겹치는 회원]"
        )

        duplicated.forEach {
            (name, list) ->

            println(
                "  $name : ${list.size}명  " +
                    "(${list.joinToString(", ") { it.email }})"
            )
        }
    }
}
```

`forEach { (domain, list) -> ... }`는 `Map.Entry`를 key와 value로 구조 분해해 사용한다.

`joinToString()`은 컬렉션 원소에서 필요한 값을 추출한 뒤 지정한 구분자를 이용해 하나의 문자열로 연결한다.

---

## 7. 네 버전 비교

| 기준        | 배열 버전           | 추상 클래스 버전        | 인터페이스 버전          | 컬렉션 버전                      |
| --------- | --------------- | ---------------- | ----------------- | --------------------------- |
| 주요 학습 내용  | 배열 기반 데이터 관리    | 다형성              | 규약과 구현 분리         | 자료구조와 불변 객체                 |
| 핵심 문법     | 배열, 최상위 함수      | `abstract class` | `interface`, `by` | `MutableList`, `data class` |
| 회원 표현     | `Array<String>` | `Member` 하위 클래스  | `Member`          | `data class Member`         |
| 저장소       | 고정 크기 배열        | `Array<Member?>` | 배열 기반 구현          | `MutableList<Member>`       |
| 빈 요소 처리   | 빈 문자열           | `null`           | 구현 방식에 따라 처리      | 빈 요소 없음                     |
| 회원 수 관리   | 별도 `var`        | `private set`    | 구현체의 프로퍼티         | `members.size`              |
| 검색        | 직접 반복문          | `findIndex()`    | 저장소 구현에 위임        | `find()`                    |
| 삭제        | 직접 요소 이동        | 직접 요소 이동         | 구현에 따라 처리         | `removeAll()`               |
| 수정        | 배열 값 직접 변경      | 객체 프로퍼티 변경       | 저장소 구현에 따라 처리     | `copy()`로 새 객체 교체           |
| 이메일 중복 검사 | 추가 시 검사         | 추가 시 검사          | 구현에 따라 처리         | 추가와 수정 모두 검사                |

컬렉션 버전은 배열을 직접 관리하던 코드를 컬렉션 라이브러리에 위임하고, 대신 객체의 변경 가능 여부와 컬렉션 반환 방식 등을 고려한다.

각 버전은 서로 독립적인 구조가 아니라 함께 결합할 수 있다. 예를 들어 인터페이스 버전의 `MemberStorage` 구현체를 `MutableList` 기반으로 구현하면 인터페이스와 컬렉션 구조를 함께 적용할 수 있다.

---

## 8. 최종 완성 체크리스트

* [ ] `Member`를 `data class`로 선언한다.
* [ ] `name`, `email`, `phone`을 모두 `val`로 선언한다.
* [ ] 사용자 출력용 `display` 커스텀 getter를 구현한다.
* [ ] 빈 회원 생성을 위한 부 생성자를 사용하지 않는다.
* [ ] 회원 저장소를 `mutableListOf<Member>()`로 구성한다.
* [ ] 회원 저장소를 `private`으로 제한한다.
* [ ] `memberCnt`를 별도로 저장하지 않고 `members.size`로 계산한다.
* [ ] `find()`를 이용해 한 명을 조회한다.
* [ ] `filter()`를 이용해 여러 회원을 조회한다.
* [ ] `any()`를 이용해 이메일 중복을 검사한다.
* [ ] `indexOfFirst()`를 이용해 수정 대상 위치를 찾는다.
* [ ] `removeAll()`로 회원을 삭제한다.
* [ ] `getAll()`에서 `toList()`로 새로운 리스트를 반환한다.
* [ ] `copy()`를 이용해 회원 정보를 수정한다.
* [ ] 수정 시 이메일 중복 여부를 검사한다.
* [ ] 기존 이메일과 새 이메일이 다를 때만 중복 검사를 수행한다.
* [ ] 삭제 과정에서 직접 요소 이동 코드를 사용하지 않는다.
* [ ] 회원 수 증가·감소 코드를 직접 작성하지 않는다.
* [ ] `groupBy()`, `sortedBy()`, `joinToString()`을 이용해 통계 기능을 구현한다.

---

## 9. 개선 및 도전 과제

### 9.1 빈 입력 검증

회원 이름이나 이메일에 빈 문자열이 저장되지 않도록 입력 단계에서 `isBlank()`로 검증할 수 있다.

```kotlin
if (name.isBlank()) {
    println(
        "이름은 비워 둘 수 없습니다."
    )
    return
}
```

---

### 9.2 이메일 형식 검증

`Member`의 `init` 블록이나 입력 단계에서 이메일 형식을 검증할 수 있다.

```kotlin
data class Member(
    val name: String,
    val email: String,
    val phone: String
) {

    init {
        require(
            email.contains("@")
        ) {
            "이메일 형식이 아닙니다."
        }
    }
}
```

생성자 내부에서 검증하면 잘못된 상태의 객체 생성을 막을 수 있지만, 예외 처리 방식도 함께 고려해야 한다.

---

### 9.3 수정 결과 세분화

현재 `update()`는 `Boolean`을 반환하므로 회원이 존재하지 않는 경우와 이메일 중복을 하나의 실패 결과로 표현한다.

`enum class`를 이용해 결과를 구분할 수 있다.

```kotlin
enum class UpdateResult {
    OK,
    NOT_FOUND,
    DUPLICATE_EMAIL
}
```

`MemberApp`에서는 반환값에 따라 서로 다른 메시지를 출력할 수 있다.

---

### 9.4 배열 기반 통계 구현

도메인별 통계를 배열만으로 구현해 컬렉션 함수와 비교할 수 있다.

배열에서는 도메인 목록 구성, 중복 제거, 도메인별 회원 수 계산 등을 직접 처리해야 한다.

이를 `groupBy()` 기반 구현과 비교하면 컬렉션 함수가 대신 처리하는 작업을 확인할 수 있다.

---

### 9.5 동명이인 전체 조회

현재 `findByName()`은 `find()`를 사용하므로 동일한 이름을 가진 회원 중 첫 번째 회원만 반환한다.

```kotlin
fun findByName(
    name: String
): Member?
```

이를 `filter()`로 변경하면 같은 이름을 가진 모든 회원을 반환할 수 있다.

```kotlin
fun findByName(
    name: String
): List<Member>
```

반환형 변경에 따라 `MemberApp`의 처리 방식도 함께 수정해야 한다.

---

### 9.6 정렬 기준 확장

이름 외에도 이메일 도메인이나 여러 조건을 기준으로 정렬할 수 있다.

```kotlin
members.sortedWith(
    compareBy(
        {
            it.email
                .substringAfter("@")
        },
        {
            it.name
        }
    )
)
```

`compareBy()`는 앞의 기준이 같은 경우 다음 기준을 순서대로 적용한다.

---

### 9.7 인터페이스 버전과 결합

인터페이스 과제에서 만든 `MemberStorage`를 구현하는 `ListMemberStorage`를 만들 수 있다.

```text
MemberStorage
├── ArrayMemberStorage
└── ListMemberStorage
```

`Main.kt`에서 구현체만 교체해 배열 저장소와 리스트 저장소를 변경할 수 있도록 구성할 수 있다.

---

### 9.8 `Set` 기반 저장

회원 저장소를 `MutableSet<Member>`로 변경할 수 있다.

`data class`의 `equals()`와 `hashCode()`는 모든 주 생성자 프로퍼티를 기준으로 생성된다.

따라서 이름, 이메일, 연락처가 모두 동일한 회원은 중복으로 판단하지만 이메일만 같은 회원은 서로 다른 객체로 판단할 수 있다.

이메일만으로 회원 동일성을 판단하려면 `equals()`와 `hashCode()`를 직접 정의해야 한다.

---

### 9.9 `Map` 기반 저장

이메일을 key로 사용하는 `MutableMap` 기반 저장 구조를 사용할 수 있다.

```kotlin
mutableMapOf<String, Member>()
```

이메일을 key로 사용하면 이메일 조회와 중복 검사를 key 기반으로 처리할 수 있다.

반면 이름 검색이나 별도의 순서 관리에서는 추가 처리가 필요할 수 있다.

자료구조 선택에 따라 효율적인 작업과 불편해지는 작업이 달라진다.

---

### 9.10 추가 통계 함수 활용

다른 컬렉션 함수를 활용해 통계 기능을 확장할 수 있다.

```kotlin
members.count {
    it.email.endsWith(
        "gmail.com"
    )
}

members.partition {
    it.phone.startsWith(
        "010"
    )
}
```

`count()`는 조건에 맞는 원소 개수를 반환하고, `partition()`은 조건 만족 여부에 따라 컬렉션을 두 그룹으로 나눈다.

---

### 9.11 파일 저장

회원 정보를 파일로 저장하고 다시 불러오는 기능을 추가할 수 있다.

저장 시 `joinToString()`을 활용하고, 읽은 데이터를 `split()`과 `map()`을 이용해 `Member` 객체 목록으로 변환할 수 있다.

---

## 10. 정리

이 과제에서는 배열 기반 회원 관리 프로그램을 `MutableList`와 `data class` 중심의 컬렉션 구조로 변경한다.

고정 크기 배열에서 필요했던 빈 공간 초기화, 직접적인 회원 수 관리, 삭제 후 데이터 이동 작업을 제거하고 컬렉션이 제공하는 기능을 활용한다.

`Member`는 모든 프로퍼티를 `val`로 선언하고 `copy()`를 이용해 수정함으로써 직접 상태 변경을 제한한다.

회원 검색, 중복 검사, 삭제, 정렬, 그룹화는 `find`, `any`, `filter`, `removeAll`, `sortedBy`, `groupBy` 등의 컬렉션 함수로 구현한다.

이를 통해 컬렉션 활용, 불변 객체, `data class`, 방어적 복사, 컬렉션 함수 기반 데이터 처리 흐름을 함께 학습한다.
