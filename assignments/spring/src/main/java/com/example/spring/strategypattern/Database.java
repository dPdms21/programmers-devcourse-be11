package com.example.spring.strategypattern;

import java.util.*;

public class Database {
    private List<User> users = new ArrayList<>();
    void open()  { System.out.println("  [컨텍스트] 연결 열기"); }
    void close() { System.out.println("  [컨텍스트] 연결 닫기"); }
    List<User> getUsers() { return users; }
}
