package com.example.spring.ch03.ex_3_2.dao;

import com.example.spring.ch03.ex_3_2.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// * 전략 패턴의 적용
// - 컨텍스트
// 변하지 않는 부분 : JDBC 커넥션/실행/자원관리 공통 흐름
// - 전략
// 변하는 부분 : 어떤 PreparedStatement를 만들지 -> 인터페이스로 추상화

// 컨텍스트는 '인터페이스(StatementStrategy)에만' 의존하고, 실제 전략은 런타임에 주입받음
// 그래서 새 기능을 추가해도 컨텍스트 코드는 닫혀 있고(수정X) 전략만 새로 만들면 된다(확장O) = OCP

public class UserDAO {
    private SimpleConnectionMaker simpleConnectionMaker;

    public UserDAO(SimpleConnectionMaker simpleConnectionMaker) {
        this.simpleConnectionMaker = simpleConnectionMaker;
    }

    protected UserDAO() {

    }

    // 컨텍스트 : 변하지 않는 JDBC 작업의 공통 흐름
    // - 커넥션을 얻고, 전달받은 '전략'에게 statement 생성을 맡기고, 실행하고, 자원을 정리
    // - 어떤 SQL을 실행할지는 전혀 모름. 그건 strategy가 결정 (인터페이스에만 의존)
    public void jdbcContextWithStatementStrategy(StatementStrategy statementStrategy) throws SQLException, ClassNotFoundException {
        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement ps = statementStrategy.makeStatement(conn); // 변하는 부분을 전략에 위임
        ) {
            ps.executeUpdate();
        }
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        jdbcContextWithStatementStrategy(new UserDAOAdd(user));
    }

    public void deleteAll() throws SQLException, ClassNotFoundException {
        jdbcContextWithStatementStrategy(new UserDAODeleteAll());
    }
}
