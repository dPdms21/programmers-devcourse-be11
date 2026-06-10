package textrpg;

public class Monster extends Character {
    public Monster(String name) {
        super(name, 30, 5);
    }

    public Monster(String name, int hp, int power) {
        super(name, hp, power);
    }
}
