package com.example.spring.ch01.ex_1_2.dao;

import java.sql.Connection;
import java.sql.SQLException;

// interface를 사용하는 UserDAO 입장에서
// 어떤 클래스로 만들어졌는지 상관없이 makeNewConnection를 호출하면
// Connection 타입의 오브젝트를 돌려줄 것이라고 기대할 수 있음

public interface SimpleConnectionMaker_2 {
    Connection makeNewConnection() throws ClassNotFoundException, SQLException;
}
