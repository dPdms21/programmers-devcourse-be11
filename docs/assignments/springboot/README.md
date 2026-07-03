# Spring Boot 과제 정리

Spring Boot 학습 과정에서 진행한 HTTP, 세션, 쿠키, 외부 API 호출 기반 웹 애플리케이션 과제 모음이다.

각 과제는 문서와 실제 Java 구현 코드를 함께 확인할 수 있도록 정리한다.

## 과제 목록

| 과제 | 문서 | 코드 | 설명 |
| --------- | ---------------------------------------- | ------------------------------------------------------------------------------ | -------------- |
| 세션과 쿠키 | [session-cookie.md](./session-cookie.md) | [🔗 session-cookie/](../../../assignments/springboot/session-cookie/src/main) | 로그인 상태는 세션으로 관리하고 테마와 마지막 방문 기록은 쿠키에 저장하여 사용자별 대시보드를 구현 |
| 날씨 API 호출 | [weather-api.md](./weather-api.md) | [🔗 weather-api/](../../../assignments/springboot/weather-api/src/main) | OpenFeign으로 기상청 초단기실황 API를 호출하고 중첩 JSON 응답을 DTO로 매핑하여 날씨 정보를 조회 |

## 학습 흐름

1. 세션과 쿠키에서는 로그인 상태와 사용자 설정의 저장 위치를 구분하고, 세션 기반 접근 제어와 쿠키 기반 상태 유지를 구현한다.
2. 날씨 API 호출에서는 OpenFeign으로 외부 API를 선언형으로 호출하고, 쿼리 파라미터와 서비스키를 전달하며 중첩 JSON 응답을 DTO로 매핑한다.
