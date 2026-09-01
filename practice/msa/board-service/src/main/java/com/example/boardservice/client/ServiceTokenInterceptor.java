package com.example.boardservice.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// * Feign RequestInterceptor - 이 서비스의 "모든" Feign 요청 직전에 호출되는 훅
// 서비스 간 인증 토큰(X-Service-Token)을 매 요청에 자동으로 실어 보냄

// (서블릿) Filter vs Interceptor
// Filter: 스프링의 DispatcherServlet(분배기)보다 "앞"에서 동작
// Interceptor: DispatcherServlet "뒤", 컨트롤러의 앞단에서 동작

@Component
public class ServiceTokenInterceptor implements RequestInterceptor {

    public static final String SERVICE_TOKEN_HEADER = "X-Service-Token";

    @Value("${service.token}")
    private String serviceToken;

    @Override
    public void apply(RequestTemplate template) {
        template.header(SERVICE_TOKEN_HEADER, serviceToken);
    }
}
