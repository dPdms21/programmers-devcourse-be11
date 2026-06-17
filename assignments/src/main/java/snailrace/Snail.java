package snailrace;

import java.util.*;

public class Snail extends Thread {
    private String name;
    private int position = 0;
    private final int FINISH = 30;
    private Random rand = new Random();
    private Race race;

    public Snail(String name, Race race) {
        this.name = name;
        this.race = race;
    }

    @Override
    public void run() {
        while (position < FINISH && !race.isOver()) {
            position += rand.nextInt(3) + 1;

            if (position > FINISH) {
                position = FINISH;
            }

            printProgress();

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                return;
            }
        }

        if (position >= FINISH) {
            race.finish(name);
        }
    }

    private void printProgress() {
        StringBuilder bar = new StringBuilder();

        for (int i=0; i<position; i++) {
            bar.append("=");
        }

        bar.append(">");
        System.out.println(name + ": " + bar);
    }
}
