package com.example.spring.ch06.ex_6_1.dao;

import com.example.spring.ch06.ex_6_1.service.TransactionHandler;
import com.example.spring.ch06.ex_6_1.service.UserService;
import com.example.spring.ch06.ex_6_1.service.UserServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

// DaoFactory를 스프링 빈 팩토리가 사용할 수 있는 설정 정보로 리팩토링

@Configuration // 애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정 정보라는 표시
public class DaoFactory {
    private static final Dotenv dotenv = Dotenv.load();

    @Bean
    public UserService userService() {
        TransactionHandler txHandler = new TransactionHandler(userServiceImpl(), transactionManager(), "upgrade");

        // * 다이나믹 프록시를 런타임에 생성
        // 모든 메서드 호출이 txHandler.invoke()로 전달
        // "메모리에 흉내낼 클래스를 임시로 만들어서 메서드에 맞게 처리"
        // - 메모리에 — .java/.class 파일로 디스크에 저장되는 게 아니라, JVM이 런타임에 바이트코드를 즉석 생성해서 메모리에 올림
        // - 흉내낼 클래스 — UserService 인터페이스를 구현한 $Proxy0을 만듦. 그래서 UserService인 척할 수 있음
        // - 메서드에 맞게 처리 — 호출된 메서드 정보를 invoke()로 넘겨, 우리가 정한 대로 처리
        // * 프록시 자신은 "처리"를 안 함
        // $Proxy0(프록시)은 처리 로직이 전혀 없음. 어떤 메서드가 불리든 무조건 invoke()로 떠넘기기만 함
        return (UserService) Proxy.newProxyInstance(
                getClass().getClassLoader(),    // (1) 프록시 클래스를 적재할 클래스로더
                new Class[]{UserService.class}, // (2) 프록시가 구현(흉내)할 인터페이스
                txHandler                       // (3) 호출을 받아 처리할 핸들러
        );
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