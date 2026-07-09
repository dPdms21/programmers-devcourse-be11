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

// * @DataJpaTest
// - 기본적으로 내 DataSource 설정을 버리고 "기본 모드 임베디드 H2"로 갈아 끼움
// - JPA 리포지토리와 EntityManager 등 "데이터 계층"에 필요한 빈만 로드 (컨트롤러/서비스는 안 뜸 -> 가벼움)
// - 각 테스트는 트랜잭션 안에서 돌고 "끝나면 자동 롤백"됨 -> 테스트끼리 데이터가 안 섞임
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .userId("test")
                .password("1234")
                .userName("홍길동")
                .build();

        memberRepository.save(member);
    }

    @Test
    @DisplayName("existsByUserId - 존재하는 아이디면 true 반환")
    void existsByUserId_존재하면_true() {
        // when
        boolean exists = memberRepository.existsByUserId("test");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUserId - 존재하지 않는 아이디면 false 반환")
    void existsByUserId_없으면_false() {
        // when
        boolean exists = memberRepository.existsByUserId("nobody");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByUserId - 존재하는 아이디로 조회하면 회원이 담긴 Optional 반환")
    void findByUserId_존재하면_회원() {
        // when
        Optional<Member> found = memberRepository.findByUserId("test");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("findByUserId - 없는 아이디로 조회하면 빈 Optional 반환")
    void findByUserId_없으면_빈_Optional() {
        // when
        Optional<Member> found = memberRepository.findByUserId("nobody");

        // then
        assertThat(found).isEmpty();
    }
}
