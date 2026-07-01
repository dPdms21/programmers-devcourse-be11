package com.example.spring.ch06.ex_6_2;

import com.example.spring.ch06.ex_6_2.dao.DaoFactory;
import com.example.spring.ch06.ex_6_2.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// * 스프링의 프록시 팩토리 빈
// * 문제점
// 다이나믹 프록시를 직접 다뤘음
// DaoFactory.userService() bean안에서 Proxy.newProxyInstance를 직접 호출해서 프록시를 얻었고,
// TransactionHandler도 직접 작성해야 함
// 게다가 '어떤 메서드에 적용할지(패턴)'도 핸들러 안에 섞여 있음
//   구체적으로 TransactionHandler(ex_6_1)의 두 부분이 그럼:
//     (1) private String pattern;                          // '어디에 적용할지' 정보를 핸들러가 직접 보유
//     (2) if (method.getName().startsWith(pattern)) { ... } // invoke() 안에서 적용 대상을 직접 판단
// 즉 '무엇을 할지(트랜잭션)'와 '어디에 걸지'가 한 클래스에 엉켜 있었음

// 스프링의 'ProxyFactoryBean'
//   Advisor = Pointcut(어디에) + Advice(무엇을) 로 분리
//     - startsWith(pattern) 같은 선정 로직 -> Pointcut 객체로 분리
//     - 트랜잭션 거는 로직            -> Advice 객체로 분리
//   target과 advisor만 등록하면 스프링이 프록시를 알아서 만들어주고, 둘을 독립적으로 갈아끼우고 조합할 수 있음

// * 스프링 AOP의 구성 개념
// - Advice   (무엇을) : 부가기능 자체 (예: TransactionAdvice)
// - Pointcut (어디에) : 부가기능을 적용할 메서드 선별 (예: NameMatchMethodPointcut("upgrade*"))
// - Advisor           : Advice + Pointcut 묶음 (예: DefaultPointcutAdvisor)
// - ProxyFactoryBean  : target + advisor를 받아 '프록시'를 생산하는 스프링 팩토리 빈

// => 부가기능(Advice)과 적용대상(Pointcut)이 '분리'되어 각각 독립적으로 재사용됨
//  - 같은 트랜잭션 Advice를, 다른 Pointcut으로 다른 메서드들에 적용할 수 있고
//  - 같은 Pointcut에 다른 Advice(로깅 등)를 얹을 수도 있음

// 호출 흐름:
//  클라이언트 -> (스프링이 만든 프록시) -> Advisor가 Pointcut으로 매칭 판단
//      -> 매칭되면 TransactionAdvice.invoke() -> invocation.proceed()로 target 실행
//      target = UserServiceImpl -> UserDAO

public class Start {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserService userService = context.getBean("userService", UserService.class);
        userService.upgradeLevels(); // Pointcut("upgrade*") -> TransactionAdvice 적용
//        userService.add(...); // 매칭 안됨 -> 트랜잭션 없이 그대로 진행
    }
}
