package part2.member

// 회원 목록을 보관하고 규칙을 담당하는 클래스

class MemberManager(planNo: Int) {
    val totalCnt = planNo * 10

    var memberCnt = 0
        private set

    // Array<Member>
    private val members = Array(totalCnt) { Member() }

    val isFull: Boolean
        get() = memberCnt == totalCnt

    // 회원 추가
    fun addMember(member: Member): Boolean {
        if (isFull) return false
        if (findIndex(member.email) != -1) return false

        members[memberCnt] = member
        memberCnt++

        return true
    }

    // 이메일 조회
    fun findByEmail(email: String): Member? {
        val idx = findIndex(email)

        return if (idx == -1) null else members[idx]
    }

    // 이름 조회
    fun findByName(name: String): Member? {
        for (i in 0 until memberCnt) {
            if (members[i].name == name) return members[i]
        }

        return null
    }

    // 전체 조회: 등록된 회원만 잘라서 돌려줌
    fun getAll(): Array<Member> {
        return members.copyOfRange(0, memberCnt)
    }

    fun delete(email: String): Boolean {
        val idx = findIndex(email)

        if (idx == -1) return false

        for (i in idx until memberCnt - 1) {
            members[i] = members[i + 1]
        }

        memberCnt--
        members[memberCnt] = Member()

        return true
    }

    private fun findIndex(email: String): Int {
        for (i in 0 until memberCnt) {
            if (members[i].email == email) {
                return i
            }
        }

        return -1
    }
}