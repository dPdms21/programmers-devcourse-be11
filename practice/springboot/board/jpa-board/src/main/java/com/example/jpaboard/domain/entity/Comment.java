package com.example.jpaboard.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created;

    // * FetchType.LAZY를 "직접 명시" 해야 하는 이유
    // fetchType 기본적으로 어노테이션마다 다름
    // @ManyToOne, @OneToMany -> 기본 EAGER(즉시 로딩) // 그래서 여기에 Lazy 설정을 해둠
    // @OneToMany, @ManyToMany -> 기본 LAZY(지연 로딩)
    // - Eager로 두면 댓글을 꺼낼 때마다 게시글이 "필요 없어도 항상" 같이 조회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}
