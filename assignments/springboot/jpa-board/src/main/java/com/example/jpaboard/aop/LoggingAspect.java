package com.example.jpaboard.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(* com.example.jpaboard.controller..*(..))")
    public void controllerLayer() {

    }

    @Around("controllerLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String name = pjp.getSignature().toShortString();
        long start = System.currentTimeMillis();
        System.out.println("===> 시작: " + name);

        try {
            Object result = pjp.proceed();

            return result;
        }
        finally {
            long end = System.currentTimeMillis() - start;
            System.out.println("<=== 종료: " + name + " (" + end + "ms)");
        }
    }
}
