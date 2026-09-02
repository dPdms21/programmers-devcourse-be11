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

    return readln().toIntOrNull() ?: -1
}

fun getMoney(): Int {
    println("----------------------------------------")
    println("금액 투입: ")

    val money = readln().toIntOrNull()

    if (money == null) {
        println("----------------------------------------")
        println("잘못된 입력. 숫자로 입력!")
        return 0
    }

    if (money % 100 != 0) {
        println("----------------------------------------")
        println("100원 단위로 넣기")

        return 0
    }

    return money
}

fun calcMoney(totalMoney: Int, price: Int): Int {
    return totalMoney - price
}

fun calcMoneyException() {
    println("----------------------------------------")
    println("잔돈 부족")
}

fun buy(totalMoney: Int, price: Int, name: String): Int {
    val result = calcMoney(totalMoney, price)

    if (result < 0) {
        calcMoneyException()

        return totalMoney
    }

    println("----------------------------------------")
    println("${name} 나옴")

    return result
}

fun main() {
    var totalMoney = 0

    while (true) {
        printMenu(totalMoney)
        val choice = getChoice()

        when (choice) {
            1 -> totalMoney = buy(totalMoney, COKE, "콜라")
            2 -> totalMoney = buy(totalMoney, CIDER, "사이다")
            3 -> totalMoney = buy(totalMoney, FANTA, "환타")
            4 -> totalMoney = buy(totalMoney, WATER, "물")
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