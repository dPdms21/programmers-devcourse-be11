package com.example.spring.ioc;

public class CoffeeContainer {
    CoffeeMaker getCoffeeMaker() {
        Bean bean = new ColombiaBean();

        return new CoffeeMaker(bean);
    }
}
