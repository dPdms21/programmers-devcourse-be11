package starvation;

public class Main {
    public static void main(String[] args) {
        SharedResource sr = new SharedResource();

        new WorkerThread(sr, "Worker1").start();
        new WorkerThread(sr, "Worker2").start();
        new WorkerThread(sr, "Worker3").start();

        Thread provider = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(2000);
                    sr.makeResourceAvailable(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            sr.printS();
        });

        provider.start();
    }
}
