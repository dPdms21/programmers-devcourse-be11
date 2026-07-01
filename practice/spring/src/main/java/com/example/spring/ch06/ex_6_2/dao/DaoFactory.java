package com.example.spring.ch06.ex_6_2.dao;

import com.example.spring.ch06.ex_6_2.service.TransactionAdvice;
import com.example.spring.ch06.ex_6_2.service.UserServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
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

    // UserService 빈 = ProxyFactoryBean이 생산하는 프록시
    // - target과 advisor만 등록하면, 스프링이 프록시를 알아서 만들어줌
    // - 여러 advisor를 addAdvisor로 얹을 수도 있음 (부가기능 여러 개 조합)
    @Bean
    public ProxyFactoryBean userService() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(userServiceImpl());
        proxyFactoryBean.addAdvisor(transactionAdvisor());

        return proxyFactoryBean;
    }

    // Advisor = Pointcut + Advice
    // - 부가기능과 적용대상을 묶은 한 덩어리. ProxyFactoryBean에는 이 Advisor 단위로 등록
    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        return new DefaultPointcutAdvisor(transactionPointcut(), transactionAdvice());
    }

    // Advice(무엇을) : 적용할 부가기능 자체(트랜잭션 경계)
    @Bean
    public TransactionAdvice transactionAdvice() {
        return new TransactionAdvice(transactionManager());
    }

    // Pointcut(어디에) : 어떤 메서드에 부가기능을 적용할지 '선별'
    // - ex_6_1에서는 핸들러 안에서 startsWith로 직접 걸렀지만, 이제 그 책임이 Pointcut으로 분리됨
    // - NameMatchMethodPointcut: 메서드 이름 패턴으로 매칭. "upgrade*" -> upgradeLevels 등
    @Bean
    public NameMatchMethodPointcut transactionPointcut() {
        NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
        nameMatchMethodPointcut.setMappedNames("upgrade*");
        return nameMatchMethodPointcut;
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