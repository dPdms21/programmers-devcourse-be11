package part3;

// * 세마포어
// - 동시에 임계 영역에 들어갈 수 있는 스레드 "수(N)"를 제한하는 잠금 도구
// - 뮤텍스가 "열쇠 1개"라면, 세마포어는 "열쇠 N개"

// * 실제 사용
// - DB 커넥션 풀, 동시 다운로드 개수 제한 등 "자원이 한정"되어 있어
//   동시 사용 개수를 조절하고 싶을 때

// * 비유: 자리가 3개뿐인 주차장
// - 차(스레드)가 들어오려면 입구에서 "주차권"을 받아야 함 (acquire)
// - 주차권은 딱 3장뿐 -> 3대까지만 들어가고, 4번째 차는 빈자리가 날 때까지 기다림
// - 차가 나가면 주차권을 반납한다. (release) -> 기다리던 차가 들어올 수 있음

// * 핵심 메서드 (java.util.concurrent.Semaphore)
// - new Semaphore(N) : 동시에 N개까지 허용 (열쇠 N개)
// - acquire()        : 열쇠 받기 (없으면 생길 때까지 대기)
// - release()        : 열쇠 반납하기

import java.util.concurrent.Semaphore;

class Car implements Runnable {
    // 주차장 - 자리 3개
    private static final Semaphore parkingLot = new Semaphore(3);
    private String name;

    public Car(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + " : 주차 자리를 기다리는중...");
            parkingLot.acquire(); // 주차권 받기 -> 자리 없으면 대기

            System.out.println(name + " : 주차 완료! (남은 자리 : " + parkingLot.availablePermits() + ")");
            Thread.sleep(2000);

            System.out.println(name + " : 출차");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 주차권 반납 -> 기더리던 다음 차가 들어옴
            parkingLot.release();
        }
    }
}

public class C_thread_6_sem {
    public static void main(String[] args) {
        // 차 6대가 한꺼번에 주차를 시도
        for ( int i = 1; i <= 6; i++ ) {
            new Thread( new Car("Car-" + i) ).start();
        }
    }
}
