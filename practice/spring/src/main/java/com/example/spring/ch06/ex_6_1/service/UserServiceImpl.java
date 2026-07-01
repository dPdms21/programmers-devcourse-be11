package com.example.spring.ch06.ex_6_1.service;

import com.example.spring.ch06.ex_6_1.dao.Level;
import com.example.spring.ch06.ex_6_1.dao.UserDAO;
import com.example.spring.ch06.ex_6_1.domain.User;

import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    private UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // 신규가입
    @Override
    public void add(User user) throws SQLException, ClassNotFoundException {
        user.setLevel(Level.BASIC);
        userDAO.add(user);
    }

    // 업그레이드 담당
    @Override
    public void upgradeLevels() {
        try {
            List<User> users = userDAO.getAll();

            for (User user : users) {
                if (canUpgrade(user)) {
                    upgradeLevel(user);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("레벨 업그레이드 중 오류가 발생해 롤백", e);
        }
    }

    // '올릴 수 있는가'
    private boolean canUpgrade(User user) {
        Level curLevel = user.getLevel();

        switch (curLevel) {
            case BASIC:
                return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
            case SILVER:
                return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            case GOLD:
                return false;
            default:
                throw new IllegalStateException("Unexpected value: " + curLevel);
        }
    }

    // 실제 업그레이드
    protected void upgradeLevel(User user) throws SQLException, ClassNotFoundException {
        user.upgradeLevel();
        userDAO.update(user);
    }
}
