package snailrace;

public class Race {
    private volatile boolean over = false;

    public boolean isOver() {
        return over;
    }

    public synchronized void finish(String name) {
        if (!over) {
            over = true;
            System.out.println("\n*** 우승: " + name + " ***");
        }
    }
}
