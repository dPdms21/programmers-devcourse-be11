// * 연산자
// - 산술 연산자 : +, -, *, /, %
// - 단항 연산자 : ++, --
// - 비교 연산자 : >, <, >=, <=, ==, !=
// - 대입 연산자 : =
// - 기타 : ?:, 복합대입연산자

// * 연산자 우선순위
// 곱셈과 나눗셈은 덧셈과 뺄샘보다 우선순위가 높다.

public class F_operator {
    // [산술 연산자]
    public static void operExam1() {
        int a = 10;
        int b = 4;

        System.out.printf("%d + %d = %d\n", a, b, a + b);
        System.out.printf("%d - %d = %d\n", a, b, a - b);
        System.out.printf("%d * %d = %d\n", a, b, a * b);
        System.out.printf("%d / %d = %d\n", a, b, a / b);
        // % 연산자를 출력하기 위해 %%를 사용해야한다.
        // 단일 %는 포맷 지정자로 인식이 안되기 때문
        System.out.printf("%d %% %d = %f\n", a, b, (float)a % b);
    }

    // [단항 연산자]
    public static void operExam2() {
        // 증감 연산자 : 피연산자의 값을 1 증가시킨다.
        // i = i + 1; -> i+=1;
        int i = 5;
        System.out.println("========= 증감 =========");
        System.out.println("전위형 : " + ++i); // 5 + 1
        System.out.println(i); // 6
        System.out.println("후위형 : " + i++); // 6 -> 6 + 1
        System.out.println(i); // 7
        i = i + 1;
        i += 1;
        System.out.println("i = " + i);

        // 감소 연산자 : 피연산자의 값을 1감소시킨다
        i = 5;
        System.out.println("========= 감소 =========");
        System.out.println("전위형 : " + --i);
        System.out.println(i);
        System.out.println("후위형 : " + i--);
        System.out.println(i);
        i = i - 1;
        i -= 1;
        System.out.println("i = " + i);
    }

    // [비교 연산자]
    public static void operExam3() {
        System.out.printf("10 == 10.0f \t %b\n", 10 == 10.0f );
        System.out.printf("'0' == 0 \t %b\n", '0' == 0);
        System.out.printf("'A' == 65 \t %b\n", 'A' == 65 );
        System.out.printf("'A' > 'B' \t %b\n", 'A' > 'B');
        System.out.printf("'A' + 1 != 'B' \t %b\n", 'A' + 1 != 'B');
    }

    // [문자열 비교]
    public static void operExam4() {
        String str1 = new String("Hello");
        String str2 = new String("Hello");
        System.out.println(str1 == str2);
        // &456 -> Hello -> str1 <- &456
        // &789 -> Hello -> str2 <- &789
        // str1 == str2 -> false

        String str3 = "Hello";
        String str4 = "Hello";
        System.out.println(str3 == str4); // true
        // &123 -> "Hello" -> str3상자 <- &123
        // "Hello" -> str4변수 &123
        // str3 = &123;
        // str4 = &123;
        // str3 == str4 -> true

        // 두 문자열을 비교할 때는 비교 연산자 '=='대신 equals()라는 메서드를 이용해야한다.
        System.out.println(str1.equals(str2));
    }

    // [논리 연산자]
    // && : 그리고(AND) - 모두 true일 때, true
    // || : 또는(OR) - 둘 중 하나만 true여도 true
    // ! : 논리 부정 연산 - x가 true 일 때, !x는 false
    public static void operExam5() {

        boolean a = true;
        boolean b = false;
        boolean c = true;

        System.out.println("a && b : " + (a && b));
        System.out.println("c && a : " + (c && a));
        System.out.println("a || b : " + (a || b));
        System.out.println("!a : " + !a);
    }

    // [비트 연산자]
    // & : 둘 다 1이면 1
    // | : 둘 중 하나라도 1이면 1
    // ^ : 서로 다르면 1
    // ~ : 비트 반전
    public static void operExam6() {
        int a = 5;  // 0101
        int b = 3;  // 0011

        System.out.println("a & b : " + (a & b)); // 0001 -> 1
        System.out.println("a | b : " + (a | b)); // 0111 -> 7
        System.out.println("a ^ b : " + (a ^ b)); // 0110 -> 6
        System.out.println("~a : " + (~a));       // -(a + 1) -> -6
    }

    // [시프트 연산자]
    // << : 비트를 왼쪽으로 이동, 2배씩 증가
    // >> : 비트를 오른쪽으로 이동, 2로 나눈 몫
    public static void operExam7() {
        int num = 8; // 1000

        System.out.println("num << 1 : " + (num << 1)); // 16
        System.out.println("num << 2 : " + (num << 2)); // 32
        System.out.println("num >> 1 : " + (num >> 1)); // 4
        System.out.println("num >> 2 : " + (num >> 2)); // 2
    }

    public static void main(String[] args) {
        operExam5();
    }
}