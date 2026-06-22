package part2;

/*
 * 자판기
 * 현재 금액과 판매 음료들을 관리한다.
 */

public class L_vendingmachine {
    private int totalMoney;
    private L_drink[] drinks;

    public L_vendingmachine() {
        totalMoney = 0;
        // 부모 타입 (Drink) 배열에 자식 객체들을 담는다 (다형성)
        drinks = new L_drink[] {
                new L_coke(),  // 0
                new L_cider(), // 1
                new L_fanta(), // 2
                new L_water()  // 3
        };
    }

    // 돈 넣기 : insertMoney
    public void insertMoney(int money) {
        totalMoney += money;
        System.out.println(money + "원을 넣었습니다.");
    }

    // 음료구매 : buy - 메뉴 번호(1~4)로 선택
    public void buy(int menuNumber) {
        L_drink drink = drinks[menuNumber - 1];

        if ( totalMoney < drink.getPrice() ) {
            System.out.println("잔돈이 부족합니다.");
            return;
        }

        totalMoney -= drink.getPrice();
        drink.dispense();
    }

    // 종료 시 잔돈 반환
    public int returnMoney() {
        int returnMoney = totalMoney;
        totalMoney = 0;

        return returnMoney;
    }

    // 메뉴출력
    public void printMenu() {
        System.out.println("============== 자판기 ==============");
        System.out.println("[1]콜라 : 500  [2]사이다 : 500  [3]환타 : 700  [4]물 : 200");
        System.out.println("[5]돈 넣기  [6]종료");
        System.out.println("현재 금액 : " + totalMoney);
        System.out.println("====================================");
    }
}
