package part3;

// * 예외처리
// 프로그램 실행 중에 발생할 수 있는 오류(예외)를 처리하여, 프로그램이 갑자기 멈추지 않도록 하는 방법
// 예외가 발생해도 프로그램이 정상적으로 흐름을 이어가거나, 안전하게 마무리할 수 있게 해줌

// * 예외(Exception)의 종류
// - Checked Exception: 컴파일 시점에 처리(try-catch 또는 throws)를 강제하는 예외. (예: IOException, FileNotFoundException)
// - Unchecked Exception: 실행 중에 발생하며 처리를 강제하지 않는 예외. (RuntimeException 계열, 예: NullPointerException, ArithmeticException)

// * 기본 구조
// - try: 예외가 발생할 수 있는 코드를 작성하는 블록
// - catch: try에서 예외가 발생하면 그 예외를 잡아 처리하는 블록
// - finally: 예외 발생 여부와 상관없이 항상 실행되는 블록. (주로 자원 정리에 사용)

import java.io.FileInputStream;
import java.io.IOException;

public class B_try_catch {
    // 1. 기본 try-catch
    public static void exam1() {
        try {
            int a = 10;
            int b = 0;
            int result = a / b;
            System.out.println(result);
        } catch (ArithmeticException e) {
            // 발생한 예외를 e로 받아 처리
            System.out.println("예외 발생: 0으로 나눌 수 없음");
            System.out.println("예외 메시지: " + e.getMessage());
        }

        System.out.println("프로그램은 계속 실행");
    }

    // 2. 멀티 catch
    public static void exam2() {
        try {
            int[] arr = new int[5];
            arr[9] = 10;
        } catch (ArithmeticException e) {
            System.out.println("산술 예외 처리");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("배열 인덱스 예외 처리: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("그 외 예외처리: " + e.getMessage());
        }
    }

    // 2.2 멀티 catch
    // - 처리 방식이 같은 여러 예외는 |(파이프)로 묶어 하나의 catch에서 처리할 수 있음. (Java 7 이상)
    public static void exam2_2() {
        try {
            String text = null;
            System.out.println(text.length());
        } catch (NullPointerException | ArithmeticException e) {
            System.out.println("Null 이거나 산술 예외 처리 발생: " + e.getMessage());
        }
    }

    // 3. finally
    // - finally 블록은 예외가 발생하든 안 하든, try/catch가 끝나면 "항상" 실행
    // - 파일 닫기, 연결 해제 등 반드시 처리해야 하는 마무리 작업에 사용
    public static void exam3() {
        try {
            System.out.println("try 블록 실행");
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("catch 블록 실행");
        } finally {
            // 예외가 발생해도, 발생하지 않아도 이 블록은 무조건 실행
            System.out.println("finally 항상 실행");
        }
    }

    // 4. throw / throws
    // 19세 체크
    private void checkAge(int age) {
        if (age < 19) {
            throw new IllegalArgumentException("미성년자는 가입 불가: " + age);
        }
        System.out.println("가입이 가능한 나이");
    }

    public static void exam4() {
        try {
            B_try_catch tryCatch = new B_try_catch();
            tryCatch.checkAge(18);
        } catch (IllegalArgumentException e) {
            System.out.println("나이 검증 실패: " + e.getMessage());
        }
    }

    // 5. try-with-resources
    // - try( ... ) 괄호 안에서 자원(스트림 등)을 선언하면, try 블록이 끝날 때 자동으로 close()가 호출
    // - AutoCloseable 인터페이스를 구현한 객체만 사용할 수 있음 (대부분의 입출력 스트림이 해당)
    // - finally에서 직접 close()를 호출하지 않아도 되므로 코드가 간결하고, 자원 해제 누락을 막을 수 있음
    public static void exam5() {
        try (FileInputStream fis = new FileInputStream("test.txt")) {
            int data = fis.read();
            System.out.println("읽은 데이터: " + data);
        } catch (IOException e) {
            System.out.println("파일 처리 중 예외 발생: " + e.getMessage());
        }
        // 위 try가 끝나면 (정상이든 예외든) fis.close()가 자동 호출
    }

    // 5.2
    public static void exam5_2() {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream("test.txt");
            int data = fis.read();
            System.out.println("읽은 데이터: " + data);
        } catch (IOException e) {
            System.out.println("파일 처리 중 예외 발생: " + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        exam4();
    }
}
