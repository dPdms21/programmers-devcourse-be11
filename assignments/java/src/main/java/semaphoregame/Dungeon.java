package semaphoregame;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Dungeon {
    private final Semaphore slots;
    private final int capacity;

    public Dungeon(int capacity) {
        this.slots = new Semaphore(capacity, true);
        this.capacity = capacity;
    }

    public int enter(String name) throws InterruptedException {
        System.out.println(name + " 던전 입장 대기...");

        boolean acquired = slots.tryAcquire(2, TimeUnit.SECONDS);

        if (!acquired) {
            System.out.println("[입장 포기] " + name + " -> 마을로 복귀");
            return 0;
        }

        try {
            System.out.println("[입장] "+ name + " (남은 자리: " + slots.availablePermits() + "/" + capacity + ")");

            boolean bossEncounter = Math.random() < 0.3;

            int time = (int)(Math.random() * 2000) + 1000;
            int gold = (int)(Math.random() * 400) + 100;

            if (bossEncounter) {
                System.out.println("----------------------------");
                System.out.println("[보스 출현] " + name);
                System.out.println("----------------------------");
                time += 2000;
                gold *= 2;
            }

            Thread.sleep(time);
            System.out.println("[클리어] " + name + " → " + gold + " 골드 획득");

            return gold;
        }
        finally {
            System.out.println("[퇴장] " + name);
            slots.release();
        }
    }
}