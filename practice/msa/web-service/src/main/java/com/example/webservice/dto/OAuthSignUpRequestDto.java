package com.example.webservice.dto;

import com.example.webservice.enums.Role;
import lombok.Getter;

// 소셜 가입 동의 요청
// 가입에 필요한 "SNS 프로필"(provider/providerId/email/name)은 전부 토큰 "안"에 서명된 채로
// 들어있고, 서버는 토큰을 검증·해석(TokenProvider.getSignupPayload)해서 꺼내 씀
// 클라이언트가 이름/이메일을 본문으로 직접 보내게 하면 조작이 가능하지만, 이 방식은 불가능함
//
// role만 본문으로 받는 이유: 권한은 SNS가 알려주는 정보가 아니라 "가입 시점에 사용자가
// 선택하는 값"이라서 토큰(SNS 프로필의 운반체)에 넣을 수 없음 — 자체 가입 폼과 같은 위치
// ※ 사용자가 스스로 관리자를 고를 수 있는 건 권한 실습을 위한 이 예제만의 설정
//   실서비스라면 role은 클라이언트가 정할 수 없고, 서버가 ROLE_USER로 고정 후 관리자가 승격
@Getter
public class OAuthSignUpRequestDto {
    private String signupToken;
    private Role role; // "ROLE_USER" / "ROLE_ADMIN" 문자열이 enum으로 자동 역직렬화됨
}
