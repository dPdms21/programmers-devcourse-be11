package lambda;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n===== 1. 익명 클래스 vs 람다 (같은 동작) =====");
        Operation add1 = new Operation() {
            @Override
            public int apply(int a, int b) {
                return a + b;
            }
        };

        Operation add2 = (a, b) -> a + b;

        System.out.println("익명 클래스 add: " + add1.apply(3, 4));
        System.out.println("람다 add: " + add2.apply(3, 4));

        System.out.println("\n===== 2. 람다로 만든 연산들 =====");
        Operation add3 = (int a, int b) -> {return a + b;};
        Operation add4 = (a, b) -> {return a + b;};
        Operation add5 = (a, b) ->  a + b;

        Operation sub = (a, b) -> a - b;
        Operation mul = (a, b) -> a * b;

        System.out.println("3 + 4 = " + add3.apply(3, 4));
        System.out.println("9 - 2 = " + sub.apply(9, 2));
        System.out.println("3 * 5 = " + mul.apply(3, 5));

        System.out.println("\n===== 3. 매개변수 개수별 람다 =====");
        Runnable r = () -> System.out.println("(0개) 안녕, 람다!");
        r.run();

        Printer p = s -> System.out.println("(1개) [로그] " +  s);
        p.print("시작");

        Operation add = (a, b) -> a + b;
        System.out.println("(2개) 10 + 20 = " + add.apply(10, 20));

        System.out.println("\n===== 4. 실전: Comparator로 길이순 정렬 =====");
        ArrayList<String> list = new ArrayList<>();
        list.add("가나다");
        list.add("가");
        list.add("라마");
        System.out.println("정렬 전: " + list);

        list.sort((a, b) -> a.length() - b.length());
        System.out.println("정렬 후: " + list);
    }
}
