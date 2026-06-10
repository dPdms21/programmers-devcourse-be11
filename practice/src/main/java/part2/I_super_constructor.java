package part2;

// * super() : 조상 클래스의 생성자
// this()와 마찬가지로 super() 역시 생성자
// this()는 같은 클래스의 다른 생성자를 호출하는데 사용되지만,
// super()는 조상클래스의 생성자를 호출하는데 사용

// 자식 클래스의 인스턴스를 생성하면, 자식의 멤버와 부모의 멤버가 모두 합쳐진 하나의 인스턴스가 생성
// 그래서 자식 클래스의 인스턴스가 부모 클래스의 멤버들을 사용할 수 있음
// 이때 조상 클래스 멤버의 초기화 작업이 수행되어야 하기 때문
// 자식 클래스의 생성자에서 조상 클래스의 생성자가 호출되어야 함

// 생성자의 첫 줄에서 조상클래스의 생성자를 호출해야하는 이유는 자식 클래스의 멤버가 조상 클래스이 멤버를 사용할 수도 있으므로
// 조사의 멤버들이 먼저 초기화되어 있어야 함

// 이와 같은 조상 클래스 생성자의 호출은 클래스의 상속관계를 거슬러 올라가면서 계속 반복
// 마지막으로 모든 클래스의 최고 조상인 Object 클래스의 생성자인 Object()까지 가서야 끝이 남
// 그래서 Object 클래스를 제외한 모든 클래스의 생성자는 첫 줄에 반드시 자신의 다른 생성자 또는
// 조상의 생성자를 호출해야함
// 그렇지 않으면 생성자는 첫 줄에 super()를 자동으로 추가

class SuperPoint {
    int x = 10;
    int y = 20;

    public SuperPoint(int x, int y) {
        // super();는 Point의 조상인 Object 클래스의 기본생성자인 Object()를 의미한다.
        System.out.println("나는 부모이다.");
        this.x = x;
        this.y = y;
    }
}

class SubPoint3D extends SuperPoint {
    int z = 30;

    public SubPoint3D(int x, int y, int z) {
        super(x, y);
        System.out.println("나는 자식이다.");
        this.z = z;
    }
}

public class I_super_constructor {
    public static void main(String[] args) {
        SubPoint3D subPoint3D = new SubPoint3D(10, 20, 30);
    }
}
