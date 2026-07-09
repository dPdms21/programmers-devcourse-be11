package com.example.jpaboard.domain.repository;

import com.example.jpaboard.domain.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        memberRepository.save(Member.builder()
                .userId("hong")
                .password("1234")
                .userName("홍길동")
                .build());
    }

    @Test
    void existsByUserId_존재하면_true() {
        assertThat(memberRepository.existsByUserId("hong")).isTrue();
        assertThat(memberRepository.existsByUserId("nobody")).isFalse();
    }

    @Test
    void findByUserId_있으면_회원() {
        assertThat(memberRepository.findByUserId("hong")).isPresent();
        assertThat(memberRepository.findByUserId("nobody")).isEmpty();
    }
}
