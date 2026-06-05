import java.util.Scanner;

public class I_vending_machine {
    // [요구사항]
    // 돈 넣기
    // 메뉴선택 시 -> 음료가 나온다. 돈이 차감된다.
    // 종료 시 잔돈 반환

    static final int COKE = 500, CIDER = 500, FANTA = 300, WATER = 200;

    // 사용자 메뉴가 출력 -> 현재 잔금도 표시
    public static void printMenu(int totalMoney) {
        System.out.println("============== 자판기 ==============");
        System.out.println("[1]콜라 : 500, [2]사이다 : 500, [3]환타 : 300, [4]물 : 200, [5]돈 넣기, [6]종료");
        System.out.println("현재 금액 : " + totalMoney + "원");
        System.out.println("====================================");
    }

    // 사용자로부터 메뉴 번호를 받는 함수
    public static int getChoice() {
        System.out.print("메뉴 번호 입력: ");
        Scanner sc = new Scanner(System.in);

        return sc.nextInt();
    }

    // 사용자한테 돈 받기
    public static int getMoney() {
        System.out.print("추가 금액 입력: ");
        Scanner sc = new Scanner(System.in);

        return sc.nextInt();
    }

    public static int calcMoney(int totalMoney, int price) {
        return totalMoney - price;
    }

    //예외
    public static void calcMoneyException() {
        System.out.println("잔돈 부족");
    }

    public static void main(String[] args) {
        int totalMoney = 0;

        while (true) {
            printMenu(totalMoney);
            //사용자 주문번호
            int choice = getChoice();
            int result = -1;

            switch (choice) {
                case 1:
                    result = calcMoney(totalMoney, COKE);

                    if (result < 0) {
                        calcMoneyException();
                    }
                    else {
                        totalMoney = result;
                        System.out.println("콜라");
                    }
                    break;
                case 2:
                    result = calcMoney(totalMoney, CIDER);

                    if (result < 0) {
                        calcMoneyException();
                    }
                    else {
                        totalMoney = result;
                        System.out.println("사이다");
                    }
                    break;
                case 3:
                    result = calcMoney(totalMoney, FANTA);

                    if (result < 0) {
                        calcMoneyException();
                    }
                    else {
                        totalMoney = result;
                        System.out.println("환타");
                    }
                    break;
                case 4:
                    result = calcMoney(totalMoney, WATER);

                    if (result < 0) {
                        calcMoneyException();
                    }
                    else {
                        totalMoney = result;
                        System.out.println("물");
                    }
                    break;
                case 5:
                    totalMoney += getMoney();
                    break;
                case 6:
                    System.out.println("\n잔돈 " + totalMoney + "원 반환");
                    return;
                default:
                    System.out.println("잘못 입력, 다시 입력");
                    break;
            }
        }
    }
}
