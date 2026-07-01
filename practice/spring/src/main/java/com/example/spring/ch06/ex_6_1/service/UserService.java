package com.example.spring.ch06.ex_6_1.service;

import com.example.spring.ch06.ex_6_1.domain.User;

import java.sql.SQLException;

public interface UserService {
    void add(User user) throws SQLException, ClassNotFoundException;
    void upgradeLevels();
}
