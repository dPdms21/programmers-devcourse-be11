package accountbook2;

import java.util.*;

public class Start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AccountBook ab = new AccountBookImpl();

        while (true) {
            System.out.println("===== 가계부 (File) =====");
            System.out.println("1. 내역 추가");
            System.out.println("2. 내역 조회");
            System.out.println("3. 삭제");
            System.out.println("4. 항목 삭제");
            System.out.println("5. 항목 검색");
            System.out.println("6. 월별 합계");
            System.out.println("7. 종료");
            System.out.println("------------------------");
            System.out.print("번호 입력 > ");

            int menu;

            try {
                menu = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("------------------------");
                System.out.println("숫자만 입력");
                continue;
            }

            switch (menu) {
                case 1:
                    ab.addAccount();
                    break;
                case 2:
                    ab.showAccount();
                    break;
                case 3:
                    ab.deleteAccount();
                    break;
                case 4:
                    ab.deleteItem();
                    break;
                case 5:
                    ab.searchAccount();
                    break;
                case 6:
                    ab.showMonthlyTotal();
                    break;
                case 7:
                    System.out.println("========= 종료! =========");
                    return;
                default:
                    System.out.println("------------------------");
                    System.out.println("잘못된 번호");
            }
        }
    }
}
