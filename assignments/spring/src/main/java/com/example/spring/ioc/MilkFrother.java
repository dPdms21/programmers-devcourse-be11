package com.example.spring.ioc;

public interface MilkFrother {
    String froth();
}

class BasicMilkFrother implements MilkFrother {
    public String froth() {
        return "우유 거품";
    }
}
