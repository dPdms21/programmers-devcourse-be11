package com.example.jpaboard.domain.repository;

import com.example.jpaboard.domain.entity.QBoard;
import com.example.jpaboard.domain.entity.QComment;
import com.example.jpaboard.domain.entity.QMember;
import com.example.jpaboard.dto.BoardListItemResponseDto;
import com.example.jpaboard.dto.BoardSearchRequestDto;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private static final QBoard board = QBoard.board;
    private static final QComment comment = QComment.comment;
    private static final QMember member = QMember.member;

    // * Projections.constructor
    // - "이 클래스의 생성자를 리플렉션으로 찾아서 끼워 맞춰라"라는 뜻
    // - 클래스 이름과 인자를 "나중에(실행 때)"끼워 맞추므로, 컴파일러는 검사를 못함
    // -> 인자 순서/타입이 생성자와 어긋나면 컴파일은 통과하고 "실행 시" 터짐 (런타임 오류)
    // - 대신 Dto는 QueryDSL을 전혀 모름 (순수한 Dto 상태 유지)
    @Override
    public Page<BoardListItemResponseDto> searchBoards(BoardSearchRequestDto condition, Pageable pageable) {
        List<BoardListItemResponseDto> content = queryFactory.select(
                        Projections.constructor(
                                BoardListItemResponseDto.class,
                                board.id,
                                board.title,
                                board.userId,
                                member.userName,
                                commentCountOf(board), // 서브쿼리
                                board.created
                        )
                )
                .from(board)
                .leftJoin(member).on(board.userId.eq(member.userId))
                .where(
                        titleContains(condition.getTitle()),
                        userIdEquals(condition.getUserId()),
                        createdGoe(condition.getFrom()),
                        createdLoe(condition.getTo())
                )
                .orderBy(board.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(board.count())
                .from(board)
                .where(
                        titleContains(condition.getTitle()),
                        userIdEquals(condition.getUserId()),
                        createdGoe(condition.getFrom()),
                        createdLoe(condition.getTo())
                );

        // * PageableExecutionUtils.getPage: 개수 쿼리를 "필요할 때만" 실행하는 최적화까지 해줌
        // - 예) 마지막 페이지가 아니고 결과가 페이지 크기보다 작으면 굳이 count 쿼리를 안 날림
        // - countQuery::fetchOne을 "지금 실행"이 아니라 "필요하면 실행할 함수"로 넘김

        // - countQuery::fetchOne
        /*
        1) 익명 클래스
        PageableExecutionUtils.getPage(content, pageable, new LongFunction<Long>() {
            @Override
            public Long apply(long value) {
                return countQuery.fetchOne();
            }
        });
         */
        /*
        2) 람다
        PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
         */

        // 3) 메서드 참조
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 제목 부분 일치 (Like %title%). 빈 값이면 조건 없음(null)
    private BooleanExpression titleContains(String title) {
        return (title == null || title.isBlank()) ? null : board.title.contains(title);
    }

    // 작성자 아이디 정확히 일치. 빈 값이면 조건 없음(null)
    private BooleanExpression userIdEquals(String userId) {
        return (userId == null || userId.isBlank()) ? null : board.userId.eq(userId);
    }

    // * goe / loe는 비교 연산자의 약어
    // - gt(Greater Than, >)
    // - goe(Greater Than or Equal, >=)
    // - lt(Less Than, <)
    // - loe(Less Than or Equal, <=)
    // -> 아래 goe + loe 한 쌍이 "from 이상 AND to 이하" => Between 기간 검색 됨
    private BooleanExpression createdGoe(LocalDate from) {
        return from == null ? null : board.created.goe(from.atStartOfDay());
    }

    private BooleanExpression createdLoe(LocalDate to) {
        return to == null ? null : board.created.loe(to.atTime(LocalTime.MAX));
    }

    // * JPAQueryFactory와 JPAExpressions
    // - JPAQueryFactory: EntityManager를 품은 "본 쿼리" 생성기. fetch()/fetchOne()으로 SQL을 "실행"할 수 있음
    // - JPAExpressions: static 유틸. "서브쿼리 표현식(조각)"만 만들고, 실행 능력이 없음
    private Expression<Long> commentCountOf(QBoard board) {
        return JPAExpressions
                .select(comment.count())
                .from(comment)
                .where(comment.board.id.eq(board.id));
    }
}
