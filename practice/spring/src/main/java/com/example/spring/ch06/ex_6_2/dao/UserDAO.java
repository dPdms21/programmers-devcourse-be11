package com.example.spring.ch06.ex_6_2.dao;

import com.example.spring.ch06.ex_6_2.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// * 레벨 관리를 위해 확장된 UserDAO
public class UserDAO {
    private JdbcContext jdbcContext;

    protected UserDAO() {

    }

    public UserDAO(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    private RowMapper<User> userRowMapper = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));

            return user;
        }
    };

    public void add(User user) throws ClassNotFoundException, SQLException {
        StatementStrategy strategy = new StatementStrategy() {
            @Override
            public PreparedStatement makeStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users(id, name, password, level, login, recommend) " + "VALUES(?, ?, ?, ?, ?, ?)"
                );

                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());
                ps.setInt(4, user.getLevel().getValue());
                ps.setInt(5, user.getLogin());
                ps.setInt(6, user.getRecommend());

                return ps;
            }
        };

        jdbcContext.workWithStatementStrategy(strategy);
    }

    public void deleteAll() throws SQLException, ClassNotFoundException {
        StatementStrategy strategy = new StatementStrategy() {
            @Override
            public PreparedStatement makeStatement(Connection conn) throws SQLException {
                return conn.prepareStatement("DELETE FROM users");
            }
        };

        jdbcContext.workWithStatementStrategy(strategy);
    }

    public User get(String id) throws SQLException, ClassNotFoundException {
        StatementStrategy strategy = new StatementStrategy() {
            @Override
            public PreparedStatement makeStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
                ps.setString(1, id);

                return ps;
            }
        };

        return jdbcContext.queryForObject(strategy, userRowMapper);
    }

    public List<User> getAll() throws SQLException, ClassNotFoundException {
        StatementStrategy strategy = new StatementStrategy() {
            @Override
            public PreparedStatement makeStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users ORDER BY id");

                return ps;
            }
        };

        return jdbcContext.query(strategy, userRowMapper);
    }

    public int getCount() throws SQLException, ClassNotFoundException {
        StatementStrategy strategy = new StatementStrategy() {
            @Override
            public PreparedStatement makeStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users");

                return ps;
            }
        };

        return jdbcContext.queryForObject(strategy, new RowMapper<>() {
            @Override
            public Integer mapRow(ResultSet rs) throws SQLException {
                return rs.getInt(1);
            }
        });
    }

    public void update(User user) throws SQLException, ClassNotFoundException {
        StatementStrategy strategy = new StatementStrategy() {
            @Override
            public PreparedStatement makeStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE users SET name = ?, password = ?, level = ?, login = ?, recommend = ? WHERE id = ?");
                ps.setString(1, user.getName());
                ps.setString(2, user.getPassword());
                ps.setInt(3, user.getLevel().getValue());
                ps.setInt(4, user.getLogin());
                ps.setInt(5, user.getRecommend());
                ps.setString(6, user.getId());

                return ps;
            }
        };

        jdbcContext.workWithStatementStrategy(strategy);
    }
}
