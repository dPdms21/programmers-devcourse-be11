package vendingmachine2;

public class VendingMachine {
    private int totalMoney;
    private Drink[] drinks;

    public VendingMachine() {
        totalMoney = 0;
        drinks = new Drink[] {new Coke(), new Cider(), new Fanta(), new Water()};
    }

    public void insertMoney(int money) {
        totalMoney += money;
        System.out.println("-------------------------------");
        System.out.println(money + "원 입금");
    }

    public void printMenu() {
        System.out.println("============ 자판기 ============");
        System.out.println("[1]콜라 : 500  [2]사이다 : 500");
        System.out.println("[3]환타 : 300  [4]물 : 200");
        System.out.println("[5]돈 넣기     [6]종료");
        System.out.println("-------------------------------");
        System.out.println("현재 금액 : " + totalMoney);
        System.out.println("===============================");
    }

    public void buy(int menuNum) {
        Drink drink = drinks[menuNum - 1];

        if (totalMoney < drink.getPrice()) {
            System.out.println("-------------------------------");
            System.out.println("잔돈 부족!");
            return;
        }

        totalMoney -= drink.getPrice();
        drink.dispense();
    }

    public int returnChange() {
        int charge = totalMoney;
        totalMoney = 0;

        return charge;
    }
}
