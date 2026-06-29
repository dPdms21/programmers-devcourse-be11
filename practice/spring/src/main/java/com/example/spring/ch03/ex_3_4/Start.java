package com.example.spring.ch03.ex_3_4;

// * 문제점
// UserDAO의 컨텍스트 메서드인 jdbcContextWithStatementStrategy()는
// PreparedStatement를 실행하는 기능을 가진 메서드에서 공유할 수 있음
// 즉 다른 DAO에서도 사용이 가능

// '클래스 분리'

// * JdbcContext를 왜 UserDAO 안에서 직접 new 하지 않고, 빈으로 등록해 주입받나?
// 첫째는 JdbcContext가 스프링 컨테이너의 싱글톤 레지스트리에서 관리되는 싱글톤 빈이 되기 때문
//   - JdbcContext는 필드에 SimpleConnectionMaker(또는 DataSource) 하나만 가질 뿐,
//     요청마다 바뀌는 데이터(상태)를 전혀 담지 않음. 즉 '읽기 전용 의존성'만 갖는 무상태 오브젝트
//     -> 그래서 여러 DAO가 하나의 인스턴스를 공유해도 안전 (앞서 배운 싱글톤의 무상태 원칙)
//   - 또한 JdbcContext는 변경될 일이 거의 없고, JDBC 컨텍스트 로직은 애플리케이션 전체에서 동일
//     이런 오브젝트는 DAO마다 new로 새로 만들면 메모리/생성 비용만 낭비
//     -> 스프링 컨테이너가 '딱 하나(싱글톤)'만 만들어 등록하고 모두가 공유하게 하는 것이 자연스러움
//   - 즉 "무상태 + 변경 없음 + 공유 가능"이라는 싱글톤 빈의 조건을 완벽히 만족하므로,
//     컨테이너의 싱글톤 레지스트리가 관리하는 빈으로 두는 것이 가장 적절

// 둘째는 JdbcContext가 DI를 통해 다른 빈에 의존하고 있기 때문
//   - JdbcContext는 혼자 동작하지 못하고, 커넥션을 만들어줄 SimpleConnectionMaker(또는 DataSource)에 의존
//     이렇게 '다른 빈에 의존하는 오브젝트'는, 그 의존관계를 누군가 맺어줘야 함
//   - DI의 핵심은 "주입받는 쪽도, 주입하는 쪽도 모두 컨테이너가 관리하는 빈이어야"
//     컨테이너가 그 관계를 대신 설정해줄 수 있다는 점
//     -> 만약 JdbcContext를 UserDAO가 코드에서 직접 new로 만들면 (빈이 아니면),
//        그 안에 들어갈 connectionMaker도 UserDAO가 직접 찾아 넣어줘야 해서
//        UserDAO가 불필요하게 JdbcContext의 내부 의존관계까지 알게 됨 (결합도 증가)
//   - 반대로 JdbcContext를 빈으로 등록하면, 컨테이너가
//     connectionMaker -> jdbcContext -> userDao 로 이어지는 의존관계를 알아서 엮어줌
//     -> 그래서 'DI를 통해 다른 빈에 의존하는' 오브젝트는 그 자신도 빈이 되는 것이 옳음

public class Start {
    public static void main(String[] args) {

    }
}
