package com.example.spring.solid;

public class Lsp {
    public static class Bird {
        void eat() {
            System.out.println("먹이 먹는중");
        }
    }

    public static class FlyingBird extends Bird {
        void fly() {
            System.out.println("하늘 나는중");
        }
    }

    public static class Sparrow extends FlyingBird {

    }

    public static class Penguin extends Bird {
        void swim() {
            System.out.println("헤엄 치는중");
        }
    }
}
