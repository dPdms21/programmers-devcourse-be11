package membermanagement4

interface MemberStorage {
    val storageName: String
    val memberCnt: Int
    val isFull: Boolean
    val capacity: Int

    fun add(member: Member): Boolean
    fun findByEmail(email: String): Member?
    fun findByName(name: String): List<Member>
    fun delete(email: String): Boolean
    fun getAll(): List<Member>

    fun isEmpty(): Boolean {
        return memberCnt == 0
    }

    fun update(email: String, newName: String, newEmail: String, newPhone: String): UpdateResult

    fun searchByName(keyword: String): List<Member>

    fun sortedByName(): List<Member>

    fun groupByDomain(): Map<String, List<Member>>

    fun duplicatedNames(): Map<String, List<Member>>
}