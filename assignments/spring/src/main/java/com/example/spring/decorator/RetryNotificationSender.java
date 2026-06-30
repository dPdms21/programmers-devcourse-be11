package com.example.spring.decorator;

public class RetryNotificationSender implements NotificationSender {
    private final NotificationSender delegate;
    private final int maxRetry;

    public RetryNotificationSender(NotificationSender delegate) {
        this(delegate, 3);
    }

    public RetryNotificationSender(NotificationSender delegate, int maxRetry) {
        this.delegate = delegate;
        this.maxRetry = maxRetry;
    }

    @Override
    public void send(String to, String message) {
        RuntimeException lastException = null;

        for (int i=1; i<=maxRetry; i++) {
            try {
                delegate.send(to, message);

                return;
            }
            catch (RuntimeException e) {
                lastException = e;
                System.out.println("[RETRY] " + i + "번째 발송 실패: " + e.getMessage());
            }
        }

        throw lastException;
    }
}
