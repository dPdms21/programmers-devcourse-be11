import java.util.*;

public class VendingMachine {
    static final int COKE = 500, CIDER = 700, FANTA = 300, WATER = 200;

    public static void printMenu(int totalMoney) {
        System.out.println("========================================");
        System.out.println("              자판기 메뉴");
        System.out.println("[1] 콜라   - 500원");
        System.out.println("[2] 사이다 - 700원");
        System.out.println("[3] 환타   - 300원");
        System.out.println("[4] 물     - 200원");
        System.out.println("[5] 돈 넣기");
        System.out.println("[6] 종료");
        System.out.println("========================================");
        System.out.println("현재 금액 : " + totalMoney + "원");
        System.out.println("========================================");
    }

    public static int buyDrink(int totalMoney, int price, String drinkName) {
        System.out.println("----------------------------------------");
        System.out.printf("%s 선택\n", drinkName);

        if (totalMoney >= price) {
            totalMoney -= price;
            System.out.println("구매 완료");
            System.out.println("남은 금액 : " + totalMoney + "원");
            System.out.println("----------------------------------------");
        } else {
            System.out.println("잔액 부족");
            System.out.println("----------------------------------------");
        }
        return totalMoney;
    }

    public static int addMoney(int totalMoney, int add) {
        totalMoney += add;
        System.out.println("----------------------------------------");
        System.out.println("금액 추가 완료");
        System.out.println("현재 금액 : " + totalMoney + "원");
        System.out.println("----------------------------------------");

        return totalMoney;
    }

    public static void main(String[] args) {
        System.out.println("----------------------------------------");
        System.out.print("금액 입력 : ");
        Scanner sc = new Scanner(System.in);
        int money = Integer.parseInt(sc.nextLine());

        while (true) {
            printMenu(money);

            System.out.println("----------------------------------------");
            System.out.print("메뉴 입력: ");
            int menu = Integer.parseInt(sc.nextLine());

            switch (menu) {
                case 1:
                    money = buyDrink(money, COKE, "콜라");
                    break;
                case 2:
                    money = buyDrink(money, CIDER, "사이다");
                    break;
                case 3:
                    money = buyDrink(money, FANTA, "환타");
                    break;
                case 4:
                    money = buyDrink(money, WATER, "물");
                    break;
                case 5:
                    System.out.println("----------------------------------------");
                    System.out.print("추가 금액 입력 : ");
                    int money2 = Integer.parseInt(sc.nextLine());
                    money = addMoney(money, money2);
                    break;
                case 6:
                    System.out.println("========================================");
                    System.out.println("              자판기 종료");
                    System.out.println("========================================");
                    sc.close();
                    return;
                default:
                    System.out.println("----------------------------------------");
                    System.out.println("               !다시 입력!");
            }
        }
    }
}
