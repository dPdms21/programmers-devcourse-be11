package part2;

public class F_person2 {
    // 멤버변수
    String name;
    int age;

    // 매개변수가 있는 생성자
    public F_person2(String name, int age) {
        // name, age -> 지역변수
        // 초기화
        this.name = name;
        this.age = age;
    }

    public void display() {
        System.out.println(name + " " + age);
    }
}