package semaphoregame;

public class Adventurer extends Thread {
    private Dungeon dungeon;
    private String name;
    private int totalGold;

    public Adventurer(Dungeon dungeon, String name) {
        this.dungeon = dungeon;
        this.name = name;
    }

    public String getAdventurerName() {
        return name;
    }

    public int getTotalGold() {
        return totalGold;
    }

    public void run() {
        for (int i=1; i<=3; i++) {
            System.out.println("\n[" + name + "의 " + i + "번째 도전]");

            try {
                totalGold += dungeon.enter(name);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
        }

        System.out.println("----------------------------");
        System.out.println("[도전 종료] " + name + " → 총 " + totalGold + " 골드");
        System.out.println("----------------------------");
    }
}
