package part2;

public class L_water extends L_drink{
    public L_water() {
        super("물", 200);
    }

    @Override
    public void dispense() {
        System.out.println("물이 나왔습니다.");
    }
}
