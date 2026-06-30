package com.example.spring.ch05.ex_5_4.service;

import java.util.ArrayList;
import java.util.List;

// * MockMailSender - 보낸 메일을 '기록'하는 테스트 대역 (목 오브젝트)
// DummyMailSender가 '그냥 무시'한다면, 이 목 오브젝트는 한 걸음 더 나아가
// '무엇을 보냈는지'를 안에 저장
//  -> 테스트에서 "업그레이드된 사용자에게 메일이 정확히 갔는가?"를 검증할 수 있게 해줌
//     (예: upgradeLevels() 후 getSentTo()에 기대한 수신자들이 들어 있는지 확인)

// 보통 이런 목 클래스는 src/test에 두지만, 추상화의 효과를 한곳에서 보여주려고 여기 함께 둠
// 핵심: 실제 메일 서버 없이도, 추상화(MailSender) 덕분에 발송 동작을 검증할 수 있다는 점

public class MockMailSender implements MailSender {
    // 발송된 메일의 수신자 목록을 기록한다.
    private List<String> sentTo = new ArrayList<>();

    @Override
    public void send(Mail mail) {
        sentTo.add(mail.getTo());
    }

    public List<String> getSentTo() {
        return sentTo;
    }
}
