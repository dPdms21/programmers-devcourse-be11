package part2.member

// 회원 한 명의 정보

class Member(var name: String, var email: String, var phone: String) {
    // 아직 회원이 들어오지 않은 빈 칸을 만들 때는 부 생성자
    constructor() : this("", "", "")

    override fun toString(): String {
        return "[이름]: $name, [이메일]: $email, [연락처]: $phone"
    }
}