package com.example.spring.ch02.ex_2_1.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface SimpleConnectionMaker {
    Connection makeNewConnection() throws ClassNotFoundException, SQLException;
}
