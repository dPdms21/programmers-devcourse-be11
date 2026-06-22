package waitnotify;

public class Main {
    public static void main(String[] args) {
        Chat chat = new Chat();

        QuestionThread q1 = new QuestionThread(chat);
        QuestionThread q2 = new QuestionThread(chat);

        AnswerThread a1 = new AnswerThread(chat);
        AnswerThread a2 = new AnswerThread(chat);

        q1.start();
        q2.start();
        a1.start();
        a2.start();
    }
}
