package waitnotify;

public class Chat {
    private  boolean flag = false;

    public synchronized void question(String msg) {
        while (flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Question: " + msg);
        flag = true;
        notify();
    }

    public synchronized void answer(String msg) {
        while (!flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Answer: " + msg);
        flag = false;
        notify();
    }
}
