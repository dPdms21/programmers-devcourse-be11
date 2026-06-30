package com.example.spring.ch05.ex_5_4;

// * 메일 서비스 추상화
// 요구 사항 추가 : 레벨이 업그레이드된 사용자에게 안내 메일을 보냄

// * 문제점
// 만약 UserServiceImpl이 '직접' 메일을 보낸다면
// - 테스트마다 진짜 메일이 나가거나, SMTP 서버가 없으면 테스트가 실패
// - 외부 환경(메일 서버)에 코드와 테스트가 묶임

// '메일을 보낸다'를 MailSender 인터페이스로 추상화하고 DI로 주입

public class Start {
    public static void main(String[] args) {

    }
}
