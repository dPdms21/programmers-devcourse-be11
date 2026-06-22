package com.example.spring.solid;

import java.util.ArrayList;
import java.util.List;

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

    public static class MockSender implements MessageSender {
        private final List<String> list = new ArrayList<>();

        public void send(String msg) {
            list.add(msg);
        }

        public List<String> getMsg() {
            return list;
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
