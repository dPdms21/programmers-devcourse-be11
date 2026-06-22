package part2;

// * 캡슐화
// 객체지향프로그래밍의 기본 원칙 중 하나로, 데이터를 숨기고 (은닉) 그 데이터를 조작하는 메서드만 외부에 노출하여
// 객체 내부의 상태를 보호하는 개념
// 캡슐화의 핵심 목표는 외부에서 객체의 내부 구조를 직접적으로 접근하거나 수정하지 못하게 하고,
// 이를 통해 데이터의 무결성과 보안성을 유지하는 것

// * 캡슐화의 주요 특징
// 1. 데이터 은닉
// 객체의 필드(변수)를 private 또는 protected로 설정하여 외부 클래스에서 직접 접근할 수 없도록 함
// 이렇게 하면 객체 내부의 상태가 무분별하게 변경되는 것을 막을 수 있음
// 2. 접근 제어자
// 자바에서는 private, protected, public 등의 접근 제어자를 사용하여 클래스 멤버(필드와 메서드)에 대한 접근 권한을 제어
// 이를 통해 어떤 클래스가 특정 멤버에 접근할 수 있는지를 정의
// 3. Getter와 Setter
// 필드가 private으로 설정된 경우, 외부에서 이 필드에 접근하기 위해 getter와 setter메서드를 제공
// 이 메서드를 통해 필드에 접근하거나 값을 변경할 수 있으며, 이때 유효성 검사를 추가할 수 있음

public class J_encapsulation {
    // private 필드로 선언하여 외부에서 직접 접근할 수 없도록 함
    private String name;
    private int age;

    // setter 메서드 : 외부에서 name 필드의 값을 설정할 수 있도록 허용
    public void setName(String name) {
        this.name = name;
    }

    // getter 메서드 : 외부에서 name 필드의 값을 접근할 수 있도록 허용
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // setter 메서드 : 외부에서 age 필드 값을 설정할 수 있도록 허용
    // 유효성 검사를 추가하여 올바른 값만 설정되도록 할 수 있음
    public void setAge(int age) {
        if (age < 0) {
            System.out.println("올바른 값이 압니다.");
            return;
        }

        this.age = age;
    }
}
