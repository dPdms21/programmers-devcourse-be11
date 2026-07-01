package com.example.spring.ch06.ex_6_2.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcContext {
    private DataSource dataSource;

    public JdbcContext(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy statementStrategy) throws SQLException, ClassNotFoundException {
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (
                PreparedStatement ps = statementStrategy.makeStatement(conn);
        ) {
            ps.executeUpdate();
        }
        finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public <T> List<T> query(StatementStrategy strategy, RowMapper<T> rowMapper) throws SQLException, ClassNotFoundException {
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (
                PreparedStatement ps = strategy.makeStatement(conn);
                ResultSet rs = ps.executeQuery();
        ) {
            List<T> results = new ArrayList<>();

            while (rs.next()) {
                results.add(rowMapper.mapRow(rs));
            }

            return results;
        }
        finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public <T> T queryForObject(StatementStrategy strategy, RowMapper<T> rowMapper) throws SQLException, ClassNotFoundException {
        List<T> results = query(strategy, rowMapper);

        if (results.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return results.get(0);
    }
}
