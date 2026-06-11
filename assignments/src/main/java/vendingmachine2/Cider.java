package vendingmachine2;

public class Cider extends Drink {
    public Cider() {
        super("사이다", 500);
    }

    @Override
    public void dispense() {
        System.out.println("-------------------------------");
        System.out.println("사이다 나옴!");
    }
}
