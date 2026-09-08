package part2.member_interface

interface MemberStorage {
    val storageName: String     // 화면에 보여 줄 저장소 이름
    val memberCnt: Int          // 현재 회원 수
    val isFull: Boolean         // 더 넣을 수 있는가

    fun add(member: Member): Boolean
    fun findByEmail(email: String): Member?
    fun findByName(name: String): Member?
    fun delete(email: String): Boolean
    fun getAll(): Array<Member>

    fun isEmpty(): Boolean {
        return memberCnt == 0
    }
}