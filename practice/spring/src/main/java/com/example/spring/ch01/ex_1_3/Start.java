package com.example.spring.ch01.ex_1_3;

import com.example.spring.ch01.ex_1_3.dao.DConnectionMaker;
import com.example.spring.ch01.ex_1_3.dao.SimpleConnectionMaker;
import com.example.spring.ch01.ex_1_3.dao.UserDAO;

// * 개방 폐쇄 원칙(OCP, Open-Closed Principle)
// '클래스나 모듈은 확장에는 열려 있어야 하고 변경에는 닫혀 있어야 함'
// 반면 인터페이스를 이용하는 클래스는 자신의 변화가 불필요하게 일어나지 않도록 굳게 폐쇄되어있음

// '높은 응집도와 낮은 결합도'
// - 높은 응집도
// 변경이 일어날 때 모듈의 많은 부분이 함께 바뀐다면 응집도가 높다고 말할 수 있음
// 만약 모듈의 일부분에만 변경이 일어나도 된다면,
// 모듈 전체에서 어떤 부분이 바뀌어야하는지 파악해야 하고,
// 또 그 변경으로 인해 바뀌지 않는 부분에는 다른 영향을 미치지 않는지 확인해야하는 이중의 부담이 생김
// - 낮은 결합도
// 책임과 관심사가 다른 오브젝트 또는 모듈과는 낮은 결합도, 즉 느슨하게 연결된 형태를 유지하는 것이 바람직
// 느슨한 연결은 관계를 유지하는 데 꼭 필요한 최소한의 방법만 간접적인 형태로 제공하고,
// 나머지는 서로 독립적이고 알 필요도 없게 만들어주는 것
// 하나의 변경이 발생할 때 마치 파문이 이는 것처럼 여타 모듈과 객체로 변경에 대한 요구가 전파되지 않는 상태를 말함

// ** 객체지향 설계 원칙(SOLID)
// - SRP(The Single Responsibility Principle) : 단일 책임 원칙
// - OCP : 개방 폐쇄 원칙
// - LSP : 리스코프 치환 원칙
// - ISP : 인터페이스 분리 원칙
// - DIP : 의존관계 역전 원칙

// * 전략 패턴
// 자신의 기능 맥락에서, 필요에 따라 변경이 필요한 알고리즘을 인터페이스를 통해 통째로 외부로 분리시키고,
// 이를 구현한 구체적인 알고리즘 클래스를 필요에 따라 바꿔서 사용할 수 있게 하는 디자인 패턴
// UserDAO는 컨텍스트에 해당
// 컨텍스트(UserDAO)를 사용하는 클라이언트(Start)는
// 컨텍스트가 사용할 전략(SimpleConnectionMaker를 구현한 클래스)을 컨텍스트의 생성자 등을 통해 제공해주는 게 일반적

public class Start {
    public static void main(String[] args) {
        SimpleConnectionMaker conn = new DConnectionMaker();
        UserDAO dao = new UserDAO(conn);
    }
}
