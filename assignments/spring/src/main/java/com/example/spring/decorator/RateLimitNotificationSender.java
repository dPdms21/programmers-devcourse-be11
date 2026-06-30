package com.example.spring.decorator;

public class RateLimitNotificationSender implements NotificationSender {
    private final NotificationSender delegate;
    private long lastSentTime;

    public RateLimitNotificationSender(NotificationSender delegate) {
        this.delegate = delegate;
    }

    @Override
    public void send(String to, String message) {
        long now = System.currentTimeMillis();

        if (now - lastSentTime < 1000) {
            long delay = 1000 - (now - lastSentTime);

            try {
                Thread.sleep(delay);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("발송 대기 중 인터럽트 발생", e);
            }
        }

        delegate.send(to, message);
        lastSentTime = System.currentTimeMillis();
    }
}
