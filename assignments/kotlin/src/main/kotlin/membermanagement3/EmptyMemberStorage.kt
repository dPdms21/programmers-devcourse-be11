package membermanagement3

class EmptyMemberStorage : MemberStorage {
    override var memberCnt = 0

    override val storageName = "빈 저장소"

    override val isFull = false

    override val capacity = 0

    override fun add(member: Member): Boolean {
        return false
    }

    override fun findByEmail(email: String): Member? {
        return null
    }

    override fun findByName(name: String): Member? {
        return null
    }

    override fun delete(email: String): Boolean {
        return false
    }

    override fun getAll(): Array<Member> {
        return arrayOf()
    }
}