package vendingmachine3;

public class Coke implements Drink {
    private String name = "콜라";
    private int price = 500;
    private int stock = 10;

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
        System.out.println("콜라 나옴!");
    }
}
