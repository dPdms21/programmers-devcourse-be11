/*
 * 글쓰기 화면
 *   POST /api/boards   (본문: JSON)
 */

$(document).ready(() => {
    $('#submitBtn').on('click', createBoard);
});

let createBoard = () => {
    // 화면에서 값을 모아 서버의 BoardCreateRequest 와 "같은 이름"의 키로 만듦
    // 이름이 다르면 JSON -> 코틀린 객체 변환에서 값이 비어버림
    let board = {
        userId: $('#userId').val().trim(),
        title: $('#title').val().trim(),
        content: $('#content').val().trim()
    };

    // 빈 값은 화면에서 먼저 걸러줌. (사용자에게 바로 알려 주는 편이 친절)
    if (!board.userId || !board.title || !board.content) {
        alert('작성자, 제목, 내용을 모두 입력해 주세요.');
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/api/boards',
        contentType: 'application/json',
        data: JSON.stringify(board),
        success: (response) => {
            // 서버는 방금 저장된 글(BoardResponse)을 돌려줌. id도 여기 들어 있음
            alert('게시글이 등록되었습니다.');
            location.href = '/';
        },
        error: (xhr) => {
            console.error('오류 발생:', xhr);
            alert('등록에 실패했습니다.\n' + readErrorMessage(xhr));
        }
    });
}