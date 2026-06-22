package waitnotify;

public class Chat {
    private  boolean flag = false;

    public synchronized void question(String msg) {
        while (flag) {
            try {
                wait(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Question: " + msg);
        flag = true;
        notifyAll();
    }

    public synchronized void answer(String msg) {
        while (!flag) {
            try {
                wait(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Answer: " + msg);
        flag = false;
        notifyAll();
    }
}
