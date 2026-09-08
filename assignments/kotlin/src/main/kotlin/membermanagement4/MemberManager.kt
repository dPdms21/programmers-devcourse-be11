package membermanagement4

class MemberManager(planNo: Int) {
    val totalCnt = planNo * 10

    private val members = mutableListOf<Member>()

    val memberCnt: Int
        get() = members.size

    val isFull: Boolean
        get() = memberCnt >= totalCnt

    fun findByEmail(email: String): Member? = members.find { it.email == email }

    fun findByName(name: String): Member? = members.find { it.name == name }

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

    fun update(email: String, newName: String, newEmail: String, newPhone: String): Boolean {
        val idx = members.indexOfFirst { it.email == email }

        if (idx == -1) {
            return false
        }

        if (newEmail != email && members.any { it.email == newEmail }) {
            return false
        }

        members[idx] = members[idx].copy(name = newName, email = newEmail, phone = newPhone)

        return true
    }

    fun sortedByName(): List<Member> = members.sortedBy { it.name }

    fun groupByDomain(): Map<String, List<Member>> = members.groupBy { it.email.substringAfter("@", "(도메인없음)") }

    fun duplicatedNames(): Map<String, List<Member>> = members.groupBy { it.name }.filter { it.value.size > 1 }
}