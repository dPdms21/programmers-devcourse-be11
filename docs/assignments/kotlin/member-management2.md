# 회원 관리 프로그램 만들기 - Kotlin 추상 클래스

배열 기반 회원 관리 프로그램을 객체지향 구조로 개선한다.

회원에 일반 회원과 VIP 회원 등급을 추가하고, `abstract class`를 이용해 회원의 공통 상태와 동작을 정의한다. 등급별로 달라지는 월회비와 전용 기능은 각 하위 클래스에서 구현한다.

선행 과제는 배열을 이용한 회원 관리 프로그램이다.

---

## 1. 구현 기능

기본적인 회원 관리 기능은 배열 버전과 동일하다.

회원 추가 시 등급을 선택하며, 전체 회원 조회에서는 각 회원의 월회비와 월 예상 매출을 함께 출력한다.

```text
[요금제를 선택하세요]
[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명
> 1

[수행할 업무를 선택하세요 - 현재 회원수 : 0/10]
[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)
[4]회원전체조회 [5]회원정보 수정 [6]회원삭제
[7]프로그램 종료
> 1

이름을 입력하세요.
> 홍길동
이메일을 입력하세요.
> hong@a.com
연락처를 입력하세요.
> 010-1111-1111
등급을 선택하세요. [1]일반(10000원) [2]VIP(8000원)
> 2

VIP 회원이 등록되었습니다.
```

전체 조회에서는 회원별 정보와 월회비를 출력하고 월 예상 매출을 계산한다.

```text
1. [일반] [이름] 홍길동, [이메일] hong@a.com, [연락처] 010-1, [월회비] 10000원
2. [VIP] [이름] 김VIP, [이메일] vip@a.com, [연락처] 010-2, [월회비] 8000원
월 예상 매출 : 18000원
```

VIP 회원을 이메일로 조회하는 경우 VIP 전용 사은품 안내를 추가한다.

```text
[VIP] [이름] 김VIP, [이메일] vip@a.com, [연락처] 010-2, [월회비] 8000원
  → 김VIP 님께 VIP 사은품을 보냈습니다.
```

---

## 2. 요구사항

| 번호 | 기능        | 설명                                                         |
| -- | --------- | ---------------------------------------------------------- |
| 0  | 요금제 선택    | Lite(10명), Basic(20명), Premium(30명) 중 하나를 선택해 회원 정원을 결정한다. |
| 1  | 회원 추가     | 이름, 이메일, 연락처, 등급을 입력받아 회원을 추가하고 정원 초과와 이메일 중복을 방지한다.       |
| 2  | 회원 조회(메일) | 이메일로 회원을 조회하며 VIP 회원이면 사은품 안내를 추가한다.                       |
| 3  | 회원 조회(이름) | 이름으로 회원을 조회한다.                                             |
| 4  | 회원 전체 조회  | 모든 회원과 월회비를 출력하고 월 예상 매출을 계산한다.                            |
| 5  | 회원 정보 수정  | 이름, 이메일, 연락처를 수정하며 등급은 변경하지 않는다.                           |
| 6  | 회원 삭제     | 이메일로 회원을 찾아 삭제하고 뒤 회원들을 앞으로 이동한다.                          |
| 7  | 종료        | 프로그램을 종료한다.                                                |

### 등급별 규칙

| 등급  |     월회비 | 전용 기능               |
| --- | ------: | ------------------- |
| 일반  | 10,000원 | 없음                  |
| VIP |  8,000원 | 사은품 발송 (`sendGift`) |

---

## 3. 학습 목표

| 개념               | 활용                                                          |
| ---------------- | ----------------------------------------------------------- |
| `abstract class` | 회원의 공통 상태와 동작을 `Member`에 정의한다.                              |
| 추상 프로퍼티          | `abstract val grade: String`으로 등급 구현을 하위 클래스에 위임한다.         |
| 추상 메서드           | `abstract fun monthlyFee(): Int`로 등급별 월회비 계산을 하위 클래스에 위임한다. |
| 상속과 `override`   | `NormalMember`, `VipMember`가 `Member`를 상속하고 필요한 동작을 재정의한다.  |
| `toString()` 재정의 | 회원 객체를 직접 출력할 수 있도록 회원 정보 출력 형식을 정의한다.                      |
| 다형성              | 하나의 `Member` 배열에 서로 다른 회원 등급 객체를 함께 저장한다.                   |
| 업캐스팅             | 하위 클래스 객체를 `Member` 타입으로 전달한다.                              |
| `is` 스마트 캐스트     | VIP 회원인 경우에만 `sendGift()`를 호출한다.                            |
| 캡슐화              | 회원 배열과 회원 수 변경 권한을 `MemberManager` 내부로 제한한다.                |
| `Array<Member?>` | 추상 클래스 객체를 직접 생성할 수 없으므로 빈 배열 요소를 `null`로 관리한다.             |
| 파일 분리            | 회원, 관리 로직, 입출력, 실행 코드를 역할별 파일로 분리한다.                        |

---

## 4. 핵심 개념

### 4.1 배열 기반 구조에서 클래스로 변경

배열 버전에서는 회원 상태와 이를 처리하는 함수가 분리되어 있었다.

```kotlin
var totalCnt = 0
var memberCnt = 0
```

파일 최상위에 선언된 변수는 여러 함수에서 직접 변경할 수 있다.

또한 회원 정보를 문자열 배열로 저장하면 각 인덱스의 의미를 기억해야 한다.

```kotlin
members[i][0] // 이름
members[i][1] // 이메일
members[i][2] // 연락처
```

객체지향 구조에서는 회원 데이터와 관련 기능을 클래스로 묶어 관리한다.

```kotlin
member.name
member.email
member.phone
```

이를 통해 데이터의 의미를 이름으로 표현하고 상태 변경 범위를 클래스 내부로 제한할 수 있다.

---

### 4.2 등급별 동작을 하위 클래스로 분리

회원 등급에 따라 월회비가 달라지는 기능을 하나의 클래스 안에서 조건문으로 처리할 수 있다.

```kotlin
class Member(
    var name: String,
    var grade: String
) {
    fun monthlyFee(): Int {
        if (grade == "VIP") {
            return 8000
        }

        return 10000
    }
}
```

이 방식은 새로운 등급이 추가될 때 등급을 확인하는 조건문도 함께 수정해야 한다.

추상 클래스를 이용하면 등급에 따라 달라지는 동작을 하위 클래스에 분리할 수 있다.

```kotlin
abstract class Member(var name: String) {
    abstract fun monthlyFee(): Int
}

class NormalMember(name: String) : Member(name) {
    override fun monthlyFee() = 10000
}

class VipMember(name: String) : Member(name) {
    override fun monthlyFee() = 8000
}
```

상위 클래스는 공통 구조를 정의하고, 실제 월회비 계산은 각 하위 클래스가 담당한다.

---

### 4.3 `abstract class`

추상 클래스는 공통 상태와 구현을 가지면서 일부 동작을 하위 클래스에 구현하도록 요구할 수 있다.

```kotlin
abstract class Member(
    var name: String,
    var email: String,
    var phone: String
) {

    abstract val grade: String

    abstract fun monthlyFee(): Int

    override fun toString(): String {
        return "[$grade] [이름] $name, [이메일] $email, [연락처] $phone, [월회비] ${monthlyFee()}원"
    }
}
```

`Member`는 추상 클래스이므로 직접 객체를 생성할 수 없다.

```kotlin
Member("홍길동", "hong@a.com", "010")
```

위 코드는 컴파일되지 않는다.

하위 클래스는 `grade`와 `monthlyFee()`를 반드시 구현해야 한다.

상위 클래스의 `toString()`에서 `monthlyFee()`를 호출하면 실제 객체의 타입에 따라 재정의된 메서드가 실행된다.

---

### 4.4 다형성을 이용한 회원 관리

`MemberManager`는 회원의 구체적인 등급을 구분하지 않고 모두 `Member` 타입으로 관리한다.

```kotlin
private val members = arrayOfNulls<Member>(totalCnt)
```

회원 추가 함수 역시 `Member` 타입을 받는다.

```kotlin
fun add(member: Member): Boolean {
    ...
}
```

따라서 `NormalMember`와 `VipMember`를 동일한 배열에 저장할 수 있다.

월 예상 매출을 계산할 때도 등급을 직접 확인할 필요가 없다.

```kotlin
fun totalMonthlyFee(): Int {
    var sum = 0

    for (i in 0 until memberCnt) {
        sum += members[i]!!.monthlyFee()
    }

    return sum
}
```

각 객체가 자신의 `monthlyFee()` 구현을 실행하므로 `if`나 `when`을 이용해 등급을 구분하지 않아도 된다.

---

### 4.5 `Array<Member?>`와 null 처리

배열 기반 버전에서는 빈 회원 정보를 빈 문자열 배열로 초기화할 수 있었다.

```kotlin
Array(totalCnt) { Array(3) { "" } }
```

하지만 `Member`는 추상 클래스이므로 빈 `Member` 객체를 생성할 수 없다.

```kotlin
Array(totalCnt) {
    Member("", "", "")
}
```

따라서 회원 배열의 빈 요소는 `null`로 관리한다.

```kotlin
private val members = arrayOfNulls<Member>(totalCnt)
```

자료형은 다음과 같다.

```text
Array<Member?>
```

회원 정보에 접근할 때는 nullable 처리가 필요하다.

```kotlin
members[i]?.email
```

회원이 반드시 존재하는 범위라는 것을 코드 구조에서 보장하는 경우에는 `!!`을 사용할 수 있다.

```kotlin
members[i]!!.monthlyFee()
```

---

### 4.6 `is`와 스마트 캐스트

`sendGift()`는 `VipMember`에만 존재하는 기능이다.

`Member` 타입으로 조회한 객체에서는 직접 호출할 수 없다.

```kotlin
val member: Member = manager.findByEmail(email) ?: return
```

`is`를 이용해 타입을 확인하면 해당 블록에서 자동으로 `VipMember` 타입으로 처리된다.

```kotlin
if (member is VipMember) {
    member.sendGift()
}
```

별도의 명시적 형변환 없이 하위 클래스 전용 기능을 사용할 수 있다.

---

## 5. 파일 구조

배열 버전과 달리 역할별 클래스를 분리한다.

```text
src/main/kotlin/member_abstract/
├── Member.kt
├── MemberManager.kt
├── MemberApp.kt
└── Main.kt
```

| 파일                 | 역할                                                  |
| ------------------ | --------------------------------------------------- |
| `Member.kt`        | 추상 클래스 `Member`와 `NormalMember`, `VipMember`를 정의한다. |
| `MemberManager.kt` | 회원 목록과 추가, 조회, 삭제 등의 관리 규칙을 담당한다.                   |
| `MemberApp.kt`     | 메뉴 출력과 사용자 입력을 담당한다.                                |
| `Main.kt`          | 객체를 생성하고 연결해 프로그램을 실행한다.                            |

Kotlin은 파일 하나에 클래스 하나만 선언하도록 제한하지 않는다.

공통 부모와 관련 하위 클래스인 `Member`, `NormalMember`, `VipMember`는 하나의 `Member.kt`에 함께 정의한다.

---

## 6. Step by Step

### Step 1. 회원 클래스 구현 (`Member.kt`)

**목표**

추상 클래스 `Member`와 하위 클래스 `NormalMember`, `VipMember`를 구현한다.

**구현 내용**

1. `Member`를 `abstract class`로 선언한다.
2. 주 생성자로 `name`, `email`, `phone`을 받는다.
3. `abstract val grade: String`을 선언한다.
4. `abstract fun monthlyFee(): Int`를 선언한다.
5. `toString()`을 재정의한다.
6. `NormalMember`, `VipMember`에서 추상 멤버를 구현한다.
7. `VipMember`에 `sendGift()`를 추가한다.

**힌트**

```kotlin
package member_abstract

abstract class Member(
    var name: String,
    var email: String,
    var phone: String
) {

    abstract val grade: String

    abstract fun monthlyFee(): Int

    override fun toString(): String {
        return "[$grade] [이름] $name, [이메일] $email, [연락처] $phone, [월회비] ${monthlyFee()}원"
    }
}

class NormalMember(
    name: String,
    email: String,
    phone: String
) : Member(name, email, phone) {

    override val grade = "일반"

    override fun monthlyFee() = 10000
}

class VipMember(
    name: String,
    email: String,
    phone: String
) : Member(name, email, phone) {

    override val grade = "VIP"

    override fun monthlyFee() = 8000

    fun sendGift() {
        println("  → $name 님께 VIP 사은품을 보냈습니다.")
    }
}
```

하위 클래스의 생성자 매개변수는 상위 클래스 생성자로 전달하기 위한 값이므로 별도의 프로퍼티가 필요하지 않은 경우 `val`이나 `var`를 붙이지 않는다.

`abstract` 멤버는 재정의를 전제로 하므로 별도의 `open` 선언이 필요하지 않다.

**확인**

`VipMember` 객체를 생성해 출력했을 때 등급과 월회비가 포함된 회원 정보가 정상적으로 출력되는지 확인한다.

`Member`를 직접 생성했을 때 컴파일 오류가 발생하는지도 확인한다.

---

### Step 2. 회원 관리자 구현 (`MemberManager.kt`)

**목표**

회원 배열을 내부에 보관하고 회원 정원과 현재 회원 수를 관리하는 클래스를 구현한다.

**구현 내용**

1. 생성자로 요금제 번호를 받는다.
2. `totalCnt`를 요금제 번호를 기준으로 계산한다.
3. `memberCnt`는 외부에서 읽을 수 있지만 직접 변경할 수 없도록 `private set`을 적용한다.
4. 회원 배열은 `private`으로 선언한다.
5. 현재 정원이 가득 찼는지 확인하는 `isFull` 프로퍼티를 구현한다.

**힌트**

```kotlin
class MemberManager(planNo: Int) {

    val totalCnt = planNo * 10

    var memberCnt = 0
        private set

    private val members = arrayOfNulls<Member>(totalCnt)

    val isFull: Boolean
        get() = memberCnt == totalCnt
}
```

`planNo`는 `totalCnt` 계산에만 사용하므로 별도 프로퍼티로 저장하지 않는다.

`memberCnt`에 `private set`을 적용하면 외부에서는 값을 조회할 수 있지만 직접 변경할 수 없다.

**확인**

다음과 같이 외부에서 `memberCnt`를 변경하려고 했을 때 컴파일되지 않는지 확인한다.

```kotlin
MemberManager(1).memberCnt = 5
```

---

### Step 3. 회원 검색과 추가

**목표**

이메일과 이름으로 회원을 검색하고 회원 추가 기능을 구현한다.

**구현 내용**

1. `findIndex(email: String): Int`로 이메일에 해당하는 내부 배열 인덱스를 찾는다.
2. `findByEmail(email: String): Member?`를 구현한다.
3. `findByName(name: String): Member?`를 구현한다.
4. `add(member: Member): Boolean`로 정원과 중복을 확인하고 회원을 추가한다.

**힌트**

```kotlin
private fun findIndex(email: String): Int {
    for (i in 0 until memberCnt) {
        if (members[i]?.email == email) {
            return i
        }
    }

    return -1
}

fun findByEmail(email: String): Member? {
    val idx = findIndex(email)

    return if (idx == -1) {
        null
    } else {
        members[idx]
    }
}

fun findByName(name: String): Member? {
    for (i in 0 until memberCnt) {
        if (members[i]?.name == name) {
            return members[i]
        }
    }

    return null
}

fun add(member: Member): Boolean {
    if (isFull) {
        return false
    }

    if (findIndex(member.email) != -1) {
        return false
    }

    members[memberCnt] = member
    memberCnt++

    return true
}
```

`findIndex()`는 배열 내부 인덱스를 다루는 구현 세부사항이므로 `private`으로 제한한다.

객체를 반환하는 `findByEmail()`과 `findByName()`은 회원을 찾지 못한 경우 `null`을 반환한다.

`add()`는 사용자 메시지를 출력하지 않고 성공 여부만 `Boolean`으로 반환한다. 사용자 출력은 `MemberApp`에서 처리한다.

**확인**

컴파일이 정상적으로 완료되는지 확인한다.

---

### Step 4. 삭제, 전체 목록, 월회비 합계

**목표**

회원 삭제, 전체 회원 조회를 위한 목록 반환, 월 예상 매출 계산 기능을 구현한다.

**힌트**

```kotlin
fun delete(email: String): Boolean {
    val idx = findIndex(email)

    if (idx == -1) {
        return false
    }

    for (i in idx until memberCnt - 1) {
        members[i] = members[i + 1]
    }

    memberCnt--
    members[memberCnt] = null

    return true
}

fun getAll(): Array<Member> {
    return Array(memberCnt) {
        members[it]!!
    }
}

fun totalMonthlyFee(): Int {
    var sum = 0

    for (i in 0 until memberCnt) {
        sum += members[i]!!.monthlyFee()
    }

    return sum
}
```

회원 객체는 이름, 이메일, 연락처를 하나의 객체로 묶어 저장하므로 삭제 과정에서 회원 객체 참조 하나만 이동하면 된다.

삭제 순서는 다음과 같다.

```text
뒤의 회원 이동 → memberCnt 감소 → 마지막 요소 null 처리
```

`getAll()`은 내부 배열을 그대로 외부에 노출하지 않고 현재 등록된 회원만 새로운 배열로 만들어 반환한다.

`0 until memberCnt` 범위에는 회원이 존재하는 것을 `MemberManager`가 보장하므로 반환 자료형은 `Array<Member>`로 구성할 수 있다.

---

### Step 5. 화면 처리 구현 (`MemberApp.kt`)

**목표**

회원 관리 메뉴와 사용자 입력 흐름을 담당하는 클래스를 구현한다.

**힌트**

```kotlin
class MemberApp(
    private val manager: MemberManager
) {

    fun start() {
        while (true) {
            when (printMenu()) {
                1 -> { /* Step 6 */ }
                2 -> { /* Step 7 */ }
                3 -> { /* Step 7 */ }
                4 -> { /* Step 7 */ }
                5 -> { /* Step 8 */ }
                6 -> { /* Step 8 */ }

                7 -> {
                    println("이용해주셔서 감사합니다.")
                    return
                }

                else -> println("올바른 번호를 입력하세요.")
            }
        }
    }

    private fun printMenu(): Int {
        println("\n[수행할 업무를 선택하세요 - 현재 회원수 : ${manager.memberCnt}/${manager.totalCnt}]")
        println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
        println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제")
        println("[7]프로그램 종료")
        print("> ")

        return readln().toIntOrNull() ?: -1
    }
}
```

`MemberApp`은 `MemberManager`를 내부에서 직접 생성하지 않고 생성자를 통해 전달받는다.

메뉴 출력과 사용자 입력을 처리하는 함수는 클래스 외부에서 직접 사용할 필요가 없으므로 `private`으로 제한한다.

---

### Step 6. 회원 추가

**목표**

회원 등급을 입력받아 `NormalMember` 또는 `VipMember` 객체를 생성하고 관리자에 전달한다.

**힌트**

```kotlin
private fun addMember() {
    if (manager.isFull) {
        println("회원이 꽉 찼습니다.")
        return
    }

    println("이름을 입력하세요.")
    val name = readln()

    println("이메일을 입력하세요.")
    val email = readln()

    println("연락처를 입력하세요.")
    val phone = readln()

    println("등급을 선택하세요. [1]일반(10000원) [2]VIP(8000원)")
    val gradeNo = readln().toIntOrNull() ?: 1

    val member = if (gradeNo == 2) {
        VipMember(name, email, phone)
    } else {
        NormalMember(name, email, phone)
    }

    if (manager.add(member)) {
        println("${member.grade} 회원이 등록되었습니다.")
    } else {
        println("이미 존재하는 회원입니다.")
    }
}
```

`if`의 두 분기에서 각각 `VipMember`와 `NormalMember` 객체를 반환하지만 공통 상위 타입인 `Member`로 사용할 수 있다.

구체적인 회원 등급 객체를 선택하는 로직은 `MemberApp`의 회원 추가 과정에만 존재하며 이후 관리 로직은 `Member` 타입을 기준으로 처리한다.

---

### Step 7. 회원 조회

**목표**

이메일, 이름, 전체 회원 조회 기능을 구현하고 VIP 회원의 전용 기능을 처리한다.

**힌트**

```kotlin
private fun selectByEmail() {
    println("이메일을 입력하세요.")
    val email = readln()

    val member = manager.findByEmail(email)

    if (member == null) {
        println("찾으시는 정보가 없습니다.")
        return
    }

    println(member)

    if (member is VipMember) {
        member.sendGift()
    }
}

private fun selectAll() {
    val all = manager.getAll()

    if (all.isEmpty()) {
        println("등록된 회원이 없습니다.")
        return
    }

    for (i in all.indices) {
        println("${i + 1}. ${all[i]}")
    }

    println("월 예상 매출 : ${manager.totalMonthlyFee()}원")
}
```

`println(member)`를 호출하면 `Member`에서 재정의한 `toString()`을 이용해 회원 정보를 출력한다.

VIP 전용 기능은 `is VipMember`로 타입을 확인한 뒤 스마트 캐스트를 이용해 호출한다.

전체 조회에서는 각 객체의 `monthlyFee()` 구현을 이용해 월 예상 매출을 계산한다.

---

### Step 8. 회원 수정과 삭제

**목표**

회원 정보 수정과 삭제 기능을 `MemberApp`에 연결한다.

**힌트**

```kotlin
private fun updateMember() {
    println("수정할 회원의 이메일을 입력하세요.")
    val email = readln()

    val member = manager.findByEmail(email)

    if (member == null) {
        println("찾으시는 회원이 없습니다.")
        return
    }

    println("현재 정보 → $member")

    println("새 이름을 입력하세요.")
    member.name = readln()

    println("새 이메일을 입력하세요.")
    member.email = readln()

    println("새 연락처를 입력하세요.")
    member.phone = readln()

    println("수정이 완료되었습니다.")
}

private fun deleteMember() {
    println("삭제할 회원의 이메일을 입력하세요.")
    val email = readln()

    if (manager.delete(email)) {
        println("삭제가 완료되었습니다.")
    } else {
        println("찾으시는 회원이 없습니다.")
    }
}
```

`findByEmail()`은 관리자가 보관하고 있는 회원 객체를 반환하므로 반환된 객체의 프로퍼티를 변경하면 저장된 회원 정보도 함께 변경된다.

현재 구조에서는 수정 과정이 `MemberManager`를 거치지 않으므로 새로운 이메일에 대한 중복 검사를 수행하지 않는 한계가 있다.

회원 등급은 `grade`가 `val`이며 객체 생성 후 실제 클래스 타입을 변경할 수 없으므로 수정 대상에서 제외한다.

---

### Step 9. 프로그램 조립 (`Main.kt`)

**목표**

요금제를 선택하고 필요한 객체를 생성해 프로그램을 실행한다.

**힌트**

```kotlin
package member_abstract

private fun printPricePlan(): Int {
    println("[요금제를 선택하세요]")
    println("[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명")
    print("> ")

    return readln().toIntOrNull() ?: 1
}

fun main() {
    val planNo = printPricePlan()

    val app = MemberApp(MemberManager(planNo))
    app.start()
}
```

`main()`은 필요한 객체를 생성하고 연결한 뒤 실행만 담당한다.

메뉴 반복과 기능 실행 로직은 `MemberApp.start()`에서 처리한다.

---

## 7. 배열 버전과의 차이

| 배열 버전                                  | 추상 클래스 버전                            |
| -------------------------------------- | ------------------------------------ |
| `totalCnt`, `memberCnt`를 파일 최상위 변수로 관리 | `MemberManager`의 프로퍼티로 관리            |
| `memberCnt`를 외부에서 직접 변경할 수 있음          | `private set`으로 변경 권한 제한             |
| `Array(3) { "" }` 한 행으로 회원 표현          | `Member` 객체 하나로 회원 표현                |
| `members[i][1]`                        | `member.email`                       |
| 배열을 각 함수에 전달                           | `MemberManager`가 배열을 내부에서 관리         |
| 열 번호를 이용해 검색                           | `findByEmail()`, `findByName()`으로 구분 |
| 검색 실패를 `-1`로 표현                        | 객체 검색 실패를 `null`로 표현                 |
| 별도의 `printMember()` 사용                 | `toString()` 재정의                     |
| 등급에 따른 조건 분기 필요                        | 다형성을 이용해 등급별 동작 구현                   |
| 삭제 시 문자열 세 칸을 각각 이동                    | 회원 객체 참조 하나를 이동                      |
| 빈 요소를 `""`로 초기화                        | `null`로 관리하고 nullable 처리             |
| 하나의 파일에 구현                             | 역할별 파일로 분리                           |

---

## 8. 최종 완성 체크리스트

* [ ] `Member`를 `abstract class`로 선언한다.
* [ ] `grade`와 `monthlyFee()`를 추상 멤버로 선언한다.
* [ ] `NormalMember`, `VipMember`에서 추상 멤버를 재정의한다.
* [ ] `toString()`을 재정의해 회원 정보를 출력한다.
* [ ] `MemberManager`의 회원 배열을 `private`으로 관리한다.
* [ ] `memberCnt`에 `private set`을 적용한다.
* [ ] `isFull`을 커스텀 getter로 구현한다.
* [ ] `findByEmail()`, `findByName()`이 `Member?`를 반환한다.
* [ ] `add()`, `delete()`가 `Boolean`을 반환하고 화면 출력을 담당하지 않는다.
* [ ] `totalMonthlyFee()`에서 등급별 `if`, `when`을 사용하지 않는다.
* [ ] `MemberApp`이 생성자로 `MemberManager`를 전달받는다.
* [ ] `is VipMember`와 스마트 캐스트를 이용해 VIP 전용 기능을 호출한다.
* [ ] `main()`은 객체 생성과 실행 연결만 담당한다.

---

## 9. 개선 및 도전 과제

### 9.1 회원 수정 로직 개선

현재 `updateMember()`는 조회한 회원 객체를 직접 수정한다.

새 이메일이 기존 회원과 중복되는지 확인하지 않으므로 `MemberManager`에 다음과 같은 수정 기능을 추가할 수 있다.

```kotlin
update(
    email: String,
    newName: String,
    newEmail: String,
    newPhone: String
): Boolean
```

회원 관리 규칙을 `MemberManager` 내부에서 처리하면 중복 검사와 상태 변경을 한 곳에서 관리할 수 있다.

---

### 9.2 빈 입력 검증

이름, 이메일, 연락처에 빈 값이 저장되지 않도록 `Member`의 `init` 블록이나 `MemberApp`의 입력 과정에서 `isBlank()`를 이용해 검증할 수 있다.

---

### 9.3 실패 결과 세분화

`add()`의 반환값이 `Boolean`이므로 회원 추가 실패 원인이 정원 초과인지 이메일 중복인지 구분할 수 없다.

여러 종류의 결과를 표현할 수 있는 자료형을 적용하면 실패 원인을 명확하게 전달할 수 있다.

---

### 9.4 회원 등급 추가

새로운 회원 등급 클래스를 추가하고 `Member`의 추상 멤버를 구현해 기존 다형성 구조를 확장할 수 있다.

예를 들어 학생 회원의 월회비를 5,000원으로 추가할 수 있다.

```kotlin
class StudentMember(
    name: String,
    email: String,
    phone: String
) : Member(name, email, phone) {

    override val grade = "학생"

    override fun monthlyFee() = 5000
}
```

기존 `MemberManager`는 구체적인 회원 타입을 사용하지 않으므로 수정하지 않고 그대로 사용할 수 있다.

---

### 9.5 회원 등급 변경

객체 생성 이후 클래스 자체를 변경할 수 없으므로 일반 회원을 VIP 회원으로 변경하려면 기존 정보로 새로운 `VipMember` 객체를 생성해야 한다.

이를 `MemberManager`의 기능으로 분리할 수 있다.

---

### 9.6 요금 구조 변경

등급별로 월회비 전체 값을 구현하는 대신 상위 클래스에 기본 요금을 정의하고 각 하위 클래스가 할인율만 제공하도록 변경할 수 있다.

```kotlin
abstract val discountRate: Int

fun monthlyFee(): Int {
    return 10000 * (100 - discountRate) / 100
}
```

공통 계산 규칙은 상위 클래스에 두고 등급별 차이만 하위 클래스에서 구현한다.

---

### 9.7 특정 등급 조회

전체 회원 중 `VipMember`만 선택해 출력하는 기능을 추가할 수 있다.

```kotlin
if (member is VipMember) {
    ...
}
```

---

### 9.8 정렬 조회

전체 회원 목록을 월회비 기준으로 정렬해 출력하도록 확장할 수 있다.

---

### 9.9 인터페이스 구조와 비교

`abstract class` 대신 `interface`를 사용하면 생성자를 통해 공통 상태를 전달할 수 없다.

따라서 `name`, `email`, `phone` 등의 상태를 각 구현 클래스에서 직접 정의해야 한다.

공통 상태를 함께 관리해야 하는 현재 구조에서는 추상 클래스를 이용할 수 있으며, 이후 인터페이스 기반 구현과 구조 차이를 비교할 수 있다.

---

## 10. 정리

이 과제에서는 배열 기반 회원 관리 프로그램을 추상 클래스와 다형성을 이용한 객체지향 구조로 개선한다.

회원의 공통 상태와 규약은 `Member`에 정의하고, 등급마다 달라지는 동작은 하위 클래스가 구현한다.

`MemberManager`는 구체적인 회원 등급과 분리된 상태로 회원 목록과 관리 규칙을 담당하며, `MemberApp`은 사용자 입력과 출력 흐름을 담당한다.

이를 통해 상속, 추상 클래스, 다형성, 캡슐화, nullable 배열과 스마트 캐스트를 함께 적용한다.
