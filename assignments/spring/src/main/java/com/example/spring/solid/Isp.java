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
        public void print() { System.out.println("구형 프린터: 인쇄만"); }
    }

    public static class SmartMachine implements Printer, Scanner {
        public void print() { System.out.println("복합기: 인쇄"); }
        public void scan() { System.out.println("복합기: 스캔"); }
    }
}
