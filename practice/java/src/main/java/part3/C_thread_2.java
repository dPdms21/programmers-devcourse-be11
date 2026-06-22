package part3;

// * start()와 run()의 차이

// run()을 직접 부르면?
// - 그냥 평범한 메서드를 호출한 것. 즉 새로운 스레드가 생산되지 않음
// - 지금 실행 중인 스레드가 run()안의 코드를 그대로 실행

// start()를 부르면?
// - 새로운 스레드를 만들고, 그 스레드가 run()을 대신 실행해줌

// * 참고 (스레드의 라이프사이클)
// - 스레드마다 자기만의 작업 공간(호출스택)을 가짐. 스레드가 끝나면 그 공간도 사라짐
// - 스레드가 여러 개면, 누가 언제 얼마나 실행될지는 스케줄러가 정함
//   (내 차례가 오면 실행, 시간이 끝나면 다시 대기)
// - main 메서드도 사실은 하나의 스레드(main 스레드)
// - main 이 먼저 끝나도, 다른 스레드가 아직 작업 중이면 프로그램은 끝나지 않음

class C_thread_2_1 extends Thread {
    @Override
    public void run() {
        System.out.println("th2_1 run()~!");
    }

    public void throwException() {
        try {
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class C_thread_2 {
    public static void exam1() {
        C_thread_2_1 t2_1 = new C_thread_2_1();
        t2_1.start();
    }

    public static void exam2() {
        C_thread_2_1 t2_1 = new C_thread_2_1();
        t2_1.run();
    }

    public static void main(String[] args) {
        exam2();
    }
}
