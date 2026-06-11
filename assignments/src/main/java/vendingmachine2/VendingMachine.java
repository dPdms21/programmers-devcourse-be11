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
        System.out.println("[1]" + drinks[0].getName() + " : " + drinks[0].getPrice() + "원 (" + drinks[0].getStock() + "개)");
        System.out.println("[2]" + drinks[1].getName() + " : " + drinks[1].getPrice() + "원 (" + drinks[1].getStock() + "개)");
        System.out.println("[3]" + drinks[2].getName() + " : " + drinks[2].getPrice() + "원 (" + drinks[2].getStock() + "개)");
        System.out.println("[4]" + drinks[3].getName() + " : " + drinks[3].getPrice() + "원 (" + drinks[3].getStock() + "개)");
        System.out.println("[5]돈 넣기     [6]종료");
        System.out.println("-------------------------------");
        System.out.println("현재 금액 : " + totalMoney);
        System.out.println("===============================");
    }

    public void buy(int menuNum) {
        Drink drink = drinks[menuNum - 1];

        if (drink.isSoldOut()) {
            System.out.println("-------------------------------");
            System.out.println("품절!!");
            return;
        }

        if (totalMoney < drink.getPrice()) {
            System.out.println("-------------------------------");
            System.out.println("잔돈 부족!");
            return;
        }

        totalMoney -= drink.getPrice();
        drink.decreaseStock();
        drink.dispense();
    }

    public int returnChange() {
        int change = totalMoney;
        totalMoney = 0;

        return change;
    }
}
