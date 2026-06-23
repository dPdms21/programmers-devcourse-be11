package com.example.spring.singleton;

public class NaiveTicketMachine {
    private int lastNumber = 0;

    int issue() {
        lastNumber++;
        return lastNumber;
    }
}
