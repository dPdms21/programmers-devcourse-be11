package vendingmachine3;

public class Fanta implements Drink {
    private String name = "환타";
    private int price = 300;
    private int stock = 5;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public int getStock() {
        return stock;
    }

    @Override
    public boolean isSoldOut() {
        return stock <= 0;
    }

    @Override
    public void decreaseStock() {
        stock--;
    }

    @Override
    public void dispense() {
        System.out.println("환타 나옴!");
    }
}
