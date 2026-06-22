package part2;

// * 객체지향 프로그래밍(Object Oriented Programming, OOP)
// 프로그램을 여러 개의 객체(Object)로 나누어 작성하는 방법을 말한다.
// 객체지향 프로그래밍은 코드의 재사용성과 유지보수성을 높이고,
// 복잡한 문제를 쉽게 해결할 수 있도록 하는 데 중점을 둔다.

// * class
// 객체를 정의해 놓은 것
// 클래스는 객체를 생성하는데 사용.

// * 객체
// 실제로 존재하는 것. 사물 또는 개념

// * 인스턴스
// 클래스로부터 객체를 만드는 과정을 클래스의 인스턴스라고 하며,
// 어떤 클래스로부터 만들어진 객체를 그 클래스의 인스턴스라고 한다.

// 클래스 --> (인스턴스화) --> 인스턴스(객체)
class TV {
    // TV의 속성 -> 멤버 변수
    String color;
    boolean power;
    int volume;
    int channel;

    // TV의 기능 -> 메서드
    public void power() {
        power = !power; // 토글
        // 3항 연산자 : 조건식 ? true : false
        // => if (poser) {} else {}
        System.out.println("TV 전원을 " + ( power ? "킴" : "끔"));
    }

    public void volumeUp() {
        volume++;
        System.out.println("볼륨 올림");
    }

    public void volumeDown() {
        volume--;
        System.out.println("볼륨 내림");
    }

    public void channelUp() {
        channel++;
        System.out.println("채널 올림");
    }

    public void channelDown() {
        channel--;
        System.out.println("채널 내림");
    }

}

public class A_object_class {
    public static void main(String[] args) {
        // 객체 생성
        TV tv = new TV();

        // 초기화 -> 멤버변수 접근
        tv.channel = 7;
        tv.color = "black";
        tv.power = false;
        tv.volume = 8;

        // 기능 사용 -> 메서드 사용
        tv.power();
        tv.volumeUp();
        tv.volumeDown();
        tv.channelUp();
        tv.channelDown();
    }
}
