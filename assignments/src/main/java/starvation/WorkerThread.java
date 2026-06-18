package starvation;

public class WorkerThread extends Thread {
    private SharedResource sr;
    private String name;

    public WorkerThread(SharedResource sr, String name) {
        this.sr = sr;
        this.name = name;
    }

    public void run() {
        while (true) {
            sr.waitForResource(name);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
