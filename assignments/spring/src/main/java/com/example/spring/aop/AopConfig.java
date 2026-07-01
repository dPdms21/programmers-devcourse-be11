package com.example.spring.aop;

import com.example.spring.aop.service.*;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {
    @Bean
    public DefaultAdvisorAutoProxyCreator autoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public Advisor performanceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.example.spring.aop.service..*.*(..))");

        return new DefaultPointcutAdvisor(pointcut, new PerformanceMonitorAdvice());
    }

    @Bean
    public Advisor exceptionLoggingAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.example.spring.aop.service..*.*(..))");

        return new DefaultPointcutAdvisor(pointcut, new ExceptionLoggingAdvice());
    }

    @Bean
    public OrderService orderService()  {
        return new OrderServiceImpl();
    }

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl();
    }

    @Bean
    public ProductService productService() {
        return new ProductServiceImpl();
    }
}
