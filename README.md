# Programmers Devcourse BE 11

> [11기] K-Digital Training: 클라우드 기반 백엔드 엔지니어링

프로그래머스 데브코스 백엔드 11기 과정에서 학습한 내용을 정리하는 저장소입니다.

강의 정리, Java·Spring·Spring Boot 실습과 과제, Java·SQL 코딩 테스트, TIL을 분리하여 관리합니다.

---

## Directory Guide

| Path                          | Description                      |
| ----------------------------- | -------------------------------- |
| [assignments/](./assignments) | Java·Spring·Spring Boot 과제 코드    |
| [codingtest/](./codingtest)   | Java·SQL 코딩 테스트 풀이               |
| [docs/](./docs)               | 과제 문서 및 실습 보조자료                  |
| [lectures/](./lectures)       | 강의 내용 및 개념 정리                    |
| [practice/](./practice)       | Java·Spring·Spring Boot 수업 실습 코드 |
| [til/](./til)                 | 날짜별 학습 회고                        |

---

## Directory Structure

```text
programmers-devcourse-be11/
│
├── assignments/
│   ├── java/                   # Java 과제 코드
│   ├── spring/                 # Spring 과제 코드
│   └── springboot/             # Spring Boot 과제 코드
│
├── codingtest/
│   ├── java/                   # Java 코딩 테스트 풀이
│   └── sql/                    # SQL 코딩 테스트 풀이
│
├── docs/
│   ├── assignments/
│   │   ├── java/               # Java 과제 문서
│   │   ├── spring/             # Spring 과제 문서
│   │   └── springboot/         # Spring Boot 과제 문서
│   │
│   └── practice/               # 실습 보조자료
│
├── lectures/                   # 강의 내용 및 개념 정리
│
├── practice/
│   ├── java/                   # Java 수업 실습 코드
│   ├── spring/                 # Spring 수업 실습 코드
│   └── springboot/             # Spring Boot 수업 실습 코드
│
└── til/
    └── YYYY-MM/                # 월별 TIL
```

---

## Commit Convention

```text
docs(assignments): 과제 문서 정리
docs(lectures): YYYY-MM-DD 강의 내용 정리
docs(practice): 실습 보조자료 정리
docs(til): YYYY-MM-DD 학습 회고 정리

study(java): YYYY-MM-DD Java 실습·과제 및 학습 코드 작성
study(spring): YYYY-MM-DD Spring 실습·과제 및 학습 코드 작성
study(springboot): YYYY-MM-DD Spring Boot 실습·과제 및 학습 코드 작성

solve(java): YYYY-MM-DD Java 문제명 (풀이 방식)
solve(sql): YYYY-MM-DD SQL 문제명 (풀이 방식)

fix(scope): scope 코드 오류 수정
style(scope): scope 코드 형식 수정
refactor(scope): scope 코드 구조 개선
chore(project): 프로젝트 설정 및 구조 변경
```

※ `scope`에는 `java`, `spring`, `springboot` 등 실제 변경 영역을 작성한다.
