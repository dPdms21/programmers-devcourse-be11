package com.example.spring.ch02.ex_2_1;

import com.example.spring.ch02.ex_2_1.dao.DaoFactory;
import com.example.spring.ch02.ex_2_1.domain.User;
import com.example.spring.ch02.ex_2_1.dao.UserDAO;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

// * 문제점
// Start.java 테스트의 문제점
// - 수동 확인 작업의 번거로움
// 콘솔에 출력된 값을 보고 등록과 조회가 성공했는지 확인하는 것은 사람의 책임

// - 실행 작업의 번거로움
// 만약 DAO가 수백 개가 되고 각각의 main() 메서드도 그만큼 만들어진다면,
// 전체 기능을 테스트하기 위해 main() 메서드를 수백 번 실행해야 함

// * 단위 테스트
// 테스트는 가능한 한 작은 단위로 나누어 집중해서 수행할 수 있어야 함
// 관심사의 분리 원리가 테스트에도 적용됨
// 단위 테스트를 하는 이유는 개발자가 설계하고 만든 코드가 의도한 대로 동작하는지를
// 빠르고 반복적으로 확인하기 위해서임

// 다만 Spring 컨테이너를 실행하고 실제 DB에 연결하는 테스트는
// 여러 구성 요소가 함께 동작하므로 엄밀히 말하면 단위 테스트보다 통합 테스트에 가까움

// * 자동 수행 테스트 코드
// 테스트는 자동으로 수행할 수 있도록 코드로 작성하는 것이 중요
// 애플리케이션을 구성하는 클래스 안에 테스트 코드를 포함하기보다는
// 별도의 테스트 클래스를 만들어 테스트 코드를 작성하는 편이 나음
// 자동으로 수행되는 테스트의 장점은 같은 검증을 자주 반복할 수 있다는 것

// * 테스트 결과
// 테스트 결과는 성공과 실패로 구분할 수 있음
// 실패는 테스트 실행 중 예외가 발생한 경우와,
// 예외는 발생하지 않았지만 실제 결과가 기대한 결과와 다른 경우로 나눌 수 있음

// * JUnit
// JUnit은 Java 테스트 코드를 작성하고 자동으로 실행할 수 있도록 지원하는
// 대표적인 테스트 프레임워크
// Start.java의 main()을 직접 실행하고 콘솔 출력을 눈으로 확인하던 방식을 대체할 수 있음

// - 프레임워크의 특징: 제어의 역전(IoC)
// main() 방식에서는 개발자가 테스트 실행 흐름을 직접 제어하지만,
// JUnit에서는 테스트 메서드만 작성하면 프레임워크가 해당 메서드를 찾아 실행함
// 개발자가 프레임워크를 호출해 흐름을 제어하는 것이 아니라,
// 프레임워크가 개발자의 테스트 코드를 호출하고 실행 흐름을 관리함

// - 기본 사용법(JUnit 5 기준)
// · @Test: 해당 메서드가 테스트 메서드임을 표시
// · @BeforeEach: 각 @Test 실행 전에 매번 실행하며 공통 준비 작업을 담당
// · @AfterEach: 각 @Test 실행 후에 매번 실행하며 공통 정리 작업을 담당
// · assertEquals(기대값, 실제값) 등의 단언 메서드로 결과를 코드에서 검증
//   → 사람이 콘솔 출력을 보고 판단할 필요가 없음
//   → 기대값과 실제값이 다르거나 예외가 발생하면 테스트 실패로 처리됨

// - 위 코드를 JUnit 테스트로 바꾸면 다음과 같은 형태가 됨:
// @Test
// void getUser() throws Exception {
//      var context = new AnnotationConfigApplicationContext(DaoFactory.class);
//      UserDAO userDao = context.getBean("userDAO", UserDAO.class);
//      User user = userDao.get("test1");
//      assertEquals("기대하는이름", user.getName());  // System.out.println 대신 단언으로 검증
// }

public class Start {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        UserDAO userDAO = context.getBean("userDAO", UserDAO.class);
        User user = userDAO.get("test1");
        System.out.println(user.getName());
    }
}
