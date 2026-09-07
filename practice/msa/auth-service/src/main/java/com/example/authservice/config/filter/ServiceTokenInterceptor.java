package com.example.authservice.config.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
