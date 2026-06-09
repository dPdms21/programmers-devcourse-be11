package part1;

// * 반복문
// 어떤 작업이 반복적으로 수행되도록 할 때 사용된다.

import java.util.Scanner;

public class H_loop {
    /*
        * for 문
        for ( 초기값; 조건식; 증감식 ) {
            // 조건식이 참일 때 수행될 문장들을 적는다.
        }
     */
    public static void exam1() {
        for (int i=0; i<10; i++) {
            System.out.println("cnt : " + i);
        }
    }

    public static void exam2() {
        for (int i=1; i<10; i++) {
            System.out.println("2 * " + i + " = " + (2 * i));
        }
    }

    // 중첩
    public static void exam3() {
        for (int i=1; i<3; i++) {
            System.out.println("i= " + i);
            for ( int j=1; j<5; j++) {
                System.out.println("j= " + j);
            }
        }
    }

    // 구구단 : 2단부터 9단까지 중첩for문을 사용해서
    public static void practice1() {
        System.out.println("=== 구구단 ===");
        for (int i=2; i<10; i++) {
            System.out.println("=== " + i + "단 ===");
            for ( int j=1; j<10; j++) {
                System.out.println(i + " * " + j + " = " + i * j);
            }
        }
    }

    // continue : 1 ~ 100까지 짝수만 출력
    public static void exam4() {
        for (int i= 1; i <=100; i++) {
            if (i % 2 != 0) {
                //홀수
                continue;
            }
            //짝수
            System.out.println(i);
        }
    }

    // 홀수만 출력
    public static void practice2() {
        for (int i=1; i <=100; i++) {
            if (i % 2 == 0) {
                continue;
            }
            System.out.println(i);
        }
    }

    //break : 1 ~ 100까지 올라가는데, 30에 도달했을 때 멈춤(탈출)
    public static void exam5() {
        for (int num=1; num<=100; num++) {
            if (num == 30) {
                break;
            }
            System.out.println(num);
        }
        System.out.println("탈출한다");
    }

    public static void exam6() {
        for (int i=0; i<3; i++) {
            System.out.println("첫 번째 루프 : " + i);
            for (int j=0; j<3; j++) {
                System.out.println("두 번째 루프 : " + j);
                for (int k=0; k<3; k++) {
                    System.out.println("세 번째 루프 : " + k);
                }
            }
        }
    }

    public static void exam7() {
        int i=0;
        for (; i<3;) {
            //수행
            System.out.println(i++);
        }
    }

    public static void exam8() {
        int i=0;
        for (;;) {
            if (i>10) break;

            System.out.println(i++);
        }
    }

    public static void exam9() {
        //무한 루프
        int i=0;
        for (;true;) {
            System.out.println(i++);
        }
    }

    public static void exam10() {
        for (int i=9; i>=0; i--) {
            System.out.println(i);
        }
    }

    // 구구단 9단부터 2단까지 역으로 출력
    public static void practice3() {
        System.out.println("=== 구구단 ===");
        for (int i=9; i>=2; i--) {
            System.out.println("=== " + i + "단 ===");
            for ( int j=9; j>=1; j--) {
                System.out.println(i + " * " + j + " = " + i * j);
            }
        }
    }

    // * while문
    /*
        while (조건식) {
            //조건식의 연산결과가 참(true)인 동안, 반복할 문장을 적는다.
       }
     */
    public static void exam11() {
        int cnt = 0;
        while (cnt<=10) {
            System.out.println("cnt = " + cnt);
            cnt++;
        }
    }

    public static void practice4() {
        System.out.println("구구단 2단 출력");

        int i=1;
        while (i<10) {
            System.out.println("2 * " + i + " = " + 2 * i);
            i++;
        }
    }

    //while 중첩 가능 -> 전체 구구단 출력
    public static void practice5() {
        System.out.println("구구단 전체 출력");
        int i=2;

        while (i<10) {
            System.out.println("=== " + i + "단 ===");
            int j=1;

            while (j<10) {
                System.out.println(i + " * " + j + " = " + i * j);
                j++;
            }
            i++;
        }
    }

    public static void exam12() {
        int i=3;

        while (true) {
            if (i == 0) {
                break;
            }
            System.out.println(i--);
        }
    }

    public static void exam13() {
        int i=10;
        while (--i > 0) {
            System.out.println(i);
        }

        System.out.println("==========");
        i = 10;
        while (i-- > 0) {
            System.out.println(i);
        }
    }

    // 무한루프 : while (true)
    // 사용자로부터 점수값을 입력받아 합계를 내도록 구현
    // ex) 사용자 : 10입력 -> 10 + 9 + 8 + 7 + 6 + 5 + 4 + 3 + 2 + 1 = 55
    // 결과 : 55출력하고
    // 다시 정수값을 입력받아 합계를 낸다.
    // 사용자가 0을 입력하면 종료
    public static void practice6() {
        Scanner sc = new Scanner(System.in);
        
        //무한루프
        while (true) {
            //사용자한테 점수 값을 입력받는다.
            System.out.print("숫자 입력 (종료: 0): ");
            int n = sc.nextInt();

            //0을 누르면 탈출하는 조건
            if (n == 0) {
                break;
            }

            //합계 로직을 구현
            int sum = 0;
            for (int i=1; i<=n; i++) {
                sum += i;
            }
            System.out.println("합계 : " + sum);
        }

        System.out.println("프로그램 종료");
    }
    
    //요구사항: 원하는 구구단의 단을 입력하면 해당 단 출력
    //0을 입력하면 종료
    public static void practice7() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("숫자 입력 (종료: 0): ");
            int n = sc.nextInt();

            if (n == 0) {
                break;
            }

            for (int i=1; i<=9; i++) {
                System.out.println(n + " * " + i + " = " + n * i);
            }
        }

        System.out.println("프로그램 종료");
    }

    //짝수인 경우만 출력
    public static void exam14() {
        int i=1;
        while (i <= 10) {
            if (i % 2 == 0) {
                System.out.println(i);
            }
            i++;
        }
    }

    // * do-while문 : 최소한 한 번은 수행될 것을 보장
    /*
        do {
            //조건식의 연산결과가 참일 때 수행될 문장들
        } while (조건식)
        ...
     */
    public static void exam15() {
        int i=0;

        while (i != 0) {
            System.out.println("while문");
        }

        do {
            System.out.println("do-while문");
        }
        while (i != 0);
    }

    public static void main(String[] args) {
        practice7();
    }
}