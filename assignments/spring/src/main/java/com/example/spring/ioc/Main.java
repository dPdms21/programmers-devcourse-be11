package com.example.spring.ioc;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n===== 2. DI: 제어를 바깥(main)으로 =====");
        new CoffeeMaker(new ColombiaBean()).brew();
        new CoffeeMaker(new EthiopiaBean()).brew();


        System.out.println("\n===== 3. IoC 컨테이너: 조립까지 위임 =====");
        CoffeeContainer container = new CoffeeContainer();
        CoffeeMaker maker = container.getCoffeeMaker();
        maker.brew();

        System.out.println("\n===== 4. 헐리우드 원칙: 흐름의 역전 =====");
        Hollywood.run();
    }
}
