SET NAMES utf8mb4;

CREATE DATABASE board_auth
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE TABLE board_auth.user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20),                                        -- 표시 이름
    email VARCHAR(50),
    user_id VARCHAR(50),                                     -- 로그인 아이디 (토큰의 sub)
    password VARCHAR(100),                                   -- BCrypt 해시 (소셜 회원은 NULL)
    role ENUM('ROLE_USER','ROLE_ADMIN') DEFAULT 'ROLE_USER',
    provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',           -- LOCAL / KAKAO (소셜 연동)
    provider_id VARCHAR(100),                                -- SNS 회원번호
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',            -- 탈퇴 saga: ACTIVE/WITHDRAWING/WITHDRAWN
    status_updated_at DATETIME NULL                          -- 상태 변경 시각 (saga 복구 스케줄러의 잔류 판정 근거)
);
