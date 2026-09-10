package part3.member

class MemberApp(private val manager: MemberManager) {
    fun start() {
        while (true) {
            when(printMenu()) {
                1 -> addMember()
                2 -> selectByEmail()
                3 -> selectByName()
                4 -> selectAll()
                5 -> updateMember()
                6 -> deleteMember()
                7 -> searchByName()
                8 -> printStatics()
                9 -> {
                    println("프로그램 종료")

                    return
                }
                else -> println("올바른 번호 입력")
            }
        }
    }

    private fun printMenu(): Int {
        println("[수행할 업무 선택 - 현재 회원수: ${manager.memberCnt} / ${manager.totalCnt}]")
        println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
        println("[4]전체조회 [5]정보수정 [6]회원삭제")
        println("[7]이름검색 [8]도메인별통계 [9]프로그램 종료")

        return readln().toInt()
    }

    private fun addMember() {
        if (manager.isFull) {
            println("회원 초과")

            return
        }

        println("이름 입력")
        val name = readln()
        println("이메일 입력")
        val email = readln()
        println("연락처 입력")
        val phone = readln()

        if (manager.addMember(Member(name, email, phone))) {
            println("회원 등록 완료")
        } else {
            println("이미 존재하는 회원")
        }
    }

    private fun selectByEmail() {
        println("이메일 입력")
        val email = readln()

        val member = manager.findByEmail(email)

        if (member == null) {
            println("정보 없음")

            return
        }

        println(member.display)
    }

    private fun selectByName() {
        println("이름 입력")
        val name = readln()

        val member = manager.findByName(name)

        if (member == null) {
            println("정보 없음")

            return
        }

        println(member.display)
    }

    private fun selectAll() {
        val all = manager.getAll()

        if (all.isEmpty()) {
            println("등록된 회원 없음")
            return
        }

        all.forEachIndexed { index, member ->
            println("${index + 1}. ${member.display}")
        }
    }

    private fun updateMember() {
        println("수정할 회원의 이메일 입력")
        val email = readln()
        val member = manager.findByEmail(email)

        if (member == null) {
            println("회원 없음")

            return
        }

        println("현재 정보 -> ${member.display}")

        // ifBlank: 빈 문자열일 때 대신 쓸 값을 정함
        println("새 이름 입력 (Enter만 누르면 유지)")
        val name = readln().ifBlank { member.name }
        println("새 이메일 입력 (Enter만 누르면 유지)")
        val newEmail = readln().ifBlank { member.email }
        println("새 연락처 입력 (Enter만 누르면 유지)")
        val phone = readln().ifBlank { member.phone }

        if (manager.updateMember(email, newEmail, name, phone)) {
            println("수정 완료")
        } else {
            println("이미 사용중인 이메일")
        }
    }

    private fun deleteMember() {
        println("삭제할 회원의 이메일 입력")
        val email = readln()

        if (manager.deleteMember(email)) {
            println("삭제 완료")
        } else {
            println("회원 없음")
        }
    }

    private fun searchByName() {
        println("검색할 이름의 일부를 입력")
        val keyword = readln()

        val found = manager.searchByName(keyword)

        if (found.isEmpty()) {
            println("검색 결과가 없음")

            return
        }

        println("${found.size}명을 찾음")
        found.forEachIndexed { index, member ->
            println("${index + 1}. ${member.display}")
        }
    }

    private fun printStatics() {
        if (manager.memberCnt == 0) {
            println("등록된 회원이 없음")

            return
        }

        println("[이메일 도메인별]")
        manager.groupByDomain().forEach {
                (domain, members) ->
            println(" ${domain}: ${members.size}명 ${ members.joinToString(", ") { it.name } } ")
        }

        println("[이름순]")
        println( " ${ manager.sortedByName().joinToString( ", ") { it.name } }" )

        val duplicatedNames = manager.duplicatedNames()

        if (duplicatedNames.isNotEmpty()) {
            println("[이름이 겹치는 회원]")
            duplicatedNames.forEach {
                    (name, members) ->
                println(" $name : ${members.size}명 ${ members.joinToString(", ") { it.email } } ")
            }
        }
    }
}