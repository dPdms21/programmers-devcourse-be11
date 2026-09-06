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

    fun update(email: String, newName: String, newEmail: String, newPhone: String): Boolean {
        val idx = findIndex(email)

        if (idx == -1) {
            return false
        }

        val idx2 = findIndex(newEmail)

        if (idx2 != -1 && idx2 != idx) {
            return false
        }

        members[idx]?.name = newName
        members[idx]?.email = newEmail
        members[idx]?.phone = newPhone

        return true
    }

    fun upgrade(email:String) : Boolean {
        val idx = findIndex(email)

        if (idx == -1) {
            return false
        }

        val member = members[idx] ?: return false

        members[idx] = VipMember(
            member.name,
            member.email,
            member.phone
        )

        return true
    }

    fun getVipMembers(): Array<Member> {
        var vipCnt = 0

        for (i in 0 until memberCnt) {
            if (members[i] is VipMember) {
                vipCnt++
            }
        }

        val vipMembers = arrayOfNulls<Member>(vipCnt)
        var idx = 0

        for (i in 0 until memberCnt) {
            val member = members[i]

            if (member is VipMember) {
                vipMembers[idx] = member
                idx++
            }
        }

        return Array(vipCnt) {
            vipMembers[it]!!
        }
    }
}