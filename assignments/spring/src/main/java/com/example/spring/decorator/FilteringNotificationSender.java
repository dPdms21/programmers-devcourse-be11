package com.example.spring.decorator;

public class FilteringNotificationSender implements NotificationSender {
    private final NotificationSender delegate;

    public FilteringNotificationSender(NotificationSender delegate) {
        this.delegate = delegate;
    }

    @Override
    public void send(String to, String message) {
        if (message.contains("광고")) {
            System.out.println("[BLOCK] 금칙어 포함되어 발송 차단");
            return;
        }

        delegate.send(to, message);
    }
}
