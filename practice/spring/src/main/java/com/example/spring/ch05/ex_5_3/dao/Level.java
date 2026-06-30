package com.example.spring.ch05.ex_5_3.dao;

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

    // 다음 단계 레벨을 돌려줌. 최고 등급인 GOLD는 현재 등급을 그대로 반환
    // - '레벨 순서(BASIC->SILVER->GOLD)'라는 규칙을 Level 자신이 갖게 해서,
    //   업그레이드 로직이 곳곳에 흩어지지 않도록 함 (레벨 변경 규칙의 책임을 Level에 둠)
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
