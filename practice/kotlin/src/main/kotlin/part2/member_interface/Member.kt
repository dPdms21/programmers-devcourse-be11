package part2.member_interface

class Member(var name: String, var email: String, var phone: String) {
    override fun toString(): String {
        return "[이름] $name, [이메일] $email, [연락처] $phone"
    }
}