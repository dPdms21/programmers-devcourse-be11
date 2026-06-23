package com.example.spring.ch01.ex_1_4.dao;

public class AccountDAO {
    private SimpleConnectionMaker simpleConnectionMaker;

    public AccountDAO(SimpleConnectionMaker simpleConnectionMaker) {
        this.simpleConnectionMaker = simpleConnectionMaker;
    }
}
