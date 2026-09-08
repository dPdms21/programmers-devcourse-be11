package membermanagement4

private fun printPricePlan(): Int {
    println("=============================================================")
    println("[요금제 선택]")
    println("[1]Lite: 10명 [2]Basic: 20명 [3]Premium: 30명")
    print("> ")

    return readln().toIntOrNull() ?: 1
}

private fun askSampleData(manager: MemberManager) {
    println("-------------------------------------------------------------")
    print("샘플 회원을 넣고 시작? (y/n) > ")

    if (readln().lowercase() != "y") return

    listOf(
        Member("김철수", "kim@naver.com", "010-1111-1111"),
        Member("이영희", "lee@naver.com", "010-2222-2222"),
        Member("박민수", "park@gmail.com", "010-3333-3333"),
        Member("김철수", "kim2@gmail.com", "010-4444-4444")
    ).forEach { manager.add(it) }

    println("${manager.memberCnt}명 세팅")
}

fun main() {
    val planNo = printPricePlan()

    val manager = MemberManager(planNo)
    askSampleData(manager)

    MemberApp(manager).start()
}