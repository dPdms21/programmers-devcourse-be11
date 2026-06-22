package part2;

public class M_water implements M_drink {
    private String name = "물";
    private int price = 200;

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
        System.out.println("물이 나왔습니다.");
    }
}
