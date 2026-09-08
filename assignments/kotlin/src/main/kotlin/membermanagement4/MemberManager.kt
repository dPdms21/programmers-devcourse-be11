package membermanagement4

class MemberManager(planNo: Int) {
    val totalCnt = planNo * 10

    private val members = mutableListOf<Member>()

    val memberCnt: Int
        get() = members.size

    val isFull: Boolean
        get() = memberCnt >= totalCnt

    fun findByEmail(email: String): Member? = members.find { it.email == email }

    fun findByName(name: String): List<Member> = members.filter { it.name == name }

    fun searchByName(keyword: String): List<Member> = members.filter { it.name.contains(keyword) }

    fun getAll(): List<Member> = members.toList()

    fun add(member: Member): Boolean {
        if (isFull) {
            return false
        }

        if (members.any { it.email == member.email }) {
            return false
        }

        members.add(member)

        return true
    }

    fun delete(email: String): Boolean = members.removeAll { it.email == email }

    fun update(email: String, newName: String, newEmail: String, newPhone: String): UpdateResult {
        val idx = members.indexOfFirst { it.email == email }

        if (idx == -1) {
            return UpdateResult.NOT_FOUND
        }

        if (newEmail != email && members.any { it.email == newEmail }) {
            return UpdateResult.DUPLICATE_EMAIL
        }

        members[idx] = members[idx].copy(name = newName, email = newEmail, phone = newPhone)

        return UpdateResult.OK
    }

    fun sortedByName(): List<Member> = members.sortedBy { it.name }

    fun groupByDomain(): Map<String, List<Member>> = members.groupBy { it.email.substringAfter("@", "(도메인없음)") }

    fun duplicatedNames(): Map<String, List<Member>> = members.groupBy { it.name }.filter { it.value.size > 1 }
}

enum class UpdateResult {
    OK,
    NOT_FOUND,
    DUPLICATE_EMAIL
}