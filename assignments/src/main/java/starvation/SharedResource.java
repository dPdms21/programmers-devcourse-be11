package starvation;

import java.util.*;

public class SharedResource {
    private int resourceCount = 0;
    private final Queue<String> q = new LinkedList<>();
    private final Map<String, Integer> cnt = new HashMap<>();

    public synchronized void waitForResource(String threadName) {
        q.offer(threadName);

        while (resourceCount == 0 || !threadName.equals(q.peek())) {
            try {
                System.out.println(threadName + " is waiting for resource...");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                q.remove(threadName);
                return;
            }
        }

        q.poll();
        resourceCount--;

        cnt.put(threadName, cnt.getOrDefault(threadName, 0) + 1);

        System.out.println("------------------------------");
        System.out.println(threadName + " got the resource!");
        System.out.println("남은 자원: " + resourceCount);
        System.out.println("------------------------------");

        notifyAll();
    }

    public synchronized void makeResourceAvailable(int count) {
        resourceCount += count;

        System.out.println(count + "개의 자원 공급 → 현재 자원: " + resourceCount);
        // notify();
        notifyAll();
    }

    public synchronized void printS() {
        System.out.println("\n자원 획득 통계");

        for (Map.Entry<String, Integer> entry : cnt.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "회");
        }
    }
}
