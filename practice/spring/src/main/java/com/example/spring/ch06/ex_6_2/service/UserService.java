package com.example.spring.ch06.ex_6_2.service;

import com.example.spring.ch06.ex_6_2.domain.User;

import java.sql.SQLException;

public interface UserService {
    void add(User user) throws SQLException, ClassNotFoundException;
    void upgradeLevels();
}
