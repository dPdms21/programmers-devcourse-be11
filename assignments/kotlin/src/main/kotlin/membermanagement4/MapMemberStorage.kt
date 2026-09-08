package membermanagement4

class MapMemberStorage(planNo: Int) : MemberStorage {
    private val totalCnt = planNo * 10
    private val members = mutableMapOf<String, Member>()

    override val storageName = "맵 저장소 (정원 ${totalCnt}명)"

    override val memberCnt: Int
        get() = members.size

    override val isFull: Boolean
        get() = memberCnt == totalCnt

    override val capacity = totalCnt

    override fun add(member: Member): Boolean {
        if (isFull) {
            return false
        }

        if (members.containsKey(member.email)) {
            return false
        }

        members[member.email] = member

        return true
    }

    override fun findByEmail(email: String): Member? {
        return members[email]
    }

    override fun findByName(name: String): List<Member> {
        return members.values.filter { it.name == name }
    }

    override fun delete(email: String): Boolean {
        return members.remove(email) != null
    }

    override fun getAll(): List<Member> {
        return members.values.toList()
    }

    override fun update(email: String, newName: String, newEmail: String, newPhone: String): UpdateResult {
        val member = members[email]
            ?: return UpdateResult.NOT_FOUND

        if (newEmail != email && members.containsKey(newEmail)) {
            return UpdateResult.DUPLICATE_EMAIL
        }

        val updatedMember = member.copy(
            name = newName,
            email = newEmail,
            phone = newPhone
        )

        if (newEmail != email) {
            members.remove(email)
        }

        members[newEmail] = updatedMember

        return UpdateResult.OK
    }

    override fun searchByName(keyword: String): List<Member> {
        return members.values.filter { it.name.contains(keyword) }
    }

    override fun sortedByName(): List<Member> {
        return members.values.sortedBy { it.name }
    }

    override fun groupByDomain(): Map<String, List<Member>> {
        return members.values.groupBy {
            it.email.substringAfter("@", "(도메인없음)")
        }
    }

    override fun duplicatedNames(): Map<String, List<Member>> {
        return members.values
            .groupBy { it.name }
            .filter { it.value.size > 1 }
    }
}