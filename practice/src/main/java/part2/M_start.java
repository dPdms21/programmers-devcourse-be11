package part2;

import java.util.Scanner;

public class M_start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // cmd + opt + v -> windows 단축키
        M_vendingmachine machine = new M_vendingmachine();

        while (true) {
            machine.printMenu();
            System.out.println("원하는 메뉴를 선택하세요.");
            int choice = sc.nextInt();

            if (choice >= 1 && choice <= 4) {
                machine.buy(choice);
            } else if (choice == 5) {
                System.out.println("돈을 넣으세요");
                int money = sc.nextInt();
                machine.insertMoney(money);
            } else if (choice == 6) {
                int returnMoney = machine.returnMoney();
                System.out.println("잔돈 " + returnMoney + "원이 반환되었습니다.");
                return;
            } else {
                System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
            }

        }
    }
}
