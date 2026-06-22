package com.example.spring.solid;

public class Ocp {
    public interface DiscountPolicy {
        int discount(int price);
    }

    public static class BasicDiscount implements DiscountPolicy {
        public int discount(int price) {
            return price;
        }
    }

    public static class GoldDiscount implements DiscountPolicy {
        public int discount(int price) {
            return price * 90 / 100;
        }
    }

    public static class VipDiscount implements DiscountPolicy {
        public int discount(int price) {
            return price * 80 / 100;
        }
    }
}
