package com.example.spring.ch04;

// * 예외 처리 전략 3가지
// '모든 예외는 복구되든지, 아니면 분명히 통보되어야 한다'는 원칙을 실제로 지키는 방법은
// 1) 예외 복구, 2) 예외 회피, 3) 예외 전환

import java.sql.SQLException;

public class Exception_03 {
    // 1) 예외 복구
    // - 예외 상황을 파악하고, 문제를 해결해서 '정상 흐름으로 되돌리는' 것
    // - 예외가 났어도 사용자/프로그램 입장에선 아무 문제 없이 작업이 끝난 것처럼 만듦
    // - 대표 예: 재시도(retry), 대체값/대체경로(fallback)
    //   주의) 단순히 catch로 잡고 무시하는 '예외 블랙홀'은 복구가 아님. 정상 상태로 되돌려야 복구
    String 예외복구_재시도() {
        int maxRetry = 3;

        for (int attempt = 0; attempt < maxRetry; attempt++) {

            try {
                return fetchFromNetwork();
            } catch (SQLException e) {
                // 일시적 오류일 수 있으니, 정해진 횟수만큼 다시 시도
                System.out.println((attempt + 1) + "번째 시도 실패, 재시도");
            }
        }

        // 정해진 횟수를 모두 실패했다면, 더는 무시하지 말고 분명히 통보 (중단)
        throw new RuntimeException("재시도 " + maxRetry + "회 모두 실패");
    }

    // 2) 예외 회피
    // - 자신이 처리하지 않고, 자신을 호출한 쪽으로 예외를 '넘기는' 것
    // - 두 가지 방식: (a) throws로 그대로 던지거나, (b) catch한 뒤 다시 던짐
    // - 단, 아무 생각 없이 던지는 회피는 Exception_01의 '무책임한 throws' 안티패턴이 됨
    //   -> "이 예외는 나보다 호출한 쪽이 처리하는 게 맞다"는 분명한 이유가 있을 때만 회피

    // (a) throws로 그대로 넘김. 처리 책임을 호출자에게 위임
    void 예외회피_throws() throws SQLException {
        fetchFromNetwork();
    }

    // (b) 잡아서 로그 등 부가 작업만 하고, 처리 자체는 다시 던져 호출자에게 맡김
    void 예외회피_다시던지기() throws SQLException {
        try {
            fetchFromNetwork();
        } catch (SQLException e) {
            System.out.println("회피: 여기서는 처리하지 않고 호출자에게 넘김");
            throw e;   // 원래 예외를 그대로 다시 던짐
        }
    }

    // 3) 예외 전환
    // - 잡은 예외를 그대로 넘기지 않고, '더 적절한 예외로 바꿔서' 던지는 것
    // - 반드시 원인 예외(e)를 담아서 던져야 함 (원인 정보/스택트레이스 보존). 이를 '중첩 예외'라 함

    // 목적 (A) 더 의미 있는 예외로 바꾸기
    void 예외전환_의미부여(String id) {
        try {
            insertUser(id);
        } catch (SQLException e) {
            if (isDuplicateKey(e)) {
                // 호출하는 쪽은 'SQL오류'가 아니라, '아이디 중복'이라는 의미를 받게 됨
                throw new DuplicateUserIdException(id, e);
            }

            // 중복이 아닌 다른 SQL 오류라면 아래 (B)처럼 런타임으로 전환해 던짐
            throw new RuntimeException("회원 저장 중 DB 오류", e);
        }
    }

    // 목적 (B) 불필요한 체크 예외를 런타임(언체크) 예외로 포장하기
    // - 어차피 복구할 수 없는 체크 예외라면, throws로 계속 떠넘기게 만들지 말고
    //   런타임 예외로 감싸 던져서 호출하는 코드가 깨끗해지게 함
    // - 스프링이 SQLException을 DataAccessException(런타임)으로 바꿔주는 것이 바로 이 방식
    void 예외전환_런타임포장() {
        try {
            fetchFromNetwork();
        } catch (SQLException e) {
            throw new RuntimeException("작업 중 DB 오류가 발생 ", e);
        }
    }

    // ========== 설명 보조 코드 ==========
    String fetchFromNetwork() throws SQLException {
        throw new SQLException("일시적 연결 오류");
    }

    void insertUser(String id) throws SQLException {
        throw new SQLException("Duplicate entry", "23000"); // SQLState 23000 = 무결성 제약 위반
    }

    boolean isDuplicateKey(SQLException e) {
        return "23000".equals(e.getSQLState());
    }

    // 업무적 의미를 가진 사용자 정의 예외(언체크). 원인 예외를 담을 수 있게 만듦
    static class DuplicateUserIdException extends RuntimeException {
        DuplicateUserIdException(String id, Throwable cause) {
            super("이미 존재하는 아이디: " + id, cause); // cause로 원인 보존
        }
    }

    public static void main(String[] args) {

    }
}
