package textrpg;

import java.util.*;

public class Start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Hero hero = new Hero("용사", 100, 25);

        Monster[] monsters = {
                new Monster("슬라임"),
                new Monster("고블린", 50, 8),
                new Dragon("드래곤", 120, 20)
        };

        boolean clear = true;

        for (Character m : monsters) {
            System.out.println("==========================");
            System.out.print("다음 상대: ");
            m.showStatus();

            while (hero.isAlive() && m.isAlive()) {
                System.out.println("==========================");
                System.out.println("[1] 공격 [2] 방어 [3] 회복");
                System.out.println("==========================");
                System.out.print("번호 입력 > ");
                int num = Integer.parseInt(sc.nextLine());

                switch (num) {
                    case 1:
                        hero.attack(m);
                        m.showStatus();

                        if (m.isAlive()) {
                            m.attack(hero);
                            hero.showStatus();
                        } else {
                            System.out.println("--------------------------");
                            System.out.println("몬스터 쓰러짐!!");
                        }

                        break;

                    case 2:
                        System.out.println("--------------------------");
                        System.out.println("방어!");

                        if (m.isAlive()) {
                            int damage = m.getPower() / 2;
                            System.out.println("--------------------------");
                            System.out.println(m.getName() + "의 공격! 용사에게 " + damage + " 피해");
                            hero.takeDamage(damage);
                            hero.showStatus();
                        }

                        break;

                    case 3:
                        System.out.println("--------------------------");
                        System.out.println("체력을 회복했습니다!");
                        hero.heal(20);

                        if (m.isAlive()) {
                            m.attack(hero);
                            hero.showStatus();
                        }
                        break;

                    default:
                        System.out.println("--------------------------");
                        System.out.println("다시 입력");
                }
            }

            if (!hero.isAlive()) {
                System.out.println("==========================");
                System.out.println("게임 오버...");
                System.out.println("==========================");
                clear = false;
                break;
            }
        }

        if (clear) {
            System.out.println("==========================");
            System.out.println("클리어!!!");
            System.out.println("==========================");
        }
    }
}
