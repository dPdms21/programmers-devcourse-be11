package bingogame;

import java.util.*;

public class BingoGame {
    static final int TARGET = 3;
    static final int SIZE = 5;
    static final int MAX = 25;

    static boolean[] called = new boolean[MAX+1];
    static Scanner sc = new Scanner(System.in);
    static Random rand = new Random();

    public void play() {
        System.out.println("======= 빙고 게임! =======");
        System.out.println("  컴퓨터와 빙고 게임하기!!");
        System.out.println("=========================");

        int[][] playerBoard = new int[SIZE][SIZE];
        boolean[][] playerMarked = new boolean[SIZE][SIZE];
        int[][] computerBoard = new int[SIZE][SIZE];
        boolean[][] computerMarked = new boolean[SIZE][SIZE];

        makeBoard(playerBoard);
        makeBoard(computerBoard);

        System.out.println("     " + TARGET + "줄 완성하면 승리!");

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

            if (playerBingo >= TARGET && computerBingo >= TARGET) {
                System.out.println("\n======== 무승부! ========");
                System.out.println("     사용자 " + playerBingo + "줄 빙고!!");
                System.out.println("     컴퓨터 " + computerBingo + "줄 빙고!!");
                System.out.println("========================");
                break;
            }
            else if (playerBingo >= TARGET) {
                System.out.println("\n========================");
                System.out.println("     사용자 " + playerBingo + "줄 빙고!!");
                System.out.println("========================\n");
                break;
            }
            else if (computerBingo >= TARGET) {
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

    public static void makeBoard(int[][] board) {
        List<Integer> nums = new ArrayList<>();

        for (int i=1; i<=MAX; i++) {
            nums.add(i);
        }
        Collections.shuffle(nums);

        int idx = 0;

        for (int r=0; r<SIZE; r++) {
            for (int c=0; c<SIZE; c++) {
                board[r][c] = nums.get(idx++);
            }
        }
    }

    public static void printBoard(int[][] board, boolean[][] marked) {
        for (int r=0; r<SIZE; r++) {
            for (int c=0; c<SIZE; c++) {
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

    public static void mark(int[][] board, boolean[][] marked, int num) {
        for (int r=0; r<SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == num) {
                    marked[r][c] = true;
                }
            }
        }
    }

    public static int countBingo(boolean[][] marked) {
        int cnt = 0;

        for (int r=0; r<SIZE; r++) {
            boolean all = true;

            for (int c = 0; c < SIZE; c++) {
                if (!marked[r][c]) {
                    all = false;
                }
            }

            if (all) {
                cnt++;
            }
        }

        for (int c = 0; c < SIZE; c++) {
            boolean all = true;

            for (int r=0; r<SIZE; r++) {
                if (!marked[r][c]) {
                    all = false;
                }
            }

            if (all) {
                cnt++;
            }
        }

        boolean d1 = true;

        for (int i=0; i<SIZE; i++) {
            if (!marked[i][i]) {
                d1 = false;
            }
        }

        if (d1) {
            cnt++;
        }

        boolean d2 = true;

        for (int i=0; i<SIZE; i++) {
            if (!marked[i][SIZE-1-i]) {
                d2 = false;
            }
        }

        if (d2) {
            cnt++;
        }

        return cnt;
    }

    public static int playerPick() {
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
            if (num < 1 || num > MAX) {
                System.out.println("-------------------------");
                System.out.println("1~25 사이 입력");
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

    public static int computerPick() {
        while (true) {
            int num = rand.nextInt(MAX) + 1;

            if (!called[num]) {
                System.out.println("-------------------------");
                System.out.println("컴퓨터가 부른 숫자 > " + num);
                return num;
            }
        }
    }
}
