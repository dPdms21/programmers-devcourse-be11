package com.example.jpaboard.domain.repository;

import com.example.jpaboard.domain.entity.Board;
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
import java.util.Optional;

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

    // 만약 그냥 board 하나만 조회한 뒤 board.getComments()를 순회하면?
    // - comments는 LAZY라, 순회하는 순간 "댓글을 가져오는 SQL이 추가로" 나감
    // - 게시글이 여러 개면 게시글마다 댓글 쿼리가 또 나가서 총 1 + N 번 (N+1 문제)
    // - N + 1
    //   1 = 처음 의도하고 날린 쿼리 1번
    //   N = 그 결과 행 수만큼 "추가로" 나가는 쿼리 N번 (게시글마다 댓글 조회 1번씩)

    // # 실제 SQL 흐름 (게시글이 100개라고 가정)
    // List<Board> boards = boardRepository.findAll();

    // for (Board board : boards) {
    //     board.getComments(); // 순회하며 LAZY 가 깨어날 때마다...
    // }
    // select * from comments where board_id = 1;
    // select * from comments where board_id = 2;
    // select * from comments where board_id = 3;
    // ...
    // select * from comments where board_id = 100;
    // -> DB에는 총 101번의 쿼리가 나감

    // 참고: LAZY라서 생기는 문제는 아님
    // - EAGER로 바꿔도 N + 1은 그대로. 추가 쿼리가 "순회할 때"가 아니라 "조회 직후"에 나갈 뿐
    // - 근본 원인은 "연관 데이터를 행마다 따로 가져오는 것" => fetch join

    // * fetchJoin()을 붙이면, 게시글과 댓글을 "조인해서 한 방에" 가져와 이 문제를 없앰
    // - leftJoin(board.comments, comment): 매핑된 연관관계를 따라가는 조인
    // - .fetchJoin(): "조인한 comment를 board.comments에 즉시 채우라"는 지시
    @Override
    public Optional<Board> findWithComments(Long id) {
        Board result = queryFactory
                .selectFrom(board)
                .leftJoin(board.comments, comment).fetchJoin()
                .where(board.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
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
