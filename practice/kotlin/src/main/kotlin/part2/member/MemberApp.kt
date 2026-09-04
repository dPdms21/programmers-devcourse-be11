package part2.member

// 메뉴와 입력, 출력을 담당하는 클래스

class MemberApp(private val manager: MemberManager) {
    fun start() {
        when (printMenu()) {
            1 -> addMember()
            2 -> selectByEmail()
            3 -> selectByName()
            4 -> selectAll()
            5 -> updateMember()
            6 -> deleteMember()
            7 -> {
                println("프로그램 종료")

                return
            }
            else -> println("올바른 번호 입력")
        }
    }

    private fun printMenu(): Int {
        println("\n[수행할 업무 선택 - 현재 회원수: ${manager.memberCnt}/${manager.totalCnt}]")
        println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
        println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제")
        println("[7]프로그램 종료")
        print("> ")

        return readln().toInt()
    }

    private fun addMember() {
        // 규칙 검사는 manager가 하지만, 왜 실패했는지 알려 주려면 미리 물어봐야 함
        if (manager.isFull) {
            println("회원 정원 초과")

            return
        }

        println("이름 입력:")
        val name = readln()
        println("이메일 입력:")
        val email = readln()
        println("연락처 입력:")
        val phone = readln()

        // 입력받은 값으로 회원 객체를 만들어서 통째로 넘김
        if (manager.addMember(Member(name, email, phone))) {
            println("회원 등록 완료")
        } else {
            println("이미 존재하는 회원")
        }
    }

    private fun selectByEmail() {
        println("이메일 입력:")
        val email = readln()

        // 돌려받은 값이 Member?이므로 null 검사를 하지 않으면 컴파일이 되지 않음
        val member = manager.findByEmail(email)

        if (member == null) {
            println("정보 없음")

            return
        }

        // println(객체)를 하면 toString()이 호출됨
        println(member)
    }

    private fun selectByName() {
        println("이름 입력:")
        val name = readln()

        val member = manager.findByName(name)

        if (member == null) {
            println("정보 없음")

            return
        }

        println(member)
    }

    private fun selectAll() {
        val all = manager.getAll()

        if (all.isEmpty()) {
            println("등록된 회원 없음")

            return
        }

        // 등록된 회원만 잘라서 받았으므로 빈 칸이 섞일 걱정이 없음
        for (i in all.indices) {
            println("${i + 1}. ${all[i]}")
        }
    }

    private fun updateMember() {
        println("수정할 회원의 이메일 입력:")
        val email = readln()

        val member = manager.findByEmail(email)

        if (member == null) {
            println("회원 없음")

            return
        }

        println("현재 정보 → $member")

        // findByEmail은 저장소 안에 들어 있는 '그 객체'를 돌려줌. 복사본이 아님
        // 그래서 여기서 값을 바꾸면 저장소의 내용이 그대로 바뀜. 인덱스를 들고 다닐 필요가 없음
        // (다만 이 방법은 manager를 거치지 않으므로 이메일 중복 검사를 건너뜀. Main.kt의 [1] 참고)
        println("새 이름 입력:")
        member.name = readln()
        println("새 이메일 입력:")
        member.email = readln()
        println("새 연락처 입력:")
        member.phone = readln()

        println("수정 완료")
    }

    private fun deleteMember() {
        println("삭제할 회원의 이메일 입력:")
        val email = readln()

        if (manager.delete(email)) {
            println("삭제 완료")
        } else {
            println("회원 없음")
        }
    }
}