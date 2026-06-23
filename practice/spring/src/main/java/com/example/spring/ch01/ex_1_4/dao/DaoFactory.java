package com.example.spring.ch01.ex_1_4.dao;

public class DaoFactory {
    public UserDAO userDAO() {
//        SimpleConnectionMaker connection = new DConnectionMaker();
//        UserDAO userDAO = new UserDAO(connection);

        return new UserDAO(connectionMaker());
    }

    public AccountDAO accountDAO() {
//        SimpleConnectionMaker connection = new DConnectionMaker();
//        AccountDAO accountDAO = new AccountDAO(connection);

        return new AccountDAO(connectionMaker());
    }

    public MessageDAO messageDAO() {
//        SimpleConnectionMaker connection = new DConnectionMaker();
//        MessageDAO messageDAO = new MessageDAO(connection);

        return new MessageDAO(connectionMaker());
    }

    private SimpleConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}
