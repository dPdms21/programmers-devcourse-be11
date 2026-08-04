package com.example.oauth2.domain.repository;

import com.example.oauth2.config.oauth2.AuthProvider;
import com.example.oauth2.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    Optional<User> findByProviderAndProviderId(
            AuthProvider provider,
            String providerId
    );
}
