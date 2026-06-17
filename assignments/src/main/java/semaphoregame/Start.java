package semaphoregame;

public class Start {
    public static void main(String[] args) {
        Dungeon dungeon = new Dungeon(2);

        String[] names = { "전사", "마법사", "궁수", "도적", "성기사" };

        for (String name : names) {
            new Adventurer(dungeon, name).start();
        }
    }
}
