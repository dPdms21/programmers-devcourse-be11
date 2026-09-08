package membermanagement4

class ListMemberStorage(planNo: Int) : MemberStorage {
    private val totalCnt = planNo * 10
    private val members = mutableListOf<Member>()

    override val memberCnt: Int
        get() = members.size

    override val storageName = "리스트 저장소 (정원 ${totalCnt}명)"

    override val isFull: Boolean
        get() = memberCnt == totalCnt

    override val capacity = totalCnt

    override fun add(member: Member): Boolean {
        if (isFull) {
            return false
        }

        if (members.any { it.email == member.email }) {
            return false
        }

        members.add(member)

        return true
    }

    override fun findByEmail(email: String): Member? {
        return members.find { it.email == email }
    }

    override fun findByName(name: String): List<Member> {
        return members.filter { it.name == name }
    }

    override fun delete(email: String): Boolean {
        return members.removeAll { it.email == email }
    }

    override fun getAll(): List<Member> {
        return members.toList()
    }

    override fun update(
        email: String,
        newName: String,
        newEmail: String,
        newPhone: String
    ): UpdateResult {
        val idx = members.indexOfFirst { it.email == email }

        if (idx == -1) {
            return UpdateResult.NOT_FOUND
        }

        if (newEmail != email && members.any { it.email == newEmail }) {
            return UpdateResult.DUPLICATE_EMAIL
        }

        members[idx] = members[idx].copy(
            name = newName,
            email = newEmail,
            phone = newPhone
        )

        return UpdateResult.OK
    }

    override fun searchByName(keyword: String): List<Member> {
        return members.filter { it.name.contains(keyword) }
    }

    override fun sortedByName(): List<Member> {
        return members.sortedBy { it.name }
    }

    override fun groupByDomain(): Map<String, List<Member>> {
        return members.groupBy {
            it.email.substringAfter("@", "(도메인없음)")
        }
    }

    override fun duplicatedNames(): Map<String, List<Member>> {
        return members
            .groupBy { it.name }
            .filter { it.value.size > 1 }
    }
}