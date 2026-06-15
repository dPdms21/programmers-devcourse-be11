package part3;

// * enum(열거형)
// 자바에서 특정한 상수 집합을 정의하는 데 사용되는 특별한 데이터 타입
// enum은 클래스처럼 보이지만 실제로는 고정된 상수들의 집합을 정의한 것이며,
// 모든 enum은 java.lang.Enum 클래스를 상속받음

// * enum 사용의 장점
// - 코드의 명확성 : 코드에서 상수 값을 직접 사용하지 않고, enum을 사용하여 의미를 면확하게 전달할 수 있음
// - 타입 안전성 : 잘못된 값이 사용되는 것을 방지
// - 유지보수 용이성 : 새로운 상수를 추가하거나 기존 상수를 변경할 때 코드 전체를 쉽게 수정할 수 있음

public class B_enum {
    public static void exam1() {
        B_day today = B_day.MONDAY;

        switch (today) {
            case MONDAY:
                System.out.println("Today is Monday");
                break;
            case TUESDAY:
                System.out.println("Today is Tuesday");
                break;
            // ...
        }
    }

    public static void exam2() {
        // 모든 enum값 순회
        for (B_day_2 day : B_day_2.values()) {
            System.out.println( day + " : " + day.getDesc() + " : " + day.name() );
        }
    }

    public static void main(String[] args) {
        exam2();
    }
}
