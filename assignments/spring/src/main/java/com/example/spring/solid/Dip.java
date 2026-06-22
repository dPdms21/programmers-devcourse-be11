package com.example.spring.solid;

public class Dip {
    public interface MessageSender {
        void send(String msg);
    }

    public static class EmailSender implements MessageSender {
        public void send(String msg) {
            System.out.println("[이메일] " + msg);
        }
    }

    public static class SmsSender implements MessageSender {
        public void send(String msg) {
            System.out.println("[SMS] " + msg);
        }
    }

    public static class NotificationService {
        private MessageSender sender;

        NotificationService(MessageSender sender) {
            this.sender = sender;
        }

        void notifyUser(String msg) { sender.send(msg); }
    }
}
