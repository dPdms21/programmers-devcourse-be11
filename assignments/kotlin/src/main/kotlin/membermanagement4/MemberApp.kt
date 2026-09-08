package membermanagement4

class MemberApp(private val manager: MemberManager) {
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
                    println("=============================================================")
                    println("프로그램 종료")
                    println("=============================================================")

                    return
                }
                else -> {
                    println("-------------------------------------------------------------")
                    println("올바른 번호 입력하기")
                }
            }
        }
    }

    private fun printMenu(): Int {
        println("=============================================================")
        println("[수행할 업무 선택 - 현재 회원수: ${manager.memberCnt}/${manager.totalCnt}]")
        println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
        println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제")
        println("[7]이름검색 [8]도메인별통계 [9]프로그램 종료")
        print("> ")

        return readln().toIntOrNull() ?: -1
    }

    private fun addMember() {
        if (manager.isFull) {
            println("-------------------------------------------------------------")
            println("회원 정원 초과")

            return
        }

        println("-------------------------------------------------------------")
        print("이름 입력: ")
        val name = readln()
        print("이메일 입력: ")
        val email = readln()
        print("연락처 입력: ")
        val phone = readln()

        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            println("-----------------------------------------------------")
            println("빈 값 입력 불가")

            return
        }

        if (manager.add(Member(name, email, phone))) {
            println("-------------------------------------------------------------")
            println("회원 등록 완료")
        } else {
            println("-------------------------------------------------------------")
            println("이미 존재하는 이메일")
        }
    }

    private fun selectByEmail() {
        println("-------------------------------------------------------------")
        print("이메일 입력: ")
        val email = readln()

        val member = manager.findByEmail(email)

        if (member == null) {
            println("-------------------------------------------------------------")
            println("정보 없음")

            return
        }

        println(member.display)
    }

    private fun selectByName() {
        println("-------------------------------------------------------------")
        print("이름 입력: ")
        val name = readln()

        val member = manager.findByName(name)

        if (member == null) {
            println("-------------------------------------------------------------")
            println("정보 없음")

            return
        }

        println(member.display)
    }

    private fun selectAll() {
        val all = manager.getAll()

        if (all.isEmpty()) {
            println("-------------------------------------------------------------")
            println("등록된 회원 없음")

            return
        }

        all.forEachIndexed { i, member ->
            println("${i + 1}. ${member.display}")
        }
    }

    private fun updateMember() {
        println("-------------------------------------------------------------")
        print("수정할 회원 이메일 입력: ")
        val email = readln()

        val member = manager.findByEmail(email)

        if (member == null) {
            println("-------------------------------------------------------------")
            println("회원 없음")

            return
        }

        println("현재 정보 → ${member.display}")

        println("-------------------------------------------------------------")
        print("새 이름 입력 (Enter 만 누르면 유지): ")
        val name = readln().ifBlank { member.name }
        print("새 이메일 입력 (Enter 만 누르면 유지): ")
        val newEmail = readln().ifBlank { member.email }
        print("새 연락처 입력 (Enter 만 누르면 유지): ")
        val phone = readln().ifBlank { member.phone }

        if (manager.update(email, name, newEmail, phone)) {
            println("-------------------------------------------------------------")
            println("수정 완료")
        } else {
            println("-------------------------------------------------------------")
            println("이미 사용 중인 이메일")
        }
    }

    private fun deleteMember() {
        println("-------------------------------------------------------------")
        print("삭제할 회원 이메일 입력: ")
        val email = readln()

        if (manager.delete(email)) {
            println("-------------------------------------------------------------")
            println("삭제 완료")
        } else {
            println("-------------------------------------------------------------")
            println("회원 없음")
        }
    }

    private fun searchByName() {
        println("-------------------------------------------------------------")
        print("검색할 이름의 일부 입력: ")
        val keyword = readln()

        val found = manager.searchByName(keyword)

        if (found.isEmpty()) {
            println("-------------------------------------------------------------")
            println("검색 결과 없음")

            return
        }

        println("-------------------------------------------------------------")
        println("${found.size}명 찾음")
        found.forEachIndexed { i, member -> println("${i + 1}. ${member.display}") }
    }

    private fun printStatistics() {
        if (manager.memberCnt == 0) {
            println("-------------------------------------------------------------")
            println("등록된 회원 없음")

            return
        }

        println("-------------------------------------------------------------")
        println("[이메일 도메인별]")
        manager.groupByDomain().forEach { (domain, list) ->
            println(" $domain: ${list.size}명 (${list.joinToString(", ") { it.name }})")
        }

        println("-------------------------------------------------------------")
        println("[이름순]")
        println(" ${manager.sortedByName().joinToString(", ") { it.name }}")

        val dup = manager.duplicatedNames()

        if (dup.isNotEmpty()) {
            println("-------------------------------------------------------------")
            println("[이름이 겹치는 회원]")
            dup.forEach { (name, list) ->
                println(" $name : ${list.size}명 (${list.joinToString(", ") { it.email }})")
            }
        }
    }
}