package com.example.spring.ch06.ex_6_4.service;

import com.example.spring.ch06.ex_6_4.domain.User;

import java.sql.SQLException;

public interface UserService {
    void add(User user) throws SQLException, ClassNotFoundException;
    void upgradeLevels();
}
