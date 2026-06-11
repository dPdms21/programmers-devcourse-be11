package vendingmachine3;

public interface Drink {
    String getName();
    int getPrice();
    int getStock();
    void decreaseStock();
    boolean isSoldOut();
    void dispense();
}
