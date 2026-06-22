package com.example.spring.ch01.ex_1_1.dao;

// * UserDAO의 관심사항
// - DB와 연결을 위한 커넥션을 어떻게 가져올 것인가?

import com.example.spring.ch01.ex_1_1.domain.User;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class UserDAO_2 {

    private static final Dotenv dotenv = Dotenv.load();

    public void add(User user) throws ClassNotFoundException, SQLException {
        String query = "INSERT INTO users (id, name, password) VALUES (?, ?, ?)";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();
        }
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        String query = "SELECT * FROM users WHERE id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, id);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                User user = new User();
                user.setId(resultSet.getString("id"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));

                return user;
            }
        }
    }

    // 중복 코드의 메서드 추출 → 메서드 추출
    // 리팩토링: 기존 코드의 외부 동작 방식은 유지하면서 내부 구조를 재구성하는 작업
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
                dotenv.get("DB_URL"),
                dotenv.get("DB_USERNAME"),
                dotenv.get("DB_PASSWORD")
        );
    }
}