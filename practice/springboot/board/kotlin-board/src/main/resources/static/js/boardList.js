/*
 * 게시글 목록 화면
 *   GET /api/boards?page=1&size=10&keyword=검색어
 */

const PAGE_SIZE = 10;    // 한 페이지에 보여 줄 게시글 수
let currentKeyword = ''; // 현재 적용된 검색어 (페이지를 넘겨도 유지해야 함)

// $(document).ready(...) : HTML이 모두 준비된 뒤에 실행하라는 뜻
// <head>에서 이 스크립트를 읽는 시점엔 아직 <body>의 요소들이 없으므로,
// 여기 안에서 이벤트를 걸어야 $('#searchBtn')이 제대로 잡힘
$(document).ready(() => {
    loadBoards(1); // 처음엔 1페이지를 보여 준다

    // 검색 - 조건이 바뀌면 결과 전체가 달라지므로 1페이지부터 다시 보여줌
    $('#searchBtn').on('click', () => {
        currentKeyword = $('#keyword').val().trim();
        loadBoards(1);
    });

    // 초기화 - 검색어를 비우고 전체 목록으로 돌아감
    $('#resetBtn').on('click', () => {
        $('#keyword').val('');
        currentKeyword = '';
        loadBoards(1);
    });

    // 검색창에서 엔터로도 검색되게 함 (작은 편의 기능)
    $('#keyword').on('keydown', (e) => {
        if (e.key === 'Enter') $('#searchBtn').trigger('click');
    });
});

/** page번째 페이지를 불러와 화면을 다시 그림 */
let loadBoards = (page) => {
    // GET 요청의 data는 ?page=1&size=10&keyword=... 쿼리 파라미터가 됨
    // 키 이름이 컨트롤러의 @RequestParam 이름과 같아야 바인딩됨
    // 검색어가 없으면 아예 키를 담지 않음 (빈 문자열을 보내지 않으려고)
    let data = { page: page, size: PAGE_SIZE };
    if (currentKeyword) data.keyword = currentKeyword;

    $.ajax({
        type: 'GET',
        url: '/api/boards',
        data: data,
        success: (response) => {
            // 서버의 BoardPageResponse가 그대로 JSON이 됨
            // { boards: [...], page, totalPages, totalElements, last }
            renderBoards(response.boards);
            renderPagination(response.page, response.totalPages);
        },
        error: (xhr) => {
            console.error('오류 발생:', xhr);
            alert('목록을 불러오지 못했습니다.\n' + readErrorMessage(xhr));
        }
    });
}

/** 목록 테이블 본문을 그림 */
let renderBoards = (boards) => {
    const $body = $('#boardBody');
    $body.empty(); // 기존 내용 비우기 (다시 그릴 때 중복으로 쌓이지 않도록)

    if (boards == null || boards.length === 0) {
        $body.append('<tr><td colspan="4" class="empty">게시글이 없습니다.</td></tr>');
        return;
    }

    boards.forEach((board) => {
        $body.append(
            `
            <tr>
                <td class="center">${board.id}</td>
                <td><a href="/detail?id=${board.id}">${escapeHtml(board.title)}</a></td>
                <td>${escapeHtml(board.userId)}</td>
                <td>${board.created}</td>
            </tr>
            `
        );
    });
}

/** 아래쪽 페이지 번호 버튼(1, 2, 3 ...)을 그림 */
let renderPagination = (currentPage, totalPages) => {
    const $pagination = $('#pagination');
    $pagination.empty();

    for (let p = 1; p <= totalPages; p++) {
        const $btn = $(`<button type="button" class="btn">${p}</button>`);

        if (p === currentPage) {
            // 현재 페이지는 강조하고 클릭할 수 없게 막음
            $btn.addClass('active');
            $btn.prop('disabled', true);
        }

        $btn.on('click', () => loadBoards(p));
        $pagination.append($btn);
    }
}