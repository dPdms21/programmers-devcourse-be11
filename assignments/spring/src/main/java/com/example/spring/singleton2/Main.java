package com.example.spring.singleton2;

public class Main {
    static int bad = 0;
    static int good = 0;

    public static void main(String[] args) throws InterruptedException {
        int N = 30;

        System.out.println("\n===== 같은 싱글톤을 30개 스레드가 동시에 사용 =====");
        Thread[] t1 = new Thread[N];

        for (int i=0; i<N; i++) {
            final String name = "손님" + (i+1);

            t1[i] = new Thread(() -> {
                String s = GreetingServiceBad.getInstance().greet(name);

                if (!s.equals(name)) {
                    synchronized (Main.class) {
                        bad++;
                    }
                }
            });
        }

        for (Thread t : t1) {
            t.start();
        }

        for (Thread t : t1) {
            t.join();
        }

        Thread[] t2 = new Thread[N];

        for (int i=0; i<N; i++) {
            final String name = "손님" + (i+1);

            t2[i] = new Thread(() -> {
                String s = GreetingServiceGood.getInstance().greet(name);

                if (!s.equals(name)) {
                    synchronized (Main.class) {
                        good++;
                    }
                }
            });
        }

        for (Thread t : t2) {
            t.start();
        }

        for (Thread t : t2) {
            t.join();
        }

        System.out.println("[필드에 저장] 엉킴: " + bad + " / " + N + "건");
        System.out.println("[파라미터로]  엉킴: " + good + " / " + N + "건");

        System.out.println("\n===== 필드에 둬도 되는 것: 다른 싱글톤 참조 =====");
        UserDAO d1 = UserDAO.getInstance();
        UserDAO d2 = UserDAO.getInstance();

        System.out.println(d1.findUser("kim"));
        System.out.println(d2.findUser("lee"));
        System.out.println("같은 DAO인가? " + (d1 == d2));
    }
}
