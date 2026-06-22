package part3;

// * 멀티 스레딩이란?
// - 하나의 프로그램 안에서 여러 작업을 동시에 돌리는 것

// * 장점
// - CPU를 놀리지 않고 더 효율적으로 사용 가능
// - 작업 중에도 사용자 입력에 바로바로 반응
// - 작업을 나눠 코드가 더 깔끔해짐

// * 주의할 점
// - 여러 스레드가 같은 자원을 함께 쓰다보면 문제가 생길 수 있음
// - 동기화, 교착상태 등...

// * 스레드를 만드는 2가지 방법
// 1) Thread 클래스를 상속받기
// 2)  Runnable 인터페이스를 구현하기
// -> 보통 2번을 더 많이 씀
// Thread를 상속하면 다른 클래스를 상속받을 수 없어서 제약이 생기기 때문

class C_thread_1_1 extends Thread{
    @Override
    public void run() {
        // 수행할 동작 정의
        for (int i=0; i<10; i++) {
            // getName(): Thread 클래스가 제공하는 메서드로 현재 실행 중인 스레드의 이름을 반환
            System.out.println("th1: " + i + " : " + getName());
        }
    }
}

class C_thread_1_2 implements Runnable{
    @Override
    public void run() {
        for (int i=0; i<10; i++) {
            // Thread.currentThread(): 현재 이 코드를 실행하고 있는 스레드의 Thread 객체를 반환하는 static 메서드
            // Runnable을 구현한 경우 이 클래스는 Thread를 상속하지 않으므로 바로 getName() 호출 불가
            System.out.println("th2: " + i + " : " + Thread.currentThread().getName());
        }
    }
}

public class C_thread_1 {
    public static void main(String[] args) {
        C_thread_1_1 th1 = new C_thread_1_1();
        th1.start();
        System.out.println("main 스레드");

        Runnable r = new C_thread_1_2();
        Thread th2 = new Thread(r);
        th2.start();

        // * 스레드는 "일회용"
        // - 한 번 실행하고 끝나면 그 스레드는 다시 못 씀
        // th1.start(); -> 불가

        th1 = new C_thread_1_1();
        th1.start();
    }
}
