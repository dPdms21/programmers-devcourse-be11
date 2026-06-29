package com.example.spring.ch05.ex_5_1.service;

import com.example.spring.ch05.ex_5_1.dao.Level;
import com.example.spring.ch05.ex_5_1.dao.UserDAO;
import com.example.spring.ch05.ex_5_1.domain.User;

import java.sql.SQLException;
import java.util.List;

// * UserService - 사용자 레벨 관리 '비즈니스 로직'을 담는 계층

// 왜 DAO가 아니라 별도의 서비스 계층인가?
// - UserDAO는 'DB에 어떻게 넣고 빼는가(데이터 접근)'를 책임짐
// - 레벨 업그레이드 조건/순서 같은 '업무 규칙(비즈니스 로직)'은 데이터 접근과 성격이 다름
// -> 이 둘을 한 클래스에 두면 책임이 섞임 (SRP 위반). 그래서 서비스 계층으로 분리
// - UserService는 UserDAO에 의존하되, 인터페이스가 아니라 구현을 직접 쓰더라도 DI로 주입받음

// [업그레이드 규칙]
// - BASIC  + 로그인 50회 이상  -> SILVER
// - SILVER + 추천 30회 이상    -> GOLD
// - GOLD   -> 더 이상 업그레이드 없음
public class UserService {
    // 업그레이드 기준값을 상수로 둠
    // - 매직 넘버(50, 30)를 코드 곳곳에 흩지 않고 한곳에서 의미를 드러냄
    // - 기준이 바뀌면 여기만 고치면 됨 (변경 지점의 집중)
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    private UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // 신규가입
    public void add(User user) throws SQLException, ClassNotFoundException {
        user.setLevel( Level.BASIC );
        userDAO.add(user);
    }

    // 업그레이드 담당
    public void upgradeLevels() throws SQLException, ClassNotFoundException {
        List<User> users = userDAO.getAll();

        for ( User user : users ) {
            if ( canUpgrade(user) ) {
                upgradeLevel(user);
            }
        }

        // [짚고 넘어갈 점 -> 다음 단계 추상화]
        // 이 반복 도중 중간에서 예외가 나면, 앞쪽 사용자는 이미 update 되고 뒤쪽은 안 된 채로 끝남
        // '전부 성공 아니면 전부 취소(원자성)'가 보장되지 않는 것
        // -> 이를 해결하는 것이 '트랜잭션'이고, 스프링이 이를 기술과 무관하게 다루도록 해주는 것이
        //    바로 5장의 핵심인 '트랜잭션 서비스 추상화' (이어지는 예제에서 다룸)
    }

    // '올릴 수 있는가'
    private boolean canUpgrade(User user) {
        Level curLevel = user.getLevel();
        switch (curLevel) {
            case BASIC:
                return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
            case SILVER:
                return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            case GOLD:
                return false;
            default:
                throw new IllegalStateException("Unexpected value: " + curLevel);
        }
    }

    // 실제 업그레이드
    protected void upgradeLevel(User user) throws SQLException, ClassNotFoundException {
        user.upgradeLevel();
        userDAO.update(user);
    }
}
