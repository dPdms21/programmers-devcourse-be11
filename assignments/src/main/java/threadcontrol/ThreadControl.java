package threadcontrol;

import java.util.*;

class PrintDash extends Thread {
    public void run() {
        for (int i=0; i<300; i++) {
            System.out.print("-");
        }
    }
}

class PrintBar extends Thread {
    public void run() {
        for (int i=0; i<300; i++) {
            System.out.print("|");
        }
    }
}

class SleepThread extends Thread {
    public void run() {
        for (int i=0; i<300; i++) {
            System.out.print("-");
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("\n인터럽트로 종료");
            return;
        }

        System.out.println("종료");
    }
}

class CountThread extends Thread {
    public void run() {
        int i=10;

        while (i != 0 && !isInterrupted()) {
            System.out.println(i--);

            for (long x=0; x<2_500_000_000L; x++) ;
        }

        System.out.println("카운트 종료");
    }
}

class CountSleepThread extends Thread {
    public void run() {
        int i=10;

        while (i != 0 && !isInterrupted()) {
            System.out.println(i--);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("자다 깨어남 (InterruptedException)");
                break;
            }
        }

        System.out.println("카운트 종료");
    }
}

class YieldThread extends Thread {
    private String name;

    public YieldThread(String name) {
        this.name = name;
    }

    public void run() {
        for (int i=0; i<5; i++) {
            System.out.println(name + " 실행 중. 반복 " + i);
            Thread.yield();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

class ManyPrintThread extends Thread {
    private final char c;

    public ManyPrintThread(char c) {
        this.c = c;
    }

    public void run() {
        for (int i = 0; i < 1000; i++) {
            System.out.print(c);
        }
    }
}

class InterruptCheckThread extends Thread {
    @Override
    public void run() {
        while (!isInterrupted()) {
            // interrupt()가 호출될 때까지 실행
        }

        System.out.println("isInterrupted() 1: " + isInterrupted());
        System.out.println("isInterrupted() 2: " + isInterrupted());

        System.out.println("interrupted() 1: " + Thread.interrupted());
        System.out.println("interrupted() 2: " + Thread.interrupted());
    }
}

class DaemonThread extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println("데몬 스레드 실행 중");

            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                return;
            }
        }
    }
}

class RunnableTask implements Runnable {
    private final char c;

    public RunnableTask(char c) {
        this.c = c;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            System.out.print(c);
        }
    }
}

public class ThreadControl {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1. 스레드 동시 실행
//        PrintDash dash = new PrintDash();
//        PrintBar bar = new PrintBar();
//
//        dash.start();
//        bar.start();


        // 2. sleep()
//        SleepThread sleepThread = new SleepThread();
//
//        System.out.println("생성 직후: " + sleepThread.getState());
//
//        sleepThread.start();
//        System.out.println("시작 직후: " + sleepThread.getState());
//
//        try {
//            Thread.sleep(100);
//            System.out.println("sleep 중: " + sleepThread.getState());
//
//            sleepThread.join();
//        } catch (InterruptedException e) {
//            return;
//        }
//
//        System.out.println("종료 후: " + sleepThread.getState());


        // 3. 실행 중인 스레드 interrupt()
//        CountThread countThread = new CountThread();
//
//        countThread.start();
//
//        System.out.println("Enter를 누르면 interrupt()");
//        sc.nextLine();
//
//        countThread.interrupt();


        // 4. sleep 중인 스레드 interrupt()
//        CountSleepThread countSleepThread = new CountSleepThread();
//
//        countSleepThread.start();
//
//        System.out.println("Enter를 누르면 interrupt()");
//        sc.nextLine();
//
//        countSleepThread.interrupt();


        // 5. yield()
//        YieldThread yieldThread1 = new YieldThread("스레드1");
//        YieldThread yieldThread2 = new YieldThread("스레드2");
//
//        yieldThread1.start();
//        yieldThread2.start();


        // 6. join()
//        ManyPrintThread printThread1 = new ManyPrintThread('-');
//        ManyPrintThread printThread2 = new ManyPrintThread('|');
//
//        long start = System.currentTimeMillis();
//
//        printThread1.start();
//        printThread2.start();

//        try {
//            printThread1.join();
//            printThread2.join();
//        }
//        catch (InterruptedException e) {
//            return;
//        }
//
//        long end = System.currentTimeMillis();
//
//        System.out.println();
//        System.out.println("소요 시간: " + (end - start) + "ms");

        // 도전 과제 2. isInterrupted()와 interrupted() 비교
//        InterruptCheckThread thread = new InterruptCheckThread();
//
//        thread.start();
//
//        try {
//            Thread.sleep(100);
//        }
//        catch (InterruptedException e) {
//            return;
//        }
//
//        thread.interrupt();

        // 도전 과제 3. join(시간)
//        ManyPrintThread printThread = new ManyPrintThread('-');
//
//        printThread.start();
//
//        try {
//            printThread.join(10);
//        }
//        catch (InterruptedException e) {
//            return;
//        }
//
//        System.out.println();
//        System.out.println("join(10) 이후 상태: " + printThread.getState());
//        System.out.println("main 실행 계속");

        // 도전 과제 4. 데몬 스레드
//        DaemonThread daemonThread = new DaemonThread();
//
//        daemonThread.setDaemon(true);
//        daemonThread.start();
//
//        try {
//            Thread.sleep(2000);
//        }
//        catch (InterruptedException e) {
//            return;
//        }
//
//        System.out.println("main 종료");

        // 도전 과제 5. 올바른 인터럽트 처리
//        SleepThread sleepThread = new SleepThread();
//
//        sleepThread.start();
//
//        try {
//            Thread.sleep(100);
//        }
//        catch (InterruptedException e) {
//            return;
//        }
//
//        sleepThread.interrupt();

        // 도전 과제 6. Runnable
        RunnableTask task1 = new RunnableTask('-');
        RunnableTask task2 = new RunnableTask('|');

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        }
        catch (InterruptedException e) {
            return;
        }

        System.out.println();
        System.out.println("Runnable 방식 실행 완료");
    }
}
