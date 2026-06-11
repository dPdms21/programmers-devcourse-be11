package vendingmachine2;

public class Coke extends Drink {
    public Coke() {
        super("콜라", 500);
    }

    @Override
    public void dispense() {
        System.out.println("-------------------------------");
        System.out.println("콜라 나옴!");
    }
}
