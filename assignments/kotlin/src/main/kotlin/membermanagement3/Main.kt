package membermanagement3

private fun printPricePlan(): Int {
    println("=======================================================")
    println("[요금제 선택]")
    println("[1]Lite: 10명 [2]Basic: 20명 [3]Premium: 30명")
    print("> ")

    return readln().toIntOrNull() ?: 1
}

fun main() {
    val planNo = printPricePlan()

    // val storage: MemberStorage = ArrayMemberStorage(planNo)
    val storage: MemberStorage = LoggingMemberStorage(ArrayMemberStorage(planNo))

    val app = MemberApp(storage)
    app.start()
}