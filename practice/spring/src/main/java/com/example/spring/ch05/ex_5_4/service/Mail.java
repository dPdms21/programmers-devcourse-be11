package com.example.spring.ch05.ex_5_4.service;

// 보낼 메일 한 통을 나타내는 값 객체

public class Mail {
    private String to;
    private String subject;
    private String text;

    public Mail(String to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }
}
