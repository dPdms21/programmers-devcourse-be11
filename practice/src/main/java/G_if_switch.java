// * 조건문
// 조건식의 연산결과에 따라 실행할 문장이 달라져서 프로그램의 실행흐름을 바꿀 수 있다.

import java.util.Scanner;

public class G_if_switch {
    // 1) if문
    // if (조건식 참) { 실행 }
    public static void exam1() {
        int score = 90;

        if (score >= 90) {
            // 조건식 참일 때 수행할 내용
            System.out.println("A학점 입니다.");
        }
    }

    // 1-1) if - else
    public static void exam2() {
        int score = 9;

        if (score >= 60) {
            System.out.println("합격입니다.");
        } else {
            System.out.println("불합격입니다.");
        }
    }

    // 1-2) if - else if - else
    public static void exam3() {
        System.out.println("점수를 입력해주세요.");
        Scanner sc = new Scanner(System.in);
        int score = sc.nextInt(); // 사용자 값을 입력받아주세요.

        if (score >= 90) {
            System.out.println("A학점 입니다.");
        } else if (score >= 80) {
            System.out.println("B학점 입니다.");
        } else if (score >= 70) {
            System.out.println("C학점 입니다.");
        } else if (score >= 60) {
            System.out.println("D학점 입니다.");
        } else {
            System.out.println("F학점 입니다.");
        }
    }

    public static void main(String[] args) {
        exam2();
    }
}