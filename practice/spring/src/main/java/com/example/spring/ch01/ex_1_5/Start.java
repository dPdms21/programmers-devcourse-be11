package com.example.spring.ch01.ex_1_5;

import com.example.spring.ch01.ex_1_5.dao.DaoFactory;
import com.example.spring.ch01.ex_1_5.dao.UserDAO;
import com.example.spring.ch01.ex_1_5.domain.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

// * 스프링의 제어의 역전(IoC, Inversion of Control)
// 이제 DaoFactory를 스프링에서 사용할 수 있도록 변신
// 스프링에서는 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트를 빈(Bean)이라고 부름
// 스프링 빈은 스프링 컨테이너가 생성하고 관계 설정, 사용, 생명주기 등을 관리하는 오브젝트를 의미
// 자바빈(JavaBean)과 스프링 빈(Spring Bean)은 서로 다른 개념
// 스프링에서는 빈의 생성과 관계 설정 같은 제어를 담당하는 IoC 오브젝트를 빈 팩토리라고 함
// 정확히는 빈 팩토리보다 이를 확장한 애플리케이션 컨텍스트를 주로 사용
// 애플리케이션 컨텍스트는 IoC 방식으로 만들어진 일종의 빈 팩토리

// * 컴포넌트(Component)
// 컴포넌트란 애플리케이션을 구성하는, 독립적으로 갈아 끼울 수 있는 부품 같은 오브젝트를 말함
// 우리 예제에서는 실제 일을 하는 UserDAO, DConnectionMaker 같은 오브젝트가 컴포넌트에 해당
// 컴포넌트의 핵심은 "자신이 할 일(비즈니스 로직)에만 집중하고,
// 자신이 어떻게 만들어지는지, 누구와 연결되는지는 신경 쓰지 않는다"는 점
// 즉 UserDAO는 "DB에 사용자를 넣고 빼는 일"만 알면 되고,
// 어떤 ConnectionMaker 구현체와 연결될지는 외부에 맡김
// 이렇게 만들어진 컴포넌트는 코드 수정 없이 다른 컴포넌트로 교체하거나 재사용할 수 있음
// (예: DConnectionMaker -> NConnectionMaker 로 교체해도 UserDAO 코드는 그대로)

// * 컨테이너(Container)
// 컨테이너란 이 컴포넌트들을 담아두고, 생성하고, 서로 연결(관계 설정)해주고,
// 생명주기(언제 만들고 언제 없앨지)까지 관리해주는 실행 환경을 말함
// 컴포넌트가 '부품'이라면, 컨테이너는 그 부품들을 조립하고 가동시키는 '공장'인 셈

// 우리가 직접 만든 DaoFactory가 바로 컨테이너의 아주 단순한 형태
// - 오브젝트를 생성하고 (new UserDAO ...)
// - 어떤 ConnectionMaker와 연결할지 관계를 맺어줌
// 다만 DaoFactory는 우리가 손으로 만든 코드라서, 컴포넌트가 늘어날수록 직접 다 관리해야 함

// 스프링은 이 역할을 프레임워크 차원에서 대신해주는 '진짜 컨테이너'를 제공
// - 빈 팩토리(BeanFactory) / 애플리케이션 컨텍스트(ApplicationContext)가 그것
// 이 컨테이너가 빈(컴포넌트)들의 생성과 관계 설정, 사용, 소멸까지 모두 제어
// -> 제어권이 개발자 코드가 아니라 컨테이너로 넘어가는 것, 이것이 곧 '제어의 역전(IoC)'

// 정리:
// - 컴포넌트 = 애플리케이션의 기능을 담당하는 독립적인 구성 요소
// - 빈 = 스프링 컨테이너에 등록되어 생성과 관계 설정, 생명주기를 관리받는 오브젝트
// - 컨테이너 = 그 부품들을 만들고 연결하고 생명주기를 관리하는 IoC 오브젝트
// - DaoFactory = 우리가 직접 만든 미니 컨테이너
// - 애플리케이션 컨텍스트 = 스프링이 제공하는 본격적인 IoC 컨테이너

// * DaoFactory를 오브젝트 팩토리로 직접 사용했을 때와 비교해서 애플리케이션 컨텍스트를 사용했을 때 얻을 수 있는 장점
// - 클라이언트는 구체적인 팩토리 클래스를 알 필요가 없음
// 클라이언트가 필요한 오브젝트를 가져오려면 어떤 팩토리 클래스를 사용해야 할지 알아야 하고,
// 필요할 때마다 팩토리 오브젝트를 생성해야 하는 번거로움이 있음
// 애플리케이션 컨텍스트를 사용하면 오브젝트 팩토리가 아무리 많아져도 이를 알아야 하거나 직접 사용할 필요가 없음

/* "부연설명 : 클라이언트는 구체적인 팩토리 클래스를 알 필요가 없음"

  "클라이언트"가 누구를 가리키는가
  이 문장에서 클라이언트는 팩토리에서 생성된 빈을 가져와 사용하는 코드를 의미
  예제에서는 Start와 같이 UserDAO를 요청하고 사용하는 코드가 클라이언트에 해당

  그 관점에서 보면:
  // 설정/조립 단계 (전체 앱에서 보통 딱 1번, main 근처)
  AnnotationConfigApplicationContext context =
          new AnnotationConfigApplicationContext(DaoFactory.class);  // ← 여기만 팩토리를 앎

  // 사용 단계 (앱 전체 곳곳에서 수백 번)
  UserDAO dao = context.getBean(UserDAO.class);   // ← DaoFactory를 전혀 모름
  빈을 꺼내 쓰는 수많은 코드는 DaoFactory라는 이름을 한 번도 언급하지 않음. 그냥 컨텍스트에서 필요한 타입을 달라고 할 뿐.
  이게 "구체적 팩토리 클래스를 알 필요가 없다"의 진짜 의미
  직접 팩토리를 쓰던 때와 비교하면 차이가 분명

  // 애플리케이션 컨텍스트 없이 직접 팩토리를 쓰면:
  UserDAO dao = new DaoFactory().userDAO();   // 쓰는 쪽마다 DaoFactory를 알아야 함
  이 방식은 빈을 쓰려는 모든 곳이 DaoFactory를 직접 new 하고 그 메서드 이름까지 알아야 함
  팩토리가 여러 개로 늘어나면 클라이언트가 "어떤 팩토리의 어떤 메서드"인지 다 외워야 함. 컨텍스트를 쓰면 이 결합이 사라짐

  1. "메인에서 한 줄로 첫 설정 하는 건 불가피"
    - 어디선가 "설정 정보가 이것이다"라고 알려주는 진입점(부트스트랩)은 반드시 한 번 필요
      이건 모순이 아니라 설정 단계와 사용 단계의 분리. 설정 단계는 단 한 곳에 격리되고, 사용 단계는 팩토리를 모름
  2. "컴포넌트 스캔(@ComponentScan)을 쓰면 해결" (부분적으로)
    - @ComponentScan / Spring Boot의 자동 설정을 쓰면 DaoFactory.class를 직접 적지 않고 패키지를 스캔해 빈을 등록할 수 있어,
      그 한 줄에서마저 특정 팩토리 클래스 명시를 줄일 수 있음
    - 다만 엄밀히 말하면 "완전히 없어진다"기보다 명시 대상이 바뀌는 것:
      스캔할 패키지/설정 클래스, 또는 부트의 @SpringBootApplication이 그 진입점 역할을 대신
      즉 진입점 자체는 어떤 형태로든 남음
*/

// - 애플리케이션 컨텍스트는 종합 IoC 서비스를 제공
// 오브젝트와의 관계 설정뿐만 아니라, 오브젝트가 만들어지는 방식, 시점과 전략을 다르게 가져갈 수도 있고
// 이에 부가적으로 자동 생성, 오브젝트에 대한 후처리, 정보의 조합, 설정 방식의 다변화, 인터셉터 등
// 오브젝트를 효과적으로 활용할 수 있는 다양한 기능을 제공
// 또, 빈이 사용할 수 있는 기반 기술 서비스나 외부 시스템과의 연동 등을 컨테이너 차원에서 제공

// - 애플리케이션 컨텍스트는 빈을 검색하는 다양한 방법을 제공

public class Start {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // 1) 스프링 컨테이너(애플리케이션 컨텍스트)를 만듦
        // - AnnotationConfigApplicationContext : @Configuration 자바 클래스를 설정 정보로 읽는 컨텍스트 구현체
        // - 생성자에 넘긴 DaoFactory.class 가 바로 "어떤 빈을 어떻게 만들지" 알려주는 설정 정보
        // - 이 줄이 실행되는 순간 컨테이너는 DaoFactory의 @Bean 메서드들을 호출해
        // UserDAO, ConnectionMaker 오브젝트를 미리 만들어 관계까지 맺어 담아둠
        // (즉, 객체 생성·연결의 제어권이 우리 코드가 아니라 컨테이너로 넘어갔다 = 제어의 역전)
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        // 2) 컨테이너에서 필요한 빈을 꺼내 씀 (직접 new 하지 않음)
        // - getBean("userDAO", UserDAO.class)
        //  · "userDAO"     : 가져올 빈의 이름. 기본적으로 @Bean 메서드 이름(userDAO)이 빈 이름이 됨
        //  · UserDAO.class : 돌려받을 타입. 형변환 없이 바로 UserDAO로 받기 위해 타입을 함께 지정
        // - 이미 connectionMaker가 주입되어 완성된 UserDAO 오브젝트를 컨테이너가 돌려줌
        UserDAO userDAO = context.getBean("userDAO", UserDAO.class);
        User user = userDAO.get("test1");
        System.out.println(user.getName());
    }
}
