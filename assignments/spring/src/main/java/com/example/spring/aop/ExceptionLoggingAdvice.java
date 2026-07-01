package com.example.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class ExceptionLoggingAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        }
        catch (Exception e) {
            String name = invocation.getMethod().getDeclaringClass().getSimpleName()
                    + "." + invocation.getMethod().getName();

            System.out.printf("[ERROR] %s : %s%n", name, e.getMessage());

            throw e;
        }
    }
}
