package com.example.spring.ch03.ex_3_3.dao;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NConnectionMaker implements SimpleConnectionMaker {
    private static final Dotenv dotenv = Dotenv.load();

    @Override
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection conn = DriverManager.getConnection(
                dotenv.get("DB_URL"),
                dotenv.get("DB_USERNAME"),
                dotenv.get("DB_PASSWORD")
        );

        return conn;
    }
}
