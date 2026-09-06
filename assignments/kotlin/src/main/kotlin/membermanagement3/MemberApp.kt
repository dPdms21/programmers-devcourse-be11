package membermanagement3

import membermanagement.EMAIL
import membermanagement.NAME
import membermanagement.PHONE

class MemberApp(private val storage: MemberStorage) {
    fun start() {
        println("저장소: ${storage.storageName}")

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
                    println("올바른 번호 입력!!")
                }
            }
        }
    }

    private fun printMenu(): Int {
        println("=======================================================")
        println("[수행할 업무 선택 - 현재 회원수: ${storage.memberCnt}]")
        println("[1]회원추가 [2]회원조회(메일) [3]회원조회(이름)")
        println("[4]회원전체조회 [5]회원정보 수정 [6]회원삭제")
        println("[7]프로그램 종료")
        print("> ")

        return readln().toIntOrNull() ?: -1
    }

    private fun addMember() {
        if (storage.isFull) {
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

        if (storage.add(Member(name, email, phone))) {
            println("-------------------------------------------------------")
            println("회원 등록 완료")
        } else {
            println("-------------------------------------------------------")
            println("이미 존재하는 회원")
        }
    }

    private fun selectAll() {
        if (storage.isEmpty()) {
            println("-------------------------------------------------------")
            println("등록된 회원 없음")

            return
        }

        val all = storage.getAll()

        for (i in all.indices) {
            println("${i + 1}. ${all[i]}")
        }
    }

    private fun selectByEmail() {
        println("-------------------------------------------------------")
        print("이메일 입력: ")
        val email = readln()

        val member = storage.findByEmail(email)

        if (member == null) {
            println("-------------------------------------------------------")
            println("정보 없음")

            return
        }

        println(member)
    }

    private fun selectByName() {
        println("-------------------------------------------------------")
        print("이름 입력: ")
        val name = readln()

        val member = storage.findByName(name)

        if (member == null) {
            println("-------------------------------------------------------")
            println("정보 없음")

            return
        }

        println(member)
    }

    private fun updateMember() {
        println("-------------------------------------------------------")
        print("수정할 회원 이메일 입력: ")
        val email = readln()

        val member = storage.findByEmail(email)

        if (member == null) {
            println("-------------------------------------------------------")
            println("회원 없음")

            return
        }

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

        println("-----------------------------------------------")
        println("수정 완료")
    }

    private fun deleteMember() {
        println("-------------------------------------------------------")
        print("삭제할 회원 이메일 입력: ")
        val email = readln()

        if (storage.delete(email)) {
            println("-------------------------------------------------------")
            println("삭제 완료")
        } else {
            println("-------------------------------------------------------")
            println("회원 없음")
        }
    }
}