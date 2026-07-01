package com.example.spring.ch06.ex_6_4.dao;

import com.example.spring.ch06.ex_6_4.service.UserService;
import com.example.spring.ch06.ex_6_4.service.UserServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
// * @EnableTransactionManagement
// - 선언적 트랜잭션(@Transactional)을 켜는 스위치
// - 이 한 줄이 6.3에서 손수 등록하던 것들(자동 프록시 생성기 + 트랜잭션 Advisor 등)을
//  스프링이 내부적으로 대신 등록해줌
// => DaoFactory에서 Advice/Pointcut/Advisor/AutoProxyCreator 빈이 전부 사라짐!
@EnableTransactionManagement
public class DaoFactory {
    private static final Dotenv dotenv = Dotenv.load();

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userDAO());
    }

    @Bean // 오브젝트 생성을 담당하는 IoC용 메서드라는 표시
    public UserDAO userDAO() {
        return new UserDAO(jdbcContext());
    }

    @Bean
    public JdbcContext jdbcContext() {
        return new JdbcContext(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dotenv.get("DB_URL"));
        dataSource.setUsername(dotenv.get("DB_USERNAME"));
        dataSource.setPassword(dotenv.get("DB_PASSWORD"));

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}