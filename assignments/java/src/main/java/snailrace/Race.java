package snailrace;

import java.util.*;

public class Race {
    private final List<String> rank = new ArrayList<>();
    private final List<Snail> snails = new ArrayList<>();

    public void addSnail(Snail snail) {
        snails.add(snail);
    }

    public String getWinner() {
        return rank.get(0);
    }

    public List<String> getRanking() {
        return rank;
    }

    public synchronized void finish(String name) {
        rank.add(name);
    }

    public synchronized void printRace() {
        System.out.print("\033[2J\033[H");
        System.out.flush();

        System.out.println();

        for (Snail snail : snails) {
            System.out.println(snail.getTrack());
        }
    }
}
