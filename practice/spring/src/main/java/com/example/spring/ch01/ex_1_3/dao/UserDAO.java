package com.example.spring.ch01.ex_1_3.dao;

import com.example.spring.ch01.ex_1_3.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// * 문제점
// UserDAO의 다른 모든 곳에서는 인터페이스를 이용하게 만들어서
// DB커넥션을 제공하는 클래스에 대한 구체적인 정보는 모두 제거했지만,
// 초기에 한 번 어떤 클래스의 오브젝트를 사용할지를 결정하는 모든 생성자의 코드는 제거되지 않고 남아 있음
// 여전히 UserDAO 소스코드를 함께 제공해서,
// 필요할 때마다  UserDAO의 생성자 메서드를 직접 수정하라고 하지 않고는
// 고객에게 자유로운 DB커넥션 확장 기능을 가진 UserDAO를 제공할 수 없음

// "관계설정 책임의 분리"
// UserDAO의 클라이언트 오브젝트가
// 제3의 관심사항인 UserDAO와 ConnectionMaker 구현 클래스의 관계를 결정해주는 기능을 분리해서 두기에 적절한 곳
// UserDAO 오브젝트가 다른 오브젝트와 관계를 맺으려면 관계를 맺을 오브젝트가 있어야 할 텐데,
// 이 오브젝트를 꼭 UserDAO의 코드 내에서 만들 필요는 없음
// 오브젝트는 얼마든지 메서드 파라미터 등을 이용해 전달할 수 있으니 외부에서 만들 걸 가져올 수도 있음

// 물론 UserDAO 오브젝트가 동작하려면 특정 클래스의 오브젝트와 관계를 맺어야 함
// 인터페이스 자체는 기능이 없으니 이를 사용할 수도 없음
// 결국 특정 클래스가 오브젝트와 관계를 맺음
// 하지만 클래스 사이에 관계가 만들어진 것은 아니고, 단지 오브젝트 사이에 다이나믹한 관계가 만들어지는 것
// 클래스 사이의 관계는 코드에 다른 클래스 이름이 나타나기 때문에 만들어지는 것
// 하지만 오브젝트 사이의 관계는 그렇지 않음
// 코드에서는 특정 클래스를 전혀 알지 못하더라도 해당 클래스가 구현한 인터페이스를 사용했다면,
// 그 클래스의 오브젝트를 인터페이스 타입으로 받아서 사용할 수 있음
// '다형성'이라는 특징이 있는 덕분
// UserDAO 오브젝트가 DConnectionMaker 오브젝트를 사용하게 하려면 두 클래스의 오브젝트 사이에
// 런타임 사용관계 또는 링크, 또는 의존관계라고 불리는 관계를 맺어주면 됨

// -> 런타임 오브젝트 관계를 갖는 구조로 만들어주는 게 바로 클라이언트의 책임

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
}
