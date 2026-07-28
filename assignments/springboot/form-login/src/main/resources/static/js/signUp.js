$(document).ready(() => {
    $('#signup').click(() => {
        let formData = {
            userId: $('#user_id').val(),
            password: $('#password').val(),
            userName: $('#user_name').val()
        }

        $.ajax({
            type: 'POST',
            url: '/api/users/join',
            data: JSON.stringify(formData),                  // 이쪽은 JSON (우리 컨트롤러의 @RequestBody가 받음)
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function(response) {
                alert('회원가입이 성공했습니다.\n로그인해주세요.');
                window.location.href = response.url;
            },
            error: function(error) {
                alert('회원가입 중 오류가 발생했습니다.');
            }
        });
    });
});