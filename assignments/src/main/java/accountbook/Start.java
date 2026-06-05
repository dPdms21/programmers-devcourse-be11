package accountbook;

import java.util.Scanner;

public class Start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AccountBook book = new AccountBookImpl();

        while (true) {
            System.out.println("===== 가계부 =====");
            System.out.println("1. 내역 추가");
            System.out.println("2. 내역 조회");
            System.out.println("3. 날짜 전체 삭제");
            System.out.println("4. 항목 하나 삭제");
            System.out.println("5. 종료");
            System.out.println("-----------------");
            System.out.print("메뉴 선택 > ");

            int menu;

            try {
                menu = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력");
                continue;
            }

            switch (menu) {
                case 1:
                    book.addAccount();
                    break;
                case 2:
                    book.showAccount();
                    break;
                case 3:
                    book.deleteAll();
                    break;
                case 4:
                    book.deleteItem();
                    break;
                case 5:
                    System.out.println("====== 종료 ======");
                    return;
                default:
                    System.out.println("잘못된 번호");
            }
        }
    }
}
