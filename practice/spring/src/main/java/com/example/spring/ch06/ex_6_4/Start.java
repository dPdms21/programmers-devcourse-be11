package com.example.spring.ch06.ex_6_4;

// * 애너테이션 기반 선언적 트랜잭션
// [동작은 동일] 내부적으로는 여전히 'AOP 프록시'가 만들어져 트랜잭션을 감쌈
//  다만 그 프록시를 만들고 적용하는 일을 우리가 아니라 스프링이 애너테이션을 보고 처리할 뿐

// [실무] 오늘날 트랜잭션은 거의 이 방식(@Transactional)을 씀
//  추가로 propagation(전파), isolation(격리수준), readOnly, rollbackFor 등의 '트랜잭션 속성'을
//  애너테이션 속성으로 선언할 수 있음. 예) @Transactional(readOnly = true)

public class Start {
    public static void main(String[] args) {

    }
}
