package part2;

// * 회원 인터페이스
// 인터페이스는 필드(상태)나 생성자를 가질 수 없고, 메서드 목록만 정함
// 단, 공통 동작은 default메서드로 구현할 수 있음

public interface O_member {
    String getName();
    String getEmail();
    String getPhone();
    String getGrade();
    String getBenefit();
    void update(String name, String email, String phone);

    // * default 메서드 : 인터페이스 안의 다른 메서드만 써서 구현
    // 목적 : 하위호환
    // 인터페이스에 새 메서드를 추가하면,
    // 그 인터페이스를 구현한 모든 클래스가 한순간에 컴파일 에러가 발생
    default void printInfo() {
        System.out.println("[" + getGrade() + "] " + getName() + ", " + getEmail() + ", " + getPhone() + ", " + getBenefit());
    }
}
