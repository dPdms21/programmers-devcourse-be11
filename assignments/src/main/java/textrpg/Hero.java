package textrpg;

public class Hero extends Character {
    public Hero(String name, int hp, int power) {
        super(name, hp, power);
    }

    public void heal(int amount) {
        int newHp = getHp() + amount;

        if (newHp > 100) {
            newHp = 100;
        }

        setHp(newHp);

        System.out.println(getName() + " HP 회복! 현재 HP: " + getHp());
    }
}
