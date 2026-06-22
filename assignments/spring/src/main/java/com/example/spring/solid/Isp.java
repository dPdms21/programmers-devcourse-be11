package com.example.spring.solid;

public class Isp {
    public interface Printer {
        public void print();
    }

    public interface Scanner {
        public void scan();
    }

    public interface Faxer {
        public void fax();
    }

    public static class SimplePrinter implements Printer {
        public void print() {
            System.out.println("구형 프린터: 인쇄만");
        }
    }

    public static class SmartMachine implements Printer, Scanner {
        public void print() {
            System.out.println("복합기: 인쇄");
        }

        public void scan() {
            System.out.println("복합기: 스캔");
        }
    }

    public static class AllInOneMachine implements Printer, Scanner, Faxer {
        public void print() {
            System.out.println("올인원 기기: 인쇄");
        }

        public void scan() {
            System.out.println("올인원 기기: 스캔");
        }

        public void fax() {
            System.out.println("올인원 기기: 팩스");
        }
    }
}
