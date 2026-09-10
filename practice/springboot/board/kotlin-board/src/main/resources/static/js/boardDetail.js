/*
 * 상세 화면
 *   GET    /api/boards/{id}   조회
 *   DELETE /api/boards/{id}   삭제
 */

$(document).ready(() => {
    loadBoard();

    $('#editBtn').on('click', () => {
        location.href = '/update/' + $('#boardId').val();
    });

    $('#deleteBtn').on('click', deleteBoard);
});

let loadBoard = () => {
    let id = $('#boardId').val(); // 컨트롤러가 model에 담아 준 값이 숨은 input에 들어 있음

    $.ajax({
        type: 'GET',
        url: '/api/boards/' + id,
        success: (response) => {
            // .text()로 넣으면 태그가 실행되지 않으므로 escape가 따로 필요 없음
            // (.html()로 넣으면 사용자가 쓴 <script>가 실행될 수 있음)
            $('#title').text(response.title);
            $('#userId').text(response.userId);
            $('#created').text(response.created);
            $('#content').text(response.content);
        },
        error: (xhr) => {
            console.error('오류 발생:', xhr);
            alert('게시글을 불러오지 못했습니다.\n' + readErrorMessage(xhr));
            location.href = '/';
        }
    });
}

let deleteBoard = () => {
    // 되돌릴 수 없는 동작이므로 한 번 확인
    if (!confirm('정말 삭제하시겠습니까?')) return;

    $.ajax({
        type: 'DELETE',
        url: '/api/boards/' + $('#boardId').val(),
        // 서버는 204 No Content를 돌려줌. 본문이 없어도 2xx이므로 success로 들어옴
        success: () => {
            alert('삭제되었습니다.');
            location.href = '/';
        },
        error: (xhr) => {
            console.error('오류 발생:', xhr);
            alert('삭제에 실패했습니다.\n' + readErrorMessage(xhr));
        }
    });
}