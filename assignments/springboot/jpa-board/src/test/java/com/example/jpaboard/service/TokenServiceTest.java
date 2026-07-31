package com.example.jpaboard.service;

import com.example.jpaboard.config.jwt.TokenProvider;
import com.example.jpaboard.config.jwt.TokenStatus;
import com.example.jpaboard.domain.entity.Member;
import com.example.jpaboard.domain.entity.Role;
import com.example.jpaboard.domain.repository.MemberRepository;
import com.example.jpaboard.dto.TokenPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    MemberRepository memberRepository;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    TokenService tokenService;

    @Test
    void login_성공하면_Access_Token과_Refresh_Token을_발급한다() {
        Member member = createMember();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "hong",
                null
        );

        given(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).willReturn(authentication);

        given(memberRepository.findByUserId("hong"))
                .willReturn(Optional.of(member));

        given(tokenProvider.createAccessToken(member))
                .willReturn("access-token");

        given(tokenProvider.createRefreshToken(member))
                .willReturn("refresh-token");

        TokenPair result = tokenService.login(
                "hong",
                "1234"
        );

        assertThat(result.accessToken())
                .isEqualTo("access-token");

        assertThat(result.refreshToken())
                .isEqualTo("refresh-token");

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verify(memberRepository).findByUserId("hong");
        verify(tokenProvider).createAccessToken(member);
        verify(tokenProvider).createRefreshToken(member);
    }

    @Test
    void refresh_성공하면_새로운_Access_Token과_Refresh_Token을_발급한다() {
        Member member = createMember();
        String refreshToken = "valid-refresh-token";

        given(tokenProvider.validateToken(refreshToken))
                .willReturn(TokenStatus.VALID);

        given(tokenProvider.getTokenType(refreshToken))
                .willReturn("REFRESH");

        given(tokenProvider.getUserId(refreshToken))
                .willReturn("hong");

        given(memberRepository.findByUserId("hong"))
                .willReturn(Optional.of(member));

        given(tokenProvider.createAccessToken(member))
                .willReturn("new-access-token");

        given(tokenProvider.createRefreshToken(member))
                .willReturn("new-refresh-token");

        TokenPair result = tokenService.refresh(refreshToken);

        assertThat(result.accessToken())
                .isEqualTo("new-access-token");

        assertThat(result.refreshToken())
                .isEqualTo("new-refresh-token");

        verify(tokenProvider).validateToken(refreshToken);
        verify(tokenProvider).getTokenType(refreshToken);
        verify(tokenProvider).getUserId(refreshToken);
        verify(memberRepository).findByUserId("hong");
    }

    @Test
    void refresh_토큰이_유효하지_않으면_401_예외가_발생한다() {
        String refreshToken = "invalid-refresh-token";

        given(tokenProvider.validateToken(refreshToken))
                .willReturn(TokenStatus.INVALID);

        assertThatThrownBy(
                () -> tokenService.refresh(refreshToken)
        )
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException =
                            (ResponseStatusException) exception;

                    assertThat(
                            responseStatusException.getStatusCode().value()
                    ).isEqualTo(401);
                });

        verify(memberRepository, never())
                .findByUserId(any());

        verify(tokenProvider, never())
                .createAccessToken(any());

        verify(tokenProvider, never())
                .createRefreshToken(any());
    }

    @Test
    void refresh_Access_Token을_전달하면_401_예외가_발생한다() {
        String accessToken = "valid-access-token";

        given(tokenProvider.validateToken(accessToken))
                .willReturn(TokenStatus.VALID);

        given(tokenProvider.getTokenType(accessToken))
                .willReturn("ACCESS");

        assertThatThrownBy(
                () -> tokenService.refresh(accessToken)
        )
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException =
                            (ResponseStatusException) exception;

                    assertThat(
                            responseStatusException.getStatusCode().value()
                    ).isEqualTo(401);
                });

        verify(memberRepository, never())
                .findByUserId(any());

        verify(tokenProvider, never())
                .createAccessToken(any());
    }

    private Member createMember() {
        return Member.builder()
                .userId("hong")
                .password("encoded-password")
                .userName("홍길동")
                .role(Role.ROLE_USER)
                .build();
    }
}