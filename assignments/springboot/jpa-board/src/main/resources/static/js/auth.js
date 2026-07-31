const getAccessToken = () => {
    return localStorage.getItem('accessToken');
};

const getTokenPayload = () => {
    const accessToken = getAccessToken();

    if (!accessToken) {
        return null;
    }

    try {
        const payload = accessToken.split('.')[1];
        const decodedPayload = payload
            .replace(/-/g, '+')
            .replace(/_/g, '/');

        return JSON.parse(
            decodeURIComponent(
                atob(decodedPayload)
                    .split('')
                    .map(character =>
                        '%' + character.charCodeAt(0)
                            .toString(16)
                            .padStart(2, '0')
                    )
                    .join('')
            )
        );
    } catch (error) {
        console.error('Access Token 해석 실패:', error);
        localStorage.removeItem('accessToken');
        return null;
    }
};

const refreshAccessToken = () => {
    return $.ajax({
        type: 'POST',
        url: '/api/tokens/refresh'
    }).then(response => {
        localStorage.setItem(
            'accessToken',
            response.accessToken
        );

        return response.accessToken;
    });
};

const logout = () => {
    $.ajax({
        type: 'POST',
        url: '/api/members/logout',

        complete: () => {
            localStorage.removeItem('accessToken');
            window.location.href = '/';
        }
    });
};

const restoreAccessToken = () => {
    if (getAccessToken()) {
        return $.Deferred().resolve().promise();
    }

    return refreshAccessToken()
        .fail(() => {
            localStorage.removeItem('accessToken');
        });
};

let isRefreshing = false;
let refreshRequest = null;

$(document).ajaxSend((event, xhr, settings) => {
    if (settings.url === '/api/tokens/refresh') {
        return;
    }

    const accessToken = getAccessToken();

    if (accessToken) {
        xhr.setRequestHeader(
            'Authorization',
            `Bearer ${accessToken}`
        );
    }
});

$(document).ajaxError((event, xhr, settings) => {
    if (
        xhr.status !== 401
        || settings.url === '/api/tokens/refresh'
        || settings._retry
    ) {
        return;
    }

    settings._retry = true;

    if (!isRefreshing) {
        isRefreshing = true;

        refreshRequest = refreshAccessToken()
            .always(() => {
                isRefreshing = false;
            });
    }

    refreshRequest
        .done(() => {
            $.ajax(settings);
        })
        .fail(() => {
            localStorage.removeItem('accessToken');

            if (window.location.pathname !== '/members/login') {
                window.location.href = '/members/login';
            }
        });
});