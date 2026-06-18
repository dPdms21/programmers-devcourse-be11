package waitnotify;

public class AnswerThread extends Thread {
    private Chat chat;
    private String[] answers = { "Hello", "I'm fine, thank you!", "I'm coding in Java" };

    public AnswerThread(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void run() {
        for (String a : answers) {
            chat.answer(a);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
