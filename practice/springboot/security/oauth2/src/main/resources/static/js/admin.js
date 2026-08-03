$(document).ready(() => {
    setupAjax();
    checkAdminAccess();
});

// [관리자 페이지 접근 판정]
// 페이지(HTML) 자체는 공개지만, 진입하자마자 관리자 전용 API(@PreAuthorize hasRole('ADMIN'))를 호출
// - 성공(200)        → 관리자 확인 → 내용 표시
// - 권한 부족(403)    → 관리자가 아님 → 메인으로 쫓아냄
// - 인증 실패(401 등) → access token 재발급 후 한 번 재시도, 그래도 안 되면 로그인 페이지로
let checkAdminAccess = async () => {
    try {
        renderAdmin(await getAdminAuthority());
    } catch (e) {
        if (e.status === 403) {
            denyAccess();
            return;
        }
        try {
            await refreshTokens();
            renderAdmin(await getAdminAuthority());
        } catch (e2) {
            e2.status === 403 ? denyAccess() : redirectToLogin();
        }
    }
}

let getAdminAuthority = () => {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'GET',
            url: '/api/users/admin',
            dataType: 'json',
            success: (response) => resolve(response),
            error: (xhr) => reject(xhr)
        });
    });
}

let renderAdmin = (response) => {
    $('#admin-message').text(response.message);
    $('#admin-content').show();
}

let denyAccess = () => {
    alert('관리자만 접근할 수 있는 페이지입니다.');
    window.location.href = '/';
}