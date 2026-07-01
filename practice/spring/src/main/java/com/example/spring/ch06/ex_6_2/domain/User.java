package com.example.spring.ch06.ex_6_2.domain;

import com.example.spring.ch06.ex_6_2.dao.Level;

// 사용자 정보를 저장할 User클래스
public class User {

    private String id;
    private String name;
    private String password;
    private Level level;
    private int login;
    private int recommend;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void upgradeLevel() {
        this.level = this.level.nextLevel();
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }
}
