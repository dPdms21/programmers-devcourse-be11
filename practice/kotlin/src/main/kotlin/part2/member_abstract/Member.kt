package part2.member_abstract

abstract class Member(var name: String, var email: String, var phone: String) {
    abstract val grade: String

    abstract fun monthlyFee(): Int

    override fun toString(): String {
        return "[$grade] [이름] $name, [이메일] $email, [연락처] $phone, [월회비] ${monthlyFee()}원"
    }
}

// 자식 1) 일반 회원
class NormalMember(name: String, email: String, phone: String) : Member(name, email, phone) {
    override val grade = "일반"

    override fun monthlyFee() = 10000
}

// 자식 2) VIP 회원
class VipMember(name: String, email: String, phone: String) : Member(name, email, phone) {
    override val grade = "VIP"

    override fun monthlyFee() = 8000            // 할인가

    // 자식만의 메서드. 부모에는 없으므로 Member 타입으로는 부를 수 없음
    // 이것을 쓰려면 is로 확인해야 함 (MemberApp의 selectByEmail 참고)
    fun sendGift() {
        println("  → $name VIP 사은품 전달")
    }
}