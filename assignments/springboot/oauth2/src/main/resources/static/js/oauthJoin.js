$(document).ready(() => {
    const signupToken = new URLSearchParams(window.location.search).get('signupToken');
    if (!signupToken) {
        alert('잘못된 접근입니다. 소셜 로그인부터 진행해주세요.');
        window.location.href = '/users/login';
        return;
    }

    history.replaceState(null, '', window.location.pathname);

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
        console.error('토큰 디코딩 실패:', e);
    }

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
                localStorage.setItem('accessToken', response.accessToken);
                alert(response.message);
                window.location.href = response.url;
            },
            error: (xhr) => {
                let response = xhr.responseJSON;
                alert(response && response.message ? response.message : '가입 중 오류가 발생했습니다.');
                window.location.href = '/users/login';
            }
        });
    });
});