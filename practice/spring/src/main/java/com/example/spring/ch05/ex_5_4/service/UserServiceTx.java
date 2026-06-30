package com.example.spring.ch05.ex_5_4.service;

import com.example.spring.ch05.ex_5_4.domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

public class UserServiceTx implements UserService {
    private PlatformTransactionManager transactionManager;
    private UserServiceImpl userService;

    public UserServiceTx(PlatformTransactionManager transactionManager, UserServiceImpl userService) {
        this.transactionManager = transactionManager;
        this.userService = userService;
    }

    @Override
    public void add(User user) throws SQLException, ClassNotFoundException {
        userService.add(user);
    }

    @Override
    public void upgradeLevels() {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            userService.upgradeLevels();
            transactionManager.commit(status);
        }
        catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
