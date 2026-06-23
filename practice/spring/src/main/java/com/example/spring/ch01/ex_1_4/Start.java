package com.example.spring.ch01.ex_1_4;

import com.example.spring.ch01.ex_1_4.dao.DaoFactory;
import com.example.spring.ch01.ex_1_4.dao.UserDAO;

// * 문제점?
// Client는 기존에 UserDAO가 직접 담당하는 기능,
// 즉 어떤 ConnectionMaker 구현 클래스를 사용할지를 결정하는 기능을 얼떨결에 떠맡았음
// 그런데 원래 Start.java는 UserDAO의 기능이 잘 동작하는지를 테스트하려고 만든 것이
// 지금 또 다른 책임까지 떠맡고 있으니 뭔가 문제가 있음

// '오브젝트 팩토리'
// 객체의 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 일을 하는 오브젝트를 '팩토리'라고 함

// * 제어의 역전
// 제어의 역전이란 오브젝트가 자신이 사용할 오브젝트를 스스로 선택 및 생성하지 않고,
// 또 자신이 언제 어떻게 만들어지는지조차 모른 채 그 제어권을 외부에 넘기는 것.

// UserDAO 입장에서 두 가지 제어권이 사라짐
// 1. 어떤 ConnectionMaker를 쓸지에 대한 제어권
//  public UserDAO(SimpleConnectionMaker simpleConnectionMaker) {
//      this.simpleConnectionMaker = simpleConnectionMaker;
//  }

// 2. 자기 자신의 생성에 대한 제어권
// UserDAO가 언제, 어떤 의존성과 함께 만들어지는지를 DaoFactory가 결정
//  public UserDAO userDao() {
//      UserDAO userDAO = new UserDAO(connectionMaker());  // 생성 + 관계설정을 팩토리가 담당
//      return userDAO;
//  }

//  private SimpleConnectionMaker connectionMaker() {
//      return new DConnectionMaker();
//  }

// 즉, "어떤 구현을 쓸지 결정"하고 "오브젝트를 생성 및 연결"하는 제어 권한이 UserDAO에서 DaoFactory로 넘어감
// 이것이 바로 '제어의 역전'

//  원래 테스트용이던 클라이언트가 "어떤 ConnectionMaker를 쓸지 결정하는 책임"까지 떠맡던 문제를, 팩토리를 도입해
//  "컴포넌트 역할 오브젝트"와 "구조를 결정하는 오브젝트"를 분리한 것

// 템플릿 메서드는 제어의 역전이라는 개념을 활용해 문제를 해결하는 디자인 패턴이라고 볼 수 있음

// 스프링은 제어의 역전을 모든 기능의 기초가 되는 기반 기술로 삼고 있으며,
// 제어의 역전을 극한까지 사용하는 프레임워크

public class Start {
    public static void main(String[] args) {
        UserDAO dao = new DaoFactory().userDAO();
    }
}
