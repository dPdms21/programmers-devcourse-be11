package com.example.spring.ioc;

public class CoffeeMaker {
    private final Bean bean;
    private final MilkFrother milkFrother;

    CoffeeMaker(Bean bean, MilkFrother milkFrother) {
        this.bean = bean;
        this.milkFrother = milkFrother;
    }

    void brew() {
        System.out.println(bean.name() + "로 커피를 내리는 중");
    }

    void addMilkFroth() {
        System.out.println(milkFrother.froth() + "추가!");
    }
}
