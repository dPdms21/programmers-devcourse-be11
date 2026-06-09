import java.util.*;

public class UpDownGame {
    private Scanner sc = new Scanner(System.in);
    private Random rand = new Random();

    private int answer;
    private int guess;
    private boolean[] called = new boolean[101];

    public void run() {
        System.out.println("===================");
        System.out.println("  Up & Down Game!");

        answer = rand.nextInt(100) + 1;
        //System.out.println(" (테스트용 정답: " + answer + ")");

        int cnt = 0;

        while (true) {
            System.out.println("-------------------");
            System.out.print("입력 (1 ~ 100)> ");

            try {
                guess = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("-------------------");
                System.out.println("숫자만 입력");
                continue;
            }

            if (guess < 1 || guess > 100) {
                System.out.println("-------------------------");
                System.out.println("1 ~ 100 중 입력");
                continue;
            }
            else if (called[guess]) {
                System.out.println("-------------------");
                System.out.println("이미 입력한 숫자");
                continue;
            }

            called[guess] = true;
            cnt++;

            if (guess < answer) {
                System.out.println("-------------------");
                System.out.println("UP!");
            } else if (guess > answer) {
                System.out.println("-------------------");
                System.out.println("DOWN!");
            } else {
                System.out.println("-------------------");
                System.out.println("정답! " + cnt + "번 소요");
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
