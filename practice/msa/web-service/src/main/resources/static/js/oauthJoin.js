// [소셜 가입 동의 페이지]
// OAuth2SuccessHandler가 미가입자를 /users/oauth-join?signupToken=... 으로 보냄
// 이 페이지가 하는 일: ① 토큰에서 프로필을 읽어 보여주고 ② 동의 시 토큰을 가입 API로 제출

$(document).ready(() => {

    // URL에서 가입 토큰 추출. 없이 직접 들어온 경우는 정상 진입이 아니므로 로그인으로
    const signupToken = new URLSearchParams(window.location.search).get('signupToken');
    if (!signupToken) {
        alert('잘못된 접근입니다. 소셜 로그인부터 진행해주세요.');
        window.location.href = '/users/login';
        return;
    }

    // 주소창에 토큰이 계속 보이지 않도록 히스토리에서 지움
    // (토큰은 위 상수에 이미 확보했으므로 화면 동작에는 지장 없음)
    history.replaceState(null, '', window.location.pathname);

    // [JWT payload 디코딩 — 학습 포인트]
    // JWT의 가운데 부분(payload)은 "암호화"가 아니라 Base64Url "인코딩"일 뿐이라
    // 브라우저에서도 이렇게 열어볼 수 있음. 그래서 민감정보를 넣으면 안 된다고 배운 것
    // 반대로, 사용자가 이 값을 고쳐서 제출해도 서명(세 번째 부분)이 일치하지 않아
    // 서버 검증(TokenProvider.getSignupPayload)에서 거부됨 — "숨기진 못해도 변조는 막음"
    try {
        const base64 = signupToken.split('.')[1]
            .replace(/-/g, '+').replace(/_/g, '/'); // Base64Url → 표준 Base64
        const payload = JSON.parse(
            new TextDecoder().decode( // 한글 닉네임(UTF-8 바이트) 복원
                Uint8Array.from(atob(base64), c => c.charCodeAt(0))
            )
        );
        $('#oauth_name').val(payload.name ?? '');
        $('#oauth_email').val(payload.email ?? '');
    } catch (e) {
        // 표시용 디코딩이 실패해도 가입 자체는 서버 검증이 담당하므로 화면만 비워둠
        console.error('토큰 디코딩 실패:', e);
    }

    // 동의 → 가입 확정 API 호출
    // 이름/이메일은 본문으로 보내지 않음 — 화면의 값은 표시용일 뿐이고,
    // 서버는 서명된 토큰에서 직접 꺼내 씀 (조작 불가)
    // 권한(role)만 본문에 싣음: SNS가 주는 정보가 아니라 사용자의 "선택"이기 때문 (자체 가입과 동일)
    $('#oauth-join').click(() => {
        $.ajax({
            type: 'POST',
            url: '/api/users/oauth-join',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({
                signupToken: signupToken,
                role: $('#role').val()
            }),
            dataType: 'json',
            success: (response) => {
                // 가입 = 즉시 로그인. 자체 로그인(signIn.js)과 동일한 마무리:
                // access token은 localStorage에, refresh token은 서버가 쿠키로 심어줌
                localStorage.setItem('accessToken', response.accessToken);
                alert(response.message);
                window.location.href = response.url;
            },
            error: (xhr) => {
                // 토큰 만료(10분 초과) 등 — 소셜 로그인부터 다시
                let response = xhr.responseJSON;
                alert(response && response.message ? response.message : '가입 중 오류가 발생했습니다.');
                window.location.href = '/users/login';
            }
        });
    });
});