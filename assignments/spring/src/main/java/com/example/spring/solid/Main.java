package com.example.spring.solid;

public class Main {
    public static void main(String[] args) {
        // Srp
        System.out.println("===== SRP: 단일 책임 =====");
        Srp.Journal journal = new Srp.Journal();
        journal.add("오늘은 자바를 배움");
        journal.add("SOLID는 어렵지만 재밌음");

        Srp.JournalSaver js = new Srp.JournalSaver();
        js.print(journal);

        // Ocp
        System.out.println("===== OCP: 개방-폐쇄 =====");
        Ocp.DiscountPolicy[] dp = {new Ocp.BasicDiscount(), new Ocp.SilverDiscount(), new Ocp.GoldDiscount(), new Ocp.VipDiscount()};
        String[] grades = {"일반", "실버", "골드", "VIP"};

        for (int i=0; i<grades.length; i++){
            System.out.println(grades[i] + " 회원 -> " + dp[i].discount(10000) + "원");
        }

        // Lsp
        System.out.println("\n===== LSP: 리스코프 치환 =====");
        Lsp.Sparrow sparrow = new Lsp.Sparrow();
        Lsp.Penguin penguin = new Lsp.Penguin();
        Lsp.Bird[] birds = {sparrow, penguin};

        for (Lsp.Bird b : birds) {
            b.eat();
        }

        sparrow.fly();
        penguin.swim();

        // Isp
        System.out.println("\n===== ISP: 인터페이스 분리 =====");
        Isp.Printer sp = new Isp.SimplePrinter();
        sp.print();

        Isp.SmartMachine sm = new Isp.SmartMachine();
        sm.print();
        sm.scan();

        Isp.AllInOneMachine allInOne = new Isp.AllInOneMachine();

        allInOne.print();
        allInOne.scan();
        allInOne.fax();

        // Dip
        System.out.println("\n===== DIP: 의존관계 역전 =====");
        new Dip.NotificationService(new Dip.EmailSender()).notifyUser("주문 완료");
        new Dip.NotificationService(new Dip.SmsSender()).notifyUser("주문 완료");

        Dip.MockSender ms = new Dip.MockSender();
        Dip.NotificationService service = new Dip.NotificationService(ms);

        service.notifyUser("테스트 메시지");

        System.out.println(ms.getMsg());
    }
}
