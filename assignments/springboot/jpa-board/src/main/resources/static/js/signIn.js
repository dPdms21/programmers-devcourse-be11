$(document).ready(() => {

    $('#signin').click(() => {

        let userId = $('#user_id').val();
        let password = $('#password').val();

        let formData = {
            username : userId,
            password : password
        }

        $.ajax({
            type: 'POST',
            url: '/api/members/login', // 서버의 엔드포인트 URL
            data: formData,
            dataType: 'json',
            success: (response) => {
                console.log('res :: ', response)

                alert(response.message);

                if (response.loggedIn) {
                    window.location.href = response.url;
                }
            },
            error: (error) => {
                console.error('오류 발생:', error);

                if (error.responseJSON) {
                    alert(error.responseJSON.message);
                } else {
                    alert('로그인 요청 중 오류가 발생했습니다.');
                }
            }
        });

    });

});