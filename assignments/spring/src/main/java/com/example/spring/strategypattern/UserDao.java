package com.example.spring.strategypattern;

public class UserDao {
    private final JdbcContext jdbcContext;

    public UserDao(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    // 클래스
    void deleteAll() {
        jdbcContext.execute(new DeleteAllStrategy());
    }

    // 익명 클래스
    void add(User user) {
        jdbcContext.execute(new StatementStrategy() {
            @Override
            public void run(Database db) {
                db.getUsers().add(user);
                System.out.println("  [전략-익명] 추가: " + user.getName());
            }
        });
    }

    // 람다
    void add2(User user) {
        jdbcContext.execute(db -> {
            db.getUsers().add(user);
            System.out.println("  [전략-람다] 추가: " + user.getName());
        });
    }

    User get(String id) {
        return jdbcContext.findUserById(id);
    }

    void deleteAll2() {
        jdbcContext.execute(db -> db.getUsers().clear());
    }


}
