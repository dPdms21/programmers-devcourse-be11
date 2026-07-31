$(document).ready(() => {
    $('#signin').click(() => {
        const userId = $('#user_id').val();
        const password = $('#password').val();

        const formData = {
            userId: userId,
            password: password
        };

        $.ajax({
            type: 'POST',
            url: '/api/members/login',
            data: formData,
            dataType: 'json',

            success: (response) => {
                localStorage.setItem(
                    'accessToken',
                    response.accessToken
                );

                window.location.href = '/';
            },

            error: (error) => {
                console.error('오류 발생:', error);

                if (error.responseJSON?.message) {
                    alert(error.responseJSON.message);
                } else {
                    alert('아이디 또는 비밀번호가 올바르지 않습니다.');
                }
            }
        });
    });
});