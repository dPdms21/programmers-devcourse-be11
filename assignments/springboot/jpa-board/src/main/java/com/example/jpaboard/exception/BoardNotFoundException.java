package com.example.jpaboard.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException(String message) {
        super(message);
    }
}
