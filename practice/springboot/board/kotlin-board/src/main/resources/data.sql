-- 1) 데이터베이스(스키마) 생성
--    - utf8mb4: 한글은 물론 이모지(4바이트 문자)까지 저장할 수 있는 문자셋
--    - utf8mb4_unicode_ci: 정렬/비교 규칙. ci = case insensitive (대소문자 구분 안 함)
CREATE DATABASE IF NOT EXISTS kotlin_board
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE kotlin_board;

-- 2) 테이블 생성
--    스크립트를 여러 번 돌려도 같은 결과가 되도록 먼저 지우고 다시 만듦
DROP TABLE IF EXISTS board;

-- board 테이블
--   컬럼 이름이 스네이크 케이스(user_id)인데 엔티티 필드는 카멜 케이스(userId)
--   -> 스프링 부트 기본 네이밍 전략이 카멜 케이스를 스네이크 케이스로 자동 변환해 주므로
--      엔티티에 @Column(name = "user_id")를 따로 적지 않아도 매핑됨
CREATE TABLE board (
   id      BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 기본키(PK). AUTO_INCREMENT = JPA의 GenerationType.IDENTITY
   title   VARCHAR(200) NOT NULL,               -- 제목
   content TEXT         NOT NULL,               -- 본문 (VARCHAR로는 부족한 긴 글이라 TEXT)
   user_id VARCHAR(50)  NOT NULL,               -- 작성자 아이디
   created DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 작성 일시
);

-- 3) 목록 페이징을 눈으로 확인하려면 한 페이지(10건)보다 넉넉해야 하므로 23건을 넣음
INSERT INTO board (title, content, user_id, created) VALUES
    ('코틀린 게시판 첫 번째 글', '안녕하세요. 코틀린으로 다시 만든 게시판입니다. 잘 부탁드립니다.', 'hong', '2026-08-01 09:12:00'),
    ('val 과 var 의 차이', 'val 은 읽기 전용(재할당 불가), var 는 재할당 가능. 기본은 val 로 시작하자.', 'kim', '2026-08-02 10:30:00'),
    ('data class 를 써 보니', 'equals/hashCode/toString/copy 를 컴파일러가 만들어 준다. DTO 에 딱이다.', 'lee', '2026-08-03 12:05:00'),
    ('널 안전성(null safety)', '타입 뒤에 ? 가 붙어야만 null 을 담을 수 있다. NPE 가 확 줄었다.', 'park', '2026-08-04 14:20:00'),
    ('엘비스 연산자 ?:', 'val name = input ?: "손님" - 왼쪽이 null 이면 오른쪽 값을 쓴다.', 'hong', '2026-08-05 08:45:00'),
    ('?: throw 조합이 편하다', 'findByIdOrNull(id) ?: throw NotFound... 자바의 orElseThrow 자리다.', 'choi', '2026-08-06 16:00:00'),
    ('생성자 주입이 한 줄', 'class BoardService(private val repo: BoardRepository) - 이게 끝이다.', 'kim', '2026-08-07 11:11:00'),
    ('롬복이 필요 없다', 'getter/setter/생성자/빌더를 언어가 이미 제공한다.', 'lee', '2026-08-08 13:33:00'),
    ('기본 인자와 이름 붙은 인자', '오버로딩을 여러 개 만들 필요가 없어졌다.', 'park', '2026-08-09 09:50:00'),
    ('JPA 엔티티는 open 이어야 한다', 'kotlin-spring / kotlin-jpa 플러그인이 알아서 열어 준다.', 'hong', '2026-08-10 18:24:00'),
    ('when 은 switch 보다 강하다', '값으로도, 조건으로도 분기할 수 있고 식(expression)으로 값을 돌려준다.', 'choi', '2026-08-11 10:10:00'),
    ('문자열 템플릿', '"게시글 $id 를 찾을 수 없습니다" 처럼 바로 끼워 넣는다.', 'kim', '2026-08-12 15:40:00'),
    ('컬렉션 함수', 'map / filter / sortedBy - 스트림 없이도 바로 쓴다.', 'lee', '2026-08-13 09:05:00'),
    ('확장 함수', '남의 클래스에도 내 함수를 붙일 수 있다. Board.toResponse() 처럼.', 'park', '2026-08-14 17:00:00'),
    ('scope 함수 let/apply/also', '상황에 맞게 골라 쓰면 코드가 짧아진다. 남용은 금물.', 'hong', '2026-08-15 11:30:00'),
    ('companion object', '자바의 static 자리. 정적 팩토리 메서드를 여기에 둔다.', 'choi', '2026-08-16 14:15:00'),
    ('페이징은 0부터 시작', 'PageRequest.of(page - 1, size) - 화면은 1페이지, 스프링은 0페이지.', 'kim', '2026-08-17 08:20:00'),
    ('변경 감지(dirty checking)', '트랜잭션 안에서 값만 바꿔도 UPDATE 가 나간다. save 호출이 없다.', 'lee', '2026-08-18 16:45:00'),
    ('@Transactional(readOnly = true)', '조회 전용 트랜잭션. 클래스에 걸고 쓰기 메서드만 다시 덮어쓴다.', 'park', '2026-08-19 10:55:00'),
    ('전역 예외 처리', '@RestControllerAdvice 로 예외를 HTTP 응답으로 한 곳에서 바꾼다.', 'hong', '2026-08-20 13:00:00'),
    ('DTO 와 엔티티를 분리하자', '엔티티를 그대로 응답하면 내부 구조가 그대로 노출된다.', 'choi', '2026-08-21 09:00:00'),
    ('널 아님 단언 !!', 'id 가 Long? 이라도 DB 에서 읽어온 값이면 !! 로 꺼낼 수 있다. 남용은 금물.', 'kim', '2026-08-22 10:20:00'),
    ('스물세 번째 글', '시드 데이터 마지막 글입니다. 수고하셨습니다!', 'lee', '2026-08-23 18:00:00');