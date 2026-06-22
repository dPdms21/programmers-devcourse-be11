package part2;

/**
 * 음료 인터페이스
 * 추상 클래스와 달리 인터페이스는 필드(상태)나 생성자를 가질 수 없고,
 * "무엇을 할 수 있는지"(메서드 목록)만 약속
 * 이 인터페이스를 implements하는 클래스는 반드시 메서드 목록을 구현
 */

public interface M_drink {
    String getName();
    int getPrice();
    void dispense();
}
