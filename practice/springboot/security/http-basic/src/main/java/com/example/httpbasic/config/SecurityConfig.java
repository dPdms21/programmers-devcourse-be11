package com.example.httpbasic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// * Spring Security의 동작 메커니즘
// Spring Security의 모든 것은 필터(Filter) 위에서 돌아감
// 웹 요청이 들어오면 실제 컨트롤러에 도달하기 전에 여러 개의 보안 필터를 순서대로 통과
// 이 진입점이 DelegatingFilterProxy
// 서블릿 컨테이너(톰캣)에 등록된 이 필터가 요청을 받아서 스프링이 관리하는 FilterChainProxy에게 위임하고,
// 이 FilterChainProxy가 내부적으로 여러 SecurityFilterChain을 들고 있음
// 즉 "톰캣 필터 -> 스프링 빈으로 관리되는 보안 필터들"로 다리를 놓아주는 구조
// - 요청 -> DelegatingFilterProxy -> FilterChainProxy -> [보안 필터 체인] -> DispatcherServlet -> Controller

// * DelegatingFilterProxy: "서블릿 컨테이너(톰캣)의 세계와 스프링의 세계를 이어주는 다리 역할을 하는 필터"
// - Delegating(위임) +  Filter + Proxy(대리인): 실제 일은 다른 녀석에게 위임하는 껍데기 필터
// 왜 이런 게 필요한가
// 핵심은 톰캣과 스프링이 서로 다른 세계라는 점

// 서블릿 컨테이너는 Filter를 자기 규칙대로 등록하고 생성/관리
// 하지만 톰캣은 스프링 Bean을 전혀 모름. 스프링 컨테이너 안에 뭐가 있는지 전혀 모름
// 그런데 우리가 쓰고 싶은 실제 보안 필터들(FilterChainProxy와 그 안의 인증/인가 필터)은 스프링 Bean
// DI, 라이프사이클 관리 등 스프링 기능을 다 써야 하기 때문

// 필터는 톰캣에 등록돼야 하는데, 정작 실행하고 싶은 로직은 스프링 Bean
// - 톰캣에 직접 스프링 Bean을 필터로 꽂을 수는 없음
// 그래서 DelegatingFilterProxy 클래스가 중간에서 다리를 놓음

// DelegatingFilterProxy 자신은 평범한 서블릿 필터라서 톰캣에 정상적으로 등록될 수 있음 (톰캣 입장에선 그냥 일반 필터 중 하나)
// 하지만 실제로 요청이 들어오면, 스스로 처리하지 않고 스프링 컨테이너(ApplicationContext)에서 특정 이름의 Bean을 찾아 그 Bean에게 일을 넘김
// Spring Security의 경우, 찾는 Bean 이름은 springSecurityFilterChain이고, 이 Bean의 정체가 바로 FilterChainProxy

// * HTTP BASIC이란?
// HTTP 표준(RFC 7617)에 정의된 가장 기본적인 인증 방식
// 별도의 로그인 페이지나 폼이 없이, HTTP 요청 헤더에 아이디/비밀번호를 실어 보내는 방식

// * HTTP BASIC 인증 (Spring Security)
// - 프로토콜 관점의 동작 원리
// 1. 클라이언트가 보호된 자원에 그냥 접근하면, 서버가 401 Unauthorized와 함께 헤더를 응답 -> WWW-Authenticate: Basic realm="..."
// 2. 브라우저는 이걸 받으면 아이디/비밀번호를 입력하는 작은 팝업 창을 띄움
// 3. 사용자가 입력하면, 클라이언트는 username:password를 Base64로 인코딩해서 헤더에 담아 다시 요청 -> Authorization: Basic ZHVzZXI6cGFzc3dvcmQ=
// 4. 서버는 이 헤더를 디코딩해서 인증을 검증

// - Spring Security에서의 동작원리
// - BasicAuthenticationFilter가 담당
// 위 프로토콜을 필터 체인 안에서 실제로 처리하는 흐름
// 1. 요청이 들어오면 BasicAuthenticationFilter가 "Authorization: Basic ZHVzZXI6cGFzc3dvcmQ=" 헤더가 있는지 확인
// 2. 헤더가 있으면 Base64를 디코딩해 아이디/비밀번호를 꺼내고, UsernamePasswordAuthenticationToken을 만듦
// 3. 이 토큰을 AuthenticationManager에게 넘겨 인증을 검증 (내부적으로 UserDetailsService로 사용자 조회 -> PasswordEncoder로 비밀번호 대조)
// 4. 성공하면 SecurityContext에 인증 정보를 저장하고 다음 필터로 넘어감
// 5. 헤더가 없거나 인증에 실패하면, BasicAuthenticationEntryPoint가 401 Unauthorized와 WWW-Authenticate: Basic 헤더를 응답 (다시 1번 상황으로)

// * 특징
// Base64는 암호화가 아니라 단순 인코딩. 누구나 즉시 디코딩해서 원래 아이디/비밀번호를 볼 수 있음
// - HTTP Basic은 반드시 HTTPS(TLS)와 함께 써야 함. 평문 HTTP에서 쓰면 비밀번호가 그대로 노출된 것이나 마찬가지
// - 모든 요청마다 Authorization 헤더를 계속 보내야 함. 서버가 상태(세션)를 기억하지 않는 stateless 방식이기 때문
// 그래서 REST API나 서버 간 통신에서 간단하게 쓰기 좋음
// - 로그아웃 개념이 애매. 브라우저가 자격증명을 캐싱해두기 때문에 명시적인 로그아웃 처리가 어려움

// * realm 은 HTTP 인증에서 "보호 영역의 이름"을 뜻함. 쉽게 말해 "지금 어느 구역에 로그인하려는 건지"를 나타내는 라벨
// 실제로 어떤 역할을 하나
// 1. 사용자에게 보여주는 안내 문구
// 브라우저가 아이디/비밀번호 팝업 창을 띄울 때, 이 realm 이름이 팝업 창에 그대로 표시됨
// 예를 들어 팝업에 "MyApp에 로그인" 또는 "'MyApp' 영역의 자격 증명을 입력하세요" 같은 문구가 뜸
// 사용자가 "아, 지금 이 사이트/구역에 로그인하는 거구나"를 알 수 있게 해주는 안내판 역할
// 2. 보호 영역을 구분하는 식별자
// 한 서버 안에 서로 다른 보호 구역이 여러 개 있을 수 있음
// 예를 들면:
//  realm="User Area" — 일반 사용자 구역
//  realm="Admin Area" — 관리자 구역
// realm 이름이 다르면 브라우저는 이들을 별개의 인증 영역으로 취급
// 그래서 사용자 구역에 로그인한 자격증명을 관리자 구역에 자동으로 재사용하지 않음
// 같은 realm 안에서는 브라우저가 한 번 입력한 자격증명을 캐싱해서 재사용

// @EnableWebSecurity
// 부트의 시큐리티 자동 설정 안에 WebSecurityEnablerConfiguration이라는 게 있는데,
// "클래스패스에 시큐리티가 있고 + 아직 @EnableWebSecurity가 적용이 안됐으면" 부트가 대신 붙여줌
// 즉 안 써도 뒤에서 똑같은 게 켜짐
// 그래도 관례적으로 붙이는게 나은 이유
// "이 클래스가 시큐리티 설정의 진입점"이라는 걸 명시적으로 드러내는 문서화 효과가 있고,
// 부트 밖으로 코드를 옮기거나 자동 설정 구성이 바뀌어도 안전하며, 비용이 전혀 없기 때문
// 그래서 공식 문서와 대부분의 예제가 붙이는 쪽을 택함
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // BasicAuthenticationFilter가 필터 체인에 등록이 됨
                // 즉 "이제부터 Authorization: Basic 헤더를 읽어서 인증을 처리하겠다"는 선언
                // Customizer.withDefaults(): 아무것도 커스터마이징하지 않고 기본값을 씀
                .httpBasic( Customizer.withDefaults() )
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated() );

        return http.build();
    }

    // BasicAuthenticationFilter
    //   → AuthenticationManager (ProviderManager)
    //        → DaoAuthenticationProvider   ← 지금 이 부분
    //             ├─ UserDetailsService  (사용자 조회)
    //             └─ PasswordEncoder     (비밀번호 대조)

    // 이 Bean의 역할은 "인증할 때 사용자 정보를 어디서 가져올지"를 정의
    // AuthenticationManager가 아이디로 사용자를 조회할 때 바로 이 UserDetailsService를 호출
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        UserDetails user = User.withUsername("user")
                .password("1234")
                .roles("USER")
                .build();

        manager.createUser(user);

        return manager;
    }

    // 이 Bean의 역할은 "비밀번호를 어떻게 비교/암호화할지"를 정의
    // 인증 흐름에서 DaoAuthenticationProvider가 "사용자가 입력한 비밀번호"와 "저장된 비밀번호"를 비교할 때 이 인코더를 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
