package com.example.jpaboard.domain.repository;

import com.example.jpaboard.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 이 인터페이스에는 구현 클래스가 없음
// - 우리는 interface만 선언하고 구현체(class)는 만들지 않음
// - 애플리케이션이 뜰 때 Spring Data JPA가 이 인터페이스의 구현체를 프록시로 자동 생성해서 빈으로 등록
// - 그래서 서비스 memberRepository를 주입받아 바로 쓸 수 있는 것 (개발자가 SQL/구현을 안 짜도 됨)

// * JpaRepository<Member, Long>의 두 타입 파라미터
// - Member: 이 레포지토리가 다루는 엔티티 타입
// - Long: 그 엔티티의 기본(@Id) 타입
// - 이것만 상속해도 기본 CRUD 메서드가 공짜로 딸려옴
// save(엔티티): 저장 / 수정
// findById(id): 기본키로 1건 조회 -> Optional 반환
// findAll(): 전체조회
// delete(엔티티): 삭제

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUserId(String userId);
    Optional<Member> findByUserId(String userId);
}
