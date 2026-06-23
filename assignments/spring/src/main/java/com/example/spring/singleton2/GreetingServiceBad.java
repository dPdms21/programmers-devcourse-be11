package com.example.spring.singleton2;

public class GreetingServiceBad {
    private static final GreetingServiceBad INSTANCE = new GreetingServiceBad();

    private GreetingServiceBad() {

    }

    public static GreetingServiceBad getInstance() {
        return INSTANCE;
    }

    private String name;

    public String greet(String reqName) {
        this.name = reqName;

        try {
            Thread.sleep(5);
        }
        catch (InterruptedException e) {

        }

        return this.name;
    }
}
