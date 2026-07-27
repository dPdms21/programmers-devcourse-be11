package com.example.formlogin.domain.repository;

import com.example.formlogin.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String userId);
}
