package com.example.jpaboard.domain.repository;

import com.example.jpaboard.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
