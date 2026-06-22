package part2;

// * 다형성
// 동일한 인터페이스나 부모 클래스를 공유하는 여러 객체들이 각자 다른 방식으로 동작하도록 할 수 있는 기능
// 다형성은 "하나의 인터페이스로 여러 형태의 객체를 처리할 수 있다"는 의미
// 이를 통해 코드의 유연성과 확장성을 크게 향상시킬 수 있음

class K_animal {
    // 부모클래스에서 정의된 메서드
    public void sound() {
        System.out.println("Animal sound");
    }
}

class K_dog extends K_animal {
    public void fetch() {
        System.out.println("Dog fetches a ball");
    }

    @Override
    public void sound() {
        System.out.println("Dog sound");
    }
}

class K_cat extends K_animal {
    @Override
    public void sound() {
        System.out.println("Cat sound");
    }
}

public class K_polymorphism {
    public static void main(String[] args) {
        K_animal myAnimal = new K_animal(); // K_animal 타입의 객체
        K_animal myDog = new K_dog(); // Dog타입의 객체, Animal타입으로 업캐스팅
        K_animal myCat = new K_cat(); // Cat타입의 객체, Animal타입으로 업캐스팅

        myAnimal.sound();
        myDog.sound(); // 런타임 시점에 Dog의 sound()호출
        myCat.sound(); // 런타임 시점에 Cat의 sound()호출

        // 다운캐스팅을 통해 다시 Dog 타입으로 변환
        // K_dog myDog2 = (K_dog) myAnimal; // 실질적인 인스턴스는 animal -> dog(x)
        K_dog mydog2 = (K_dog) myDog; // 실질적인 인스턴스가 dog이기 때문에 다운캐스팅 가능
        mydog2.fetch();
    }
}
