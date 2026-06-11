package part2;

public class M_coke implements M_drink {
    private String name = "콜라";
    private int price = 500;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public void dispense() {
        System.out.println("콜라가 나왔습니다.");
    }
}