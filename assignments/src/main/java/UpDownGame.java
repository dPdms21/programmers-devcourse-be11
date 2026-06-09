import java.util.*;

public class UpDownGame {
    private Scanner sc = new Scanner(System.in);
    private Random rand = new Random();

    private int answer;
    private int guess;
    private boolean[] called;
    private int num;

    private void printMenu() {
        System.out.println("===================");
        System.out.println("  Up & Down Game!");
        System.out.println("===================");
        System.out.println("     난이도 선택");
        System.out.println("-------------------");
        System.out.println("[1] 쉬움(1~50)");
        System.out.println("[2] 보통(1~100)");
        System.out.println("[3] 어려움(1~1000)");
    }

    private int inputNum() {
        while (true) {
            System.out.println("-------------------");
            System.out.print("번호 입력 > ");

            try {
                num = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("-------------------");
                System.out.println("숫자만 입력");
                continue;
            }

            switch (num) {
                case 1:
                    num = 50;
                    break;
                case 2:
                    num = 100;
                    break;
                case 3:
                    num = 1000;
                    break;
                default:
                    System.out.println("1~3 중 선택");
                    continue;
            }

            return num;
        }
    }

    private int inputGuess() {
        while (true) {
            System.out.println("-------------------");
            System.out.print("입력 (1 ~ " + num + ") > ");

            try {
                guess = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("-------------------");
                System.out.println("숫자만 입력");
                continue;
            }

            if (guess < 1 || guess > num) {
                System.out.println("-------------------------");
                System.out.println("1 ~ " + num + " 중 입력");
                continue;
            } else if (called[guess]) {
                System.out.println("-------------------");
                System.out.println("이미 입력한 숫자");
                continue;
            }

            return guess;
        }
    }

    private void printResult(int cnt) {
        System.out.println("-------------------");

        if (guess < answer) {
            System.out.println("UP! (" + cnt + "/7)");
        } else if (guess > answer) {
            System.out.println("DOWN! (" + cnt + "/7)");
        } else {
            System.out.println("정답! " + cnt + "번 소요");
        }
    }

    public void run() {
        while (true) {
            printMenu();

            inputNum();

            called = new boolean[num + 1];
            answer = rand.nextInt(num) + 1;

            int cnt = 0;

            while (true) {

                guess = inputGuess();

                called[guess] = true;
                cnt++;

                printResult(cnt);

                if (guess == answer) {
                    break;
                }

                if (cnt >= 7) {
                    System.out.println("-------------------");
                    System.out.println("7번 안에 못 맞힘");
                    break;
                }
            }

            System.out.println("-------------------");
            System.out.print("한 판 더? (y/n) >");
            String ans = sc.nextLine();

            if (ans.equals("n")) {
                break;
            }
        }

        sc.close();
        System.out.println("===================");
        System.out.println("     게임 종료!");
        System.out.println("===================");
    }

    public static void main(String[] args) {
        UpDownGame up = new UpDownGame();
        up.run();
    }
}
