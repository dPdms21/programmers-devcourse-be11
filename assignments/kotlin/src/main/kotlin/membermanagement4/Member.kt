package membermanagement4

data class Member(val name: String, val email: String, val phone: String) {
    val display: String
        get() = "[이름] $name, [이메일] $email, [연락처] $phone"
}