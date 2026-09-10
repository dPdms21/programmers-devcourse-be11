/*
 * 글수정 화면
 *   GET /api/boards/{id}   기존 값을 불러와 폼을 채움
 *   PUT /api/boards/{id}   수정 내용을 보냄
 */

$(document).ready(() => {
    loadBoard();
    $('#submitBtn').on('click', updateBoard);
});

/** 수정 폼에 현재 값을 채워 넣음 */
let loadBoard = () => {
    $.ajax({
        type: 'GET',
        url: '/api/boards/' + $('#boardId').val(),
        success: (response) => {
            // input / textarea의 값은 .text()가 아니라 .val()로 채움
            $('#userId').val(response.userId);
            $('#title').val(response.title);
            $('#content').val(response.content);
        },
        error: (xhr) => {
            console.error('오류 발생:', xhr);
            alert('게시글을 불러오지 못했습니다.\n' + readErrorMessage(xhr));
            location.href = '/';
        }
    });
}

let updateBoard = () => {
    let id = $('#boardId').val();

    // 서버의 BoardUpdateRequest는 제목/내용만 받음
    // 작성자와 작성일은 바뀌지 않는 값이라 애초에 요청에 담지 않음 (엔티티에서도 val로 막아 두었음)
    let board = {
        title: $('#title').val().trim(),
        content: $('#content').val().trim()
    };

    if (!board.title || !board.content) {
        alert('제목과 내용을 모두 입력해 주세요.');
        return;
    }

    $.ajax({
        type: 'PUT',
        url: '/api/boards/' + id, // 수정 대상은 URL 경로로 식별 (PUT /api/boards/{id})
        contentType: 'application/json',
        data: JSON.stringify(board),
        success: () => {
            alert('게시글이 수정되었습니다.');
            location.href = '/detail?id=' + id;
        },
        error: (xhr) => {
            console.error('오류 발생:', xhr);
            alert('수정에 실패했습니다.\n' + readErrorMessage(xhr));
        }
    });
}