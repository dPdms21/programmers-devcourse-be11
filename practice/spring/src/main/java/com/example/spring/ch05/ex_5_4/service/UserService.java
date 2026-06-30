package com.example.spring.ch05.ex_5_4.service;

import com.example.spring.ch05.ex_5_4.domain.User;

import java.sql.SQLException;

// UserService를 '인터페이스'로 두고, 구현을 둘로 나눔
// - UserServiceImpl : 순수 비즈니스 로직만
// - UserServiceTx : 트랜잭션 경계만 책임지고, 실제 일은 다른 UserService에 위임 (데코레이터)
// 클라이언트는 인터페이스에만 의존하므로 둘의 분리를 몰라도 됨

public interface UserService {
    void add(User user) throws SQLException, ClassNotFoundException;
    void upgradeLevels();
}
