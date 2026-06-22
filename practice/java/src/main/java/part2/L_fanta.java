package part2;

public class L_fanta extends L_drink{
    public L_fanta() {
        super("환타", 700);
    }

    @Override
    public void dispense() {
        System.out.println("환타가 나왔습니다.");
    }
}
