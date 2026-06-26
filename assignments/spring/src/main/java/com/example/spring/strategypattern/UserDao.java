package com.example.spring.strategypattern;

public class UserDao {
    private final Database db;

    public UserDao(Database db) {
        this.db = db;
    }

    private void context(StatementStrategy strategy) {
        db.open();
        strategy.run(db);
        db.close();
    }

    // 클래스
    void deleteAll() {
        context(new DeleteAllStrategy());
    }

    // 익명 클래스
    void add(User user) {
        context(new StatementStrategy() {
            @Override
            public void run(Database db) {
                db.getUsers().add(user);
                System.out.println("  [전략-익명] 추가: " + user.getName());
            }
        });
    }

    // 람다
    void add2(User user) {
        context(db -> {
            db.getUsers().add(user);
            System.out.println("  [전략-람다] 추가: " + user.getName());
        });
    }
}
