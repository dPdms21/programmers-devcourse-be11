package part3;

import java.util.Scanner;

public class D_start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        D_account_book book = new D_account_book_impl(sc);

        while (true) {
            System.out.println("===== 가계부 (File) =====");
            System.out.println("1. 내역 추가");
            System.out.println("2. 내역 조회");
            System.out.println("3. 삭제");
            System.out.println("4. 종료");
            System.out.println("번호 입력");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("잘못된 번호");
                continue;
            }

            switch (choice) {
                case 1:
                    book.addAccount();
                    break;
                case 2:
                    book.showAccount();
                    break;
                case 3:
                    book.deleteAccount();
                    break;
                case 4:
                    System.out.println("프로그램 종료");
                    return;
                default:
                    System.out.println("잘못된 번호");
            }
        }
    }
}
