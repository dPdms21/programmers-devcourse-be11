$(document).ready(() => {
    $('#signin').click(() => {
        let formData = {
            userId: $('#user_id').val(),
            password: $('#password').val()
        }

        $.ajax({
            type: 'POST',
            url: '/users/login',   // UsernamePasswordAuthenticationFilter가 가로채는 URL
            data: formData,        // JSON.stringify 금지! 필터는 요청 "파라미터"만 읽는다
            dataType: 'json',
            success: function(response) {          // 성공 핸들러의 200 JSON
                alert(response.message);
                window.location.href = response.url;
            },
            error: function(xhr) {                 // 실패 핸들러의 401 JSON
                let response = xhr.responseJSON;
                alert(response && response.message ? response.message : '로그인 중 오류가 발생했습니다.');
            }
        });
    });
});