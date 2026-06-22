package part3;

// * 스레드의 동기화

// * 왜 동기화가 필요할까?
// - 스레드가 1개면 자원을 혼자 쓰니 문제가 없음
// - 스레드가 여러 개면 같은 자원을 함께 쓰는데, 동시에 건드리면 값이 꼬일 수 있음

// - 임계 영역(critical section): 한 번에 한 스레드만 들어가야하는 코드 영역
// - 잠금(lock): 그 구역에 들어가려면 받아야하는 "열쇠"
// -> 열쇠를 가진 한 스레드만 들어가고, 일을 끝내고 열쇠를 반납해야
//    다음 스레드가 들어갈 수 있음

// 이렇게 한 스레드의 작업을 다른 스레드가 방해하지 못하게 막는 것을 '동기화'라고 함

// * 뮤텍스(상호 배제)
// "열쇠는 단 1개" -> 한 번에 오직 한 스레드만 임계 영역에 들어갈 수 있게 하는 잠금
// - 위에서 말한 lock(열쇠 하나, 화장실 하나)이 바로 뮤텍스 개념
// - 자바에서는 모든 객체가 lock(= 모니터)을 하나씩 가지고 있고,
//   synchronized 가 이 lock을 잡았다/풀었다 해주는 역할
//   -> 즉 synchronized(this) = "이 객체의 뮤텍스를 잡는다" 와 같은 뜻
// - 참고: 뮤텍스는 열쇠가 1개(한 명만 입장), 세마포어(Semaphore)는 열쇠가 N개(N명 입장)로
//         여러 스레드의 동시 접근 수를 조절할 때 씀 (뮤텍스는 세마포어의 특수한 경우)

// syncronizes 사용법 2가지
// 1) 메서드 전체에 걸기: 메서드가 끝날 때까지 그 객체의 lock을 잡음
// public syncronized void calcCum() { ... }
// 2) 일부 블럭에만 걸기
// synchronized(객체 참조변수) { ... 임계 영역 ... }

// -> lock을 걸고 있으면 다른 스레드는 기다려야 해서 느려짐
// 그래서 메서드 전체보다 '필요한 부분만' 동기화 블럭을 거는 게 더 효율적

class ThreadEx_account {
    private int balance = 1000;

    public int getBalance() {
        return balance;
    }

    //출금
    // 동기화 X
//    public void withdraw(int money) {
//        if (balance >= money) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            balance -= money;
//        }
//    }

    // 동기화 O
    public void withdraw(int money) {
        synchronized (this) {
            // 코드 작성
            if (balance >= money) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                balance -= money;
            }
        }
    }
}

class Thread_bank implements Runnable {
    private ThreadEx_account account = new ThreadEx_account();

    @Override
    public void run() {
        while (account.getBalance() > 0) { // 잔고가 남아 있는 동안 반복
            // 100, 200, 300 중 임의로 선택해서 출금
            int money = (int) (Math.random() * 3 + 1) * 100;
            account.withdraw(money);
            System.out.println("balance: " + account.getBalance());
        }
    }
}

public class C_thread_5_sync {
    public static void main(String[] args) {
        Runnable r = new Thread_bank();

        new Thread(r).start(); // 출금 스레드1
        new Thread(r).start(); // 출금 스레드2
    }
}
