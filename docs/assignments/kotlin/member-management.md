# 회원 관리 프로그램 만들기 - Kotlin 2차원 배열

2차원 배열에 회원 정보를 저장하고 추가, 조회, 수정, 삭제하는 콘솔 프로그램을 구현한다.

변수, 함수, 조건문, 반복문, 배열만 사용하며 클래스와 리스트는 사용하지 않는다.

---

## 1. 구현 기능

프로그램 시작 시 요금제를 선택해 회원 정원을 결정한다.

이후 메뉴를 반복해서 출력하며 회원 추가, 조회, 수정, 삭제 기능을 수행한다.

```text
[요금제를 선택하세요]
[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명
> 2

[수행할 업무를 선택하세요 - 현재 회원수 : 0/20]
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
회원이 등록되었습니다.
```

---

## 2. 요구사항

| 번호 | 기능        | 설명                                                          |
| -- | --------- | ----------------------------------------------------------- |
| 0  | 요금제 선택    | Lite(10명), Basic(20명), Premium(30명) 중 하나를 선택하고 회원 정원을 결정한다. |
| 1  | 회원 추가     | 이름, 이메일, 연락처를 입력받아 저장하며 정원 초과와 이메일 중복을 방지한다.                |
| 2  | 회원 조회(메일) | 이메일을 기준으로 회원 한 명의 정보를 조회한다.                                 |
| 3  | 회원 조회(이름) | 이름을 기준으로 회원 정보를 조회한다.                                       |
| 4  | 회원 전체 조회  | 등록된 모든 회원을 출력한다.                                            |
| 5  | 회원 정보 수정  | 이메일을 기준으로 회원을 찾은 뒤 이름, 이메일, 연락처를 수정한다.                      |
| 6  | 회원 삭제     | 이메일을 기준으로 회원을 삭제하고 뒤에 있는 회원들을 앞으로 이동한다.                     |
| 7  | 종료        | 프로그램을 종료한다.                                                 |

이메일은 회원을 구분하는 고유 식별자로 사용한다.

조회, 수정, 삭제는 이메일을 기준으로 회원 한 명을 찾는다.

---

## 3. 학습 목표

| 개념                     | 활용                                                                |
| ---------------------- | ----------------------------------------------------------------- |
| 2차원 배열                 | `Array(정원) { Array(3) { "" } }` 형태로 회원 정보를 저장한다.                  |
| 인덱스 접근                 | `members[i][0]`, `members[i][1]`, `members[i][2]` 형태로 각 속성에 접근한다. |
| 파일 최상위 `var`           | 여러 함수에서 함께 사용하는 상태를 관리한다.                                         |
| `val` / `var`          | 배열 참조와 변경되는 상태를 구분한다.                                             |
| 문자열 비교 `==`            | 이름과 이메일을 값 기준으로 비교한다.                                             |
| `for (i in 0 until n)` | 등록된 회원 범위를 인덱스로 순회한다.                                             |
| `when`                 | 메뉴 번호에 따라 기능을 분기한다.                                               |
| 함수 매개변수와 반환값           | 검색 함수 등에서 값을 전달하고 결과를 반환한다.                                       |
| `readln()`             | 사용자 입력을 받는다.                                                      |
| `return` / `-1`        | 함수 종료와 검색 실패를 표현한다.                                               |

---

## 4. 핵심 개념

### 4.1 2차원 배열로 회원 정보 저장

회원 한 명을 한 행으로 표현하고 이름, 이메일, 연락처를 각 열에 저장한다.

```text
         열0(이름)   열1(이메일)        열2(연락처)
행0 →   "홍길동"    "hong@a.com"      "010-1111-1111"
행1 →   "김철수"    "kim@b.com"       "010-2222-2222"
```

Java에서는 다음과 같이 2차원 배열을 생성한다.

```java
String[][] members = new String[정원][3];
```

Kotlin에서는 `new` 없이 다음과 같이 생성한다.

```kotlin
val members = Array(정원) { Array(3) { "" } }
```

`Array(정원) { ... }`는 지정한 크기만큼 배열을 생성하고 중괄호의 결과로 각 요소를 초기화한다.

각 요소에는 다시 `Array(3) { "" }`를 생성해 이름, 이메일, 연락처를 저장할 세 칸을 만든다.

빈 값을 `""`로 초기화하면 자료형이 `Array<String>`이 되므로 nullable 처리를 하지 않아도 된다.

```kotlin
members[i][0] // 이름
members[i][1] // 이메일
members[i][2] // 연락처
```

---

### 4.2 파일 최상위 변수로 상태 공유

여러 함수에서 공통으로 사용하는 값은 파일 최상위에 선언한다.

```kotlin
var totalCnt = 0
var memberCnt = 0
```

`totalCnt`는 요금제에 따라 결정되는 최대 회원 수를 나타낸다.

`memberCnt`는 현재 등록된 회원 수이면서 다음 회원이 저장될 배열 인덱스를 나타낸다.

두 값은 프로그램 실행 중 변경되므로 `var`로 선언한다.

배열 변수는 `val`로 선언해도 내부 요소는 변경할 수 있다.

```kotlin
val members = Array(10) { Array(3) { "" } }

members[0][0] = "홍길동"
```

---

### 4.3 선형 검색과 문자열 비교

회원의 이름이나 이메일을 찾을 때 등록된 회원 범위를 처음부터 순회한다.

```kotlin
for (i in 0 until memberCnt) {
    if (email == members[i][1]) {
        // 회원을 찾은 경우
    }
}
```

Kotlin의 `==`는 값의 동등성을 비교하며 내부적으로 `equals()`를 이용한다.

참조 자체가 같은 객체인지 비교하는 경우에는 `===`를 사용한다.

회원의 배열 인덱스를 반환하는 검색 함수에서는 찾지 못한 경우 `-1`을 반환한다.

배열 인덱스는 음수가 될 수 없으므로 `-1`을 검색 실패 값으로 사용할 수 있다.

---

### 4.4 삭제 후 배열 당기기

배열은 크기가 고정되어 있고 중간 요소를 직접 제거할 수 없다.

중간 회원을 삭제하면 뒤에 있는 회원들을 한 칸씩 앞으로 이동해 빈 공간을 제거한다.

```text
삭제 전: [A][B][C][D]

B 삭제

이동 후: [A][C][D][ ]
```

---

## 5. 파일 구조

| 파일                    | 역할                                |
| --------------------- | --------------------------------- |
| `J_member_manager.kt` | 하나의 파일에 최상위 변수, 함수, `main`을 작성한다. |

| 함수                                    | 역할                                      |
| ------------------------------------- | --------------------------------------- |
| `printPricePlan(): Int`               | 요금제 메뉴를 출력하고 선택 번호를 반환한다.               |
| `printMenu(): Int`                    | 현재 인원과 정원을 포함한 업무 메뉴를 출력하고 선택 번호를 반환한다. |
| `printMember(member: Array<String>)`  | 회원 한 명의 정보를 출력한다.                       |
| `findIndex(members, col, value): Int` | 지정한 열에서 값을 찾아 인덱스를 반환하며 없으면 `-1`을 반환한다. |
| `addMember(members)`                  | 정원과 이메일 중복을 검사한 뒤 회원을 추가한다.             |
| `selectEmail(members)`                | 이메일을 기준으로 회원을 조회한다.                     |
| `selectName(members)`                 | 이름을 기준으로 회원을 조회한다.                      |
| `selectAll(members)`                  | 등록된 모든 회원을 조회한다.                        |
| `updateMember(members)`               | 회원 정보를 수정한다.                            |
| `deleteMember(members)`               | 회원을 삭제하고 뒤의 데이터를 앞으로 이동한다.              |
| `main()`                              | 요금제 선택, 배열 생성, 메뉴 반복의 전체 흐름을 제어한다.      |

조회, 수정, 삭제 과정에서 반복되는 검색 로직은 `findIndex()` 하나로 분리해 재사용한다.

---

## 6. Step by Step

### Step 1. 요금제 선택과 메뉴 골격

**목표**

요금제를 선택해 배열 크기를 결정하고 업무 메뉴를 반복해서 출력한다.

**구현 내용**

1. 파일 최상위에 `totalCnt`, `memberCnt`를 `var`로 선언한다.
2. `printPricePlan(): Int`를 작성해 요금제 선택 번호를 반환한다.
3. 선택 결과를 이용해 `totalCnt`를 계산하고 2차원 배열을 생성한다.
4. `printMenu(): Int`를 작성해 업무 메뉴를 출력한다.
5. `while (true)`와 `when`을 이용해 메뉴 흐름을 구성한다.
6. 7번을 선택하면 `return`으로 프로그램을 종료한다.

**힌트**

```kotlin
var totalCnt = 0
var memberCnt = 0

fun printPricePlan(): Int {
    println("[요금제를 선택하세요]")
    println("[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명")
    print("> ")

    return readln().toInt()
}

fun printMenu(): Int {
    println("\n[수행할 업무를 선택하세요 - 현재 회원수 : $memberCnt/$totalCnt]")
    println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
    println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제")
    println("[7]프로그램 종료")
    print("> ")

    return readln().toInt()
}

fun main() {
    val num = printPricePlan()
    totalCnt = num * 10

    val members = Array(totalCnt) { Array(3) { "" } }

    while (true) {
        when (printMenu()) {
            1 -> { /* Step 2 */ }
            2 -> { /* Step 3 */ }
            3 -> { /* Step 4 */ }
            4 -> { /* Step 5 */ }
            5 -> { /* Step 6 */ }
            6 -> { /* Step 7 */ }

            7 -> {
                println("이용해주셔서 감사합니다.")
                return
            }

            else -> println("올바른 번호를 입력하세요.")
        }
    }
}
```

`printMenu()`는 파일 최상위 변수인 `memberCnt`, `totalCnt`를 직접 사용할 수 있으므로 별도의 매개변수가 필요하지 않다.

`when`은 Java의 `switch`와 달리 각 분기마다 `break`를 작성하지 않는다.

`readln()`은 한 줄 전체를 읽기 때문에 `Scanner`에서 발생할 수 있는 `nextInt()`와 `nextLine()` 사이의 입력 버퍼 처리 문제를 피할 수 있다.

**확인**

요금제 선택 후 업무 메뉴가 반복해서 출력되고 7번 선택 시 프로그램이 종료되는지 확인한다.

---

### Step 2. 회원 추가

**목표**

이름, 이메일, 연락처를 입력받아 배열에 저장하고 정원 초과와 이메일 중복을 방지한다.

**구현 내용**

1. `memberCnt == totalCnt`이면 회원 추가를 중단한다.
2. 이름, 이메일, 연락처를 입력받는다.
3. 기존 이메일과 중복되는지 확인한다.
4. `members[memberCnt]`에 회원 정보를 저장한다.
5. 저장 후 `memberCnt`를 증가시킨다.

**힌트**

```kotlin
fun addMember(members: Array<Array<String>>) {
    if (memberCnt == totalCnt) {
        println("회원이 꽉 찼습니다.")
        return
    }

    println("이름을 입력하세요.")
    val name = readln()

    println("이메일을 입력하세요.")
    val email = readln()

    println("연락처를 입력하세요.")
    val phone = readln()

    for (i in 0 until memberCnt) {
        if (email == members[i][1]) {
            println("이미 존재하는 회원입니다.")
            return
        }
    }

    members[memberCnt][0] = name
    members[memberCnt][1] = email
    members[memberCnt][2] = phone

    memberCnt++

    println("회원이 등록되었습니다.")
}
```

잘못된 상태를 함수 앞부분에서 확인하고 `return`하면 정상 처리 로직의 중첩을 줄일 수 있다.

검색 범위는 `totalCnt`가 아니라 실제 등록된 회원 수인 `memberCnt`를 기준으로 한다.

`members`가 `val`이어도 배열 내부 값은 변경할 수 있다.

**확인**

회원 추가 후 전체 조회에서 회원이 출력되고 같은 이메일을 다시 입력했을 때 등록이 거부되는지 확인한다.

---

### Step 3. 검색 함수와 이메일 조회

**목표**

회원 검색 로직을 하나의 함수로 분리하고 이메일 조회에 활용한다.

**구현 내용**

1. `printMember(member: Array<String>)`를 작성한다.
2. `findIndex(members, col, value): Int`를 작성한다.
3. 지정된 열에서 값을 찾으면 해당 인덱스를 반환한다.
4. 찾지 못하면 `-1`을 반환한다.
5. `selectEmail(members)`에서 이메일을 입력받아 회원을 조회한다.

**힌트**

```kotlin
fun printMember(member: Array<String>) {
    println("[이름] ${member[0]}, [이메일] ${member[1]}, [연락처] ${member[2]}")
}

fun findIndex(
    members: Array<Array<String>>,
    col: Int,
    value: String
): Int {
    for (i in 0 until memberCnt) {
        if (value == members[i][col]) {
            return i
        }
    }

    return -1
}

fun selectEmail(members: Array<Array<String>>) {
    println("이메일을 입력하세요.")
    val email = readln()

    val idx = findIndex(members, 1, email)

    if (idx == -1) {
        println("찾으시는 정보가 없습니다.")
        return
    }

    printMember(members[idx])
}
```

`members[idx]`는 회원 한 명을 나타내는 `Array<String>`이다.

`findIndex()`의 `col`에 `1`을 전달하면 이메일 열을 검색하고 `0`을 전달하면 이름 열을 검색한다.

회원 추가 시 이메일 중복 검사에도 `findIndex()`를 재사용할 수 있다.

```kotlin
if (findIndex(members, 1, email) != -1) {
    println("이미 존재하는 회원입니다.")
    return
}
```

**확인**

등록된 이메일로 조회했을 때 회원 정보가 출력되고 존재하지 않는 이메일에서는 안내 메시지가 출력되는지 확인한다.

---

### Step 4. 이름으로 조회

**목표**

`findIndex()`를 재사용해 이름으로 회원을 조회한다.

`selectEmail()`과 동일한 흐름에서 검색할 열 번호만 `0`으로 변경한다.

```kotlin
val idx = findIndex(members, 0, name)
```

현재 구현에서는 같은 이름을 가진 회원이 여러 명일 경우 가장 먼저 검색된 한 명만 반환한다.

**확인**

등록된 이름으로 조회했을 때 회원 정보가 출력되는지 확인한다.

---

### Step 5. 전체 조회

**목표**

현재 등록된 모든 회원을 출력한다.

**구현 내용**

1. `memberCnt == 0`이면 등록된 회원이 없다는 메시지를 출력한다.
2. `0 until memberCnt` 범위를 순회하며 `printMember()`를 호출한다.

**힌트**

```kotlin
fun selectAll(members: Array<Array<String>>) {
    if (memberCnt == 0) {
        println("등록된 회원이 없습니다.")
        return
    }

    for (i in 0 until memberCnt) {
        print("${i + 1}. ")
        printMember(members[i])
    }
}
```

`members.size`는 배열 전체 크기인 정원을 의미한다.

등록된 회원만 출력하려면 `memberCnt`를 기준으로 반복해야 한다.

`members.indices`도 전체 배열 크기를 기준으로 하므로 이 경우에는 사용하지 않는다.

**확인**

실제로 등록한 회원만 출력되고 비어 있는 배열 요소가 출력되지 않는지 확인한다.

---

### Step 6. 회원 정보 수정

**목표**

이메일로 회원을 찾고 이름, 이메일, 연락처를 새로운 값으로 수정한다.

**구현 내용**

1. 수정할 회원의 이메일을 입력받는다.
2. `findIndex()`를 이용해 회원 인덱스를 찾는다.
3. 회원이 없으면 함수를 종료한다.
4. 현재 정보를 출력한다.
5. 새로운 이름, 이메일, 연락처를 입력받아 기존 배열 값을 변경한다.

**힌트**

```kotlin
fun updateMember(members: Array<Array<String>>) {
    println("수정할 회원의 이메일을 입력하세요.")
    val email = readln()

    val idx = findIndex(members, 1, email)

    if (idx == -1) {
        println("찾으시는 회원이 없습니다.")
        return
    }

    println("현재 정보 → ")
    printMember(members[idx])

    println("새 이름을 입력하세요.")
    members[idx][0] = readln()

    println("새 이메일을 입력하세요.")
    members[idx][1] = readln()

    println("새 연락처를 입력하세요.")
    members[idx][2] = readln()

    println("수정이 완료되었습니다.")
}
```

`readln()`의 반환값은 `String`이므로 별도 변수에 저장하지 않고 배열 요소에 직접 대입할 수 있다.

**확인**

회원 수정 후 다시 조회했을 때 변경된 정보가 출력되는지 확인한다.

---

### Step 7. 회원 삭제와 배열 당기기

**목표**

이메일로 회원을 찾아 삭제하고 뒤의 회원을 한 칸씩 앞으로 이동한다.

**구현 내용**

1. 삭제할 회원의 이메일을 입력받는다.
2. `findIndex()`로 회원 인덱스를 찾는다.
3. `idx`부터 `memberCnt - 1` 직전까지 다음 회원을 현재 위치로 복사한다.
4. `memberCnt`를 감소시킨다.
5. 이동 이후 남는 마지막 배열 요소를 빈 문자열로 초기화한다.

**힌트**

```kotlin
fun deleteMember(members: Array<Array<String>>) {
    println("삭제할 회원의 이메일을 입력하세요.")
    val email = readln()

    val idx = findIndex(members, 1, email)

    if (idx == -1) {
        println("찾으시는 회원이 없습니다.")
        return
    }

    for (i in idx until memberCnt - 1) {
        members[i][0] = members[i + 1][0]
        members[i][1] = members[i + 1][1]
        members[i][2] = members[i + 1][2]
    }

    memberCnt--

    members[memberCnt][0] = ""
    members[memberCnt][1] = ""
    members[memberCnt][2] = ""

    println("삭제가 완료되었습니다.")
}
```

삭제 순서는 다음과 같다.

```text
뒤의 회원 이동 → memberCnt 감소 → 마지막 배열 요소 초기화
```

`memberCnt--`를 이동 작업보다 먼저 수행하면 반복 범위가 줄어 마지막 데이터가 정상적으로 이동하지 않을 수 있다.

`until`은 마지막 값을 포함하지 않으므로 `idx until memberCnt - 1` 범위에서 `i + 1`의 최대값은 `memberCnt - 1`이 된다.

배열 한 행을 복사할 때는 다음과 같이 `copyInto()`를 사용할 수도 있다.

```kotlin
members[i + 1].copyInto(members[i])
```

마지막 행도 다음과 같이 새 배열로 교체할 수 있다.

```kotlin
members[memberCnt] = Array(3) { "" }
```

**확인**

중간 회원을 삭제한 뒤 전체 조회에서 빈칸이나 중복 없이 회원 목록이 이어지고 현재 회원 수가 감소했는지 확인한다.

---

### Step 8. `main` 조립

**목표**

각 메뉴 분기에 구현한 함수를 연결해 프로그램을 완성한다.

```kotlin
while (true) {
    when (printMenu()) {
        1 -> addMember(members)
        2 -> selectEmail(members)
        3 -> selectName(members)
        4 -> selectAll(members)
        5 -> updateMember(members)
        6 -> deleteMember(members)

        7 -> {
            println("이용해주셔서 감사합니다.")
            return
        }

        else -> println("올바른 번호를 입력하세요.")
    }
}
```

실행할 코드가 한 줄이면 중괄호 없이 `->` 뒤에 바로 작성할 수 있다.

**점검 항목**

* [ ] 현재 회원 수와 정원인 `memberCnt` / `totalCnt`가 메뉴에 올바르게 출력되는가?
* [ ] 정원이 가득 찬 경우 회원 추가가 제한되는가?
* [ ] 동일한 이메일을 이용한 중복 가입이 제한되는가?
* [ ] 회원 삭제 후 전체 조회 시 빈칸이나 중복 회원이 출력되지 않는가?
* [ ] 존재하지 않는 회원을 조회, 수정, 삭제해도 프로그램이 종료되지 않는가?
* [ ] 문자열 비교에 `==`를 사용하는가?
* [ ] 검색 로직을 반복 작성하지 않고 `findIndex()`로 재사용하는가?

**테스트 시나리오**

| 입력                                                     | 기대 결과                   |
| ------------------------------------------------------ | ----------------------- |
| 요금제 `1`                                                | 정원 10명                  |
| `1` → 홍길동 / [hong@a.com](mailto:hong@a.com) / 010-1111 | 회원이 등록되었습니다.            |
| `1` → 김철수 / [hong@a.com](mailto:hong@a.com) / 010-2222 | 이미 존재하는 회원입니다.          |
| `1` → 김철수 / [kim@b.com](mailto:kim@b.com) / 010-2222   | 회원이 등록되었습니다.            |
| `4`                                                    | 두 회원 정보 출력              |
| `2` → [hong@a.com](mailto:hong@a.com)                  | 홍길동 정보 출력               |
| `2` → [none@x.com](mailto:none@x.com)                  | 찾으시는 정보가 없습니다.          |
| `6` → [hong@a.com](mailto:hong@a.com)                  | 삭제 후 전체 조회에서 김철수만 출력    |
| `9`                                                    | 올바른 번호를 입력하세요.          |
| `7`                                                    | 이용해주셔서 감사합니다. / 프로그램 종료 |

---

## 7. Java 버전과의 차이

| Java                                     | Kotlin                                 |
| ---------------------------------------- | -------------------------------------- |
| `String[][] m = new String[n][3];`       | `val m = Array(n) { Array(3) { "" } }` |
| 초기화하지 않은 참조 배열 요소는 `null`                | 초기값을 직접 지정한다.                          |
| `static int memberCnt = 0;`              | 파일 최상위 `var memberCnt = 0`             |
| `email.equals(m[i][1])`                  | `email == m[i][1]`                     |
| `switch` + `break`                       | `when`                                 |
| `for (int i = 0; i < n; i++)`            | `for (i in 0 until n)`                 |
| `Scanner`를 이용한 입력 처리                     | `readln()`                             |
| 문자열 연결                                   | 문자열 템플릿                                |
| `public static void main(String[] args)` | `fun main()`                           |
| `m.length`                               | `m.size`                               |

`readln()`은 한 줄 전체를 읽기 때문에 `Scanner`의 `nextInt()` 이후 `nextLine()`을 사용할 때 발생할 수 있는 입력 버퍼 처리 과정이 필요하지 않다.

숫자가 필요한 경우 다음과 같이 변환한다.

```kotlin
readln().toInt()
```

---

## 8. 최종 완성 체크리스트

* [ ] 요금제 선택 결과에 따라 정원 크기의 2차원 배열을 생성한다.
* [ ] 최상위 `var`를 이용해 `totalCnt`, `memberCnt`를 관리한다.
* [ ] `while (true)`와 `when`으로 메뉴 1~7을 반복 처리한다.
* [ ] `findIndex()` 하나로 중복 검사, 조회, 수정, 삭제 검색을 처리한다.
* [ ] 회원 추가 시 정원과 이메일 중복을 검사한다.
* [ ] 이메일 조회와 이름 조회를 구현한다.
* [ ] 전체 조회에서는 등록된 회원만 출력한다.
* [ ] 회원 수정 기능을 구현한다.
* [ ] 회원 삭제 시 뒤 데이터를 앞으로 이동하고 마지막 데이터를 정리한다.
* [ ] 반복 범위는 `totalCnt`가 아닌 `memberCnt`를 기준으로 한다.
* [ ] 문자열 비교에는 `==`를 사용한다.

---

## 9. 개선 및 도전 과제

### 9.1 숫자가 아닌 입력 처리

메뉴에서 숫자가 아닌 값을 입력하면 `toInt()` 과정에서 예외가 발생한다.

`toIntOrNull()`과 엘비스 연산자(`?:`)를 이용하면 잘못된 입력을 `-1`로 처리할 수 있다.

```kotlin
return readln().toIntOrNull() ?: -1
```

`-1`은 `when`의 `else` 분기로 처리할 수 있다.

---

### 9.2 빈 입력 처리

이름이나 이메일에 빈 문자열이 저장되지 않도록 `isBlank()`를 이용해 검사할 수 있다.

```kotlin
if (name.isBlank()) {
    return
}
```

---

### 9.3 `typealias` 적용

`Array<Array<String>>` 자료형을 반복해서 작성하는 대신 별칭을 선언할 수 있다.

```kotlin
typealias Members = Array<Array<String>>
```

함수 매개변수에도 다음과 같이 사용할 수 있다.

```kotlin
fun addMember(members: Members) {
    ...
}
```

---

### 9.4 열 인덱스 상수화

`members[i][0]`, `members[i][1]`, `members[i][2]`와 같은 숫자 인덱스는 의미를 파악하기 어렵다.

각 열의 인덱스를 상수로 선언할 수 있다.

```kotlin
const val NAME = 0
const val EMAIL = 1
const val PHONE = 2
```

검색 코드도 다음과 같이 작성할 수 있다.

```kotlin
findIndex(members, EMAIL, email)
```

---

### 9.5 동명이인 처리

현재 이름 검색은 `findIndex()`가 가장 먼저 찾은 회원 한 명만 반환한다.

전체 회원을 순회하면서 이름이 같은 모든 회원을 출력하도록 확장할 수 있다.

---

### 9.6 정렬 조회

회원 전체 조회 시 이름을 기준으로 정렬해 출력하도록 확장할 수 있다.

```kotlin
sortedBy { it[0] }
```

---

### 9.7 부분 일치 검색

이메일 전체가 일치하는 경우만 검색하는 대신 `contains()`를 이용해 일부 문자열을 포함하는 회원을 검색하도록 확장할 수 있다.

---

### 9.8 `Member` 클래스로 변경

객체지향을 적용하면 회원 정보를 `Member` 클래스로 표현할 수 있다.

```kotlin
Array<Member?>
```

이를 통해 다음과 같은 인덱스 기반 접근을 줄일 수 있다.

```kotlin
members[i][1]
```

대신 속성 이름을 직접 이용할 수 있다.

```kotlin
members[i]?.email
```

---

### 9.9 `MutableList` 적용

고정 크기 배열 대신 `MutableList<Member>`를 사용하면 요소 삭제 시 직접 뒤 데이터를 이동할 필요가 없다.

배열과 리스트의 데이터 추가 및 삭제 방식 차이를 비교할 수 있다.

---

### 9.10 파일 저장

프로그램 종료 이후에도 회원 정보가 유지되도록 파일에 회원 데이터를 저장하고 프로그램 시작 시 다시 불러오도록 확장할 수 있다.

---

## 10. 정리

이 과제에서는 2차원 배열을 이용해 여러 회원 정보를 관리하고 CRUD 기능을 구현한다.

고정 크기 배열을 사용하면서 정원을 미리 결정하고, 회원 삭제 시 뒤 데이터를 직접 앞으로 이동하는 과정을 통해 배열의 특성을 확인한다.

또한 검색 로직을 `findIndex()`로 분리해 여러 기능에서 재사용하면서 함수 분리와 중복 제거를 함께 연습한다.
