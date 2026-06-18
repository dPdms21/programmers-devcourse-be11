package waitnotify;

public class Main {
    public static void main(String[] args) {
        Chat chat = new Chat();

        QuestionThread qt = new QuestionThread(chat);
        AnswerThread at = new AnswerThread(chat);

        qt.start();
        at.start();
    }
}
