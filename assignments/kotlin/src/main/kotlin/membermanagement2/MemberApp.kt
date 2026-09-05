package membermanagement2

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
                7 -> {
                    println("=======================================================")
                    println("프로그램 종료")
                    println("=======================================================")

                    return
                }
                else -> {
                    println("-------------------------------------------------------")
                    println("올바른 번호 입력하기")
                }
            }
        }
    }

    private fun printMenu(): Int {
        println("=======================================================")
        println("[수행할 업무 선택 - 현재 회원수: ${manager.memberCnt}/${manager.totalCnt}]")
        println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
        println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제")
        println("[7]프로그램 종료")
        print("> ")

        return readln().toIntOrNull() ?: -1
    }

    private fun addMember() {
        if (manager.isFull) {
            println("-------------------------------------------------------")
            println("회원 정원 초과")

            return
        }

        println("-------------------------------------------------------")
        print("이름 입력: ")
        val name = readln()
        print("이메일 입력: ")
        val email = readln()
        print("연락처 입력: ")
        val phone = readln()

        println("-------------------------------------------------------")
        println("등급 선택 [1]일반(10000원) [2]VIP(8000원)")
        val gradeNo = readln().toIntOrNull() ?: 1

        val member = if (gradeNo == 2) {
            VipMember(name, email, phone)
        } else {
            NormalMember(name, email, phone)
        }

        if (manager.add(member)) {
            println("-------------------------------------------------------")
            println("${member.grade} 회원 등록 완료")
        } else {
            println("-------------------------------------------------------")
            println("이미 존재하는 회원")
        }
    }

    private fun selectByEmail() {
        println("-------------------------------------------------------")
        print("이메일 입력: ")
        val email = readln()

        val member = manager.findByEmail(email)

        if (member == null) {
            println("-------------------------------------------------------")
            println("정보 없음")

            return
        }

        println(member)

        if (member is VipMember) {
            member.sendGift()
        }
    }

    private fun selectByName() {
        println("-------------------------------------------------------")
        print("이름 입력: ")
        val name = readln()

        val member = manager.findByName(name)

        if (member == null) {
            println("-------------------------------------------------------")
            println("정보 없음")

            return
        }

        println(member)
    }

    private fun selectAll() {
        val all = manager.getAll()

        if (all.isEmpty()) {
            println("-------------------------------------------------------")
            println("등록된 회원 없음")

            return
        }

        for (i in all.indices) {
            println("${i + 1}. ${all[i]}")
        }

        println("-------------------------------------------------------")
        println("월 예상 매출: ${manager.totalMonthlyFee()}원")
    }

    private fun updateMember() {
        println("-------------------------------------------------------")
        print("수정할 회원 이메일 입력: ")
        val email = readln()

        val member = manager.findByEmail(email)

        if (member == null) {
            println("-------------------------------------------------------")
            println("회원 없음")

            return
        }

        println("-------------------------------------------------------")
        println("현재 정보 → $member")

        println("-------------------------------------------------------")
        print("새 이름 입력: ")
        val newName = readln()
        print("새 이메일 입력: ")
        val newEmail = readln()
        print("새 연락처 입력: ")
        val newPhone = readln()

        member.name = newName
        member.email = newEmail
        member.phone = newPhone

        println("-------------------------------------------------------")
        println("수정 완료")
    }

    private fun deleteMember() {
        println("-------------------------------------------------------")
        print("삭제할 회원 이메일 입력: ")
        val email = readln()

        if (manager.delete(email)) {
            println("-------------------------------------------------------")
            println("삭제 완료")
        } else {
            println("-------------------------------------------------------")
            println("회원 없음")
        }
    }
}