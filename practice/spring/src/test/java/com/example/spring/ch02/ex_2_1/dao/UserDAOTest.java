package com.example.spring.ch02.ex_2_1.dao;

import com.example.spring.ch02.ex_2_1.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

// * UserDAO 단위(통합) 테스트 (JUnit 5)
// Start.java의 main() + System.out.println 방식을 JUnit 테스트로 옮긴 것
// - 사람이 콘솔을 눈으로 확인하지 않아도, assert(단언)로 결과를 '코드가' 자동 검증
// - DAO가 늘어나도 테스트 메서드만 추가하면 한 번에 자동 실행할 수 있음

// * @Autowired로 빈을 주입받으려면? -> 테스트를 '스프링 컨테이너 안에서' 실행해야 함
// @SpringJUnitConfig(DaoFactory.class)가 그 역할을 함. 이 한 줄은 사실 두 가지를 합친 것
// 1) @ExtendWith(SpringExtension.class): JUnit5에 스프링 테스트 기능을 끼워 넣음
// 2) @ContextConfiguration(classes = DaoFactory.class): 어떤 설정으로 컨테이너를 띄울지 지정
// -> 그래서 테스트 실행 시 스프링이 DaoFactory로 컨텍스트를 만들고,
//    @Autowired가 붙은 필드에 맞는 타입의 빈(UserDAO)을 자동으로 꽂아줌 (필드 주입)
// (애너테이션이 없으면 컨테이너가 없어 userDao가 null -> NullPointerException 발생)

@SpringJUnitConfig(DaoFactory.class)
class UserDAOTest {
    // @Autowired: 타입(UserDAO)이 일치하는 빈을 스프링이 찾아 이 필드에 주입
    @Autowired // 더 이상 AnnotationConfigApplicationContext와 getBean()을 직접 호출할 필요가 없음
    private UserDAO userDAO;

    // @BeforeEach : 각 @Test 메서드가 실행되기 '직전'마다 매번 호출됨 (공통 준비 작업)
    // - deleteAll()로 '항상 동일한 출발 상태(0건)'에서 테스트가 시작되게 함
    // -> 테스트끼리 데이터가 섞이지 않고, 같은 테스트를 몇 번 돌려도 결과가 같음 (반복 가능)
    // (이전에는 고정 id 때문에 두 번 실행하면 중복키로 실패했는데, 그 문제가 사라짐)
    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        userDAO.deleteAll();
    }

    private User newUser(String id, String name, String password) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setPassword(password);

        return user;
    }

    // @Test : add() 자체가 정상 동작하는지에 집중한 테스트
    @Test
    void add_메서드_테스트() throws SQLException, ClassNotFoundException {
        // given
        User user = new User();
        user.setId("test123");
        user.setName("test234");
        user.setPassword("321");

        // when
        userDAO.add(user);

        // then
        assertEquals("test234", userDAO.get("test123").getName());
    }

    @Test
    void get_메서드_테스트() throws SQLException, ClassNotFoundException {
        // given
        User user = newUser("test123", "test234", "321");

        // when
        userDAO.add(user);

        // then
        assertEquals("test234", userDAO.get("test123").getName());
    }

    @Test
    void add_중복_id_예외() throws SQLException, ClassNotFoundException {

        final User user = newUser("dup_id", "사용자1", "3210");

        // 정상동작
        userDAO.add(user);

        // 익명클래스
        /*
        Executable action = new Executable() {
            @Override
            public void execute() throws Throwable {
                // 예외
                userDAO.add(user);
            }
        };

        assertThrows(SQLException.class, action);
        */

        assertThrows(SQLException.class, () -> userDAO.add(user));
    }

    @Test
    void get_없는_id_예외() {
        assertThrows(SQLException.class, () -> userDAO.get("존재하지_않는_id"));
    }

    @Disabled("일부러 틀린 기댓값을 넣은 학습용 실패 예제 - 실패 메시지를 보고 싶을 때만 활성화")
    @Test
    void 일부러_실패하는_테스트() throws SQLException, ClassNotFoundException {
        userDAO.add( newUser("fail_demo", "fail", "1234") );

        assertEquals(2, userDAO.getCount());
    }
}