# 회원 관리 프로그램 만들기 - Kotlin 인터페이스와 위임

기존 회원 관리 프로그램을 인터페이스 기반 구조로 변경해 저장 방식을 교체할 수 있도록 구현한다.

`MemberStorage` 인터페이스로 저장소의 공통 규약을 정의하고, 배열 기반 저장소와 로그 기능을 추가한 저장소를 각각 구현한다. `MemberApp`은 구체적인 저장 방식이 아닌 인터페이스에만 의존하도록 구성한다.

선행 과제는 배열 기반 회원 관리와 추상 클래스 기반 회원 관리 프로그램이다.

---

## 1. 구현 기능

회원 추가, 조회, 수정, 삭제 기능은 기존 회원 관리 프로그램과 동일하다.

이번 과제에서는 프로그램 시작 시 사용 중인 저장소를 출력하고, 회원 추가와 삭제 과정에서 로그를 기록한다.

```text
[요금제를 선택하세요]
[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명
> 1

저장소: 배열 저장소(정원 10명) + 로그

[수행할 업무를 선택하세요 - 현재 회원수 : 0명]
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

[LOG] add(hong@a.com) -> true
회원이 등록되었습니다.
```

같은 이메일을 추가하면 저장 실패 결과를 로그로 출력한다.

```text
[LOG] add(hong@a.com) -> false
이미 존재하는 회원입니다.
```

저장소 구현은 `Main.kt`에서 교체한다.

```kotlin
val storage: MemberStorage =
    LoggingMemberStorage(ArrayMemberStorage(planNo))
```

로그가 필요하지 않은 경우 다음과 같이 변경할 수 있다.

```kotlin
val storage: MemberStorage =
    ArrayMemberStorage(planNo)
```

`MemberApp`은 `MemberStorage`에만 의존하므로 저장소 구현을 변경해도 수정할 필요가 없다.

---

## 2. 요구사항

| 구분 | 기능     | 설명                                                              |
| -- | ------ | --------------------------------------------------------------- |
| 0  | 요금제 선택 | Lite(10명), Basic(20명), Premium(30명) 중 하나를 선택해 배열 저장소의 정원을 결정한다. |
| 1  | 회원 추가  | 이름, 이메일, 연락처를 입력받아 저장하며 정원 초과와 이메일 중복을 방지한다.                    |
| 2  | 이메일 조회 | 이메일을 기준으로 회원을 조회한다.                                             |
| 3  | 이름 조회  | 이름을 기준으로 회원을 조회한다.                                              |
| 4  | 전체 조회  | 등록된 회원 전체를 조회한다.                                                |
| 5  | 회원 수정  | 회원 정보를 수정한다.                                                    |
| 6  | 회원 삭제  | 이메일을 기준으로 회원을 삭제한다.                                             |
| 7  | 종료     | 프로그램을 종료한다.                                                     |
| A  | 저장소 규약 | `MemberStorage` 인터페이스로 저장소가 제공해야 하는 기능을 정의한다.                   |
| B  | 배열 저장소 | `ArrayMemberStorage`에서 배열을 이용한 실제 저장 기능을 구현한다.                  |
| C  | 로그 저장소 | `LoggingMemberStorage`에서 다른 저장소에 기능을 위임하고 로그를 추가한다.             |
| D  | 구현 교체  | `Main.kt`에서 저장소 구현만 변경해 애플리케이션 동작을 교체할 수 있도록 한다.                |

---

## 3. 학습 목표

| 개념                | 활용                                                             |
| ----------------- | -------------------------------------------------------------- |
| `interface`       | `MemberStorage`에서 저장소가 제공해야 하는 공통 규약을 정의한다.                    |
| 인터페이스 프로퍼티        | `memberCnt`, `isFull`, `storageName`과 같이 구현체가 제공해야 하는 값을 선언한다. |
| 기본 구현             | `isEmpty()`를 인터페이스에 구현해 모든 저장소에서 공통으로 사용한다.                    |
| 구현과 `override`    | `ArrayMemberStorage`가 `MemberStorage`의 기능을 구현한다.               |
| `val` → `var` 재정의 | 인터페이스의 읽기 전용 프로퍼티를 구현 클래스에서 변경 가능한 프로퍼티로 확장한다.                 |
| 인터페이스 의존          | `MemberApp`이 구체 클래스가 아닌 `MemberStorage` 타입을 사용한다.              |
| 위임 (`by`)         | `LoggingMemberStorage`가 기존 저장소 기능을 `origin`에 위임한다.             |
| 래핑                | 기존 구현을 수정하지 않고 로그 기능을 추가한다.                                    |
| 캡슐화               | 배열, 정원, 검색 인덱스 등 구현 세부사항을 외부에 노출하지 않는다.                        |
| 추상 클래스와 인터페이스 비교  | 공통 상태와 규약을 각각 어떤 구조로 표현하는지 비교한다.                               |

---

## 4. 핵심 개념

### 4.1 구체 구현이 아닌 규약에 의존하기

추상 클래스 버전에서는 `MemberApp`이 구체적인 `MemberManager`를 직접 사용한다.

```kotlin
class MemberApp(
    private val manager: MemberManager
)
```

이 구조에서는 관리 방식이 변경되면 `MemberApp`도 영향을 받을 수 있다.

인터페이스를 사용하면 `MemberApp`이 구체 구현 대신 저장소의 규약에만 의존하도록 만들 수 있다.

```text
MemberApp
    ↓
MemberStorage
   ↙     ↘
ArrayMemberStorage
LoggingMemberStorage
```

```kotlin
class MemberApp(
    private val storage: MemberStorage
)
```

`MemberApp`은 회원 추가, 조회, 삭제와 같은 기능만 알고 있으며 실제 저장 방식은 알 필요가 없다.

---

### 4.2 인터페이스의 프로퍼티와 기본 구현

Kotlin 인터페이스에서는 프로퍼티와 기본 구현이 있는 함수를 선언할 수 있다.

```kotlin
interface MemberStorage {

    val memberCnt: Int

    fun add(member: Member): Boolean

    fun isEmpty(): Boolean {
        return memberCnt == 0
    }
}
```

인터페이스 프로퍼티는 저장 상태 자체를 보관하지 않고 구현체가 값을 제공하도록 요구한다.

기본 구현이 있는 `isEmpty()`는 구현 클래스에서 다시 작성하지 않아도 사용할 수 있다.

---

### 4.3 인터페이스에 구현 세부사항을 포함하지 않기

기존 배열 기반 관리 구조에는 다음과 같은 값이 존재한다.

```kotlin
totalCnt
findIndex()
```

하지만 두 요소는 모든 저장소에 필요한 기능이 아니다.

`totalCnt`는 고정 크기 배열을 사용하는 구현에서만 필요한 정원 정보이며, `findIndex()`는 배열 인덱스를 이용하기 때문에 필요한 내부 구현이다.

따라서 인터페이스에는 저장 방식과 관계없이 공통으로 제공할 수 있는 기능만 포함한다.

| 항목              | 인터페이스 포함 여부 | 이유                          |
| --------------- | ----------- | --------------------------- |
| `add()`         | 포함          | 모든 저장소에서 회원을 추가할 수 있어야 한다.  |
| `delete()`      | 포함          | 모든 저장소에서 회원을 삭제할 수 있어야 한다.  |
| `findByEmail()` | 포함          | 저장 방식과 관계없이 회원 조회 기능이 필요하다. |
| `memberCnt`     | 포함          | 모든 저장소에서 현재 회원 수를 제공할 수 있다. |
| `isFull`        | 포함          | 각 저장소가 추가 가능 여부를 판단할 수 있다.  |
| `totalCnt`      | 제외          | 배열 구현의 정원 개념에 종속된다.         |
| `findIndex()`   | 제외          | 배열 인덱스를 사용하는 구현 세부사항이다.     |

인터페이스에는 구현 방법이 아니라 외부에서 필요한 기능을 정의한다.

---

### 4.4 위임을 이용한 기능 확장

`LoggingMemberStorage`는 회원을 직접 저장하지 않고 기존 `MemberStorage` 구현체를 감싼다.

```text
MemberApp
    ↓
LoggingMemberStorage
    ↓
ArrayMemberStorage
```

Kotlin의 `by`를 이용하면 인터페이스 구현을 다른 객체에 위임할 수 있다.

```kotlin
class LoggingMemberStorage(
    private val origin: MemberStorage
) : MemberStorage by origin
```

별도로 재정의하지 않은 기능은 모두 `origin`으로 전달된다.

로그가 필요한 기능만 재정의한다.

```kotlin
override fun add(member: Member): Boolean {
    val result = origin.add(member)

    println("[LOG] add(${member.email}) -> $result")

    return result
}
```

이를 통해 기존 `ArrayMemberStorage`를 수정하지 않고 로그 기능을 추가할 수 있다.

---

### 4.5 추상 클래스와 인터페이스 비교

| 기준       | 추상 클래스       | 인터페이스          |
| -------- | ------------ | -------------- |
| 상속/구현 개수 | 하나만 상속 가능    | 여러 인터페이스 구현 가능 |
| 생성자      | 사용 가능        | 사용 불가          |
| 상태 저장    | 가능           | 직접적인 상태 저장 불가  |
| 주요 목적    | 공통 상태와 구현 공유 | 기능의 규약 정의      |

회원의 경우 이름, 이메일, 연락처와 같은 공통 상태를 상위 클래스에서 관리할 수 있으므로 추상 클래스를 적용할 수 있다.

저장소의 경우 공통 상태보다 제공해야 하는 기능 자체를 정의하는 것이 중요하므로 인터페이스를 적용한다.

---

## 5. 파일 구조

```text
src/main/kotlin/member_interface/
├── Member.kt
├── MemberStorage.kt
├── ArrayMemberStorage.kt
├── LoggingMemberStorage.kt
├── MemberApp.kt
└── Main.kt
```

| 파일                        | 역할                                          |
| ------------------------- | ------------------------------------------- |
| `Member.kt`               | 회원 한 명의 이름, 이메일, 연락처를 관리한다.                 |
| `MemberStorage.kt`        | 저장소가 제공해야 하는 기능을 인터페이스로 정의한다.               |
| `ArrayMemberStorage.kt`   | 배열 기반 저장 방식을 구현한다.                          |
| `LoggingMemberStorage.kt` | 기존 저장소를 감싸 추가와 삭제 로그를 기록한다.                 |
| `MemberApp.kt`            | 사용자 입력과 출력 흐름을 처리하며 `MemberStorage`에만 의존한다. |
| `Main.kt`                 | 사용할 저장소 구현을 선택하고 애플리케이션을 실행한다.              |

---

## 6. Step by Step

### Step 1. 회원 클래스 (`Member.kt`)

**목표**

회원 한 명의 정보를 표현하는 클래스를 구현한다.

```kotlin
package member_interface

class Member(
    var name: String,
    var email: String,
    var phone: String
) {

    override fun toString(): String {
        return "[이름] $name, [이메일] $email, [연락처] $phone"
    }
}
```

이번 과제에서는 저장 방식의 구조 변경에 집중하므로 회원 등급은 사용하지 않는다.

---

### Step 2. 저장소 인터페이스 (`MemberStorage.kt`)

**목표**

저장소가 제공해야 하는 공통 기능을 인터페이스로 선언한다.

```kotlin
package member_interface

interface MemberStorage {

    val storageName: String
    val memberCnt: Int
    val isFull: Boolean

    fun add(member: Member): Boolean

    fun findByEmail(email: String): Member?

    fun findByName(name: String): Member?

    fun delete(email: String): Boolean

    fun getAll(): Array<Member>

    fun isEmpty(): Boolean {
        return memberCnt == 0
    }
}
```

`storageName`, `memberCnt`, `isFull`은 구현체가 값을 제공한다.

`isEmpty()`는 `memberCnt`만으로 계산할 수 있으므로 인터페이스에 기본 구현을 작성한다.

배열 구현에서만 필요한 `totalCnt`, `findIndex()`는 인터페이스에 포함하지 않는다.

---

### Step 3. 배열 저장소 프로퍼티와 검색

**목표**

`MemberStorage`를 구현하는 배열 저장소의 기본 구조를 작성한다.

```kotlin
class ArrayMemberStorage(
    planNo: Int
) : MemberStorage {

    private val totalCnt = planNo * 10

    private val members =
        Array(totalCnt) {
            Member("", "", "")
        }

    override var memberCnt = 0
        private set

    override val storageName =
        "배열 저장소(정원 ${totalCnt}명)"

    override val isFull: Boolean
        get() = memberCnt == totalCnt

    private fun findIndex(email: String): Int {
        for (i in 0 until memberCnt) {
            if (members[i].email == email) {
                return i
            }
        }

        return -1
    }
}
```

인터페이스에서는 `memberCnt`를 `val`로 선언했지만 구현 클래스에서는 `var`로 재정의할 수 있다.

외부에서는 읽기만 가능하도록 setter에 `private`을 적용한다.

`totalCnt`, `members`, `findIndex()`는 배열 저장소 내부에서만 사용하는 구현 세부사항이므로 `private`으로 선언한다.

---

### Step 4. 배열 저장소 기능 구현

**목표**

회원 추가, 조회, 삭제, 전체 목록 반환 기능을 구현한다.

```kotlin
override fun add(member: Member): Boolean {
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

override fun findByEmail(email: String): Member? {
    val idx = findIndex(email)

    return if (idx == -1) {
        null
    } else {
        members[idx]
    }
}

override fun findByName(name: String): Member? {
    for (i in 0 until memberCnt) {
        if (members[i].name == name) {
            return members[i]
        }
    }

    return null
}

override fun delete(email: String): Boolean {
    val idx = findIndex(email)

    if (idx == -1) {
        return false
    }

    for (i in idx until memberCnt - 1) {
        members[i] = members[i + 1]
    }

    memberCnt--
    members[memberCnt] = Member("", "", "")

    return true
}

override fun getAll(): Array<Member> {
    return members.copyOfRange(0, memberCnt)
}
```

`isEmpty()`는 인터페이스에서 기본 구현을 제공하므로 별도로 재정의하지 않는다.

`getAll()`은 내부 배열 자체가 아니라 등록된 회원 범위만 복사해 반환한다.

---

### Step 5. `MemberApp`을 인터페이스에 연결

**목표**

`MemberApp`이 구체적인 저장소 구현을 알지 못하도록 구성한다.

```kotlin
class MemberApp(
    private val storage: MemberStorage
) {

    fun start() {
        println("\n저장소: ${storage.storageName}")

        while (true) {
            when (printMenu()) {
                1 -> addMember()
                2 -> selectByEmail()
                3 -> selectByName()
                4 -> selectAll()
                5 -> updateMember()
                6 -> deleteMember()

                7 -> {
                    println("이용해주셔서 감사합니다.")
                    return
                }

                else -> println("올바른 번호를 입력하세요.")
            }
        }
    }

    private fun printMenu(): Int {
        println(
            "\n[수행할 업무를 선택하세요 - 현재 회원수 : ${storage.memberCnt}명]"
        )
        println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
        println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제")
        println("[7]프로그램 종료")
        print("> ")

        return readln().toIntOrNull() ?: -1
    }
}
```

생성자의 자료형을 `MemberStorage`로 선언하므로 `MemberApp`은 `ArrayMemberStorage`나 `LoggingMemberStorage`를 직접 알지 못한다.

저장소 구현을 변경해도 `MemberApp`의 코드를 수정할 필요가 없다.

---

### Step 6. 저장소 구현 선택 (`Main.kt`)

**목표**

배열 저장소를 생성하고 `MemberApp`에 전달한다.

```kotlin
package member_interface

private fun printPricePlan(): Int {
    println("[요금제를 선택하세요]")
    println("[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명")
    print("> ")

    return readln().toIntOrNull() ?: 1
}

fun main() {
    val planNo = printPricePlan()

    val storage: MemberStorage =
        ArrayMemberStorage(planNo)

    val app = MemberApp(storage)
    app.start()
}
```

구체적인 저장소 구현을 선택하는 위치는 `Main.kt`로 제한한다.

나머지 코드는 `MemberStorage` 인터페이스에 의존한다.

---

### Step 7. 로그 저장소 (`LoggingMemberStorage.kt`)

**목표**

기존 저장소 구현을 변경하지 않고 로그 기능을 추가한다.

```kotlin
package member_interface

class LoggingMemberStorage(
    private val origin: MemberStorage
) : MemberStorage by origin {

    override val storageName =
        "${origin.storageName} + 로그"

    override fun add(member: Member): Boolean {
        val result = origin.add(member)

        println(
            "[LOG] add(${member.email}) -> $result"
        )

        return result
    }

    override fun delete(email: String): Boolean {
        val result = origin.delete(email)

        println(
            "[LOG] delete($email) -> $result"
        )

        return result
    }
}
```

`by origin`을 사용하면 직접 재정의하지 않은 `MemberStorage`의 기능을 모두 `origin`에 위임한다.

로그 처리가 필요한 `storageName`, `add()`, `delete()`만 재정의한다.

`LoggingMemberStorage`는 내부 저장 방식이 배열인지 다른 구현인지 알 필요가 없다.

---

### Step 8. 로그 저장소 적용

**목표**

`Main.kt`에서 저장소 구현을 변경해 로그 기능을 적용한다.

```kotlin
val storage: MemberStorage =
    LoggingMemberStorage(
        ArrayMemberStorage(planNo)
    )
```

로그가 필요하지 않은 경우 다음과 같이 다시 변경할 수 있다.

```kotlin
val storage: MemberStorage =
    ArrayMemberStorage(planNo)
```

저장소를 변경해도 `MemberApp`은 수정하지 않는다.

**점검 항목**

* [ ] 프로그램 시작 시 사용 중인 저장소 이름을 출력하는가?
* [ ] 회원 추가와 삭제 시 로그를 출력하는가?
* [ ] 회원 조회에서는 로그를 출력하지 않는가?
* [ ] `MemberApp`의 생성자 자료형이 `MemberStorage`인가?
* [ ] `MemberApp` 내부에 구체적인 저장소 클래스 이름이 존재하지 않는가?
* [ ] `LoggingMemberStorage`가 `by origin`을 이용해 기능을 위임하는가?
* [ ] `Main.kt`의 저장소 생성 코드만 변경해 로그 기능을 켜고 끌 수 있는가?

---

## 7. 세 버전 비교

| 기준           | 배열 버전           | 추상 클래스 버전             | 인터페이스 버전             |
| ------------ | --------------- | --------------------- | -------------------- |
| 주요 목적        | 기본 데이터 관리       | 회원 타입별 동작 분리          | 저장 방식의 구현 분리         |
| 핵심 문법        | 배열, 최상위 함수      | `abstract class`, 다형성 | `interface`, `by` 위임 |
| 확장 대상        | 별도 구조 없음        | 회원 등급                 | 저장소 구현               |
| 회원 표현        | `Array<String>` | `Member` 하위 클래스       | `Member`             |
| 화면 계층의 의존 대상 | 별도 관리 객체 없음     | `MemberManager`       | `MemberStorage`      |
| 주요 효과        | 배열 기반 CRUD 구현   | 등급 추가 시 구조 확장         | 저장 방식 교체 시 화면 코드 유지  |

---

## 8. 최종 완성 체크리스트

* [ ] `MemberStorage` 인터페이스를 구현한다.
* [ ] 저장소의 공통 프로퍼티와 기능만 인터페이스에 포함한다.
* [ ] `totalCnt`, `findIndex()`와 같은 배열 구현 세부사항을 인터페이스에서 제외한다.
* [ ] `ArrayMemberStorage`에서 인터페이스의 기능을 구현한다.
* [ ] 내부 배열과 정원, 검색 함수는 `private`으로 관리한다.
* [ ] `memberCnt`를 `override var`와 `private set`으로 구현한다.
* [ ] `MemberApp`은 `MemberStorage` 타입에만 의존한다.
* [ ] `MemberApp` 내부에 구체적인 저장소 클래스가 등장하지 않는다.
* [ ] `LoggingMemberStorage`에서 `by origin`을 이용해 위임한다.
* [ ] 필요한 기능만 재정의해 로그를 추가한다.
* [ ] `Main.kt`에서 사용할 저장소 구현을 선택한다.
* [ ] 저장소 구현 변경 시 `MemberApp`을 수정하지 않는다.

---

## 9. 개선 및 도전 과제

### 9.1 빈 저장소 구현

실제로 회원을 저장하지 않는 `EmptyMemberStorage`를 구현할 수 있다.

회원 추가는 항상 실패하고 전체 조회는 빈 배열을 반환하도록 구성한다.

이를 `Main.kt`에서 `MemberStorage` 타입으로 교체해 `MemberApp` 수정 없이 실행할 수 있는지 확인한다.

---

### 9.2 여러 저장소 중첩

위임 기반 저장소는 여러 겹으로 조합할 수 있다.

```kotlin
LoggingMemberStorage(
    LoggingMemberStorage(
        ArrayMemberStorage(2)
    )
)
```

각 래퍼가 동일한 인터페이스를 구현하므로 저장소를 중첩해 기능을 조합할 수 있다.

---

### 9.3 조회 로그 추가

`LoggingMemberStorage`에서 `findByEmail()`을 재정의하면 회원 조회 과정에도 로그를 추가할 수 있다.

```kotlin
override fun findByEmail(
    email: String
): Member? {
    val result = origin.findByEmail(email)

    println(
        "[LOG] findByEmail($email) -> $result"
    )

    return result
}
```

위임을 이용하면 필요한 기능만 선택적으로 재정의할 수 있다.

---

### 9.4 저장소 용량 정보 설계

현재 `MemberStorage`에는 배열 저장소의 정원인 `totalCnt`를 포함하지 않는다.

정원 정보를 다시 제공하려면 공통 인터페이스에 용량 개념을 추가할지, 별도의 기능으로 분리할지 결정해야 한다.

특정 구현 클래스의 타입을 직접 확인하면 `MemberApp`이 다시 구체 구현에 의존하게 되므로 인터페이스 설계의 목적과 충돌할 수 있다.

---

### 9.5 통계 저장소

회원 추가와 삭제 횟수를 기록하는 `CountingMemberStorage`를 구현할 수 있다.

`LoggingMemberStorage`와 함께 중첩하면 로그와 통계를 동시에 적용할 수 있다.

---

### 9.6 파일 저장소

`MemberStorage`를 구현하는 파일 기반 저장소를 추가할 수 있다.

파일 입출력 방식으로 변경해도 `MemberApp`이 인터페이스에만 의존한다면 화면 로직은 그대로 유지할 수 있다.

---

### 9.7 회원 등급 구조 결합

추상 클래스 과제에서 구현한 회원 등급 구조와 저장소 인터페이스 구조를 함께 적용할 수 있다.

회원 타입은 추상 클래스와 다형성으로 확장하고, 저장 방식은 인터페이스로 분리해 각각 다른 변경 지점을 독립적으로 관리할 수 있다.

---

## 10. 정리

이 과제에서는 `MemberStorage` 인터페이스를 이용해 회원 관리 기능과 구체적인 저장 방식을 분리한다.

`MemberApp`은 저장소의 구현 클래스가 아닌 인터페이스에 의존하고, 실제 구현 선택은 `Main.kt`에서 담당한다.

`ArrayMemberStorage`는 실제 데이터 저장을 담당하며, `LoggingMemberStorage`는 `by` 위임을 이용해 기존 저장소의 동작을 유지하면서 로그 기능만 추가한다.

이를 통해 인터페이스 설계, 구현 교체, 캡슐화, 위임을 이용한 기능 확장을 함께 적용한다.
