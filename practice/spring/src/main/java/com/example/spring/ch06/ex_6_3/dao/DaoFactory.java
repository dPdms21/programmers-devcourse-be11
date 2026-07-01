package com.example.spring.ch06.ex_6_3.dao;

import com.example.spring.ch06.ex_6_3.service.TransactionAdvice;
import com.example.spring.ch06.ex_6_3.service.UserService;
import com.example.spring.ch06.ex_6_3.service.UserServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
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

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userDAO());
    }

    // * 자동 프록시 생성기 - DefaultAdvisorAutoProxyCreator
    // - 빈 후처리기. 컨테이너가 빈을 만드는 '도중에' 끼어들어 가공
    // - 등록된 모든 Advisor의 Pointcut을 검사해서, 조건에 맞는 빈을 '자동으로 프록시로 바꿔치기'
    // => 더 이상 빈마다 ProxyFactoryBean을 일일이 설정하지 않아도 됨 (ex_6_2의 반복이 사라짐)
    //  target 빈은 평범하게 등록만 해두면, 이 생성기가 알아서 프록시를 입혀줌
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
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

    // Pointcut(어디에): 어떤 메서드에 부가기능을 적용할지 '선별'
    // - ex_6_1에서는 핸들러 안에서 startsWith로 직접 걸렀지만, 이제 그 책임이 Pointcut으로 분리됨
    //  NameMatchMethodPointcut: 메서드 이름 패턴으로 매칭. "upgrade*" -> upgradeLevels 등
    @Bean
    public NameMatchMethodPointcut transactionPointcut() {
        NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
        nameMatchMethodPointcut.setMappedNames("upgrade*");

        return nameMatchMethodPointcut;
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