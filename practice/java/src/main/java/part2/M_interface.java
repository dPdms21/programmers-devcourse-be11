package part2;

// * 인터페이스
// 자바에서 클래스들이 구현해야 하는 메서드의 집합을 정의하는 데 사용되는 추상 타입
// 인터페이스는 메서드의 선언만 포함하며, 메서드의 구체적인 구현은 포함하지 않음
// 따라서 인터페이스를 구현하는 클래스는 인터페이스에 선언된 모든 메서드를 반드시 구현

// * 인터페이스의 필요성
// 1. 표준화
// 인터페이스를 사용하면 여러 클래스가 동시에 동일한 메서드를 구현하도록 강제할 수 있음
// 이는 일관된 API 설계와 코드 표준화를 가능하게 함
// 2. 다중 상속의 대안
// 자바는 클래스의 다중 상속을 지원 않지만, 인터페이스는 여러 개를 구현할 수 있음
// 이를 통해 다중 상속의 이점을 제공하면서 다중 상속에서 발생할 수 있는 복잡성과 충돌을 피할 수 있음
// 3. 유연한 설계
// 인터페이스를 사용하면 특정 클래스의 구현에 의존하지 않고, 인터페이스를 기반으로 프로그래밍 할 수 있음
// 이는 코드의 유연성과 확장성을 크게 향상시킴
// 4. 느슨한 결합
// 이는 시스템의 각 부분을 독립적으로 개발하고 변경할 수 있게 함

// * 인터페이스와 추상 클래스의 차이점
// - 다중 구현 : 클래스는 여러 인터페이스를 구현할 수 있지만, 추상 클래스는 하나만 상속 받을 수 있음
// - 구현 유무 : 인터페이스는 구현을 포함하지 않지만, 추상 클래스는 일부 메서드의 구현을 포함할 수 있음
// - 상태 유지 : 인터페이스는 상태(필드)를 가질 수 없지만, 추상 클래스는 필드를 가질 수 있음

// 인터페이스 정의 -> 명세서
interface M_animal {
    void makeSound();
    void eat();
}

class M_dog implements M_animal {

    @Override
    public void makeSound() {
        System.out.println("Dog sound");
    }

    @Override
    public void eat() {
        System.out.println("Dog eats meat");
    }
}

class M_cat implements M_animal {

    @Override
    public void makeSound() {
        System.out.println("Cat sound");
    }

    @Override
    public void eat() {
        System.out.println("Cat eats fish");
    }
}

public class M_interface {
    public static void main(String[] args) {
        // 인터페이스 타입의 변수로 여러 구현체를 참조 가능
        M_animal myDog = new M_dog();
        M_animal myCat = new M_cat();

        myDog.makeSound();
        myDog.eat();

        myCat.makeSound();
        myCat.eat();
    }
}
