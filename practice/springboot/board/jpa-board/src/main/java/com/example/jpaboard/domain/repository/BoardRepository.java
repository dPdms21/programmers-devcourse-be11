package com.example.jpaboard.domain.repository;

import com.example.jpaboard.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;


// * "extends A, B" - 클래스와 인터페이스의 규칙이 다름
// - 클래스: extends는 딱 하나만 (다중 상속 금지)
// - 인터페이스: extends를 여러 개 나열 가능

// * 왜 클래스만 금지인가? - "다이아몬드 문제" 때문
// - 클래스는 "상태(필드) + 구현(메서드 몸통)"을 물려줌
// -> 두 부모가 같은 필드/메서드를 다르게 가지면 자식은 어느 쪽을 따라야할지 답이 없음 (모호성)
// - 인터페이스는 기본적으로 구현 없는 "약속(추상 메서드) 목록"만 물려줌
// -> 겹쳐도 그냥 요구사항의 합집합일 뿐, 충돌한 "내용물"이 없음
// -> 양쪽에 같은 시그니처가 있어도 "지켜야 할 약속 1개"로 합쳐짐
// -> default메서드는 몸통을 가질 수 있는데, 그땐 컴파일 에러를 내서 자식이 직접 오버라이딩 하도록 강제
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
}
