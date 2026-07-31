package com.example.jpaboard.domain.repository;

import com.example.jpaboard.domain.entity.Member;
import com.example.jpaboard.domain.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(
                Member.builder()
                        .userId("hong")
                        .password("encoded-password")
                        .userName("홍길동")
                        .role(Role.ROLE_USER)
                        .build()
        );
    }

    @Test
    void existsByUserId_존재하면_true를_반환한다() {
        assertThat(
                memberRepository.existsByUserId("hong")
        ).isTrue();

        assertThat(
                memberRepository.existsByUserId("nobody")
        ).isFalse();
    }

    @Test
    void findByUserId_회원이_존재하면_회원을_반환한다() {
        assertThat(
                memberRepository.findByUserId("hong")
        ).isPresent();

        assertThat(
                memberRepository.findByUserId("nobody")
        ).isEmpty();
    }
}