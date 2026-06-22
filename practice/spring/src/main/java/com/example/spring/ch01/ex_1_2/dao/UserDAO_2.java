package com.example.spring.ch01.ex_1_2.dao;

import com.example.spring.ch01.ex_1_2.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// * 문제점
// N사와 D사에 UserDAO 클래스만 공급하고
// 상속을 통해 DB 커넥션 기능을 확장해서 사용하게 했던 게 다시 불가능해졌다...
// 왜냐하면 UserDAO의 코드가 SimpleConnectionMaker라는 특정 클래스에 종속되어 있기 때문
// 또 DB 커넥션을 제공하는 클래스가 어떤 것인지를 UserDAO가 구체적으로 알고 있어야 함

// "인터페이스의 도입"
// 가장 좋은 해결책은 두 개의 클래스가 서로 긴밀하게 연결되어 있지 않도록
// 중간에 추상적인 느슨한 연결고리를 만들어주는 것
// 추상화란 어떤 것들의 공통적인 성격을 뽑아내어 이를 따로 분리해내는 작업
// 자바가 추상화를 위해 제공하는 가장 유용한 도구는 바로 인터페이스
// 인터페이스를 통해 접근하게 하면 실제 구현 클래스를 바꿔도 신경 쓸 일이 없음

public class UserDAO_2 {
    private SimpleConnectionMaker_2 simpleConnectionMaker;

    public UserDAO_2() {
        simpleConnectionMaker = new NConnectionMaker_2();
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
}
