package com.example.spring.ch01.ex_1_1.dao;

import com.example.spring.ch01.ex_1_1.domain.User;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

// DAO(Data Access Object)
// DB를 사용해 데이터를 조회하거나 조작하는 기능을 전담하도록 만든 오브젝트
public class UserDAO {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String DB_URL = dotenv.get("DB_URL");
    private static final String DB_USERNAME = dotenv.get("DB_USERNAME");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    public void add(User user) throws ClassNotFoundException, SQLException {
        // JDBC 드라이버를 메모리에 로딩하는 코드
        // com.mysql.cj.jdbc.Driver 는 MySQL 드라이버 클래스의 전체 이름
        // Class.forName(...)은 해당 이름의 클래스를 찾아 메모리에 적재(로딩)
        // 이 클래스가 로딩될 때 내부적으로 DriverManager에 자기 자신을 자동 등록
        // → 등록이 되어야 아래 DriverManager.getConnection(...)으로 DB에 연결할 수 있음
        // (참고: JDBC 4.0+ 부터는 자동 등록되어 생략 가능하지만, 동작 원리 이해를 위해 작성)
        Class.forName("com.mysql.cj.jdbc.Driver");

        String query = "INSERT INTO users (id, name, password) VALUES (?, ?, ?)";

        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();
        }
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String query = "SELECT * FROM users WHERE id = ?";

        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setString(1, id);
            ResultSet resultSet = ps.executeQuery();

            resultSet.next();

            User user = new User();
            user.setId( resultSet.getString("id") );
            user.setName( resultSet.getString("name") );
            user.setPassword( resultSet.getString("password") );

            return user;
        }
    }
}