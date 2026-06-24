package com.example.spring.ch03.ex_3_1.dao;

import com.example.spring.ch03.ex_3_1.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// * 템플릿 메서드 패턴의 적용
// 상속을 통해 기능을 확장해서 사용하는 부분
// 변하지 않는 부분은 슈퍼클래스에 두고 변하는 부분은 추상 메서드로 정의해둬서
// 서브클래스에서 오버라이드하여 새롭게 정의해 쓰도록 하는 것

public abstract class UserDAO {
    private SimpleConnectionMaker simpleConnectionMaker;

    public UserDAO(SimpleConnectionMaker simpleConnectionMaker) {
        this.simpleConnectionMaker = simpleConnectionMaker;
    }

    protected UserDAO() {

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
        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement ps = makeStatement(conn); // 변하는 부분을 메서드로 추출
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

    protected abstract PreparedStatement makeStatement(Connection conn) throws ClassNotFoundException, SQLException;
}
