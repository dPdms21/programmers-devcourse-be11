package part2;

// * 정적클래스
class OuterClass {

    private int instanceVariable = 10;
    private static int staticVariable = 20;

    // 자바에서는 클래스 안에 다른 클래스를 정의할 수 있음
    // 이때 중첩된 클래스가 static을 선언하면, 해당 클래스는 정적 중첩 클래스가 됨
    static class StaticNestedClass {

        void display() {
            // 정적 중첩 클래스에서는 바깥 클래스의 static 변수에만 접근 가능
            System.out.println(staticVariable);
            // 바깥 클래스의 인스턴스 변수는 접근 불가능
            // System.out.println(instanceVariable);
        }

    }

    class InnerClass {
        void display() {
            System.out.println(staticVariable);
            System.out.println(instanceVariable);
        }
    }
}

public class D_static_class {
    // - 클래스 소속
    // 정적 중첩 클래스는 바깥 클래스의 인스턴스와 독립적
    // 비정적 중첩 클래스(Inner Class)는 바깥 클래스의 인스턴스 없이는 사용할 수 없음
    // InnerClass : 즉, 이 클래스는 바깥 클래스의 인스턴스와 연결되며, 바깥 클래스의 인스턴스 없이는 사용될 수 없음
    // StaticNestedClass : 즉, 이 클래스는 바깥 클래스의 인스턴스에 속하지 않으며, 바깥 클래스의 인스턴스 없이도 사용될 수 있음

    // - 인스턴스화
    // 정적 중첩 클래스의 인스턴스를 생성할 때는 바깥 클래스의 인스턴스가 필요하지 않음
    // 바깥 클래스의 이름만 사용하여 직접 인스턴스를 생성할 수 있음

    // - 메모리 관리와 성능
    // [정적 중첩 클래스]
    // 바깥 클래스의 인스턴스와 독립적으로 존재하므로, 메모리 사용이 더 효율적일 수 있음
    // 특히 중첩 클래스가 바깥 클래스의 인스턴스 데이터에 접근할 필요가 없는 경우, 정접 중첩 클래스를 사용하는 것이 더 적합
    // [비정적 중첩 클래스]
    // 바깥 클래스의 인스턴스와 연결되기 때문에, 인스턴스당 추가적인 메모리를 소비할 수 있음
    // 따라서, 이 클래스가 바깥 클래스의 인스턴스 데이터에 접근해야 하는 경우에만 사용하는 것이 좋음

    // - 결론
    // [정적 중첩 클래스]
    // 바깥 클래스의 인스턴스와 독립적으로 사용할 수 있고, 주로 바깥 클래스의 정적 멤버만 필요할 때 사용
    // [비정적 중첩 클래스]
    // 바깥 클래스의 인스턴스와 밀접하게 연결되어 있으며, 바깥 클래스의 인스턴스 멤버에 접근해야 할 때 사용

    public static void main(String[] args) {
        // Static Nested Class의 인스턴스를 생성하기 위해서는 바깥 클래스의 인스턴스가 필요하지 않음
        OuterClass.StaticNestedClass staticNestedClass = new OuterClass.StaticNestedClass();
        staticNestedClass.display();

        OuterClass outerClass = new OuterClass();
        OuterClass.InnerClass innerClass = outerClass.new InnerClass();
        innerClass.display();
    }
}
