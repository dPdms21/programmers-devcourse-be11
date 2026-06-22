package vendingmachine2;

public class Fanta extends Drink {
    public Fanta() {
        super("환타", 300, 5);
    }

    @Override
    public void dispense() {
        System.out.println("-------------------------------");
        System.out.println("환타 나옴!");
    }
}
