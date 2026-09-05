package membermanagement2

class MemberManager(planNo: Int) {
    val totalCnt = planNo * 10

    var memberCnt = 0
        private set

    private val members = arrayOfNulls<Member>(totalCnt)

    val isFull: Boolean
        get() = memberCnt == totalCnt

    private fun findIndex(email: String): Int {
        for (i in 0 until memberCnt) {
            if (members[i]?.email == email) {
                return i
            }
        }

        return -1
    }

    fun findByEmail(email: String): Member? {
        val idx = findIndex(email)

        return if (idx == -1) null else members[idx]
    }

    fun findByName(name: String): Member? {
        for (i in 0 until memberCnt) {
            if (members[i]?.name == name) return members[i]
        }

        return null
    }

    fun add(member: Member): Boolean {
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

    fun delete(email: String): Boolean {
        val idx = findIndex(email)

        if (idx == -1) {
            return false
        }

        for (i in idx until memberCnt-1) {
            members[i] = members[i + 1]
        }

        memberCnt--
        members[memberCnt] = null

        return true
    }

    fun getAll(): Array<Member> {
        return Array(memberCnt) {
            members[it]!!
        }
    }

    fun totalMonthlyFee(): Int {
        var sum = 0

        for (i in 0 until memberCnt) {
            sum += members[i]!!.monthlyFee()
        }

        return sum
    }
}