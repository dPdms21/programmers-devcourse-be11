package vendingmachine2;

public abstract class Drink {
    protected String name;
    protected  int price;

    public Drink(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public abstract void dispense();
}
