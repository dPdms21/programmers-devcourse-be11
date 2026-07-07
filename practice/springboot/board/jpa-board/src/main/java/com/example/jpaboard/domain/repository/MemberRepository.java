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

// * 쿼리 메서드(Query Method)
// - 기본 CRUD 외에 "조건이 있는 조회"가 필요할 때, 메서드 "이름"만 규칙대로 지으면
//   Spring Data JPA가 그 이름을 해석(파싱)해서 SQL(JPQL)을 자동으로 만들어줌
// 즉, 이름이 곧 쿼리

// * 네이밍 규칙 구조: [동사(반환 형태)] + By + [조건이 될 필드명]
// (1) 동사 부분 "무엇을/어떤 형태로" 반환할지 결정
// - find...     : 엔티티(들)를 조회  예) findByUserId -> SELECT * FROM member WHERE user_id = ?;
// - exists...   : 존재 여부(boolean) 예) existsByUserId -> 있으면 true
// - count...    : 개수(long)         예) countByUserName
// - delete...   : 삭제
// (2) By        : "여기서부터 조건(WHERE)이 시작된다"는 구분자
// (3) 조건 필드 : 엔티티의 "필드 이름"을 그대로 씀 (DB 컬럼명인 user_id가 아니라 자바 필드명 userId)
// -> 대소문자까지 필드와 정확히 맞아야 함

// * 여러 조건을 조합할 수도 있음 (규칙만 지키면 이름이 길어져도 동작)
//   findByUserIdAndPassword(String userId, String password)  -> WHERE user_id = ? AND password = ?
//   findByUserNameOrderByIdDesc(String userName)             -> WHERE user_name = ? ORDER BY id DESC
//   findByUserNameContaining(String keyword)                 -> WHERE user_name LIKE %?%
//   ...And / Or / Between / LessThan / GreaterThan / Like / Containing / OrderBy... 등 키워드 조합 가능
//   ※ 파라미터의 "개수와 순서"는 이름에 등장하는 조건 필드 순서와 일치해야 함

// * 이름만으로 안 되는 복잡한 쿼리는?
// - 조인이 많거나 조건이 복잡하면 이름이 감당 안 될 만큼 길어짐
// - 그럴 때는 메서드 위에 @Query("JPQL 또는 SQL") 로 쿼리를 직접 작성 (이 클래스엔 아직 필요 없어 생략)

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUserId(String userId);
    Optional<Member> findByUserId(String userId);
}
