package part3;

// * 데몬 스레드
// - 메인 작업을 "뒤에서 도와주는" 보조 스레드
// - 일반 스레드가 전부 끝나면, 데몬 스레드도 같이 종료

// * 실제
// - 가비지 컬렉터(GC), word의 자동저장...

public class C_thread_4_daemon implements Runnable {
    static boolean autoSave = true;

    // 3초마다 한 번씩 "자동 저장이 켜져 있으면" 저장을 실행
    // 데몬 스레드라서 main이 끝나면 함께 자동 저장
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(3 * 1000);

                if (autoSave){
                    autoSave();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void autoSave() {
        System.out.println("작업 파일 자동 저장");
    }

    public static void main(String[] args) {
        Thread th = new Thread(new C_thread_4_daemon());
        th.setDaemon(true);
        th.start();

        for (int i=0; i<10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println(i);

            if (i == 5) {
                autoSave = true;
            }
        }

        System.out.println("프로그램 종료");
    }
}
