package membermanagement2

abstract class Member(var name: String, var email: String, var phone: String) {
    abstract val grade: String
    abstract fun monthlyFee(): Int

    override fun toString(): String {
        println("-------------------------------------------------------")

        return "($grade) [이름] $name, [이메일] $email, [연락처] $phone, [월회비] ${monthlyFee()}원"
    }
}

class NormalMember(name: String, email: String, phone: String) : Member(name, email, phone) {
    override val grade = "일반"
    override fun monthlyFee() = 10000
}

class VipMember(name: String, email: String, phone: String) : Member(name, email, phone) {
    override val grade = "VIP"
    override fun monthlyFee() = 8000

    fun sendGift() {
        println(" -> ${name}님께 VIP 사은품 보냄")
    }
}