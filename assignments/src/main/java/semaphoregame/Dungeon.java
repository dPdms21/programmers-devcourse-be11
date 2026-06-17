package semaphoregame;

import java.util.concurrent.Semaphore;

public class Dungeon {
    private final Semaphore slots;
    private final int capacity;

    public Dungeon(int capacity) {
        this.slots = new Semaphore(capacity);
        this.capacity = capacity;
    }

    public void enter(String name) throws InterruptedException {
        System.out.println(name + " 던전 입장 대기...");
        slots.acquire();

        try {
            System.out.println("[입장] "+ name + "(남은 자리: " + slots.availablePermits() + "/" + capacity + ")");
            Thread.sleep((int)(Math.random() * 2000) + 1000);
            int gold = (int)(Math.random() * 400) + 100;
            System.out.println("[클리어] " + name + " → " + gold + " 골드 획득");
        } finally {
            System.out.println("[퇴장] " + name);
            slots.release();
        }
    }
}