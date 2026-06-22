package vendingmachine3;

public interface Drink {
    String getName();
    int getPrice();
    int getStock();
    void setStock(int stock);

    void dispense();

    default boolean isSoldOut() {
        return getStock() <= 0;
    }

    default void decreaseStock() {
        setStock(getStock() - 1);
    }
}
