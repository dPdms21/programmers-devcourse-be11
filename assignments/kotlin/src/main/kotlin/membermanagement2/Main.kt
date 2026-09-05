package membermanagement2

private fun printPricePlan(): Int {
    println("=======================================================")
    println("[요금제 선택]")
    println("[1]Lite: 10명 [2]Basic: 20명 [3]Premium: 30명")
    print("> ")

    return readln().toIntOrNull() ?: 1
}

fun main() {
    val planNo = printPricePlan()

    val app = MemberApp(MemberManager(planNo))
    app.start()
}