package com.example.spring.ch01.ex_1_6.dao;

import com.example.spring.ch01.ex_1_6.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// * 직접 만든 싱글톤 (자바 코드로 구현하는 전통적인 싱글톤 패턴 - GoF)
// 만드는 순서
//  1) 클래스당 오브젝트를 1개만 담아둘 자기 자신 타입의 static 필드를 만듦
//  2) 생성자를 private으로 막아 외부에서 new 로 마구 만들지 못하게 함
//  3) 유일한 오브젝트를 돌려주는 static 메서드(getInstance)를 둠
//     - 처음 호출될 때 한 번만 만들고, 그 다음부터는 이미 만든 오브젝트를 그대로 돌려줌

// * 직접 만든 싱글톤의 한계 -> 그래서 스프링은 '싱글톤 레지스트리'로 대신 관리
// - private 생성자라서 상속이 불가능 (객체지향의 장점인 상속/다형성을 못 씀)
// - 테스트가 어려움 (생성 방식이 고정되어 가짜(mock) 오브젝트로 갈아끼우기 힘듦)
// - getInstance처럼 의존 오브젝트(simpleConnectionMaker) 주입이 어색 (두 번째 호출부터는 넘긴 인자가 무시)
// - 클래스 로더가 여러 개인 환경에서는 싱글톤이 여러 개 생길 수 있음
// - static 필드라 사실상 전역 상태가 되어 아무 데서나 접근 가능해짐

public class UserDAO {
    // 1) 클래스가 자기 자신의 유일한 오브젝트를 보관 (클래스당 1개)
    private static UserDAO instance;

    private SimpleConnectionMaker simpleConnectionMaker;

    // 2) 생성자를 private 으로 막는다 -> 이제 외부에서 new UserDAO(...) 불가
    private UserDAO(SimpleConnectionMaker simpleConnectionMaker) {
        this.simpleConnectionMaker = simpleConnectionMaker;
    }

    // 3) 유일한 오브젝트를 돌려주는 통로
    //    - synchronized: 여러 스레드가 동시에 들어와 오브젝트가 2개 만들어지는 것을 막음
    public static synchronized UserDAO getInstance(SimpleConnectionMaker simpleConnectionMaker) {
        if (instance == null) {
            instance = new UserDAO(simpleConnectionMaker);
        }

        return instance;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {

        String query = "INSERT INTO users (id, name, password) VALUES (?, ?, ?)";

        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
        ) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());
            pstmt.executeUpdate();
        }
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        String query = "SELECT * FROM users WHERE id = ?";

        try (
                Connection conn = simpleConnectionMaker.makeNewConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
        ) {
            pstmt.setString(1, id);
            ResultSet resultSet = pstmt.executeQuery();

            resultSet.next();

            User user = new User();
            user.setId( resultSet.getString("id") );
            user.setName( resultSet.getString("name") );
            user.setPassword( resultSet.getString("password") );

            return user;
        }
    }
}
