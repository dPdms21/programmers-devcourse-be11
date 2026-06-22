package com.example.spring.ch01.ex_1_1;

import com.example.spring.ch01.ex_1_1.dao.UserDAO;
import com.example.spring.ch01.ex_1_1.domain.User;

import java.sql.SQLException;

// * 관심사의 분리
// 개발자가 객체를 설계할 때 가장 염두에 둬야 할 것은 '미래의 변화에 어떻게 대비할 것인가'
// 같은 변경 요청을 두 개발자에게 맡겼을 때, 한 명은 몇 줄만 고치고 다른 한 명은 같은 기능을
// 수정하는 데 5시간이 걸렸다고 해보자
// 이 차이는 분리와 확장을 고려한 설계가 있었느냐 없었느냐에서 비롯

// 고객의 요구사항에서 나오는 모든 변경과 발전은 한 번에 한 가지 관심사에 집중해서 일어남
// 문제는, 변화는 대체로 한 가지 관심사에 집중되지만 그에 따른 작업은 코드 여기저기로 흩어지는 경우가 많다는 점
// 변화가 한 가지 관심사에 집중되어 일어난다면, 우리가 준비할 일은 그 관심사가 코드의 한곳에 모이게 하는 것
// 즉, 관심이 같은 것끼리는 모으고 관심이 다른 것은 따로 떨어뜨려 놓는 것

// 프로그래밍의 기초 개념 중에 '관심사의 분리'가 있음
// 이를 객체지향에 적용하면, 관심이 같은 것끼리는 하나의 객체 안에 또는 가까운 객체로 모으고,
// 관심이 다른 것은 가능한 한 떨어뜨려 서로 영향을 주지 않게 분리하는 것이라고 볼 수 있음

public class Start {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        UserDAO dao = new UserDAO();

        User user = new User();
        user.setId("test1");
        user.setName("test2");
        user.setPassword("1234");

        dao.add(user);
    }
}
