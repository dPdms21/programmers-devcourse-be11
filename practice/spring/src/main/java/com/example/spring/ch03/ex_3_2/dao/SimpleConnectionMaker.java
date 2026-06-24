package com.example.spring.ch03.ex_3_2.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface SimpleConnectionMaker {
    Connection makeNewConnection() throws ClassNotFoundException, SQLException;
}
