# Spring Boot 과제 정리

Spring Boot 학습 과정에서 진행한 HTTP, 세션, 쿠키, 외부 API 호출, JPA, Spring Security 기반 웹 애플리케이션 과제 모음이다.

각 과제는 문서와 실제 Java 구현 코드를 함께 확인할 수 있도록 정리한다.

## 세션과 쿠키

| 과제 | 문서 | 코드 | 설명 |
| --- | --- | --- | --- |
| 세션과 쿠키 | [session-cookie.md](./session-cookie.md) | [🔗 session-cookie/](../../../assignments/springboot/session-cookie/src/main) | 로그인 상태는 세션으로 관리하고 테마와 마지막 방문 기록은 쿠키에 저장하여 사용자별 대시보드를 구현 |

## 외부 API 호출

| 과제 | 문서 | 코드 | 설명 |
| --- | --- | --- | --- |
| 날씨 API 호출 | [weather-api.md](./weather-api.md) | [🔗 weather-api/](../../../assignments/springboot/weather-api/src/main) | OpenFeign으로 기상청 초단기실황 API를 호출하고 중첩 JSON 응답을 DTO로 매핑하여 날씨 정보를 조회 |

## JPA

| 과제 | 문서 | 코드 | 설명 |
| --- | --- | --- | --- |
| JPA 게시판 | [jpa-board.md](./jpa-board.md) | [🔗 jpa-board/](../../../assignments/springboot/jpa-board/src/main) | JPA로 회원 정보를 저장하고 계층 구조와 예외 공통 처리를 적용하여 회원가입 기능을 구현 |
| JPA 게시판 2 | [jpa-board-2.md](./jpa-board-2.md) | [🔗 jpa-board/](../../../assignments/springboot/jpa-board/src/main) | JPA로 저장된 회원 정보를 조회하고 Optional로 로그인 성공 여부를 판단한 뒤 세션에 로그인 상태를 저장 |
| JPA 게시판 3 | [jpa-board-3.md](./jpa-board-3.md) | [🔗 jpa-board/](../../../assignments/springboot/jpa-board/src/main) | JPA 게시글 목록을 페이지 단위로 조회하고 상세 조회와 없는 글 404 예외 처리를 구현 |
| JPA 게시판 4 | [jpa-board-4.md](./jpa-board-4.md) | [🔗 jpa-board/](../../../assignments/springboot/jpa-board/src/main) | 게시글 작성 시 multipart 요청으로 첨부파일을 업로드하고, 저장된 파일을 다운로드하는 기능을 구현 |
| JPA 게시판 5 | [jpa-board-5.md](./jpa-board-5.md) | [🔗 jpa-board/](../../../assignments/springboot/jpa-board/src/main) | JPA 변경 감지로 게시글을 수정하고 첨부파일 삭제 순서와 FileService 분리를 적용해 게시글 삭제 기능을 구현 |
| JPA 게시판 6 | [jpa-board-6.md](./jpa-board-6.md) | [🔗 jpa-board/](../../../assignments/springboot/jpa-board/src/main) | AOP로 컨트롤러 요청 로깅을 적용하고 서비스, 리포지토리, 컨트롤러 계층 테스트를 작성해 게시판 기능을 검증 |

## Spring Security

| 과제 | 문서 | 코드 | 설명 |
| --- | --- | --- | --- |
| HTTP Basic 인증 | [http-basic.md](./http-basic.md) | [🔗 http-basic/](../../../assignments/springboot/http-basic/src/main) | Spring Security 필터 체인에 HTTP Basic 인증을 적용하고 인메모리 사용자 인증과 브라우저 자격 증명 캐싱을 확인 |
| Form Login 인증 | [form-login.md](./form-login.md) | [🔗 form-login/](../../../assignments/springboot/form-login/src/main) | DB 회원 정보를 BCrypt로 암호화하고 Spring Security 필터와 세션을 이용해 회원가입, 로그인과 로그아웃을 구현 |
| JWT 토큰 인증 | [token.md](./token.md) | [🔗 token/](../../../assignments/springboot/token/src/main) | JWT Access·Refresh Token을 발급하고 Bearer 인증 필터와 역할 기반 인가, 토큰 재발급 흐름을 구현 |
| OAuth2 소셜 로그인 | [oauth2.md](./oauth2.md) | [🔗 oauth2/](../../../assignments/springboot/oauth2/src/main) | 카카오 OAuth2 인가 코드 방식으로 소셜 회원을 인증하고 가입 동의 후 기존 JWT 인증 체계와 연결 |

## 학습 흐름

1. 세션과 쿠키에서는 로그인 상태와 사용자 설정의 저장 위치를 구분하고, 세션 기반 접근 제어와 쿠키 기반 상태 유지를 구현한다.
2. 외부 API 호출에서는 OpenFeign으로 외부 API를 선언형으로 호출하고, 쿼리 파라미터와 서비스키를 전달하며 중첩 JSON 응답을 DTO로 매핑한다.
3. JPA에서는 계층 구조를 분리해 회원과 게시판 도메인을 구현하고, 데이터 저장·조회·예외 처리와 AOP 로깅, 계층별 테스트를 적용해 JPA 기반 웹 애플리케이션 흐름을 정리한다.
4. Spring Security에서는 HTTP Basic, Form Login, JWT 토큰 인증과 OAuth2 소셜 로그인을 적용하고, 인증 방식별 처리 주체와 상태 관리, 토큰 재발급, 역할별 인가 및 소셜 회원 연동 흐름을 정리한다.
