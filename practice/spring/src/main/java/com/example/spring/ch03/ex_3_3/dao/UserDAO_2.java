package com.example.spring.ch03.ex_3_3.dao;

import com.example.spring.ch03.ex_3_3.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO_2 {
    private SimpleConnectionMaker simpleConnectionMaker;

    public UserDAO_2(SimpleConnectionMaker simpleConnectionMaker) {
        this.simpleConnectionMaker = simpleConnectionMaker;
    }

    protected UserDAO_2() {

    }

    public void jdbcContextWithStatementStrategy(StatementStrategy statementStrategy) throws SQLException, ClassNotFoundException {
        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement ps = statementStrategy.makeStatement(conn); // 변하는 부분을 전략에 위임
        ) {
            ps.executeUpdate();
        }
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        StatementStrategy strategy = new StatementStrategy() {
            @Override
            public PreparedStatement makeStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users(id, name, password) VALUES(?, ?, ?)"
                );

                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());

                return ps;
            }
        };

        jdbcContextWithStatementStrategy(strategy);
    }

    public void deleteAll() throws SQLException, ClassNotFoundException {
        StatementStrategy strategy = new StatementStrategy() {
            @Override
            public PreparedStatement makeStatement(Connection conn) throws SQLException {
                return conn.prepareStatement("DELETE FROM users");
            }
        };

        jdbcContextWithStatementStrategy(strategy);
    }
}
