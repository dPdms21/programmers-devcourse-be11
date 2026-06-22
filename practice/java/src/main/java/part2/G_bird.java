package part2;

public class G_bird extends G_animal {
    String wing;

    public void fly() {
        System.out.println(kind + " is flying " + wing);
    }

    @Override
    public void walk() {
        System.out.println("사뿐사뿐");
    }
}
