package com.example.spring.ch04;

// * 예외
// 예외가 발생하면 그것을 catch 블록을 써서 잡아내는 것까지는 좋은데,
// 그러고 아무것도 하지 않고 별문제 없는 것처럼 넘어가 버리는 건 정말 위험한 일
// 프로그램 실행 중에 어디선가 예외가 발생했는데 그것을 무시하고 계속 진행해버리기 때문
// 예외를 처리할 때 반드시 지켜야할 핵심 원칙은 한 가지
// 모든 예외는 적절하게 복구되든지 아니면 작업을 중단시키고 운영자 또는 개발자에게 분명하게 통보되어야 함

// * 예외의 종류 (자바에서 throw 할 수 있는 객체는 모두 Throwable의 자식)
// 1) Error
//   - java.lang.Error의 자식. 시스템 레벨의 심각한 오류 (OutOfMemoryError 등)
//   - 애플리케이션 코드가 잡아서 대응할 수 있는 게 아니므로 catch 하려 하지 않음
// 2) Checked Exception (체크 예외)
//   - Exception의 자식 중 RuntimeException을 '상속하지 않은' 것들(IOException, SQLException 등)
//   - 반드시 catch 하거나 throws로 선언해야 컴파일됨 (컴파일러가 강제)
//   - 그래서 잘못 쓰면 아래 '안티패턴'들이 나오기 쉬움
// 3) Unchecked Exception (언체크 예외, 런타임 예외)
//   - RuntimeException의 자식(NullPointerException, IllegalArgumentException 등)
//   - catch/throws를 강제하지 않음. 주로 '프로그램의 버그' 성격이라, 복구보다 코드 수정 대상

import java.sql.SQLException;

public class Exception_01 {
    // 안티패턴 1 : 예외블랙홀 - 아무것도 하지 않음
    // - 예외가 났다는 사실 자체가 흔적도 없이 사라짐. 프로그램은 아무 일 없다는 듯 계속 진행
    // - 가장 위험. 나중에 엉뚱한 곳에서 문제가 터지고, 원인 추적이 거의 불가능
    void 예외블랙홀() {
        try {
            riskyJob();
        } catch (SQLException e) {
            // 아무것도 안함
        }
    }

    // 안티패턴 2 : 찍기만하고 진행
    // - printStackTrace()나 println()으로 '찍는 것'은 예외를 '처리'한 것이 아님
    // - 운영 환경에선 그 출력이 묻혀서 아무도 못 보고, 프로그램은 잘못된 상태로 계속 동작
    void 찍기만하고진행() {
        try {
            riskyJob();
        } catch (SQLException e) {
            e.printStackTrace();                    // 찍기만함
            System.out.println(e.getMessage());     // 찍기만함
        }
    }

    // 안티패턴 3 : 무의미하고 무책임한 throws
    // - 어떤 예외가 왜 날 수 있는지 고민하지 않고, 그냥 throws Exception으로 전부 떠넘김
    // - 이 메서드를 쓰는 쪽도 의미 있는 정보를 못 받고 똑같이 throws Exception으로 떠넘기게 됨
    void 무책임throws() throws Exception {
        method1();
    }

    void method1() throws Exception {
        method2();
    }

    void method2() throws Exception {
        throw new Exception();
    }

    void riskyJob() throws SQLException {
        throw new SQLException("데이터베이스 연결실패");
    }

    public static void main(String[] args) {

    }
}
