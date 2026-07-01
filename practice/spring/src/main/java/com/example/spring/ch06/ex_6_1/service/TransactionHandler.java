package com.example.spring.ch06.ex_6_1.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// * TransactionHandler - 다이나믹 프록시의 '부가기능' 담당
// - InvocationHandler
// 다이나믹 프록시에 "메서드 호출이 들어오면 무엇을 할지"를 정의해주는 인터페이스
// 메서드 invoke() 하나뿐. 프록시로 들어오는 '모든' 메서드 호출이 이 invoke()로 모임

// - 리플렉션(Reflection)
// 리플렉션은 '프로그램이 실행 중(런타임)에, 자기 자신의 클래스/메서드/필드 정보를 들여다보고 다룰 수 있게 해주는 기능'
//   - 보통 컴파일 시점에 정해진 대로 호출:  userService.upgradeLevels();
//   - 리플렉션은 '메서드를 값(객체)처럼' 다룸. java.lang.reflect.Method 객체를 받아서
//     실행 중에 method.invoke(대상, 인자)로 호출 -> 어떤 메서드인지 미리 몰라도 호출 가능
//   다이내믹 프록시가 바로 이 리플렉션 위에서 동작

public class TransactionHandler implements InvocationHandler {
    // 부가기능을 적용할 실제 오브젝트 (ex: UserServiceImpl)
    private Object target;
    // 트랜잭션 추상화
    private PlatformTransactionManager transactionManager;
    // 이 이름으로 '시작'하는 메서드에만 트랜잭션 적용
    private String pattern;

    public TransactionHandler(Object target, PlatformTransactionManager transactionManager, String pattern) {
        this.target = target;
        this.transactionManager = transactionManager;
        this.pattern = pattern;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 호출된 메서드 '이름'이 패턴으로 시작하면 트랜잭션으로 감쌈
        // 예) pattern = "upgrade"이면 upgradeLevels()는 매칭, add()는 비매칭
        if(method.getName().startsWith(pattern)) {
            // 트랜잭션 경계 설정
            return invokeTransaction(method, args);
        }

        // 패턴에 안 맞는 메서드는 부가기능 없이 target에게 그냥 위임
        // method.invoke(target, args) = "target의 이 메서드를, 이 인자들로 실행" (리플렉션 호출)
        return method.invoke(target, args);
    }

    private Object invokeTransaction(Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object invoke = method.invoke(target, args);
            transactionManager.commit(status);

            return invoke;
        }
        catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
