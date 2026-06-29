package com.example.spring.ch05.ex_5_1.dao;

import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcContext {
    private SimpleConnectionMaker simpleConnectionMaker;

    public JdbcContext(SimpleConnectionMaker simpleConnectionMaker) {
        this.simpleConnectionMaker = simpleConnectionMaker;
    }

    public void workWithStatementStrategy(StatementStrategy statementStrategy) throws SQLException, ClassNotFoundException {
        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement ps = statementStrategy.makeStatement(conn); // 변하는 부분을 전략에 위임
        ) {
            ps.executeUpdate();
        }
    }

    // 조회용 컨텍스트 : 여러 건을 조회해 리스트로 돌려줌
    // - 제네릭 타입 T는 사용하기 전에 반드시 어딘가에서 선언해야 함
    // 1) 클래스 레벨 선언
    // 2) 메서드 레벨 선언
    //  - 변하지 않는 흐름: 커넥션 획득 -> executeQuery -> ResultSet을 한 줄씩 순회 -> 자원 정리
    //  - 변하는 부분 둘: '어떤 SELECT인가(strategy)'와 '한 줄을 무엇으로 만들까(rowMapper)'
    public <T> List<T> query(StatementStrategy strategy, RowMapper<T> rowMapper) throws SQLException, ClassNotFoundException {
        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement ps = strategy.makeStatement(conn);
                ResultSet rs = ps.executeQuery();
        ) {
            List<T> results = new ArrayList<>();

            while (rs.next()) {
                results.add(rowMapper.mapRow(rs));
            }

            return results;
        }
    }

    // 조회용 컨텍스트 : 정확히 한 건을 조회
    // - 결과가 없으면 의미 있는 예외(EmptyResultDataAccessException, ch04에서 배운 추상화 예외)를 던짐
    public <T> T queryForObject(StatementStrategy strategy, RowMapper<T> rowMapper) throws SQLException, ClassNotFoundException {
        List<T> results = query(strategy, rowMapper);

        if (results.isEmpty()) {
            throw new EmptyResultDataAccessException(1); // 기대한 1건이 없음
        }

        return results.get(0);
    }
}
