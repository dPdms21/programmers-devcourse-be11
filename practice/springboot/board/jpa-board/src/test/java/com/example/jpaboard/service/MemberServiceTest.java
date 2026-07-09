package com.example.jpaboard.service;

import com.example.jpaboard.domain.entity.Member;
import com.example.jpaboard.domain.repository.MemberRepository;
import com.example.jpaboard.dto.LoginRequestDto;
import com.example.jpaboard.dto.MemberJoinRequestDto;
import com.example.jpaboard.exception.DuplicateUserIdException;
import com.example.jpaboard.mapper.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

// * 순수 단위 테스트 - 서비스 로직만 검증

// * Mockito란?
// - "가짜 객체(Mock)"를 쉽게 만들어 주는 자바 테스트 라이브러리
// - Mock = 진짜와 유사한 모양의 빈 껍데기 -> 시나리오를 심어줄 수 있음

// * 왜 가짜가 필요하나?
// - 단위 테스트 "대상 하나(MemberService)"가 제대로 동작하는지만 보고 싶음
// - 그런데 MemberService는 MemberRepository에 의존
// -> 진짜 리포지토리를 쓰면 (1) DB가 떠 있어야 하고 (2) 느리고 (3) DB/리포지토리 버그까지 섞여
//    "무엇을 틀렸는지" 불분명
// - 그래서 리포지토리를 '가짜'로 바꿔, 그 행동을 내가 정해 놓고 -> 순수하게 서비스 로직만 검증

// * 자주 쓰는 Mockito 문법
// @ExtendWith(MockitoExtension.class): 이 테스트에서 Mockito 기능을 켬
// @Mock: 가짜 객체를 만듦
// @InjectMocks: 테스트 대상을 만들고 위 @Mock 등을 주입

// * 스터빙: "이렇게 부르면 이 값을 돌려줘라"
// - given(repo.existsByUserId("newbie")).willReturn(false); // 특정인자 -> false 반환하도록
// - given(repo.findByUserId("test")).willReturn(Optional.of(member)); // 회원을 담아 반환
// - given(repo.count()).willThrow(new RuntimeException()); // 호출되면 예외를 던지게

// * 검증 - "그 메서드가 호출됐는지" 확인 (verify): 주로 반환값 없는 void 로직 확인에 씀
// - verify(repo).save(entity);              // save가 "그 엔티티로" 정확히 1번 호출됐어야 함 (기본=1번)
// - verify(repo, times(2)).save(any());     // 정확히 2번
// - verify(repo, never()).save(any());      // 한 번도 호출되면 안 됨

// * 인자 매처 - "구체값 대신 '아무거나'로 느슨하게" (any, eq ...)
// - any()          : 아무 값이나 (타입 무관)         예) verify(repo).save(any());
// - anyString()    : 아무 문자열이나
// - eq("hong")     : 정확히 "hong"인 인자
// - 주의: 한 메서드의 인자 중 하나라도 매처(any 등)를 쓰면, 나머지 인자도 전부 매처로 써야 함
// 예) verify(repo).method(eq("hong"), any());   // "hong"은 그냥 값이 아니라 eq()로 감쌈

// # 핵심 아이디어: MemberService만 진짜 객체로 쓰고, 그것이 의존하는 것들(리포지토리/매퍼)은 "가짜(Mock)"로 바꿈
// - 진짜 DB 리포지토리를 쓰면 DB가 떠 있어야 하고 느림. "서비스 로직"만 보고 싶음
// - 그래서 리포지토리를 Mock으로 두고 "이 메서드는 이런 값을 돌려준다고 치자"라고 지정
// - 스프링을 아예 띄우지 않으므로 매우 빠름 (@SpringBootTest 없음에 주목)

// # 참고: 서비스에 붙은 클래스 레벨 @Transactional은 여기선 동작하지 않음
//        (트랜잭션은 스프링이 프록시로 걸어주는 기능인데, 지금은 스프링을 안 띄우고 new로 만든 순수 객체라서)

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository; // 가짜 레포지토리

    @Mock
    private MemberMapper memberMapper; // 가짜 매퍼

    @InjectMocks
    private MemberService memberService; // 테스트 대상 (위 두 Mock이 주입됨)

    @Test
    @DisplayName("로그인 - 아이디가 있고 비밀번호가 일치하면 회원을 담은 Optional 반환")
    void login_아이디와_비밀번호가_맞으면_회원을_반환한다() {
        // given - "test/1234" 회원이 DB에 있다고 가정
        Member member = Member.builder()
                .userId("test")
                .password("1234")
                .userName("홍길동")
                .build();
        given(memberRepository.findByUserId("test")).willReturn(Optional.of(member));

        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUsername("test");
        requestDto.setPassword("1234");

        // when
        Optional<Member> result = memberService.login(requestDto);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("로그인 - 비밀번호가 틀리면 빈 Optional 반환")
    void login_비밀번호가_틀리면_빈_Optional() {
        // given - "test/1234" 회원이 DB에 있다고 가정
        Member member = Member.builder()
                .userId("test")
                .password("1234")
                .userName("홍길동")
                .build();
        given(memberRepository.findByUserId("test")).willReturn(Optional.of(member));

        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUsername("test");
        requestDto.setPassword("9999");

        // when
        Optional<Member> result = memberService.login(requestDto);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("로그인 - 아이디가 없으면 빈 Optional 반환")
    void login_아이디가_없으면_빈_Optional() {
        // given
        given(memberRepository.findByUserId("nobody")).willReturn(Optional.empty());

        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUsername("nobody");
        requestDto.setPassword("9999");

        // when
        Optional<Member> result = memberService.login(requestDto);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("회원가입 - 아이디가 중복이 아니면 회원 저장")
    void join_중복이_아니면_저장한다() {
        // given
        MemberJoinRequestDto requestDto = new MemberJoinRequestDto();
        requestDto.setUserId("test");
        requestDto.setPassword("1234");
        requestDto.setUserName("홍길동");

        Member member = Member.builder()
                .userId("test")
                .password("1234")
                .userName("홍길동")
                .build();
        given(memberRepository.existsByUserId("test")).willReturn(false);
        given(memberMapper.toEntity(requestDto)).willReturn(member);

        // when
        memberService.join(requestDto);

        // then
        verify(memberRepository).save(member);
    }

    @Test
    @DisplayName("회원가입 - 아이디가 이미 있으면 DuplicateUserIdException을 던지고 저장하지 않음")
    void join_아이디가_중복이면_예외를_던지고_저장하지_않는다() {
        // given
        MemberJoinRequestDto requestDto = new MemberJoinRequestDto();
        requestDto.setUserId("test");
        requestDto.setPassword("1234");
        requestDto.setUserName("홍길동");

        given(memberRepository.existsByUserId("test")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.join(requestDto))
                .isInstanceOf(DuplicateUserIdException.class)
                .hasMessageContaining("[회원가입] 이미 존재하는 아이디");
        verify(memberRepository, never()).save(any());
    }
}
