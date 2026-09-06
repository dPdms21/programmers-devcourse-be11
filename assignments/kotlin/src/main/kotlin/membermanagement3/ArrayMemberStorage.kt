package membermanagement3

class ArrayMemberStorage(planNo: Int) : MemberStorage {
    private val totalCnt = planNo * 10
    private val members = Array(totalCnt) { Member("", "", "") }

    override var memberCnt = 0
        private set

    override val storageName = "배열 저장소 (정원 ${totalCnt}명)"

    override val isFull: Boolean
        get() = memberCnt == totalCnt

    override val capacity = totalCnt

    private fun findIndex(email: String): Int {
        for (i in 0 until memberCnt) {
            if (members[i].email == email) {
                return i
            }
        }

        return -1
    }

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

        return if (idx == -1) null else members[idx]
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

        for (i in idx until memberCnt-1) {
            members[i] = members[i+1]
        }

        memberCnt--
        members[memberCnt] = Member("", "", "")

        return true
    }

    override fun getAll(): Array<Member> {
        return members.copyOfRange(0, memberCnt)
    }
}