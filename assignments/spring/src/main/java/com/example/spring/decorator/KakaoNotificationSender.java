package com.example.spring.decorator;

public class KakaoNotificationSender implements NotificationSender {
    @Override
    public void send(String to, String message) {
        // 실제론 카카오 서버 호출. 과제에서는 콘솔 출력으로 흉내
        System.out.printf("[KAKAO] to=%s : %s%n", to, message);
    }
}
