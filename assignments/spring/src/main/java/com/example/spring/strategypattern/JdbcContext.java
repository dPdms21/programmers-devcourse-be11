package com.example.spring.strategypattern;

public class JdbcContext {
    private final Database db;

    public JdbcContext(Database db) {
        this.db = db;
    }

    public void execute(StatementStrategy strategy) {
        db.open();

        try {
            strategy.run(db);
        } finally {
            db.close();
        }
    }

    public User findUserById(String id) {
        db.open();

        try {
            for (User user : db.getUsers()) {
                if (user.getId().equals(id)) {
                    return user;
                }
            }

            return null;
        } finally {
            db.close();
        }
    }
}
