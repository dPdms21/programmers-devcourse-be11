package membermanagement3

class CountingMemberStorage(private val origin: MemberStorage) : MemberStorage by origin {
    var addCnt = 0
        private set

    var deleteCnt = 0
        private set

    override val storageName = "${origin.storageName} + 카운트"

    override fun add(member: Member): Boolean {
        addCnt++

        return origin.add(member)
    }

    override fun delete(email: String): Boolean {
        deleteCnt++

        return origin.delete(email)
    }
}