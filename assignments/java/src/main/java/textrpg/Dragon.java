package textrpg;

public class Dragon extends Monster {
    public Dragon(String name, int hp, int power) {
        super(name, hp, power);
    }

    @Override
    public void attack(Character target) {
        System.out.println("--------------------------");
        System.out.println(getName() + "의 화염 공격! " + target.getName() + "에게 " + getPower() + " 피해");
        target.takeDamage(getPower());
    }
}
