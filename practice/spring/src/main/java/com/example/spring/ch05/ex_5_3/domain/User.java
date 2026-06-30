package com.example.spring.ch05.ex_5_3.domain;

import com.example.spring.ch05.ex_5_3.dao.Level;

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

    // * 레벨 업그레이드 동작을 'User 자신'이 가짐
    // - "다음 레벨로 올린다"는 것은 사용자 데이터에 대한 처리이므로, 그 책임을 User에 둠
    // - 이렇게 해두면 UserService는 '언제 올릴지'만 판단하고, '어떻게 올릴지'는 User에 맡길 수 있음
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
