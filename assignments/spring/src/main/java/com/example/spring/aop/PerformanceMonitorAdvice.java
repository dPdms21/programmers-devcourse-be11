package com.example.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class PerformanceMonitorAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String name = invocation.getMethod().getDeclaringClass().getSimpleName()
                + "." + invocation.getMethod().getName();
        long start = System.nanoTime();

        try {
            return invocation.proceed();
        }
        finally {
            long ms = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("[PERF] %s : %dms%n", name, ms);
        }
    }
}
