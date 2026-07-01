package com.example.spring.ch06.ex_6_4.dao;

public enum Level {
    BASIC(1),
    SILVER(2),
    GOLD(3);

    private final int value;

    Level(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Level valueOf(int value) {
        switch (value) {
            case 1: return BASIC;
            case 2: return SILVER;
            case 3: return GOLD;
            default:
                throw new IllegalArgumentException("Invalid value: " + value);
        }
    }

    public Level nextLevel() {
        switch (this) {
            case BASIC:
                return SILVER;
            case SILVER:
                return GOLD;
            default:
                return GOLD;
        }
    }
}
