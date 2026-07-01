package com.example.spring.aop;

import com.example.spring.aop.service.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AopConfig.class);

        OrderService orderService = ctx.getBean(OrderService.class);
        MemberService memberService = ctx.getBean(MemberService.class);
        ProductService productService = ctx.getBean(ProductService.class);

        System.out.println("\n===== 주문 서비스 호출 =====");
        System.out.println(orderService.placeOrder("기계식 키보드"));

        System.out.println("\n===== 회원 서비스 호출 =====");
        System.out.println(memberService.register("kim"));

        System.out.println("\n===== 상품 서비스 호출 =====");
        System.out.println(productService.getProduct("A-100"));

        System.out.println("\n===== 상품 서비스 예외 호출 =====");
        try {
            productService.getProduct("");
        } catch (IllegalArgumentException e) {
            System.out.println("호출 측에서 예외 처리");
        }

        System.out.println("\n===== 진짜 프록시인지 확인 =====");
        System.out.println("orderService의 실제 타입: " + orderService.getClass());

        ctx.close();
    }
}
