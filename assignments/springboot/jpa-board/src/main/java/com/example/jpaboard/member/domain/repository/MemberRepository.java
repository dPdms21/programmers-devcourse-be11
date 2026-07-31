package com.example.jpaboard.member.domain.repository;

import com.example.jpaboard.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUserId(String userId);

    Optional<Member> findByUserId(String userId);
}
