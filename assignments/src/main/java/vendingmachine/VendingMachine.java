package vendingmachine;

import java.util.*;

public class VendingMachine {
    private Scanner sc = new Scanner(System.in);

    private static final int COKE = 500, CIDER = 700, FANTA = 300, WATER = 200;
    private int money;

    private void printMenu() {
        System.out.println("========================================");
        System.out.println("              자판기 메뉴");
        System.out.println("[1] 콜라   - 500원");
        System.out.println("[2] 사이다 - 700원");
        System.out.println("[3] 환타   - 300원");
        System.out.println("[4] 물     - 200원");
        System.out.println("[5] 돈 넣기");
        System.out.println("[6] 종료");
        System.out.println("========================================");
        System.out.println("현재 금액 : " + money + "원");
        System.out.println("========================================");
    }

    private void buyDrink(int price, String drinkName) {
        System.out.println("----------------------------------------");
        System.out.printf("%s 선택\n", drinkName);

        if (money >= price) {
            money -= price;
            System.out.println("구매 완료");
            System.out.println("남은 금액 : " + money + "원");
            System.out.println("----------------------------------------");
        } else {
            System.out.println("잔액 부족");
            System.out.println("----------------------------------------");
        }
    }

    private void addMoney(int add) {
        money += add;
        System.out.println("----------------------------------------");
        System.out.println("금액 추가 완료");
        System.out.println("현재 금액 : " + money + "원");
        System.out.println("----------------------------------------");
    }

    public void run() {
        System.out.println("----------------------------------------");
        System.out.print("금액 입력 : ");
        money = Integer.parseInt(sc.nextLine());

        while (true) {
            printMenu();

            System.out.println("----------------------------------------");
            System.out.print("메뉴 입력: ");
            int menu = Integer.parseInt(sc.nextLine());

            switch (menu) {
                case 1:
                    buyDrink(COKE, "콜라");
                    break;
                case 2:
                    buyDrink(CIDER, "사이다");
                    break;
                case 3:
                    buyDrink(FANTA, "환타");
                    break;
                case 4:
                    buyDrink(WATER, "물");
                    break;
                case 5:
                    System.out.println("----------------------------------------");
                    System.out.print("추가 금액 입력 : ");
                    int add = Integer.parseInt(sc.nextLine());
                    addMoney(add);
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

    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.run();
    }
}
