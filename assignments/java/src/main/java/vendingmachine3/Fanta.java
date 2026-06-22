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
    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public void dispense() {
        System.out.println("환타 나옴!");
    }
}
