package com.example.authservice.domain.repository;

import com.example.authservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);

    // 글 하나당 한 번씩 부르면 N+1이 되므로 in절로 묶음
    List<User> findByUserIdIn(List<String> userIds);

    boolean existsByUserId(String userId);
}
