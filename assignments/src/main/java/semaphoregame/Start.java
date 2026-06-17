package semaphoregame;

import java.util.*;

public class Start {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Adventurer> adventurers = new ArrayList<>();

        int n;

        System.out.println("========= 던전 게임 =========");

        while (true) {
            System.out.print("던전 동시 입장 정원 (1~3) > ");
            n = Integer.parseInt(sc.nextLine());

            if (n >= 1 && n <= 3) {
                break;
            }

            System.out.println("1~3 중 입력");
        }

        Dungeon dungeon = new Dungeon(n);

        String[] names = {"전사", "마법사", "궁수", "도적", "성기사"};

        for (String name : names) {
            Adventurer adventurer = new Adventurer(dungeon, name);
            adventurers.add(adventurer);
            adventurer.start();
        }

        for (Adventurer adventurer : adventurers) {
            try {
                adventurer.join();
            }
            catch (InterruptedException e) {
                return;
            }
        }

        adventurers.sort((a, b) -> Integer.compare(b.getTotalGold(), a.getTotalGold()));

        System.out.println("\n===== 골드 랭킹 =====");

        for (int i=0; i<adventurers.size(); i++) {
            Adventurer adventurer = adventurers.get(i);

            System.out.println((i + 1) + "위: " + adventurer.getAdventurerName() + " → " + adventurer.getTotalGold() + " 골드"
            );
        }

        System.out.println("====================");
    }
}
