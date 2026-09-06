package membermanagement3

class LoggingMemberStorage(private val origin: MemberStorage) : MemberStorage by origin {
    override val storageName = "${origin.storageName} + 로그"

    override fun add(member: Member): Boolean {
        val result = origin.add(member)
        println("  [LOG] add(${member.email}) -> $result")

        return result
    }

    override fun delete(email: String): Boolean {
        val result = origin.delete(email)
        println("  [LOG] delete($email) -> $result")

        return result
    }

    override fun findByEmail(email: String): Member? {
        val result = origin.findByEmail(email)
        println("  [LOG] findByEmail($email) -> $result")

        return result
    }
}