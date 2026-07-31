let currentMember = null;

$(document).ready(() => {
    $('#editBtn').hide();
    $('#deleteBtn').hide();

    restoreAccessToken()
        .always(() => {
            loadCurrentMember();
        });
});

const loadCurrentMember = () => {
    const accessToken = getAccessToken();

    if (!accessToken) {
        loadBoardDetail();
        return;
    }

    $.ajax({
        type: 'GET',
        url: '/api/members/info',

        success: (member) => {
            currentMember = member;
            loadBoardDetail();
        },

        error: function(xhr) {
            if (xhr.status === 401) {
                return;
            }

            console.error('회원 정보 조회 오류:', xhr);
            loadBoardDetail();
        }
    });
};

const loadBoardDetail = () => {
    const resourceId = $('#hiddenId').val();

    $.ajax({
        type: 'GET',
        url: `/api/boards/${resourceId}`,

        success: (response) => {
            $('#title').text(response.title);
            $('#content').text(response.content);
            $('#userId').text(response.userId);
            $('#created').text(response.created);

            updateActionButtons(response.userId);
            renderFile(response.filePath);
        },

        error: function(xhr) {
            if (xhr.status === 401) {
                return;
            }

            console.error('게시글 상세 조회 오류:', xhr);
            alert('상세 데이터를 불러오는데 오류가 발생했습니다.');
        }
    });
};

const updateActionButtons = (articleUserId) => {
    const canManage =
        currentMember !== null &&
        (
            currentMember.userId === articleUserId ||
            currentMember.role === 'ROLE_ADMIN'
        );

    if (canManage) {
        $('#editBtn').show();
        $('#deleteBtn').show();
    } else {
        $('#editBtn').hide();
        $('#deleteBtn').hide();
    }
};

const renderFile = (filePath) => {
    $('#fileList').empty();

    if (!filePath) {
        $('#fileList').append('<li>첨부된 파일이 없습니다.</li>');
        return;
    }

    const normalized = filePath.replace(/\\/g, '/');
    const fileName = normalized.substring(
        normalized.lastIndexOf('/') + 1
    );

    $('#fileList').append(`
        <li>
            <a href="/api/boards/file/download/${fileName}">
                ${fileName}
            </a>
        </li>
    `);
};

const editArticle = () => {
    const resourceId = $('#hiddenId').val();
    window.location.href = `/update/${resourceId}`;
};

const deleteArticle = () => {
    const resourceId = $('#hiddenId').val();

    $.ajax({
        type: 'DELETE',
        url: `/api/boards/${resourceId}`,

        success: () => {
            alert('게시글이 삭제되었습니다.');
            window.location.href = '/';
        },

        error: function(xhr) {
            if (xhr.status === 401) {
                return;
            }

            if (xhr.status === 403) {
                alert('게시글을 삭제할 권한이 없습니다.');
                return;
            }

            console.error('게시글 삭제 오류:', xhr);
            alert('게시글 삭제 중 오류가 발생하였습니다.');
        }
    });
};