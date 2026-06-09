package part1;

// * 배열
// 같은 타입의 여러 변수를 하나의 묶음으로 다루는 것
// 변수와 달리 배열은 각 저장공간이 연속적으로 배치되어 있다.
// int num, COKE, CIDER, ......
// 61, 64, 69, ......

// * 배열의 생성
// 타입[] 변수이름 = new 타입[길이];

// 배열의 첫 번째 주소를 알면 나머지 주소를 알 수 있다.

public class J_array {

    public static void exam1() {
        int[] scores = new int[3]; // 0, 1, 2

        scores[0] = 10;
        scores[1] = 20;
        scores[2] = 30;

        System.out.println(scores);
        System.out.println(scores[0]);
        System.out.println(scores[1]);
        System.out.println(scores[2]);
    }

    public static void exam2() {
        int[] scores = {10, 20, 30}; // 선언과 동시에 초기화
        System.out.println("len: " + scores.length);

        // 0, 1, 2
        for (int i=0; i<scores.length; i++) {
            System.out.println(scores[i]);
        }
    }

    public static void exam3() {
        int[] scores = new int[3]; // 0, 1, 2

        // 값 초기화
        for (int i=1; i<=scores.length; i++) {
            scores[i-1] = i * 10;
        }

        for (int s : scores) {
            System.out.println(s);
        }
    }

    public static void exam4() {
        char[] chars = {'a', 'b', 'c', 'd', 'e'};

        for (char c : chars) {
            System.out.println(c);
        }
    }

    // 역으로 출력
    // 단, 내장함수X -> 인덱스(번호)로 직접 접근
    public static void practice1() {
        char[] chars = {'a', 'b', 'c', 'd', 'e'};

        for (int i=chars.length-1; i>=0; i--) {
            System.out.println(chars[i]);
        }
    }

    public static void exam5() {
        String[] words = {"apple", "banana", "orange"};

        for (String word : words) {
            System.out.println(word);
        }
    }

    public static void exam6() {
        int[] scores = {10, 20, 30};
        int num = 20;

        // scores를 인덱스별로 찍기
        for (int i=0; i<scores.length; i++) {
            System.out.println(scores[i]);
        }

        exam6_sub(scores, num);
        System.out.println("==========");
        for (int i=0; i<scores.length; i++) {
            System.out.println(scores[i]);
        }
        System.out.println("==========");
        System.out.println(num);
    }

    // int[] => new -> 참조형 &123 -> call by reference
    // int num => 기본형 20 -> call by value
    public static int exam6_sub(int[] scores, int num) {
        scores[1] = 90; // 배열은 주소 이용으로 값 변경 가능 (주소에 의한 호출)
        num = 90; // void: exam6_sub의 num / 변수는 값 변경 불가 (값에 의한 호출)

        return num;
    }

    public static void main(String[] args) {
        exam6();
    }
}
