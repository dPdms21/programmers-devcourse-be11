package vendingmachine3;

public class Water implements Drink {
    private String name = "물";
    private int price = 200;
    private int stock = 15;

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
        System.out.println("물 나옴!");
    }
}
