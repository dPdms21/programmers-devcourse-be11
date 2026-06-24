package stream;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private List<String> items;

    public Order(int id, List<String> items) {
        this.id = id;
        this.items = items;
    }

    public List<String> getItems() {
        return items;
    }
}
