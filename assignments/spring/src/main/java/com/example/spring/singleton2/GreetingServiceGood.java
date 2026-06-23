package com.example.spring.singleton2;

public class GreetingServiceGood {
    private static final GreetingServiceGood INSTANCE = new GreetingServiceGood();

    private GreetingServiceGood() {

    }

    public static GreetingServiceGood getInstance() {
        return INSTANCE;
    }

    String greet(String reqName) {
        try {
            Thread.sleep(5);
        }
        catch (InterruptedException e) {

        }

        return reqName;
    }
}
