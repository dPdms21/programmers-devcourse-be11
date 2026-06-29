package com.example.spring.ch03.ex_3_4.dao;

import com.example.spring.ch03.ex_3_4.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {
    private JdbcContext jdbcContext;

    public UserDAO(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    protected UserDAO() {

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

        jdbcContext.workWithStatementStrategy(strategy);
    }

    public void deleteAll() throws SQLException, ClassNotFoundException {

        StatementStrategy strategy = new StatementStrategy() {
            @Override
            public PreparedStatement makeStatement(Connection conn) throws SQLException {
                return conn.prepareStatement("DELETE FROM users");
            }
        };

        jdbcContext.workWithStatementStrategy(strategy);
    }
}
