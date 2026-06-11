package part2;

public class M_cider implements M_drink {
    private String name = "사이다";
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
        System.out.println("사이다가 나왔습니다.");
    }
}