package part2;

// * super
// super는 자식 클래스에서 조상 클래스로부터 상속받은 멤버를 참조하는데 사용되는 참조 변수
// 멤버변수와 지역변수 이름이 같을 때 this를 붙여 구별하였듯이 상속받은 멤버와 자신의 멤버가 이름이 같을 때는
// super를 붙여서 구별할 수 있음

// 조상 클래스로부터 상속받은 멤버도 자식 클래스 자신의 멤버이므로 super 대신 this를 사용할 수 있음
// 그래도 조상 클래스의 멤버와 자식클래스의 멤버가 중복 정의되어 서로 구별해야하는 경우에만 super를 사용하는게 좋음
// 조상의 멤버와 자신의 멤버를 구별하는데 사용된다는 점을 제외하고는 super와 this는 근본적으로는 같음
// 모든 인스턴스에는 자신이 속한 인스턴스와 주소가 지역변수로 지정되는데,
// 이것이 참조변수인 this와 super의 값이 됨
// static 메서드 인스턴스와 관련이 없음
// 그래서 this와 super는 static 메서드에서는 사용할 수 없고, 인스턴스에서만 사용할 수 있음

class SuperParent {
    int x = 10;
    int y = 20;

    public String getLocation() {
        return "x = " + x + ", y = " + y;
    }
}

class SuperChild extends SuperParent {
    int y = 30;
    int z = 40;

    void method() {
        System.out.println( "x = " + x );
        System.out.println("this.x = " + this.x );
        System.out.println("super.x = " + super.x );

        System.out.println( "y = " + y );
        System.out.println("this.y = " + this.y );
        System.out.println("super.y = " + super.y );
    }

    @Override
    public String getLocation() {
        return super.getLocation() + ", z = " + z;
    }
}

public class I_super {
    public static void main(String[] args) {
        SuperChild superChild = new SuperChild();
        superChild.method();
        System.out.println(superChild.getLocation());
    }
}
