package membermanagement

var totalCnt = 0
var memberCnt = 0

typealias Members = Array<Array<String>>

const val NAME = 0
const val EMAIL = 1
const val PHONE = 2

fun printPricePlan(): Int {
    println("===============================================")
    println("[요금제 선택]")
    println("[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명")
    print("> ")

    return readln().toIntOrNull() ?: -1
}

fun printMenu(): Int {
    println("===============================================")
    println("[수행할 업무 선택 - 현재 회원 수: $memberCnt/$totalCnt]")
    println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
    println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제")
    println("[7]프로그램 종료")
    print("> ")

    return readln().toIntOrNull() ?: -1
}

fun addMember(members: Members) {
    if (memberCnt == totalCnt) {
        println("-----------------------------------------------")
        println("회원 초과!")

        return
    }

    println("-----------------------------------------------")
    print("이름 입력: ")
    val name = readln()
    print("이메일 입력: ")
    val email = readln()
    print("연락처 입력: ")
    val phone = readln()

    if (name.isBlank() || email.isBlank() || phone.isBlank()) {
        println("-----------------------------------------------")
        println("빈 값 입력 불가")

        return
    }

    if (findIndex(members, EMAIL, email) != -1) {
        println("-----------------------------------------------")
        println("이미 존재하는 회원")

        return
    }

    members[memberCnt][NAME] = name
    members[memberCnt][EMAIL] = email
    members[memberCnt][PHONE] = phone
    memberCnt++
    println("-----------------------------------------------")
    println("회원 등록 완료")
}

fun printMember(member: Array<String>) {
    println("[이름] ${member[NAME]}, [이메일] ${member[EMAIL]}, [연락처] ${member[PHONE]}")
}

fun findIndex(members: Members, col: Int, value: String): Int {
    for (i in 0 until memberCnt) {
        if (value == members[i][col]) {
            return i
        }
    }

    return -1
}

fun selectEmail(members: Members) {
    println("-----------------------------------------------")
    print("이메일 키워드 입력: ")
    val keyword = readln()

    var count = 0

    for (i in 0 until memberCnt) {
        if (members[i][EMAIL].contains(keyword)) {
            printMember(members[i])
            count++
        }
    }

    if (count == 0) {
        println("-----------------------------------------------")
        println("정보 없음!")
    }
}

fun selectName(members: Members) {
    println("-----------------------------------------------")
    print("이름 입력: ")
    val name = readln()

    var count = 0

    for (i in 0 until memberCnt) {
        if (members[i][NAME] == name) {
            printMember(members[i])
            count++
        }
    }

    if (count == 0) {
        println("-----------------------------------------------")
        println("정보 없음!")
    }
}

fun selectAll(members: Members) {
    if (memberCnt == 0) {
        println("-----------------------------------------------")
        println("회원 없음")

        return
    }

    val sortedMembers = members
        .take(memberCnt)
        .sortedBy { it[NAME] }

    for ((index, member) in sortedMembers.withIndex()) {
        print("${index + 1}. ")
        printMember(member)
    }
}

fun updateMember(members: Members) {
    println("-----------------------------------------------")
    print("수정할 회원의 이메일 입력: ")
    val email = readln()

    val idx = findIndex(members, EMAIL, email)

    if (idx == -1) {
        println("-----------------------------------------------")
        println("회원 없음")

        return
    }

    println("-----------------------------------------------")
    print("현재 정보 → ")
    printMember(members[idx])

    println("-----------------------------------------------")
    print("새 이름 입력: ")
    val newName = readln()
    print("새 이메일 입력: ")
    val newEmail = readln()
    print("새 연락처 입력: ")
    val newPhone = readln()

    if (newName.isBlank() || newEmail.isBlank() || newPhone.isBlank()) {
        println("-----------------------------------------------")
        println("빈 값 입력 불가")

        return
    }

    members[idx][NAME] = newName
    members[idx][EMAIL] = newEmail
    members[idx][PHONE] = newPhone

    println("-----------------------------------------------")
    println("수정 완료")
}

fun deleteMember(members: Members) {
    println("-----------------------------------------------")
    print("삭제할 회원 이메일 입력: ")
    val email = readln()

    val idx = findIndex(members, EMAIL, email)

    if (idx == -1) {
        println("-----------------------------------------------")
        println("회원 없음")

        return
    }

    for (i in idx until memberCnt-1) {
        members[i][NAME] = members[i + 1][NAME]
        members[i][EMAIL] = members[i + 1][EMAIL]
        members[i][PHONE] = members[i + 1][PHONE]
    }

    memberCnt--

    members[memberCnt][NAME] = ""
    members[memberCnt][EMAIL] = ""
    members[memberCnt][PHONE] = ""

    println("-----------------------------------------------")
    println("삭제 완료")
}

fun main() {
    var num: Int

    while (true) {
        num = printPricePlan()

        if (num in 1..3) {
            break
        }

        println("올바른 번호 입력!")
    }

    totalCnt = num * 10
    val members = Array(totalCnt) { Array(3) { "" } }

    while (true) {
        when (printMenu()) {
            1 -> addMember(members)
            2 -> selectEmail(members)
            3 -> selectName(members)
            4 -> selectAll(members)
            5 -> updateMember(members)
            6 -> deleteMember(members)
            7 -> {
                println("-----------------------------------------------")
                println("프로그램 종료")
                println("===============================================")

                return
            }
            else -> {
                println("-----------------------------------------------")
                println("올바른 번호 입력!")
            }
        }
    }
}