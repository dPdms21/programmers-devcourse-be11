package com.example.spring.singleton2;

interface ConnectionMaker {
    String makeConnection();
}

class SimpleConnectionMaker implements ConnectionMaker {
    private static final SimpleConnectionMaker INSTANCE = new SimpleConnectionMaker();

    private SimpleConnectionMaker() {

    }

    static SimpleConnectionMaker getInstance() {
        return INSTANCE;
    }

    public String makeConnection() {
        return "DB 연결";
    }
}

class UserDAO {
    private static final UserDAO INSTANCE = new UserDAO();

    private UserDAO() {

    }

    public static UserDAO getInstance() {
        return INSTANCE;
    }

    private ConnectionMaker connectionMaker = SimpleConnectionMaker.getInstance();

    String findUser(String userID) {
        return userID + " 조회 [" + connectionMaker.makeConnection() + "]";
    }
}

public class Dao {

}
