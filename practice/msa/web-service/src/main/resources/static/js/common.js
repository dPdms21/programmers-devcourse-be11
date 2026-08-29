// 모든 ajax 요청에 access token을 Authorization 헤더로 실어 보냄
// (beforeSend가 요청 때마다 localStorage를 읽으므로 토큰 갱신 후 재설정할 필요 없음)
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

// refresh 쿠키로 access/refresh 토큰 재발급
// 성공: 새 access token은 localStorage에 저장, 새 refresh token은 서버가 기존 쿠키에 덮어씀
// 실패(쿠키 없음/만료): reject → 호출한 쪽에서 로그인 페이지로 보냄
let refreshTokens = () => {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'POST',
            url: '/api/tokens/refresh',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            xhrFields: {
                withCredentials: true // refresh token 쿠키를 포함해서 요청
            },
            success: (response) => {
                localStorage.setItem('accessToken', response.accessToken);
                resolve(response);
            },
            error: (xhr) => reject(xhr)
        });
    });
}

// 로그인한 사용자 정보 조회 (인증 필요 API)
let getUserInfo = () => {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: 'GET',
            url: '/api/users/info',
            dataType: 'json',
            success: (response) => resolve(response),
            error: (xhr) => reject(xhr)
        });
    });
}

let redirectToLogin = () => {
    alert('로그인이 필요합니다. 다시 로그인해주세요.');
    localStorage.removeItem('accessToken');
    window.location.href = '/users/login';
}

// ===== [MSA 연결부] 게시판 프론트(basic-board)를 토큰 인증에 잇는 공통 유틸 =====

// 게시판 API도 auth와 동일하게 같은 출처(/api/...)로 호출함 —
// web-service가 Feign(BoardClient)으로 edge를 거쳐 board-service에 대리 호출하는 구조
// 브라우저 입장에서 API 주소가 전부 같은 출처라 CORS 문제가 아예 생기지 않음

// 원본(세션 방식) 게시판의 checkSession()을 대체하는 토큰 확인
// 세션 방식에서는 서버가 페이지를 렌더링할 때 사용자 정보를 hidden input에 심어줬지만,
// 토큰 방식에서는 페이지 요청(주소창 이동)에 신원이 없음 — Authorization 헤더를 못 싣음
// 그래서 페이지 도착 "후" JS가 토큰으로 사용자 정보를 조회해 같은 자리(hidden input)를 채움
// 이후의 원본 JS 로직(작성자 본인 확인, 댓글 작성자 등)은 그대로 동작
let checkAuth = async () => {
    setupAjax();
    try {
        if (localStorage.getItem('accessToken') == null) {
            await refreshTokens(); // 새로고침/새 탭이면 메모리 토큰이 없음 → refresh 쿠키로 복원
        }
        return fillUserInputs(await getUserInfo());
    } catch (e) {
        try {
            await refreshTokens(); // access 만료(401)였다면 한 번 재발급 후 재시도
            return fillUserInputs(await getUserInfo());
        } catch (e2) {
            redirectToLogin(); // refresh까지 없거나 만료 → 로그인 페이지로
            return null;       // 리다이렉트 뒤 남은 코드가 돌지 않도록 호출부에서 null 체크
        }
    }
}

// 세션 방식에서 서버가 채워주던 hidden input을 토큰에서 복원한 값으로 채움
let fillUserInputs = (user) => {
    $('#hiddenUserId').val(user.userId);
    $('#hiddenUserName').val(user.userName);
    return user;
}

// 회원 탈퇴 — 서버에서 탈퇴 saga(계정 상태 변경 + 게시글·댓글 삭제)가 돎
// 실패(503)하면 서버가 보상으로 원상복구를 끝낸 상태라, 사용자는 그냥 다시 시도하면 됨
let withdraw = () => {
    if (!confirm('정말 탈퇴하시겠습니까? 작성한 글과 댓글이 모두 삭제됩니다.')) return;

    $.ajax({
        type: 'DELETE',
        url: '/api/users/me',
        dataType: 'json',
        xhrFields: {
            withCredentials: true // refresh token 쿠키를 포함해야 서버가 지울 수 있음
        },
        success: (response) => {
            localStorage.removeItem('accessToken');
            alert(response.message);
            window.location.href = response.url;
        },
        error: (xhr) => {
            let response = xhr.responseJSON;
            alert(response && response.message ? response.message : '탈퇴 처리에 실패했습니다.');
        }
    });
}

// 서버가 refresh token 쿠키를 지우고, 클라이언트는 access token을 지움
// (게시판 페이지들이 함께 쓰는 공용 함수)
let logout = () => {
    $.ajax({
        type: 'POST',
        url: '/api/users/logout',
        dataType: 'json',
        xhrFields: {
            withCredentials: true // refresh token 쿠키를 포함해야 서버가 지울 수 있음
        },
        success: (response) => {
            localStorage.removeItem('accessToken');
            alert(response.message);
            window.location.href = response.url;
        },
        error: () => {
            // 서버 호출이 실패해도 로컬 토큰은 지우고 로그인 페이지로 보냄
            localStorage.removeItem('accessToken');
            window.location.href = '/users/login';
        }
    });
}