package com.example.spring.strategypattern;

public class User {
    private String id;
    private String name;

    User(String id,String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
