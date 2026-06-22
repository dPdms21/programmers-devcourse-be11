package part2;

// * Object 클래스 - 모든 클래스의 조상
// Object클래스는 모든 클래스 상속계층도의 최상위에 있는 조상클래스
// 다른 클래스로부터 상속 받지 않는 모든 클래스들은 자동적으로 Object 클래스로부터 상속받게 함으로써 이것을 가능하게 함
// 컴파일러는 자동적으로 상속이 없는 클래스에 extends Object를 추가함으로써 모든 클래스의 조상이 되도록 함
// 만약 다른 클래스로부터 상속을 받는다고 하더라도 상속계층도를 따라 조상클래스, 조상클래스의 조상클래스를 찾아 올라가 보면
// 결국 최상위 조상은 Object 클래스

// 따라서 자바의 모든 클래스들은 Object클래스의 멤버들을 상속 받기 때문에
// Object클래스에 정의된 멤버들을 사용할 수 있음
// toString()이나 equals()와 같은 메서드를 따로 정의하지 않고도 사용할 수 있었던 이유

class Radio { // extends Object가 생략되어 있음 (컴파일러가 자동으로 추가)

}

public class H_object_class {
    public static void main(String[] args) {
        Radio radio = new Radio();

        System.out.println(radio.equals(radio));
        System.out.println(radio.toString());
        System.out.println(radio.getClass());
        //...
    }
}
