package vendingmachine2;

public abstract class Drink {
    protected String name;
    protected  int price;
    protected int stock;

    public Drink(String name, int price,int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public abstract void dispense();

    public void decreaseStock() {
        stock--;
    }

    public boolean isSoldOut() {
        return stock <= 0;
    }
}
