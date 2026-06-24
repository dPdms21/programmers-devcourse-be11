package com.example.spring.ch03.ex_3_2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAODeleteAll implements StatementStrategy {
    @Override
    public PreparedStatement makeStatement(Connection conn) throws SQLException {
        return conn.prepareStatement("DELETE FROM users");
    }
}
