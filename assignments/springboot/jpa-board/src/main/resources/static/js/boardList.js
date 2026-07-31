$(document).ready(() => {
    restoreAccessToken()
        .always(() => {
            loadBoard(1);
            updateLoginView();
        });

    $('#logoutBtn').click(() => {
        logout();
    });
});

const PAGE_SIZE = 10;

const updateLoginView = () => {
    const accessToken = localStorage.getItem('accessToken');

    if (!accessToken) {
        showGuestView();
        return;
    }

    $.ajax({
        type: 'GET',
        url: '/api/members/info',

        success: (member) => {
            $('#welcomeMessage').text(
                `${member.userName}님 환영합니다.`
            );

            $('#roleBadge').text(
                member.role === 'ROLE_ADMIN'
                    ? '관리자'
                    : '일반 회원'
            );

            $('#guestMenu').hide();
            $('#memberMenu').show();
        },

        error: (error) => {
            console.error('회원 정보 조회 실패:', error);

            localStorage.removeItem('accessToken');
            showGuestView();
        }
    });
};

const showGuestView = () => {
    $('#welcomeMessage').text('게시판');
    $('#roleBadge').text('');

    $('#memberMenu').hide();
    $('#guestMenu').show();
};

const loadBoard = (page) => {
    $.ajax({
        type: 'GET',
        url: '/api/boards',
        data: {
            page: page,
            size: PAGE_SIZE
        },

        success: (response) => {
            renderBoards(response.boards);
            renderPagination(page, response.totalPages);
        },

        error: (error) => {
            console.error('게시판 조회 실패:', error);
            alert('게시판 데이터를 불러오는 데 오류가 발생했습니다.');
        }
    });
};

const renderBoards = (boards) => {
    const $content = $('#boardContent');

    $content.empty();

    if (boards == null || boards.length === 0) {
        $content.append(`
            <tr>
                <td colspan="4" style="text-align: center;">
                    글이 존재하지 않습니다.
                </td>
            </tr>
        `);

        return;
    }

    boards.forEach((item) => {
        $content.append(`
            <tr>
                <td>${item.id}</td>
                <td>
                    <a href="/detail?id=${item.id}">
                        ${item.title}
                    </a>
                </td>
                <td>${item.userId}</td>
                <td>${item.created}</td>
            </tr>
        `);
    });
};

const renderPagination = (currentPage, totalPages) => {
    const $pagination = $('#pagination');

    $pagination.empty();

    for (let page = 1; page <= totalPages; page++) {
        const $button = $(
            `<button class="btn page-btn">${page}</button>`
        );

        if (page === currentPage) {
            $button.addClass('active');
            $button.prop('disabled', true);
        }

        $button.on('click', () => {
            loadBoard(page);
        });

        $pagination.append($button);
    }
};