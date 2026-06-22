package waitnotify;

public class QuestionThread extends Thread {
    private Chat chat;
    private String[] questions = { "Hi", "How are you?", "What are you doing?" };

    public QuestionThread(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void run() {
        for (String q : questions) {
            chat.question(q);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
