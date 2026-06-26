package com.example.spring.strategypattern;

public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        JdbcContext jdbcContext = new JdbcContext(db);
        UserDao dao = new UserDao(jdbcContext);

        System.out.println("\n== (별도 클래스) deleteAll ==");
        dao.deleteAll();

        System.out.println("\n== (익명 클래스) add(김) ==");
        dao.add(new User("u1", "김"));

        System.out.println("\n== (람다) add(이) ==");
        dao.add2(new User("u2", "이"));

        System.out.println("\n현재 사용자 수: " + db.getUsers().size());

        for (User u : db.getUsers()) {
            System.out.println("사용자: " + u.getName());
        }

        User user = dao.get("u1");

        if (user != null) {
            System.out.println("조회 사용자: " + user.getName());
        }


    }
}
