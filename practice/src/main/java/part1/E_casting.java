package part1;

// * 형변환
// 변수 또는 상수의 타입을 다른 타입으로 변환하는 것.
// 기본형에서 boolean을 제외한 나머지 타입들은 서로 형변환이 가능하다.

// * 암시적(자동) 형변환과 명시적(강제) 형변환이 있다.
// 암시적 형변환 : 더 작은 자료형이 더 큰 자료형으로 변환할 때 자동으로 이루어진다.
// 명시적 형변환 : 더 큰 자료형을 더 작은 자료형으로 변환할 때 명시적으로 해야 한다.

public class E_casting {
    public static void main(String[] args) {
        double d = 3.14;
        System.out.println(d);
        int score = (int) d;
        System.out.println(score);

        // int -> char
        int n = 65;
        System.out.println(n);
        char c = (char) n;
        System.out.println(c);

        // char -> int (암시적 형변환)
        char c2 = 'A';
        System.out.println(c2);
        int n2 = c2;
        System.out.println(n2);

        // float -> int
        float f = 3.14f;
        System.out.println(f);
        int n3 = (int) f;
        System.out.println(n3);

        // int -> float (암시적 형변환)
        int n4 = 3;
        System.out.println(n4);
        float f2 = n4;
        System.out.println(f2);

        // 암시적 형변환
        byte b = 10;
        int i = b;  // 암시적 형변환: byte → int

        char cc = 'A';
        int j = cc;  // 암시적 형변환: char → int

        int k = 100;
        double dd = k;  // 암시적 형변환: int → double

        // 명시적 형변환
        int i2 = 100;
        byte b2 = (byte) i2;  // 명시적 형변환: int → byte

        double ddd = 9.78;
        int j2 = (int) ddd;  // 명시적 형변환: double → int
    }
}