package vendingmachine

const val COKE = 500
const val CIDER = 500
const val FANTA = 300
const val WATER = 200

fun printMenu(totalMoney: Int) {
    println("================ 자판기 =================")
    println("[1]콜라: ${COKE}, [2]사이다: ${CIDER}, [3]환타: ${FANTA}")
    println("[4]물: ${WATER},   [5]돈 넣기,       [6]종료")
    println("----------------------------------------")
    println("현재 금액: $totalMoney")
    println("========================================")
}

fun getChoice(): Int {
    println("메뉴 선택: ")

    return readln().toInt()
}

fun getMoney(): Int {
    println("----------------------------------------")
    println("금액 투입: ")

    return readln().toInt()
}

fun calcMoney(totalMoney: Int, price: Int): Int {
    return totalMoney - price
}

fun calcMoneyException() {
    println("----------------------------------------")
    println("잔돈 부족")
}

fun main() {
    var totalMoney = 0

    while (true) {
        printMenu(totalMoney)
        val choice = getChoice()
        var result = -1

        when (choice) {
            1 -> {
                result = calcMoney(totalMoney, COKE)

                if (result < 0) {
                    calcMoneyException()
                }
                else {
                    totalMoney = result
                    println("----------------------------------------")
                    println("콜라 나옴")
                }
            }
            2 -> {
                result = calcMoney(totalMoney, CIDER)

                if (result < 0) {
                    calcMoneyException()
                }
                else {
                    totalMoney = result
                    println("----------------------------------------")
                    println("사이다 나옴")
                }
            }
            3 -> {
                result = calcMoney(totalMoney, FANTA)

                if (result < 0) {
                    calcMoneyException()
                }
                else {
                    totalMoney = result
                    println("----------------------------------------")
                    println("환타 나옴")
                }
            }
            4 -> {
                result = calcMoney(totalMoney, WATER)

                if (result < 0) {
                    calcMoneyException()
                }
                else {
                    totalMoney = result
                    println("----------------------------------------")
                    println("물 나옴")
                }
            }
            5 -> totalMoney += getMoney()
            6 -> {
                println("----------------------------------------")
                println("잔돈 ${totalMoney}원 반환")
                println("========================================")

                return
            }
            else -> {
                println("----------------------------------------")
                println("잘못된 입력. 다시 입력!")
            }
        }
    }
}