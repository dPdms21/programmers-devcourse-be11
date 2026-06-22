package part2;

public class G_dog extends G_animal {
    public void bark() {
        System.out.println(kind + " is barking");
    }

    @Override
    public void walk() {
        System.out.println("투닥투닥");
    }
}
