package com.example.spring.aop.service;

public class ProductServiceImpl implements ProductService {
    @Override
    public String getProduct(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("상품 코드 필수");
        }

        sleep(30);
        // 실제 작업 흉내
        return "상품: " + code;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ignored) {

        }
    }
}
