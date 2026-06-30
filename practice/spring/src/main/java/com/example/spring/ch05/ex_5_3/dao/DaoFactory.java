package com.example.spring.ch05.ex_5_3.dao;

import com.example.spring.ch05.ex_5_3.service.UserService;
import com.example.spring.ch05.ex_5_3.service.UserServiceImpl;
import com.example.spring.ch05.ex_5_3.service.UserServiceTx;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

// DaoFactory를 스프링 빈 팩토리가 사용할 수 있는 설정 정보로 리팩토링

@Configuration // 애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정 정보라는 표시
public class DaoFactory {
    private static final Dotenv dotenv = Dotenv.load();

    @Bean
    public UserService userService() {
        return new UserServiceTx(transactionManager(), userServiceImpl());
    }

    @Bean
    public UserServiceImpl userServiceImpl() {
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

    // 커넥션을 우리가 직접 만들던 SimpleConnectionMaker 대신, 스프링 표준 DataSource를 씀
    // - DriverManagerDataSource: 학습용 DataSource(요청마다 커넥션 생성). 운영은 커넥션 풀을 씀
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dotenv.get("DB_URL"));
        dataSource.setUsername(dotenv.get("DB_USERNAME"));
        dataSource.setPassword(dotenv.get("DB_PASSWORD"));

        return dataSource;
    }

    // * 트랜잭션 추상화의 '실제 구현'을 여기서 결정
    // - 반환 타입은 추상화 인터페이스(PlatformTransactionManager)
    // - JDBC를 쓰므로 DataSourceTransactionManager를 꽂음
    //   (JPA면 JpaTransactionManager, 분산이면 JtaTransactionManager로 '이 한 줄만' 바꾸면 됨
    //    UserService 코드는 전혀 손대지 않음 -> 이것이 추상화의 이득)
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}