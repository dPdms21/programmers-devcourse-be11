package com.example.jpaboard.domain.repository;

import com.example.jpaboard.domain.entity.Board;
import com.example.jpaboard.dto.BoardListItemResponseDto;
import com.example.jpaboard.dto.BoardSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

// * "직접 짠 쿼리(QueryDSL)"를 위한 커스텀 레포지토리 인터페이스
// - BoardRepository는 Spring Data가 구현체를 자동으로 생성해주는 인터페이스여서, 직접 코드를 못 넣음
// - 그래서 "직접 구현할 메서드"는 이 별도 인터페이스에 선언하고, 실제 코드는 BoardRepositoryImpl에 짬
// - 그리고 BoardRepository가 이 인터페이스를 함께 상속하면(extends)
//   Spring Data가 "자동 생성 메서드 + 우리가 짠 메서드"를 하나의 레포지토리로 합쳐줌

// * 이름 규칙이 중요: 구현 클래스는 반드시 "<레포지토리 이름> + Impl"이어야 함
// - BoardRepositoryCustom의 구현체 이름은 BoardRepositoryImpl로 맞춰야 스프링이 자동으로 연결
// - BoardRepositoryCustomImpl이 아니라 BoardRepositoryImpl - 붙이는 기준은 "메인 레포지토리 이름"
public interface BoardRepositoryCustom {
    Page<BoardListItemResponseDto> searchBoards(BoardSearchRequestDto condition, Pageable pageable);
    Optional<Board> findWithComments(Long id);
}
