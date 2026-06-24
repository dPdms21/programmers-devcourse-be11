package com.example.spring.ch02.ex_2_1.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// DaoFactory를 스프링 빈 팩토리가 사용할 수 있는 설정 정보로 리팩토링

@Configuration // 애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정 정보라는 표시
public class DaoFactory {
    @Bean // 오브젝트 생성을 담당하는 IoC용 메서드라는 표시
    public UserDAO userDAO() {
        return new UserDAO(connectionMaker());
    }

    @Bean
    public SimpleConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}
