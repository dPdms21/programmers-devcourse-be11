package com.example.spring.decorator;

public class TimingNotificationSender implements NotificationSender {
    private final NotificationSender delegate;

    public TimingNotificationSender(NotificationSender delegate) {
        this.delegate = delegate;
    }

    @Override
    public void send(String to, String message) {
        long start = System.nanoTime();

        try {
            delegate.send(to, message);
        }
        finally {
            long end = System.nanoTime();
            long duration = (end - start) / 1_000_000;

            System.out.println("[TIME] 발송 소요 시간: " + duration + "ms");
        }
    }
}
