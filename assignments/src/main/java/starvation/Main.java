package starvation;

public class Main {
    public static void main(String[] args) {
        SharedResource sr = new SharedResource();

        new WorkerThread(sr, "Worker1").start();
        new WorkerThread(sr, "Worker2").start();
        new WorkerThread(sr, "Worker3").start();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                    sr.makeResourceAvailable();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
