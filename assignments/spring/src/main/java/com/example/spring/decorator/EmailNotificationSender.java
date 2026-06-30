package com.example.spring.decorator;

public class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(String to, String message) {
        // 실제론 메일 서버 호출. 과제에서는 콘솔 출력으로 흉내
        System.out.printf("[EMAIL] to=%s : %s%n", to, message);
    }
}
