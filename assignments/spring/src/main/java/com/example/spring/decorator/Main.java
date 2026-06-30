package com.example.spring.decorator;

public class Main {
    public static void main(String[] args) {
        // NotificationService가 NotificationSender 추상화에 의존하므로
        // 구현체가 바뀌어도 NotificationService의 내부 코드는 수정할 필요가 없음

        System.out.println("\n=== Part A: 추상화 테스트 ===");

        NotificationService emailService = new NotificationService(new EmailNotificationSender());
        emailService.notifyUser("user@example.com", "알림");

        NotificationService smsService = new NotificationService(new SmsNotificationSender());
        smsService.notifyUser("010-1234-5678", "알림");

        NotificationService kakaoService = new NotificationService(new KakaoNotificationSender());
        kakaoService.notifyUser("kakao-user", "알림");

        System.out.println("\n=== Part C: 데코레이터 조합 테스트 ===");

        System.out.println("\n--- Logging(Retry(...)) ---");

        NotificationSender loggingOutside =
                new TimingNotificationSender(
                        new LoggingNotificationSender(
                                new RetryNotificationSender(
                                        new FlakyEmailSender())));

        NotificationService loggingOutsideService = new NotificationService(loggingOutside);

        loggingOutsideService.notifyUser("user@example.com", "Logging 바깥 조합 테스트");

        System.out.println("\n--- Retry(Logging(...)) ---");

        NotificationSender retryOutside =
                new TimingNotificationSender(
                        new RetryNotificationSender(
                                new LoggingNotificationSender(
                                        new FlakyEmailSender())));

        NotificationService retryOutsideService = new NotificationService(retryOutside);

        retryOutsideService.notifyUser("user@example.com", "Retry 바깥 조합 테스트");

        System.out.println("\n=== 도전 과제: 금칙어 차단 ===");

        NotificationSender filteringSender =
                new FilteringNotificationSender(
                        new LoggingNotificationSender(
                                new EmailNotificationSender()));

        NotificationService filteringService = new NotificationService(filteringSender);

        filteringService.notifyUser("user@example.com", "정상 알림");

        System.out.println("-----");

        filteringService.notifyUser("user@example.com", "광고 메시지");

        System.out.println("\n=== 도전 과제: 발송 속도 제한 ===");

        NotificationSender rateLimitSender =
                new TimingNotificationSender(
                        new RateLimitNotificationSender(
                                new EmailNotificationSender()));

        NotificationService rateLimitService = new NotificationService(rateLimitSender);

        rateLimitService.notifyUser("user@example.com", "첫 번째 알림");

        rateLimitService.notifyUser("user@example.com", "두 번째 알림");
    }
}
