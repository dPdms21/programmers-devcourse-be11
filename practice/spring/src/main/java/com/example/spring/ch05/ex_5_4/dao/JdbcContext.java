package com.example.spring.ch05.ex_5_4.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// * 트랜잭션에 '참여'할 수 있게 바뀐 JdbcContext
// ex_5_1 : '매번 새 커넥션'을 만들고 try-with-resources로 바로 닫음
// -> 메서드마다 커넥션이 다르므로, 여러 DAO 호출을 '하나의 트랜잭션'으로 묶을 수 없음
// ex_5_2 : Spring DataSource + DataSourceUtils를 씀
//      · DataSourceUtils.getConnection(dataSource)
//        -> 현재 진행 중인 트랜잭션이 있으면 '그 트랜잭션에 묶인 커넥션'을 돌려줌
//           (이것이 '트랜잭션 동기화': 커넥션을 메서드 파라미터로 넘기지 않아도 공유)
//        -> 트랜잭션이 없으면 새 커넥션을 줌
//      · DataSourceUtils.releaseConnection(conn, dataSource)
//        -> 트랜잭션에 묶인 커넥션이면 '닫지 않음' (트랜잭션이 끝날 때 닫음)
//           트랜잭션 밖이면 실제로 닫음
// => 그래서 커넥션을 try-with-resources로 직접 닫으면 안 됨. (트랜잭션이 깨짐) finally에서 release
// 어떤 일련의 작업이 하나의 트랜잭션으로 묶이려면 그 작업이 진행되는 동안 DB 커넥션도 하나만 사용돼야 함

public class JdbcContext {
    private DataSource dataSource;

    public JdbcContext(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy statementStrategy) throws SQLException, ClassNotFoundException {
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (
                PreparedStatement ps = statementStrategy.makeStatement(conn); // 변하는 부분을 전략에 위임
        ) {
            ps.executeUpdate();
        }
        finally {
            DataSourceUtils.releaseConnection(conn, dataSource); // 트랜잭션 커넥션이면 닫지 않음
        }
    }

    // 조회용 컨텍스트 : 여러 건을 조회해 리스트로 돌려줌
    // - 제네릭 타입 T는 사용하기 전에 반드시 어딘가에서 선언해야 함
    // 1) 클래스 레벨 선언
    // 2) 메서드 레벨 선언
    //  - 변하지 않는 흐름: 커넥션 획득 -> executeQuery -> ResultSet을 한 줄씩 순회 -> 자원 정리
    //  - 변하는 부분 둘: '어떤 SELECT인가(strategy)'와 '한 줄을 무엇으로 만들까(rowMapper)'
    public <T> List<T> query(StatementStrategy strategy, RowMapper<T> rowMapper) throws SQLException, ClassNotFoundException {
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (
                PreparedStatement ps = strategy.makeStatement(conn);
                ResultSet rs = ps.executeQuery();
        ) {
            List<T> results = new ArrayList<>();

            while ( rs.next() ) {
                results.add( rowMapper.mapRow(rs) );
            }

            return results;
        }
        finally {
            DataSourceUtils.releaseConnection(conn, dataSource); // 트랜잭션 커넥션이면 닫지 않음
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
