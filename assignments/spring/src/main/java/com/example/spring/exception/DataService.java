package com.example.spring.exception;

import java.sql.SQLException;

public class DataService {
    private final FileLogger logger;

    DataService(FileLogger logger) {
        this.logger = logger;
    }

    String fetchWithRetry(FlakyService f) {
        int maxRetry = 3;
        SQLException lastException = null;

        for (int i=1; i<=maxRetry; i++) {
            try {
                String r = f.fetch();
                logger.log("INFO", i + "번째 시도 성공: " + r);

                return r;
            }
            catch (SQLException e) {
                lastException = e;
                logger.log("WARN", i + "번째 시도 실패: " + e.getMessage());

                if (i < maxRetry) {
                    long delay = i * 200L;

                    try {
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("재시도 대기 중 인터럽트 발생", ie);
                    }
                }
            }
        }

        logger.log("ERROR", "재시도 " + maxRetry + "회 모두 실패");
        throw new RuntimeException("재시도 " + maxRetry + "회 모두 실패", lastException);
    }

    void avoidByThrows(FlakyService f) throws SQLException {
        f.fetch();
    }

    void avoidByRethrow(FlakyService f) throws SQLException {
        try {
            f.fetch();
        }
        catch (SQLException e) {
            logger.log("WARN", "회피: 여기서 처리하지 않고 호출자에게 넘김 - " + e.getMessage());
            throw e;
        }
    }

    void registerUser(String id) {
        try {
            insertUser(id);
        }
        catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                logger.log("ERROR", "아이디 중복: " + id);
                throw new DuplicateUserIdException(id, e);
            }

            logger.log("ERROR", "회원 저장 중 DB 오류: " + id);
            throw new RuntimeException("회원 저장 중 DB 오류", e);
        }
    }

    void insertUser(String id) throws SQLException {
        throw new SQLException("Duplicate entry", "23000");
    }

    static class DuplicateUserIdException extends RuntimeException {
        DuplicateUserIdException(String id, Throwable cause) {
            super("이미 존재하는 아이디: " + id, cause);
        }
    }
}
