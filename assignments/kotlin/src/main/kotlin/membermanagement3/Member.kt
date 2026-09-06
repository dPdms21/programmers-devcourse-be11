package membermanagement3

class Member(var name: String, var email: String, var phone: String) {
    override fun toString(): String {
        println("-------------------------------------------------------")

        return "[이름] $name, [이메일] $email, [연락처] $phone"
    }
}