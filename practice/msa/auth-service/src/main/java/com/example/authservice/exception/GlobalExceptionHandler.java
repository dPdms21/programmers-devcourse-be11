package com.example.authservice.exception;

import com.example.authservice.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateUserIdException.class)
    public ResponseEntity<ErrorResponseDto> duplicateUserIdException(DuplicateUserIdException e) {
        log.warn("409응답 : {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponseDto(HttpStatus.CONFLICT.value(), e.getMessage())
                );
    }

    // 잘못된 요청 값 (없는 사용자, 유효하지 않은 가입 토큰 등) → 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> illegalArgumentException(IllegalArgumentException e) {
        log.warn("400 응답 : {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage())
                );
    }

    // 탈퇴 saga의 참여자(board) 장애 등 "일시적 실패 — 나중에 다시 하면 될" 상황 → 503
    // 400(요청 잘못)도 500(원인 불명)도 아닌, "요청은 정상이지만 지금은 처리 불가"가
    // 정확히 503(Service Unavailable)의 의미. 보상이 끝난 뒤라 재시도해도 안전함
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> illegalStateException(IllegalStateException e) {
        log.warn("503 응답 : {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        new ErrorResponseDto(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage())
                );
    }
}
