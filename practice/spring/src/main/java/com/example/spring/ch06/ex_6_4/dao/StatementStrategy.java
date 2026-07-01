package com.example.spring.ch06.ex_6_4.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementStrategy {
    PreparedStatement makeStatement(Connection conn) throws SQLException;
}
