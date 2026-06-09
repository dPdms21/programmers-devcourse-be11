package part2;

// * JVM의 메모리 구조
// 1. 메서드 영역
// 프로그램 실행 중 어떤 클래스가 사용되면, JVM은 해당 클래스의 클래스 파일을 읽어서 분석하여 클래스에 대한 정보를 이곳에 저장
// 이때, 그 클래스의 클래스 변수도 이 영역에 함께 생성

// 2. 힙
// 인스턴스가 생성되는 공간, 프로그램 실행 중 생성되는 인스턴스는 모두 이곳에 생성
// 즉, 인스턴스 변수들이 생성되는 곳

// 3. 호출 스택
// 호출 스택은 메서드의 작업에 필요한 메모리 공간을 제공
// 메서드가 호출되면, 호출 스택에 호출된 메서드를 위한 메모리가 할당되며,
// 이 메모리는 메서드가 작업을 수행하는 동안 지역변수(매개변수 포함)들과 연산의 중간결과 등을 저장하는데 사용
// 그리고 메서드가 작업을 마치면 할당되었던 메모리 공간은 반환되어 비워짐

// - 메서드가 호출되면 수행에 필요한 만큼의 메모리를 스택에 할당받음
// - 메서드가 수행을 마치고 나면 사용했던 메모리를 반환하고 스택에서 제거
// - 호출 스택의 제일 위에 있는 메서드가 현재 실행 중인 메서드

// * 기본형 매개변수: 변수의 값을 읽기만 할 수 있음
// * 참조형 매개변수: 변수의 값을 읽고 변경할 수 있음

class Data {
    int x = 5;
}

public class C_jvm_memory {
    public static void main(String[] args) {
        exam4();
    }

    public static void exam1() {
        firstMethod();
    }

    public static void firstMethod() {
        secondMethod();
    }

    public static void secondMethod() {
        System.out.println("Second Method");
    }

    public static void exam2() {
        Data d = new Data();
        System.out.println(d.x);

        changeData(d);
        System.out.println(d.x);
    }

    public static void changeData(Data d) { // * 참조형 매개변수
        d.x = 10;
    }

    // 얕은복사 & 깊은복사
    public static void exam3() {
        // 얕은복사
        Data d1 = new Data();
        Data d2 = d1; // &123

        // 깊은복사
        Data d3 = copy(d1);
        d1.x = 10;

        System.out.println(d1.x);
        System.out.println(d2.x);
        System.out.println(d3.x);
    }

    public static Data copy(Data d) {
        Data temp = new Data();
        temp.x = d.x;

        return temp;
    }

    static void exam4() {
        // * 재귀호출(Recursive Call)
        // 메서드의 내부에서 메서드 자신을 다시 호출하는 것을 '재귀호출'이라고 하고,
        // 재귀호출을 하는 메서드를 '재귀 메서드'라고 함

        int result = factorial(5);
        System.out.println(result);
    }

    static int factorial(int n) {
        // 기저조건 -> 탈출조건
        if (n == 1) {
            return 1;
        }

        // 자기 자신으로 호출
        return n * factorial(n - 1);
    }

    // f(2-1) 실행 -> 반환값 1 --> 스택에서 사라짐
    // f(3-1) 실행 -> (3-1) * (1을 반환받음) -> 2를 반환 -> 스택에서 사라짐
    // 3 -> 3 * f(3-1) -> 3 * 2 -> 스택에서 사라짐
    // 4 -> 4 * f(4-1) -> 4 * (3 * 2) -> 스택에서 사라짐
    // 5 -> 5 * f(5-1) -> 5 * (4 * (3 * (2 * 1))) -> 120 반환 -> 스택에서 사라짐
}
