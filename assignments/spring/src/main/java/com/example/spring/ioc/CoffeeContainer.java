package com.example.spring.ioc;

public class CoffeeContainer {
    CoffeeMaker getCoffeeMaker(String type) {
        Bean bean;

        if (type.equals("colombia")) {
            bean = new ColombiaBean();
        }
        else if (type.equals("ethiopia")) {
            bean = new EthiopiaBean();
        } else {
            throw new IllegalArgumentException("지원하지 않는 원두: " + type);
        }

        MilkFrother milkFrother = new BasicMilkFrother();

        return new CoffeeMaker(bean, milkFrother);
    }
}
