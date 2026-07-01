package com.example.spring.ch06.ex_6_1;

// * AOP(Aspect Oriented Programming, 관점지향 프로그래밍)
// 여러 모듈에서 '공통적으로 흩어져 있는 부가기능'(예: 트랜잭션, 로깅, 보안)을
// '핵심 비즈니스 로직'에서 떼어내, 한 곳에 모아 관리하는 프로그래밍 방법
// - 핵심 기능 : 그 객체가 진짜 해야 하는 일 (예: 레벨 업그레이드 규칙)
// - 부가 기능 : 여러 곳에서 똑같이 필요한 보조 기능 (예: 트랜잭션 경계)

// * 문제점
// 5장에서 트랜잭션이라는 '부가기 능'을 비즈니스 로직에서 분리함
// - UserServiceImpl : 핵심 기능
// - UserServiceTx : 부가 기능 -> 손으로 만든 프록시
// 프록시(proxy) : 핵심 기능을 감싸 부가기능을 더하는 중간 오브젝트
// 한계 : 부가기능 적용 서비스가 늘 때마다 UserServiceTx 같은 프록시 클래스를 '일일이' 만들어야 하고,
// 트랜잭션이 필요 없는 메서드까지 단순 위임 코드를 다 작성해야 함

// * 6장의 큰 흐름 (트랜잭션을 소재로 AOP에 도달)
//   - ex_6_1 손수 만든 프록시 -> 다이내믹 프록시
//   - ex_6_2 스프링의 프록시 팩토리 빈
//   - ex_6_3 스프링 AOP (자동 프록시 생성)
//   - ex_6_4 @Transactional (선언적 트랜잭션)

// '다이나믹 프록시'
// 위 한계를 풀려고, 프록시를 런타임에 '자동 생성' (java.lang.reflect.Proxy)
// - TransactionHandler (InvocationHandler):
//  프록시로 들어오는 '모든 메서드 호출'을 invoke() 하나로 받아, 패턴(upgrade*)에 맞으면 트랜잭션 적용
//  메서드마다 위임 코드를 쓰던 수고가 사라짐. 특정 서비스에 종속되지 않아 재사용 가능
//  다이내믹 프록시는 new로 못 만들고 Proxy.newProxyInstance(...)로만 생성되는데,
//  여기서는 DaoFactory의 userService() @Bean 메서드 안에서 직접 만들어 'userService' 빈으로 제공
// * 이 패키지의 활성 빈(userService)은 '다이내믹 프록시' 방식으로 배선돼 있음 (DaoFactory 참고)

// 호출 흐름:
//  클라이언트 -> (다이내믹 프록시) -> TransactionHandler.invoke()
//      -> upgrade* 면 트랜잭션 경계로 감싸 target 호출 / 아니면 그냥 위임
//      target = UserServiceImpl -> UserDAO

public class Start {
    public static void main(String[] args) {

    }
}
