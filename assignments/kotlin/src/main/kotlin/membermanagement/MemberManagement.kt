package membermanagement

var totalCnt = 0
var memberCnt = 0

fun printPricePlan(): Int {
    println("===============================================")
    println("[요금제 선택]")
    println("[1]Lite : 10명 [2]Basic : 20명 [3]Premium : 30명")
    print("> ")

    return readln().toInt()
}

fun printMenu(): Int {
    println("===============================================")
    println("[수행할 업무 선택 - 현재 회원 수: $memberCnt/$totalCnt]")
    println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
    println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제")
    println("[7]프로그램 종료")
    print("> ")

    return readln().toInt()
}

fun addMember(members: Array<Array<String>>) {
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

    if (findIndex(members, 1, email) != -1) {
        println("-----------------------------------------------")
        println("이미 존재하는 회원")

        return
    }

    members[memberCnt][0] = name
    members[memberCnt][1] = email
    members[memberCnt][2] = phone
    memberCnt++
    println("-----------------------------------------------")
    println("회원 등록 완료")
}

fun printMember(member: Array<String>) {
    println("[이름] ${member[0]}, [이메일] ${member[1]}, [연락처] ${member[2]}")
}

fun findIndex(members: Array<Array<String>>, col: Int, value: String): Int {
    for (i in 0 until memberCnt) {
        if (value == members[i][col]) {
            return i
        }
    }

    return -1
}

fun selectEmail(members: Array<Array<String>>) {
    println("-----------------------------------------------")
    print("이메일 입력: ")
    val email = readln()

    val idx = findIndex(members, 1, email)

    if (idx == -1) {
        println("-----------------------------------------------")
        println("정보 없음!")

        return
    }

    printMember(members[idx])
}

fun selectName(members: Array<Array<String>>) {
    println("-----------------------------------------------")
    print("이름 입력: ")
    val name = readln()

    val idx = findIndex(members, 0, name)

    if (idx == -1) {
        println("-----------------------------------------------")
        println("정보 없음!")

        return
    }

    printMember(members[idx])
}

fun selectAll(members: Array<Array<String>>) {
    if (memberCnt == 0) {
        println("-----------------------------------------------")
        println("회원 없음")

        return
    }

    for (i in 0 until memberCnt) {
        print("${i + 1}. ")
        printMember(members[i])
    }
}

fun updateMember(members: Array<Array<String>>) {
    println("-----------------------------------------------")
    print("수정할 회원의 이메일 입력: ")
    val email = readln()

    val idx = findIndex(members, 1, email)

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
    members[idx][0] = readln()
    print("새 이메일 입력: ")
    members[idx][1] = readln()
    print("새 연락처 입력: ")
    members[idx][2] = readln()

    println("-----------------------------------------------")
    println("수정 완료")
}

fun deleteMember(members: Array<Array<String>>) {
    println("-----------------------------------------------")
    print("삭제할 회원 이메일 입력: ")
    val email = readln()

    val idx = findIndex(members, 1, email)

    if (idx == -1) {
        println("-----------------------------------------------")
        println("회원 없음")

        return
    }

    for (i in idx until memberCnt-1) {
        members[i][0] = members[i+1][0]
        members[i][1] = members[i+1][1]
        members[i][2] = members[i+1][2]
    }

    memberCnt--

    members[memberCnt][0] = ""
    members[memberCnt][1] = ""
    members[memberCnt][2] = ""

    println("-----------------------------------------------")
    println("삭제 완료")
}

fun main() {
    val num = printPricePlan()
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