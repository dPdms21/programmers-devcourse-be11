package bingogame;

import java.util.*;

public class BingoGame {
    private Scanner sc = new Scanner(System.in);
    private Random rand = new Random();

    private int size;
    private int max;
    private boolean[] called;

    public void play() {
        System.out.println("======= 빙고 게임! =======");
        System.out.println("  컴퓨터와 빙고 게임하기!!");
        System.out.println("=========================");

        size = inputSize();
        max = size * size;
        called = new boolean[max + 1];

        int[][] playerBoard = new int[size][size];
        boolean[][] playerMarked = new boolean[size][size];
        int[][] computerBoard = new int[size][size];
        boolean[][] computerMarked = new boolean[size][size];

        makeBoard(playerBoard);
        makeBoard(computerBoard);

        int target = inputTarget();

        System.out.println("-------------------------");
        System.out.println("     " + target + "줄 완성하면 승리!");

        while (true) {
            System.out.println("====== 사용자 빙고판 ======");
            printBoard(playerBoard, playerMarked);

            int num = playerPick();
            called[num] = true;
            mark(playerBoard, playerMarked, num);
            mark(computerBoard, computerMarked, num);

            int num2 = computerPick();
            called[num2] = true;
            mark(playerBoard, playerMarked, num2);
            mark(computerBoard, computerMarked, num2);

            int playerBingo = countBingo(playerMarked);
            int computerBingo = countBingo(computerMarked);

            if (playerBingo >= target && computerBingo >= target) {
                System.out.println("\n======== 무승부! ========");
                System.out.println("     사용자 " + playerBingo + "줄 빙고!!");
                System.out.println("     컴퓨터 " + computerBingo + "줄 빙고!!");
                System.out.println("========================");
                break;
            }
            else if (playerBingo >= target) {
                System.out.println("\n========================");
                System.out.println("     사용자 " + playerBingo + "줄 빙고!!");
                System.out.println("========================\n");
                break;
            }
            else if (computerBingo >= target) {
                System.out.println("\n========================");
                System.out.println("     컴퓨터 " + computerBingo + "줄 빙고!!");
                System.out.println("========================\n");
                break;
            }
        }

        System.out.println("====== 사용자 빙고판 ======");
        printBoard(playerBoard, playerMarked);
        System.out.println("\n====== 컴퓨터 빙고판 ======");
        printBoard(computerBoard, computerMarked);
        System.out.println("\n======== 게임 끝! ========");
    }

    private void makeBoard(int[][] board) {
        List<Integer> nums = new ArrayList<>();

        for (int i=1; i<=max; i++) {
            nums.add(i);
        }
        Collections.shuffle(nums);

        int idx = 0;

        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                board[r][c] = nums.get(idx++);
            }
        }
    }

    private void printBoard(int[][] board, boolean[][] marked) {
        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                if (marked[r][c]) {
                    System.out.print("[ ★] ");
                }
                else {
                    System.out.printf("[%2d] ", board[r][c]);
                }
            }
            System.out.println();
        }
    }

    private void mark(int[][] board, boolean[][] marked, int num) {
        for (int r=0; r<size; r++) {
            for (int c=0; c<size; c++) {
                if (board[r][c] == num) {
                    marked[r][c] = true;
                }
            }
        }
    }

    private int countBingo(boolean[][] marked) {
        int cnt = 0;

        for (int r=0; r<size; r++) {
            boolean all = true;

            for (int c=0; c<size; c++) {
                if (!marked[r][c]) {
                    all = false;
                }
            }

            if (all) {
                cnt++;
            }
        }

        for (int c=0; c<size; c++) {
            boolean all = true;

            for (int r=0; r<size; r++) {
                if (!marked[r][c]) {
                    all = false;
                }
            }

            if (all) {
                cnt++;
            }
        }

        boolean d1 = true;

        for (int i=0; i<size; i++) {
            if (!marked[i][i]) {
                d1 = false;
            }
        }

        if (d1) {
            cnt++;
        }

        boolean d2 = true;

        for (int i=0; i<size; i++) {
            if (!marked[i][size-1-i]) {
                d2 = false;
            }
        }

        if (d2) {
            cnt++;
        }

        return cnt;
    }

    private int playerPick() {
        while (true) {
            System.out.println("-------------------------");
            System.out.print("숫자 입력 > ");
            int num;

            try {
                num = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("-------------------------");
                System.out.println("숫자만 입력!");
                continue;
            }

            if (num < 1 || num > max) {
                System.out.println("-------------------------");
                System.out.println("1~" + max + " 사이 입력");
            }
            else if (called[num]) {
                System.out.println("-------------------------");
                System.out.println("이미 부른 숫자");
            }
            else {
                return num;
            }
        }
    }

    private int computerPick() {
        while (true) {
            int num = rand.nextInt(max) + 1;

            if (!called[num]) {
                System.out.println("-------------------------");
                System.out.println("컴퓨터가 부른 숫자 > " + num);
                return num;
            }
        }
    }

    private int inputTarget() {
        while (true) {
            System.out.println("-------------------------");
            System.out.print("빙고 줄 수 입력 (3/4/5) > ");
            int num;

            try {
                num = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("-------------------------");
                System.out.println("숫자만 입력!");
                continue;
            }

            if (num < 3 || num > 5) {
                System.out.println("-------------------------");
                System.out.println("3~5 사이 입력");
            }
            else {
                return num;
            }
        }
    }

    private int inputSize() {
        while (true) {
            System.out.println("-------------------------");
            System.out.print("빙고판 크기 입력 (3/5/7) > ");
            int num;

            try {
                num = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("-------------------------");
                System.out.println("숫자만 입력!");
                continue;
            }

            if (num != 3 && num != 5 && num != 7) {
                System.out.println("-------------------------");
                System.out.println("3/5/7 중 입력");
            }
            else {
                return num;
            }
        }
    }
}
