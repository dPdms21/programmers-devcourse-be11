package textrpg;

public class Start {
    public static void main(String[] args) {
        Character hero = new Character("용사", 100, 25);
        Character[] monsters = {
                new Character("슬라임"),
                new Character("고블린", 50, 8),
                new Character("드래곤", 120, 20)
        };

        boolean clear = true;

        for (Character m : monsters) {
            System.out.println("\n=== 다음 상대: ===");
            m.showStatus();

            while (hero.isAlive() && m.isAlive()) {
                hero.attack(m);
                m.showStatus();

                if (!m.isAlive()) {
                    System.out.println("몬스터 쓰러짐!!");
                    break;
                }

                m.attack(hero);
                hero.showStatus();
            }

            if (!hero.isAlive()) {
                System.out.println("게임 오버...");
                clear = false;
                break;
            }
        }

        if (clear) {
            System.out.println("클리어!!!");
        }
    }
}
