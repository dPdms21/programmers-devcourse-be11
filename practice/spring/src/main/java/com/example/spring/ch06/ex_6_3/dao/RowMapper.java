package com.example.spring.ch06.ex_6_3.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

// * RowMapper - 조회 결과의 '변하는 부분'을 담는 전략
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
