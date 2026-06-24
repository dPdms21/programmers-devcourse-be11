package com.example.spring.ch03.ex_3_2.dao;

import com.example.spring.ch03.ex_3_2.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// * add 전략 (StatementStrategy 구현체)
// '변하는 부분'인 PreparedStatement 생성 로직만 담은 전략 클래스
// deleteAll과 달리 add는 저장할 User 데이터가 필요하므로,
// 생성자로 User를 받아 전략 안에서 파라미터까지 채워 완성된 statement를 돌려줌

public class UserDAOAdd implements StatementStrategy {
    private final User user;

    public UserDAOAdd(User user) {
        this.user = user;
    }

    @Override
    public PreparedStatement makeStatement(Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO users(id, name, password) VALUES(?, ?, ?)"
        );

        pstmt.setString(1, user.getId());
        pstmt.setString(2, user.getName());
        pstmt.setString(3, user.getPassword());

        return pstmt;
    }
}
