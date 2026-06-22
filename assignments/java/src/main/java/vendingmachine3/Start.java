package vendingmachine3;

import java.util.*;

public class Start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VendingMachine vm = new VendingMachine();

        while (true) {
            vm.printMenu();
            System.out.print("메뉴 입력 > ");
            int choice = Integer.parseInt(sc.nextLine());

            if (choice >= 1 && choice <= 4) {
                vm.buy(choice);
            }
            else if (choice == 5) {
                System.out.println("-------------------------------");
                System.out.print("금액 입력 > ");
                int price = Integer.parseInt(sc.nextLine());
                vm.insertMoney(price);
            }
            else if (choice == 6) {
                System.out.println("-------------------------------");
                System.out.println("잔돈 " + vm.returnChange() + "원 반환");
                System.out.println("===============================");
                sc.close();
                return;
            }
            else {
                System.out.println("-------------------------------");
                System.out.println("숫자 다시 입력");
            }
        }
    }
}
