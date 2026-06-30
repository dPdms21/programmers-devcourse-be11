package com.example.spring.ch05.ex_5_3;

import com.example.spring.ch05.ex_5_3.dao.DaoFactory;
import com.example.spring.ch05.ex_5_3.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// * 서비스 추상화와 단일 책임 원칙
// * 문제점
// UserService가 한 클래스에 두 가지 책임을 갖고 있음
// 1) 레벨 업그레이드 '비즈니스 로직'
// 2) 트랜잭션 경계 설정이라는 '기술적 관심사'
// -> 업무가 바뀌어도, 트랜잭션 기술이 바뀌어도 같은 파일을 고침

// '인터페이스 + 데코레이터'로 두 책임을 분리
// 의존 흐름:
//   클라이언트 -> UserService(인터페이스)
//                 └─ 실제 빈은 UserServiceTx  (트랜잭션)
//                       └─ 위임 UserServiceImpl (비즈니스 로직)
//                             └─ UserDAO

// 이렇게 하면 각 클래스가 '바뀌는 이유'가 하나씩만 남음 (SRP)
// - 업무 규칙 변경 -> UserServiceImpl만
// - 트랜잭션 방식 변경 -> UserServiceTx(또는 transactionManager 빈)만
// 또한 비즈니스 로직을 트랜잭션/DB 없이 단독으로 테스트하기도 쉬워짐

public class Start {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(DaoFactory.class);
        UserService userService = context.getBean("userService", UserService.class);

        userService.upgradeLevels();
    }
}
