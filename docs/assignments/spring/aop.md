# Spring AOP 자동 프록시 만들기 (Advice → Pointcut → Advisor → 자동 프록시)

> **Advice 방식**과 **`DefaultAdvisorAutoProxyCreator` 빈 후처리기**를 트랜잭션이 아닌 **메서드 실행 시간 측정** 부가 기능으로 구현한다.
> 이번 과제의 핵심은 마지막 Step이다. **서비스를 새로 추가해도 AOP 설정을 수정하지 않은 상태에서** 부가 기능이 해당 서비스에 자동으로 적용되는 것을 확인한다.
>
> 각 Step의 힌트는 접혀 있다. 먼저 해당 요소가 Advice인지 Pointcut인지 고민하고, 필요한 경우 힌트를 펼쳐 확인한다.

<details>
<summary>전체 정답 코드 보기</summary>

> 아래 코드는 Step 6의 서비스 추가까지 모두 반영한 완성본이다. 파일별로 나누어 작성하면 실행할 수 있다.
> 패키지는 서비스와 구현체는 `com.example.spring.aop.service`, 설정·Advice·Main은 `com.example.spring.aop`을 사용한다.

**`com/example/spring/aop/service/OrderService.java`** — 주문 서비스 인터페이스

```java
package com.example.spring.aop.service;

// 클라이언트와 자동 생성되는 프록시가 의존하는 계약이다.
// 인터페이스가 있으므로 Spring은 JDK 동적 프록시를 생성한다.
public interface OrderService {
    String placeOrder(String item);
}
```

**`com/example/spring/aop/service/OrderServiceImpl.java`** — 주문 서비스 구현체

```java
package com.example.spring.aop.service;

// 시간 측정이나 로그 코드 없이 순수한 비즈니스 로직만 가진 target 빈이다.
// 부가 기능은 프록시가 외부에서 적용한다.
public class OrderServiceImpl implements OrderService {

    @Override
    public String placeOrder(String item) {
        sleep(80);
        return "주문완료: " + item;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**`com/example/spring/aop/service/MemberService.java`** — 회원 서비스 인터페이스

```java
package com.example.spring.aop.service;

public interface MemberService {
    String register(String id);
}
```

**`com/example/spring/aop/service/MemberServiceImpl.java`** — 회원 서비스 구현체

```java
package com.example.spring.aop.service;

// 부가 기능 코드 없이 순수한 비즈니스 로직만 가진 target 빈이다.
public class MemberServiceImpl implements MemberService {

    @Override
    public String register(String id) {
        sleep(50);
        return "가입완료: " + id;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**`com/example/spring/aop/service/ProductService.java`** — 상품 서비스 인터페이스

```java
package com.example.spring.aop.service;

public interface ProductService {
    String getProduct(String code);
}
```

**`com/example/spring/aop/service/ProductServiceImpl.java`** — 상품 서비스 구현체

```java
package com.example.spring.aop.service;

// Step 6에서 추가하는 서비스다.
// AopConfig의 Advisor, Pointcut, 빈 후처리기를 수정하지 않아도
// service 패키지에 포함되므로 Pointcut 조건에 따라 프록시가 자동 적용된다.
public class ProductServiceImpl implements ProductService {

    @Override
    public String getProduct(String code) {
        sleep(30);
        return "상품: " + code;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**`com/example/spring/aop/PerformanceMonitorAdvice.java`** — Advice

```java
package com.example.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

// Advice는 적용할 부가 기능을 정의한다.
// 이 클래스는 메서드 실행 시간을 측정하고 로그를 출력한다.
public class PerformanceMonitorAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String name =
                invocation.getMethod().getDeclaringClass().getSimpleName()
                        + "."
                        + invocation.getMethod().getName();

        long start = System.nanoTime();

        try {
            return invocation.proceed();
        } finally {
            long ms = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("[PERF] %s : %dms%n", name, ms);
        }
    }
}
```

**`com/example/spring/aop/AopConfig.java`** — Pointcut, Advisor, 자동 프록시 설정

```java
package com.example.spring.aop;

import com.example.spring.aop.service.MemberService;
import com.example.spring.aop.service.MemberServiceImpl;
import com.example.spring.aop.service.OrderService;
import com.example.spring.aop.service.OrderServiceImpl;
import com.example.spring.aop.service.ProductService;
import com.example.spring.aop.service.ProductServiceImpl;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    // 자동 프록시를 생성하는 빈 후처리기다.
    // 등록된 Advisor를 확인하고 Pointcut 조건을 만족하는 빈을 프록시로 교체한다.
    @Bean
    public DefaultAdvisorAutoProxyCreator autoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    // Advisor는 Pointcut과 Advice를 하나로 묶는다.
    @Bean
    public Advisor performanceAdvisor() {
        AspectJExpressionPointcut pointcut =
                new AspectJExpressionPointcut();

        pointcut.setExpression(
                "execution(* com.example.spring.aop.service..*.*(..))"
        );

        return new DefaultPointcutAdvisor(
                pointcut,
                new PerformanceMonitorAdvice()
        );
    }

    // target 빈은 일반 빈과 동일하게 등록한다.
    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl();
    }

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl();
    }

    @Bean
    public ProductService productService() {
        return new ProductServiceImpl();
    }
}
```

**`com/example/spring/aop/Main.java`** — 실행 진입점

```java
package com.example.spring.aop;

import com.example.spring.aop.service.MemberService;
import com.example.spring.aop.service.OrderService;
import com.example.spring.aop.service.ProductService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        var context =
                new AnnotationConfigApplicationContext(AopConfig.class);

        OrderService orderService =
                context.getBean(OrderService.class);

        MemberService memberService =
                context.getBean(MemberService.class);

        ProductService productService =
                context.getBean(ProductService.class);

        System.out.println("===== 주문 서비스 호출 =====");
        System.out.println(
                orderService.placeOrder("기계식 키보드")
        );

        System.out.println("\n===== 회원 서비스 호출 =====");
        System.out.println(
                memberService.register("kim")
        );

        System.out.println(
                "\n===== 상품 서비스 호출 "
                        + "(Step 6: 설정 수정 없이 자동 적용) ====="
        );
        System.out.println(
                productService.getProduct("A-100")
        );

        System.out.println("\n===== 실제 프록시 타입 확인 =====");
        System.out.println(
                "orderService의 실제 타입: "
                        + orderService.getClass()
        );

        context.close();
    }
}
```

**실행 결과 예시**

```text
===== 주문 서비스 호출 =====
[PERF] OrderService.placeOrder : 82ms
주문완료: 기계식 키보드

===== 회원 서비스 호출 =====
[PERF] MemberService.register : 51ms
가입완료: kim

===== 상품 서비스 호출 (Step 6: 설정 수정 없이 자동 적용) =====
[PERF] ProductService.getProduct : 30ms
상품: A-100

===== 실제 프록시 타입 확인 =====
orderService의 실제 타입: class jdk.proxy3.$Proxy23
```

시간과 프록시 번호는 실행 환경마다 달라질 수 있다. 실제 타입이 `OrderServiceImpl`이 아닌 `$Proxy...` 형태로 출력되는지를 확인한다.

</details>

---

## 0. 먼저 알아둘 점

* 이번 과제에서 구현할 부가 기능은 메서드 실행에 걸린 시간을 측정해 로그로 출력하는 기능이다. 주문이나 회원 처리와는 관심사가 다른 **횡단 관심사(cross-cutting concern)** 에 해당한다.
* 핵심은 **target 코드를 수정하지 않고** 해당 부가 기능을 모든 서비스에 적용하는 것이다.
* 수업에서 구현한 `ProxyFactoryBean` 방식은 서비스마다 프록시 빈을 별도로 등록해야 한다. 서비스가 많아질수록 프록시 설정도 함께 증가한다.
* 이번 과제에서는 **`DefaultAdvisorAutoProxyCreator`와 하나의 Advisor**를 사용해 프록시 생성을 자동화한다.
* `@Aspect`와 `@EnableAspectJAutoProxy`는 사용하지 않는다. 해당 기능이 내부적으로 수행하는 Advisor 수집과 자동 프록시 생성을 직접 구성하는 것이 목적이다.

---

## 1. 무엇을 만드는가?

쇼핑몰의 주문 서비스와 회원 서비스를 일반 빈으로 등록하고, 메서드를 호출했을 때 실행 시간 로그가 자동으로 출력되는 프로그램을 완성한다.

**콘솔 출력 예시**

```text
===== 주문 서비스 호출 =====
[PERF] OrderService.placeOrder : 82ms
주문완료: 기계식 키보드

===== 회원 서비스 호출 =====
[PERF] MemberService.register : 51ms
가입완료: kim

===== 실제 프록시 타입 확인 =====
orderService의 실제 타입: class jdk.proxy3.$Proxy23
```

핵심은 다음 두 가지다.

1. `OrderServiceImpl`에는 시간 측정 코드가 없지만 `[PERF]` 로그가 출력된다.
2. 컨테이너에서 가져온 빈의 실제 타입이 `OrderServiceImpl`이 아니라 프록시다.

---

## 2. 학습 목표

| 개념                                       | 학습 위치                                    |
| ---------------------------------------- | ---------------------------------------- |
| Advice: 부가 기능 본체인 `MethodInterceptor`    | Step 1 (`PerformanceMonitorAdvice.java`) |
| Pointcut: 부가 기능 적용 대상 선정                 | Step 2 (`AopConfig.java`)                |
| Advisor: Pointcut과 Advice의 결합            | Step 3 (`AopConfig.java`)                |
| 자동 프록시: `DefaultAdvisorAutoProxyCreator` | Step 4 (`AopConfig.java`)                |
| 실행과 프록시 확인                               | Step 5 (`Main.java`)                     |
| 서비스 추가 시 자동 적용 확인                        | Step 6                                   |

---

## 3. 핵심 개념

### (1) 용어 한눈에 보기

| 용어                               | 의미                              | 이 과제에서의 역할              |
| -------------------------------- | ------------------------------- | ----------------------- |
| Advice                           | **무엇을** 수행할지 정의하는 부가 기능         | 실행 시간을 측정하고 로그 출력       |
| Pointcut                         | **어디에** 적용할지 결정하는 조건            | `service` 패키지의 모든 메서드   |
| Advisor                          | Advice와 Pointcut의 묶음            | 적용 대상과 부가 기능을 하나로 구성한 빈 |
| `DefaultAdvisorAutoProxyCreator` | 조건에 맞는 빈을 자동으로 프록시로 교체하는 빈 후처리기 | 자동 프록시 생성               |

### (2) 자동 프록시의 동작

```text
컨테이너가 빈을 생성한다.
    ↓
빈 후처리기가 등록된 Advisor를 확인한다.
    ↓
Advisor의 Pointcut 조건에 현재 빈이 포함되는지 검사한다.
    ↓
조건을 만족하면 원본 빈 대신 프록시를 등록한다.
조건을 만족하지 않으면 원본 빈을 그대로 등록한다.
```

Pointcut 조건에 맞는 빈은 모두 자동으로 프록시가 된다. 서비스가 증가하더라도 동일한 조건을 만족하면 하나의 Advisor를 공통으로 적용할 수 있다.

### (3) Advice 내부의 호출 흐름

```text
클라이언트
    ↓
자동 생성된 프록시
    ↓
Advice.invoke()
    ├── 메서드 실행 전: 시작 시각 기록
    ├── invocation.proceed(): target 메서드 실행
    └── 메서드 실행 후: 실행 시간 출력
```

`proceed()`는 실제 target 메서드를 호출한다. 해당 호출 전후에 부가 기능을 배치할 수 있다.

```text
Advice = 무엇을 적용할지
Pointcut = 어디에 적용할지
Advisor = Advice와 Pointcut의 묶음
AutoProxyCreator = 조건에 맞는 빈을 자동으로 프록시로 교체
proceed() 전후 = 부가 기능을 적용하는 위치
```

---

## 4. 파일 구조와 준비물

| 파일                                    | 역할                             |
| ------------------------------------- | ------------------------------ |
| `OrderService` / `OrderServiceImpl`   | 주문 서비스 target                  |
| `MemberService` / `MemberServiceImpl` | 회원 서비스 target                  |
| `PerformanceMonitorAdvice`            | 실행 시간을 측정하는 Advice             |
| `AopConfig`                           | Pointcut, Advisor, 자동 프록시 빈 등록 |
| `Main`                                | 실행 진입점                         |

모든 클래스는 다음 패키지에 작성한다.

```text
com.example.spring.aop.service
├── 서비스 인터페이스
└── 서비스 구현체

com.example.spring.aop
├── AopConfig
├── PerformanceMonitorAdvice
└── Main
```

Pointcut은 패키지 경로를 기준으로 적용 대상을 선정한다.

**의존성**

```gradle
implementation 'org.springframework.boot:spring-boot-starter'
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

---

## 5. Step by Step

### Step 0. target 서비스 준비하기

시간 측정 코드가 없는 순수한 서비스 두 개를 만든다. 시간 측정 부가 기능은 서비스 구현체에 직접 작성하지 않는다.

```java
package com.example.spring.aop.service;

public interface OrderService {
    String placeOrder(String item);
}
```

```java
package com.example.spring.aop.service;

public class OrderServiceImpl implements OrderService {

    @Override
    public String placeOrder(String item) {
        sleep(80);
        return "주문완료: " + item;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

회원 서비스도 같은 구조로 작성한다.

```java
String register(String id)
```

해당 메서드는 50ms 동안 대기한 뒤 다음 값을 반환한다.

```java
"가입완료: " + id
```

**확인**: 두 서비스 구현체 안에 시간 측정이나 성능 로그 출력 코드가 없어야 한다.

---

### Step 1. Advice 만들기 — 실행 시간 측정 (`PerformanceMonitorAdvice.java`)

**목표**: 메서드 실행을 가로채고 실행 전후의 시간을 측정하는 부가 기능을 한 클래스에 작성한다.

**할 일**

1. `org.aopalliance.intercept.MethodInterceptor`를 구현한다.
2. `invoke()`에서 시작 시각을 기록한다.
3. `invocation.proceed()`로 실제 target 메서드를 실행한다.
4. 실행이 끝난 뒤 걸린 시간을 로그로 출력한다.
5. 예외가 발생해도 시간이 출력되도록 `try-finally`를 사용한다.

<details>
<summary>힌트 보기</summary>

```java
package com.example.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class PerformanceMonitorAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String name =
                invocation.getMethod().getDeclaringClass().getSimpleName()
                        + "."
                        + invocation.getMethod().getName();

        long start = System.nanoTime();

        try {
            return invocation.proceed();
        } finally {
            long ms =
                    (System.nanoTime() - start) / 1_000_000;

            System.out.printf(
                    "[PERF] %s : %dms%n",
                    name,
                    ms
            );
        }
    }
}
```

`proceed()` 앞은 메서드 실행 전이고, `finally`는 메서드 실행 후다. 이 위치에 부가 기능을 작성한다.

`proceed()`가 반환한 값을 그대로 반환해야 target 메서드의 실행 결과가 클라이언트까지 전달된다.

</details>

**확인**: 이 클래스에 주문이나 회원과 같은 업무 관련 코드가 없어야 한다. Advice는 어떤 서비스에 적용되는지와 관계없이 동작해야 한다.

---

### Step 2. Pointcut 만들기 — 적용 대상 선정 (`AopConfig.java`)

**목표**: `service` 패키지에 있는 모든 클래스의 모든 메서드를 적용 대상으로 선정하는 Pointcut을 만든다.

**할 일**

1. `AspectJExpressionPointcut`을 생성한다.
2. 다음 표현식으로 적용 대상을 지정한다.

```java
execution(* com.example.spring.aop.service..*.*(..))
```

<details>
<summary>힌트 보기</summary>

```java
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

AspectJExpressionPointcut pointcut =
        new AspectJExpressionPointcut();

pointcut.setExpression(
        "execution(* com.example.spring.aop.service..*.*(..))"
);
```

표현식은 다음과 같이 해석한다.

```text
execution(
    반환형 *
    패키지 com.example.spring.aop.service..
    클래스 *
    메서드 .*
    인자 (..)
)
```

`service` 패키지와 하위 패키지에 있는 모든 클래스의 모든 메서드를 의미한다.

이름 패턴을 `*Service` 또는 `get*` 등으로 변경하면 적용 범위도 달라진다.

</details>

**확인**: Pointcut은 적용 대상을 선정하는 조건이다. Pointcut만 생성한 상태에서는 부가 기능이 실행되지 않는다. 다음 Step에서 Advice와 결합해야 한다.

---

### Step 3. Advisor 만들기 — Pointcut과 Advice 결합 (`AopConfig.java`)

**목표**: 특정 조건에 특정 부가 기능을 적용한다는 정보를 하나의 Advisor로 구성하고 빈으로 등록한다.

**할 일**

1. `DefaultPointcutAdvisor`를 사용해 Pointcut과 Advice를 결합한다.
2. 생성한 Advisor를 `@Bean`으로 등록한다.
3. Advisor를 빈으로 등록하면 자동 프록시 생성기가 이를 수집한다.

<details>
<summary>힌트 보기</summary>

```java
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;

@Bean
public Advisor performanceAdvisor() {
    AspectJExpressionPointcut pointcut =
            new AspectJExpressionPointcut();

    pointcut.setExpression(
            "execution(* com.example.spring.aop.service..*.*(..))"
    );

    return new DefaultPointcutAdvisor(
            pointcut,
            new PerformanceMonitorAdvice()
    );
}
```

Advisor는 **어디에 적용할지를 나타내는 Pointcut**과 **무엇을 적용할지를 나타내는 Advice**를 묶는다.

Advisor를 Spring 빈으로 등록하면 다음 Step의 빈 후처리기가 이를 자동으로 확인한다.

</details>

**확인**: Advisor 빈이 하나 등록되어야 한다. 이 단계에서도 target 빈에는 프록시 관련 설정을 추가하지 않는다.

---

### Step 4. 자동 프록시 활성화와 target 빈 등록 (`AopConfig.java`)

**목표**: `DefaultAdvisorAutoProxyCreator`를 빈으로 등록해 Pointcut 조건에 맞는 빈을 자동으로 프록시로 교체한다. 서비스 빈은 일반 빈과 동일하게 등록한다.

**할 일**

1. `DefaultAdvisorAutoProxyCreator`를 `@Bean`으로 등록한다.
2. `orderService`, `memberService`를 각 구현체로 생성해 등록한다.
3. target 빈에는 프록시 관련 코드를 작성하지 않는다.

<details>
<summary>힌트 보기</summary>

```java
package com.example.spring.aop;

import com.example.spring.aop.service.MemberService;
import com.example.spring.aop.service.MemberServiceImpl;
import com.example.spring.aop.service.OrderService;
import com.example.spring.aop.service.OrderServiceImpl;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    @Bean
    public DefaultAdvisorAutoProxyCreator autoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public Advisor performanceAdvisor() {
        AspectJExpressionPointcut pointcut =
                new AspectJExpressionPointcut();

        pointcut.setExpression(
                "execution(* com.example.spring.aop.service..*.*(..))"
        );

        return new DefaultPointcutAdvisor(
                pointcut,
                new PerformanceMonitorAdvice()
        );
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl();
    }

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl();
    }
}
```

target 빈에는 프록시에 대한 설정이 없다.

`DefaultAdvisorAutoProxyCreator`가 각 빈의 생성 과정에 참여하여 Advisor의 Pointcut 조건을 검사하고, 조건에 맞으면 해당 빈을 프록시로 교체한다.

</details>

**확인**: `AopConfig`에는 다음 네 개의 빈이 등록되어야 한다.

```text
빈 후처리기 1개
Advisor 1개
서비스 빈 2개
```

---

### Step 5. 실행하고 실제 프록시 확인하기 (`Main.java`)

**목표**: 서비스를 호출했을 때 성능 로그가 자동으로 출력되는지 확인하고, 컨테이너에서 가져온 빈의 실제 타입이 프록시인지 확인한다.

**할 일**

1. `AnnotationConfigApplicationContext`에 `AopConfig.class`를 전달해 컨테이너를 생성한다.
2. 서비스 빈을 인터페이스 타입으로 가져온다.
3. 각 서비스 메서드를 호출한다.
4. 서비스 객체의 `getClass()` 결과를 출력한다.

<details>
<summary>힌트 보기</summary>

```java
package com.example.spring.aop;

import com.example.spring.aop.service.MemberService;
import com.example.spring.aop.service.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        var context =
                new AnnotationConfigApplicationContext(AopConfig.class);

        OrderService orderService =
                context.getBean(OrderService.class);

        MemberService memberService =
                context.getBean(MemberService.class);

        System.out.println("===== 주문 서비스 호출 =====");
        System.out.println(
                orderService.placeOrder("기계식 키보드")
        );

        System.out.println("\n===== 회원 서비스 호출 =====");
        System.out.println(
                memberService.register("kim")
        );

        System.out.println("\n===== 실제 프록시 타입 확인 =====");
        System.out.println(
                "orderService의 실제 타입: "
                        + orderService.getClass()
        );

        context.close();
    }
}
```

빈은 인터페이스 타입으로 가져온다. JDK 동적 프록시는 `OrderServiceImpl`을 상속한 객체가 아니라 `OrderService` 인터페이스를 구현한 별도의 프록시 객체이기 때문이다.

따라서 구현체 타입인 `OrderServiceImpl.class`로 조회하면 빈을 가져오지 못할 수 있다.

</details>

**확인**: 다음 두 가지를 확인한다.

```text
[PERF] OrderService.placeOrder : 80ms
```

```text
class jdk.proxy...$Proxy...
```

실제 타입이 `OrderServiceImpl`로 출력되면 자동 프록시가 적용되지 않은 상태다. 이 경우 Pointcut 표현식과 실제 패키지 경로를 확인한다.

---

### Step 6. 서비스를 추가해도 AOP 설정은 수정하지 않기

**목표**: 새로운 서비스를 추가하고 Advice, Pointcut, Advisor, 빈 후처리기를 수정하지 않아도 부가 기능이 자동으로 적용되는지 확인한다.

**할 일**

1. `ProductService`와 `ProductServiceImpl`을 `service` 패키지에 추가한다.
2. `getProduct(String code)` 메서드는 30ms 동안 대기한 뒤 다음 값을 반환한다.

```java
"상품: " + code
```

3. `AopConfig`에는 target 빈 등록 코드만 추가한다.

```java
@Bean
public ProductService productService() {
    return new ProductServiceImpl();
}
```

4. 기존 Advisor, Pointcut, 빈 후처리기는 수정하지 않는다.
5. `Main`에서 다음 메서드를 호출한다.

```java
productService.getProduct("A-100");
```

**확인**: AOP 설정을 수정하지 않았는데도 다음 성능 로그가 자동으로 출력되면 성공이다.

```text
[PERF] ProductService.getProduct : 30ms
```

새로운 서비스도 기존 Pointcut 조건에 포함되므로 동일한 Advisor가 자동으로 적용된다.

---

## 6. 학습 체크

* [ ] Advice, Pointcut, Advisor를 각각 설명할 수 있다
* [ ] `proceed()`의 역할과 앞뒤에 부가 기능을 작성하는 이유를 설명할 수 있다
* [ ] `DefaultAdvisorAutoProxyCreator`가 빈을 언제 프록시로 교체하는지 설명할 수 있다
* [ ] target 빈에 프록시 관련 설정이 없는 이유를 설명할 수 있다
* [ ] 서비스를 추가해도 기존 AOP 설정을 수정하지 않는 이유를 설명할 수 있다

## 7. 최종 완성 체크리스트

* [ ] 서비스 구현체에 시간 측정 코드가 없다
* [ ] 서비스 호출 시 `[PERF]` 로그가 자동으로 출력된다
* [ ] 컨테이너에서 가져온 빈의 실제 타입이 프록시다
* [ ] 새 서비스를 추가해도 AOP 설정을 수정하지 않고 부가 기능이 적용된다

## 8. 선택 도전 과제

1. **이름 패턴 Pointcut**: 표현식을 다음과 같이 변경하여 `get`으로 시작하는 조회 메서드에만 적용한다.

   ```java
   execution(* com.example.spring.aop.service..*Service.get*(..))
   ```

   적용 대상에서 제외되는 메서드를 확인한다.

2. **Advice 추가**: 예외가 발생했을 때 예외 정보를 로그로 출력하는 `ExceptionLoggingAdvice`를 만들고 Advisor를 하나 더 등록한다. 여러 Advisor가 등록되어도 빈 후처리기가 모두 수집하는 것을 확인한다.

3. **직접 Pointcut 구현**: `AspectJExpressionPointcut` 대신 `Pointcut` 인터페이스를 직접 구현한다. `ClassFilter`와 `MethodMatcher`를 통해 Pointcut이 클래스 필터와 메서드 매처로 구성되는 것을 확인한다.

4. **데코레이터와 자동 프록시 비교**: 이전 과제에서 직접 조립한 `RetryNotificationSender`와 이번 자동 프록시 구조를 비교하고, 자동 프록시를 통해 어떤 과정이 자동화되었는지 정리한다.
