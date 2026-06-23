package com.example.spring.singleton;

public class TicketMachine {
    private static final TicketMachine instance = new TicketMachine();
    private int lastNumber = 0;

    private TicketMachine() {

    }

    public static TicketMachine getInstance() {
        return instance;
    }

    int issue() {
        lastNumber++;
        return lastNumber;
    }
}
