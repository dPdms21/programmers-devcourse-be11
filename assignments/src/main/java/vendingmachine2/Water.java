package vendingmachine2;

public class Water extends Drink {
    public Water() {
        super("물", 500);
    }

    @Override
    public void dispense() {
        System.out.println("-------------------------------");
        System.out.println("물 나옴!");
    }
}
