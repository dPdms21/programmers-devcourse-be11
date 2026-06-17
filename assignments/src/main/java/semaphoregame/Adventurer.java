package semaphoregame;

public class Adventurer extends Thread {
    private Dungeon dungeon;
    private String name;

    public Adventurer(Dungeon dungeon, String name) {
        this.dungeon = dungeon;
        this.name = name;
    }

    public void run() {
        try {
            dungeon.enter(name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
