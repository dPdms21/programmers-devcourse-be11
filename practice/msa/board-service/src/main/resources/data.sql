-- [DB per service] board-service 전용 스키마
-- 게시글·댓글의 소유자는 board-service뿐임. member 테이블은 없음 —
-- 계정은 auth-service(board_auth) 소유이고, 작성자 이름은 API Composition으로 조회 (D4)
-- ※ 이 파일은 실행용이 아니라 스키마 기록용 — 수동으로 1회 실행함 (ddl-auto: none)
CREATE DATABASE board_app
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE TABLE board_app.board (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     title VARCHAR(200) NOT NULL,
     content TEXT NOT NULL,
     user_id VARCHAR(50) NOT NULL,   -- 작성자 아이디 (auth의 user.user_id 값 — FK가 아니라 "느슨한 참조": DB가 다르면 FK를 걸 수 없음)
     file_path VARCHAR(255),
     created DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- comment.board_id 는 board.id 를 가리키는 외래키(FK) - "이 댓글이 어느 게시글 것인지"
-- (같은 서비스 안의 테이블끼리는 FK를 유지 — 애그리거트 내부의 일관성은 DB가 지켜줌)
CREATE TABLE board_app.comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    created DATETIME DEFAULT CURRENT_TIMESTAMP,
    board_id BIGINT NOT NULL,
    CONSTRAINT fk_comment_board FOREIGN KEY (board_id) REFERENCES board_app.board (id)
);

-- 시드 데이터 (작성자 hong/kim 등은 auth의 user 테이블에 없어 이름 없이(아이디로) 표시됨 — 정상 폴백)
INSERT INTO board_app.board (title, content, user_id, file_path, created) VALUES
    ('첫 번째 게시글입니다', '안녕하세요, 게시판 첫 글입니다. 잘 부탁드립니다.', 'hong', NULL, '2026-06-01 09:12:00'),
    ('스프링 부트 공부 중', 'JPA랑 Spring Data 개념이 아직 헷갈리네요.', 'kim', NULL, '2026-06-02 10:30:00'),
    ('오늘 점심 추천 받아요', '회사 근처 맛집 아시는 분 있나요?', 'lee', NULL, '2026-06-03 12:05:00'),
    ('첨부파일 테스트', '이미지 첨부가 잘 되는지 확인용 글입니다.', 'park', '/files/2026/06/sample1.png', '2026-06-04 14:20:00'),
    ('AOP 로깅 적용 후기', '컨트롤러 요청마다 로그가 잘 찍혀서 편하네요.', 'hong', NULL, '2026-06-05 08:45:00'),
    ('페이징 처리 질문', 'Pageable은 0부터 시작한다는 걸 이제 알았어요.', 'choi', NULL, '2026-06-06 16:00:00'),
    ('회원가입 기능 완성', '평문 저장이지만 일단 동작은 합니다.', 'kim', NULL, '2026-06-07 11:11:00'),
    ('로그인 세션 유지', 'HttpSession으로 로그인 상태를 관리했습니다.', 'lee', NULL, '2026-06-08 13:33:00'),
    ('오류 공통 처리 정리', '@RestControllerAdvice 정말 편하네요.', 'park', NULL, '2026-06-09 09:50:00'),
    ('주말에 뭐 하세요?', '다들 좋은 주말 보내시길 바랍니다.', 'hong', NULL, '2026-06-10 18:24:00'),
    ('두 번째 첨부 테스트', 'PDF 파일도 업로드 되는지 확인합니다.', 'choi', '/files/2026/06/doc2.pdf', '2026-06-11 10:10:00'),
    ('DDD 패키지 구조 공유', 'domain, service, controller로 나눠봤어요.', 'kim', NULL, '2026-06-12 15:40:00'),
    ('정적 팩토리 메서드 좋네요', 'success(), fail() 이렇게 쓰니 읽기 편합니다.', 'lee', NULL, '2026-06-13 09:05:00'),
    ('Optional 이제 좀 알겠어요', 'orElseGet이랑 orElse 차이가 핵심이네요.', 'park', NULL, '2026-06-14 17:00:00'),
    ('게시판 목록 페이징 완료', '이전/다음 버튼까지 잘 동작합니다.', 'hong', NULL, '2026-06-15 11:30:00'),
    ('람다랑 메서드 참조', '::fail 문법이 처음엔 낯설었어요.', 'choi', NULL, '2026-06-16 14:15:00'),
    ('세 번째 첨부 테스트', '엑셀 파일 업로드 확인용입니다.', 'kim', '/files/2026/06/data3.xlsx', '2026-06-17 08:20:00'),
    ('HikariCP 커넥션 풀', '기본으로 딸려온다는 걸 오늘 배웠습니다.', 'lee', NULL, '2026-06-18 16:45:00'),
    ('SessionConst 상수 분리', '매직 스트링 없애니 훨씬 안전하네요.', 'park', NULL, '2026-06-19 10:55:00'),
    ('스무 번째 게시글', '시드 데이터 마지막 글입니다. 수고하셨습니다!', 'hong', NULL, '2026-06-20 13:00:00');