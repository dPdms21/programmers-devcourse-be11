package membermanagement3

interface MemberStorage {
    val storageName: String
    val memberCnt: Int
    val isFull: Boolean
    val capacity: Int

    fun add(member: Member): Boolean
    fun findByEmail(email: String): Member?
    fun findByName(name: String): Member?
    fun delete(email: String): Boolean
    fun getAll(): Array<Member>

    fun isEmpty(): Boolean {
        return memberCnt == 0
    }
}