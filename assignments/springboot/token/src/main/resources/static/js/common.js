// 모든 ajax 요청에 access token을 Authorization 헤더로 실어 보낸다
let setupAjax = () => {
    $.ajaxSetup({
        beforeSend: (xhr) => {
            let token = localStorage.getItem("accessToken");
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        }
    });
}

// refresh 쿠키로 토큰 재발급. 성공: 새 access는 localStorage에 (새 refresh는 서버가 쿠키에 덮어씀)
let refreshTokens = () => {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'POST',
            url: '/api/tokens/refresh',
            dataType: 'json',
            xhrFields: { withCredentials: true },   // 쿠키 포함
            success: (response) => {
                localStorage.setItem('accessToken', response.accessToken);
                resolve(response);
            },
            error: (xhr) => reject(xhr)
        });
    });
}

let getUserInfo = () => {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'GET', url: '/api/users/info', dataType: 'json',
            success: resolve, error: reject
        });
    });
}

let redirectToLogin = () => {
    alert('로그인이 필요합니다. 다시 로그인해주세요.');
    localStorage.removeItem('accessToken');
    window.location.href = '/users/login';
}