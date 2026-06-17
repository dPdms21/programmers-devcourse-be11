package snailrace;

import java.util.*;

public class Snail extends Thread {
    private String name;
    private int position = 0;
    private final int finish;
    private final int delay;
    private Random rand = new Random();
    private Race race;

    public Snail(String name, Race race, int finish, int delay) {
        this.name = name;
        this.race = race;
        this.finish = finish;
        this.delay = delay;
    }

    @Override
    public void run() {
        while (position < finish) {
            position += rand.nextInt(3) + 1;

            if (position > finish) {
                position = finish;
            }

            race.printRace();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                return;
            }
        }

        race.finish(name);
    }

    public String getTrack() {
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < finish; i++) {
            if (i < position - 1) {
                bar.append("=");
            }
            else if (i == position - 1) {
                bar.append(">");
            }
            else {
                bar.append(" ");
            }
        }

        return String.format("%s |%s| %d/%d", name, bar, position, finish);
    }
}
