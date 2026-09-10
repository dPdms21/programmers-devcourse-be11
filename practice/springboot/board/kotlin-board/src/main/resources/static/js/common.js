/**
 * 실패한 요청을 사용자에게 보여 줄 문구로 바꿈
 *
 * error 콜백의 첫 번째 인자는 jqXHR 객체이고, 여기에 응답 정보가 들어 있음
 *  - xhr.status      : 상태 코드 (404, 500 ...)
 *  - xhr.responseText: 응답 본문(문자열)
 *  - xhr.responseJSON: 응답 본문이 JSON 이면 파싱된 객체
 *
 * 우리 서버는 실패할 때 상태 코드만 정확히 내려줌 (404는 본문이 아예 없음)
 * "무엇이 잘못됐는지"는 상태 코드가 이미 말해 주므로, 그 숫자를 보고 문구를 고르면 됨
 */
let readErrorMessage = (xhr) => {
    if (xhr.status === 404) return '해당 게시글이 없습니다. (이미 삭제되었을 수 있습니다)';
    return '요청을 처리하지 못했습니다. (' + xhr.status + ')';
}

/**
 * 사용자가 입력한 값을 화면에 그대로 꽂아 넣으면
 * <script> 같은 태그가 실행될 수 있음(XSS). 그래서 특수문자를 문자로 바꿔줌
 *
 * jQuery로는 이렇게 한 줄로 됨
 *  - .text(값)으로 넣으면 태그가 아니라 "글자"로 들어가고,
 *  - .html()로 다시 꺼내면 <, >, &가 이미 &lt; &gt; &amp;로 바뀌어 있음
 */
let escapeHtml = (value) => $('<div>').text(value == null ? '' : value).html();