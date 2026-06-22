package part2;

public class F_person5 {
    String name;
    int age;

    public F_person5() {
        // F_person5("John", 25); // 에러 반드시 this로 호출해야됨.
        this("John", 25);
    }

    public F_person5(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void display() {
        System.out.println(name + " " + age);
    }
}
