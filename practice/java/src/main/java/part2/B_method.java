package part2;

// * 메서드
// 메서드는 특정 작업을 수행하는 일련의 문장들을 하나로 묶은 것

// ** 메서드를 사용하는 이유
// 1. 높은 재사용성
// 2. 중복된 코드의 제거

// ** 메서드의 선언과 구현
// 메서드는 크게 두 부분 '선언부'와 '구현부'로 이루어져 있다.
// * 메서드 선언부: 메서드 선언부는 '메서드의 이름'과 '매개변수 선언', 그리고 '반환타입'으로 구성
// 메서드가 작업을 수행하기 위해 어떤 값들을 필요로 하고 작업의 결과로 어떤 타입의 값을 반환하는지에 대한 정보를 제공

/*
    public int add(int a, int b) -> 선언부
    {
        -> 구현부
        return a + b;
    }
 */

// 메서드 호출: 메서드를 정의했어도 호출되지 않으면 아무 일도 일어나지 않음
/*
    메서드 이름(값1, 값2, ...); // 메서드 호출하는 방법
 */

public class B_method {
    public static void main(String[] args) {
        B_calculator calculator = new B_calculator();

        System.out.println(calculator.add(10, 20));
        System.out.println(calculator.sub(10, 20));
        System.out.println(calculator.mul(10, 20));
        System.out.println(calculator.div(10, 20));
    }
}
