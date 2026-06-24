package com.example.spring.ch02.ex_2_1.dao;

import com.example.spring.ch02.ex_2_1.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private SimpleConnectionMaker simpleConnectionMaker;

    public UserDAO(SimpleConnectionMaker simpleConnectionMaker) {
        this.simpleConnectionMaker = simpleConnectionMaker;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        String query = "INSERT INTO users (id, name, password) VALUES (?, ?, ?)";

        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement ps = conn.prepareStatement(query);
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
                Connection conn = simpleConnectionMaker.makeNewConnection();
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

    // 테스트 시작 전에 호출해 DB를 깨끗한 상태로 만드는 용도
    public void deleteAll() throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM users";

        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.executeUpdate();
        }
    }

    public int getCount() throws ClassNotFoundException, SQLException {
        String query = "SELECT COUNT(*) FROM users";

        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet resultSet = ps.executeQuery();
        ) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}
