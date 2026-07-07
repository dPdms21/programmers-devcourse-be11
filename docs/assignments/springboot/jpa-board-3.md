# JPA 게시판 3 - 게시판 읽기 만들기

> JPA 기반 게시판 과제에서 게시글 읽기 기능을 구현한다. 게시글 목록을 페이지 단위로 조회하고, 글 하나를 선택했을 때 상세 정보를 조회하는 흐름을 만든다.
>
> 이번 단계에서는 글쓰기와 파일 업로드는 다루지 않는다. `Board` 엔티티에 `filePath` 필드는 만들지만, 아직 글쓰기 기능이 없으므로 값은 `null` 상태로 둔다.

---

## 0. 먼저 알아둘 점

이 과제는 1편 회원가입과 2편 로그인이 완료되었다는 전제로 진행한다.

이미 학습한 내용은 다음과 같다.

* 컨트롤러, 서비스, 리포지토리 계층 분리
* JPA 엔티티와 리포지토리 사용
* 요청 DTO와 응답 DTO 사용
* `Optional`을 이용한 값 유무 처리
* `@RestControllerAdvice`를 이용한 예외 공통 처리

제공된 화면과 데이터는 다음과 같다.

* 목록 화면: `board-list.html`, `boardList.js`
* 상세 화면: `board-detail.html`, `boardDetail.js`
* 목록 화면 뷰 컨트롤러: `GET /`
* 상세 화면 뷰 컨트롤러: `GET /detail`
* 시드 데이터: `data.sql`에 저장된 게시글 20개

이번 단계에서 작성할 백엔드 요소는 다음과 같다.

* `Board` 엔티티
* `BoardRepository`
* 게시글 목록 조회 서비스
* 게시글 상세 조회 서비스
* 목록 응답 DTO
* 상세 응답 DTO
* 게시글 API 컨트롤러
* 없는 글 조회 시 404 예외 처리

프론트엔드의 `boardList.js`, `boardDetail.js`는 다음 요청과 응답 형식을 기준으로 동작한다.

목록 조회 API는 다음 형식을 사용한다.

```text
GET /api/boards?page=1&size=10
```

응답 예시는 다음과 같다.

```json
{
  "boards": [
    {
      "id": 20,
      "title": "20번째 글",
      "content": "...",
      "userId": "hong",
      "filePath": null,
      "created": "2026-06-24 08:43"
    }
  ],
  "last": false,
  "totalPages": 2
}
```

상세 조회 API는 다음 형식을 사용한다.

```text
GET /api/boards/{id}
```

응답 예시는 다음과 같다.

```json
{
  "title": "20번째 글",
  "content": "...",
  "created": "2026-06-24 08:43",
  "userId": "hong",
  "filePath": null
}
```

없는 글을 조회하면 다음처럼 응답한다.

```json
{
  "message": "게시글을 찾을 수 없습니다. id=999"
}
```

이때 HTTP 상태 코드는 `404 Not Found`로 내려야 한다.

---

## 1. 무엇을 만드는가?

게시판 목록 페이지에서 글을 최신순으로 10개씩 조회한다. 하단 페이지 번호를 눌러 다음 페이지로 이동할 수 있어야 한다. 글 제목을 클릭하면 상세 페이지로 이동하고, 상세 화면에서 제목, 내용, 작성자, 작성일을 확인할 수 있어야 한다.

| 주소와 메서드                | 역할               | 계층       |
| ---------------------- | ---------------- | -------- |
| `GET /`                | 게시글 목록 화면 조회     | 뷰 컨트롤러   |
| `GET /detail?id=...`   | 게시글 상세 화면 조회     | 뷰 컨트롤러   |
| `GET /api/boards`      | 게시글 목록 데이터 조회    | API 컨트롤러 |
| `GET /api/boards/{id}` | 게시글 1건 상세 데이터 조회 | API 컨트롤러 |

동작 예시는 다음과 같다.

```text
[목록 조회]
/ 접속
→ 최신 글이 위에 오도록 게시글 목록 조회
→ 한 페이지에 10개씩 표시
→ 하단에 페이지 번호 표시

[상세 조회]
글 제목 클릭
→ /detail?id=20 이동
→ 제목, 작성자, 작성일, 내용 표시

[없는 글 조회]
/api/boards/999 요청
→ 게시글 없음
→ 404 응답
→ "게시글을 찾을 수 없습니다. id=999" 메시지 반환
```

---

## 2. 학습 목표

| 개념                                        | 학습 위치          |
| ----------------------------------------- | -------------- |
| `@Entity`로 게시글 테이블 매핑                     | Step 1         |
| `TEXT`, `nullable`, 날짜 컬럼 매핑              | Step 1         |
| `@JsonFormat`으로 날짜 출력 형식 지정               | Step 1         |
| `JpaRepository`의 `findAll(Pageable)` 사용   | Step 2, Step 3 |
| 화면 페이지 번호와 `Pageable` 페이지 번호 차이 이해        | Step 3         |
| 목록 응답 DTO에 페이지 정보 포함                      | Step 4         |
| `findById()`와 `Optional.orElseThrow()` 사용 | Step 5         |
| 없는 글 조회 시 404 예외 처리                       | Step 5         |
| 목록 페이징과 상세 조회 화면 확인                       | Step 6         |

---

## 3. 핵심 개념

### (1) 페이징

게시글이 많아지면 모든 데이터를 한 번에 조회해 화면에 내려주는 방식은 적절하지 않다. 데이터가 많을수록 응답 크기가 커지고, 화면에서도 한 번에 처리해야 할 양이 많아진다.

따라서 게시글 목록은 페이지 단위로 나누어 조회한다. Spring Data JPA에서는 페이징 조건을 `Pageable`로 표현한다.

```java
Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
```

위 코드는 다음 의미를 가진다.

| 값                            | 의미                |
| ---------------------------- | ----------------- |
| `0`                          | 조회할 페이지 번호        |
| `10`                         | 한 페이지에 가져올 데이터 개수 |
| `Sort.by("id").descending()` | `id` 기준 내림차순 정렬   |

게시글은 나중에 작성된 글일수록 `id` 값이 크다. 따라서 `id` 내림차순으로 정렬하면 최신 글이 위에 표시된다.

화면에서 사용하는 페이지 번호와 `Pageable`의 페이지 번호는 시작 기준이 다르다.

| 구분                | 시작 번호  |
| ----------------- | ------ |
| 화면 페이지 번호         | 1부터 시작 |
| `Pageable` 페이지 번호 | 0부터 시작 |

따라서 화면에서 `page=1`이 넘어오면 서비스에서는 `page - 1`을 적용해 `Pageable`의 `0`페이지로 변환해야 한다.

`findAll(pageable)`의 반환 타입은 `Page<Board>`이다. `Page`는 목록 데이터뿐 아니라 전체 페이지 수, 마지막 페이지 여부 같은 페이징 정보도 함께 제공한다.

| 메서드                  | 의미                |
| -------------------- | ----------------- |
| `getContent()`       | 현재 페이지에 담긴 게시글 목록 |
| `getTotalElements()` | 전체 게시글 수          |
| `getTotalPages()`    | 전체 페이지 수          |
| `isLast()`           | 마지막 페이지 여부        |

`page.getContent()`의 `content`는 현재 페이지에 담긴 목록을 의미한다. `board.getContent()`의 `content`는 게시글 본문을 의미한다. 이름은 같지만 의미가 다르므로 구분해서 이해해야 한다.

### (2) `findById()`와 `Optional`

상세 조회에서는 게시글 id로 게시글 1건을 찾는다. 이때 해당 id의 게시글이 존재할 수도 있고 존재하지 않을 수도 있다.

```java
Optional<Board> result = boardRepository.findById(id);
```

`findById()`가 `Optional`을 반환하는 이유는 조회 결과가 없을 수 있기 때문이다. 조회 결과가 없는 상황을 `null`로 처리하면 `NullPointerException`이 발생하기 쉽다.

상세 조회에서는 게시글이 없을 때 빈 결과를 그대로 반환하지 않고 예외를 던진다.

```java
return boardRepository.findById(id)
        .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다. id=" + id));
```

값이 있으면 `Board`를 반환하고, 값이 없으면 `BoardNotFoundException`을 던진다. 이 예외는 `@RestControllerAdvice`에서 잡아 `404 Not Found` 응답으로 변환한다.

### (3) `@JsonFormat`

`LocalDateTime`을 JSON으로 변환하면 기본적으로 ISO-8601 형식으로 출력된다. 이 경우 날짜와 시간 사이에 `T`가 포함된다.

```text
2026-06-24T08:43:00
```

화면에서는 다음 형식이 더 읽기 쉽다.

```text
2026-06-24 08:43
```

날짜 출력 형식을 지정하려면 `@JsonFormat`을 사용한다.

```java
@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
private LocalDateTime created;
```

| 구분               | 출력 예시                 |
| ---------------- | --------------------- |
| 지정하지 않은 경우       | `2026-06-24T08:43:00` |
| `@JsonFormat` 지정 | `2026-06-24 08:43`    |

---

## Step 1. 게시글 엔티티 만들기

`board` 테이블과 매핑되는 `Board` 엔티티를 작성한다. 게시글은 제목, 본문, 작성자, 첨부 파일 경로, 작성일을 가진다.

작성할 내용은 다음과 같다.

* `domain/entity/Board` 클래스 생성
* `id`, `title`, `content`, `userId`, `filePath`, `created` 필드 작성
* `content`는 길 수 있으므로 `TEXT`로 지정
* `filePath`는 첨부 파일이 없을 수 있으므로 nullable로 둠
* `created`는 날짜 출력 형식을 위해 `@JsonFormat` 사용

힌트는 다음과 같다.

```java
// domain/entity/Board.java
@Entity
@Table(name = "board")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 30)
    private String userId;

    @Column(length = 255)
    private String filePath;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created;
}
```

`filePath`는 파일 업로드가 구현되기 전까지는 값이 없을 수 있다. 따라서 `nullable = false`를 지정하지 않는다.

`created`는 `LocalDateTime` 타입으로 작성하고, 화면에서 사람이 읽기 좋은 형식으로 내려가기 위해 `@JsonFormat(pattern = "yyyy-MM-dd HH:mm")`을 붙인다.

---

## Step 2. 게시글 리포지토리 만들기

게시글 조회를 담당할 `BoardRepository`를 작성한다. 이번 단계에서는 별도의 쿼리 메서드가 필요하지 않다.

`JpaRepository`를 상속하면 기본 CRUD 메서드와 페이징 조회 메서드를 사용할 수 있다.

작성할 내용은 다음과 같다.

* `domain/repository/BoardRepository` 인터페이스 생성
* `JpaRepository<Board, Long>` 상속
* 커스텀 메서드는 작성하지 않음

힌트는 다음과 같다.

```java
// domain/repository/BoardRepository.java
public interface BoardRepository extends JpaRepository<Board, Long> {
}
```

`JpaRepository`는 `findAll(Pageable)`, `findById()`, `count()` 같은 메서드를 기본으로 제공한다.

따라서 목록 페이징 조회, 상세 조회, 전체 게시글 개수 조회는 별도 메서드 없이 구현할 수 있다.

---

## Step 3. 목록 서비스 만들기

`BoardService`에 게시글 목록 조회 로직을 작성한다. 핵심은 화면에서 넘어온 페이지 번호를 `Pageable`에 맞게 변환하는 것이다.

작성할 내용은 다음과 같다.

* `getBoardList(int page, int size)` 메서드 작성
* 화면 페이지 번호를 `page - 1`로 변환
* `id` 기준 내림차순 정렬
* `findAll(pageable).getContent()`로 현재 페이지 게시글 목록 반환
* `getTotalBoards()` 메서드 작성
* 조회 전용 로직이므로 `@Transactional(readOnly = true)` 사용

힌트는 다음과 같다.

```java
// service/BoardService.java
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<Board> getBoardList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        return boardRepository.findAll(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public int getTotalBoards() {
        return (int) boardRepository.count();
    }
}
```

처리 흐름은 다음과 같다.

```text
화면에서 page=1, size=10 요청
→ 서비스에서 page - 1 적용
→ PageRequest.of(0, 10, id 내림차순) 생성
→ findAll(pageable) 실행
→ 현재 페이지의 게시글 목록 반환
```

`getTotalBoards()`는 전체 게시글 수를 구한다. 컨트롤러에서는 이 값을 사용해 전체 페이지 수를 계산한다.

---

## Step 4. 목록 컨트롤러 만들기

목록 화면에서 호출하는 `GET /api/boards` API를 작성한다. 이 API는 게시글 목록뿐 아니라 마지막 페이지 여부와 전체 페이지 수도 함께 내려줘야 한다.

작성할 내용은 다음과 같다.

* `dto/BoardListResponseDto` 생성
* `boards`, `last`, `totalPages` 필드 작성
* `BoardApiController` 생성
* `@RestController`, `@RequestMapping("/api/boards")` 사용
* `page`, `size` 요청 파라미터 받기
* 전체 게시글 수를 기준으로 `totalPages` 계산
* 현재 페이지가 마지막 페이지인지 `last` 계산

힌트는 다음과 같다.

```java
// dto/BoardListResponseDto.java
@Getter
@Builder
public class BoardListResponseDto {

    private List<Board> boards;
    private boolean last;
    private int totalPages;
}
```

```java
// controller/BoardApiController.java
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    @GetMapping
    public BoardListResponseDto getBoardList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<Board> boards = boardService.getBoardList(page, size);
        int totalBoards = boardService.getTotalBoards();

        int totalPages = (int) Math.ceil((double) totalBoards / size);
        boolean last = page >= totalPages;

        return BoardListResponseDto.builder()
                .boards(boards)
                .last(last)
                .totalPages(totalPages)
                .build();
    }
}
```

`totalPages`는 전체 게시글 수를 페이지 크기로 나누어 계산한다. 나머지가 있으면 페이지가 하나 더 필요하므로 `Math.ceil()`로 올림 처리한다.

예를 들어 전체 게시글이 21개이고 한 페이지에 10개씩 보여주면 총 3페이지가 필요하다.

```text
21 / 10 = 2.1
Math.ceil(2.1) = 3
```

`last`는 현재 페이지가 전체 페이지 수보다 크거나 같으면 `true`로 계산한다.

---

## Step 5. 상세 조회와 없는 글 404 처리 만들기

상세 화면에서 호출하는 `GET /api/boards/{id}` API를 작성한다. 게시글 id로 1건을 조회하고, 게시글이 없으면 404 응답을 반환한다.

작성할 내용은 다음과 같다.

* `BoardService.getBoardDetail(Long id)` 메서드 작성
* `findById(id).orElseThrow()`로 없는 글 예외 처리
* `BoardNotFoundException` 생성
* `GlobalExceptionHandler`에 404 핸들러 추가
* `BoardDetailResponseDto` 생성
* `BoardApiController`에 `@GetMapping("/{id}")` 추가

힌트는 다음과 같다.

```java
// service/BoardService.java
@Transactional(readOnly = true)
public Board getBoardDetail(Long id) {
    return boardRepository.findById(id)
            .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다. id=" + id));
}
```

```java
// exception/BoardNotFoundException.java
public class BoardNotFoundException extends RuntimeException {

    public BoardNotFoundException(String message) {
        super(message);
    }
}
```

```java
// exception/GlobalExceptionHandler.java
@ExceptionHandler(BoardNotFoundException.class)
public ResponseEntity<ErrorResponseDto> handleBoardNotFound(BoardNotFoundException e) {
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), e.getMessage()));
}
```

```java
// dto/BoardDetailResponseDto.java
@Getter
@Builder
public class BoardDetailResponseDto {

    private String title;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created;

    private String userId;
    private String filePath;
}
```

```java
// controller/BoardApiController.java
@GetMapping("/{id}")
public BoardDetailResponseDto getBoardDetail(@PathVariable long id) {
    Board board = boardService.getBoardDetail(id);

    return BoardDetailResponseDto.builder()
            .title(board.getTitle())
            .content(board.getContent())
            .created(board.getCreated())
            .userId(board.getUserId())
            .filePath(board.getFilePath())
            .build();
}
```

없는 글 조회는 서버 오류가 아니라 요청한 리소스를 찾을 수 없는 상황이다. 따라서 `500 Internal Server Error`가 아니라 `404 Not Found`로 응답하는 것이 적절하다.

---

## Step 6. 화면에서 통합 실행 확인하기

모든 계층을 구현한 뒤 실제 화면에서 목록 페이징과 상세 조회가 정상 동작하는지 확인한다.

확인할 내용은 다음과 같다.

1. 애플리케이션을 실행한다.
2. 브라우저에서 `/`로 접속한다.
3. 시드 글 20개가 최신순으로 10개씩 표시되는지 확인한다.
4. 하단에 페이지 번호가 표시되는지 확인한다.
5. 2페이지로 이동했을 때 다음 글 목록이 표시되는지 확인한다.
6. 글 제목을 클릭해 `/detail?id=...`로 이동하는지 확인한다.
7. 상세 화면에서 제목, 작성자, 작성일, 내용이 표시되는지 확인한다.
8. 작성일이 `2026-06-24 08:43`처럼 `T` 없이 표시되는지 확인한다.
9. `/api/boards/999`처럼 없는 글을 요청했을 때 404 상태 코드와 메시지가 반환되는지 확인한다.

잘 안 될 때 확인할 내용은 다음과 같다.

| 증상                 | 확인할 내용                                                               |
| ------------------ | -------------------------------------------------------------------- |
| 목록이 비어 있음          | `data.sql` 시드 데이터가 실행되었는지 확인한다                                       |
| 오래된 글이 위에 표시됨      | `Sort.by("id").descending()`을 적용했는지 확인한다                             |
| 1페이지인데 목록이 비어 있음   | 화면 페이지 번호를 `page - 1`로 변환했는지 확인한다                                    |
| 페이지 번호가 표시되지 않음    | 응답에 `totalPages`, `last`가 포함되어 있는지 확인한다                              |
| 날짜에 `T`가 표시됨       | `created` 필드에 `@JsonFormat(pattern = "yyyy-MM-dd HH:mm")`을 붙였는지 확인한다 |
| 없는 글 조회 시 500이 발생함 | `BoardNotFoundException`을 처리하는 404 핸들러를 추가했는지 확인한다                   |

---

## 완성 체크리스트

* [ ] `Board` 엔티티를 작성했다
* [ ] `content`를 `TEXT`로 매핑했다
* [ ] `filePath`를 nullable 필드로 두었다
* [ ] `created`에 `@JsonFormat`을 적용했다
* [ ] `BoardRepository`가 `JpaRepository<Board, Long>`을 상속하도록 작성했다
* [ ] `getBoardList()`에서 화면 페이지 번호를 `page - 1`로 변환했다
* [ ] 게시글 목록을 `id` 내림차순으로 조회했다
* [ ] `getTotalBoards()`에서 전체 게시글 수를 조회했다
* [ ] `BoardListResponseDto`를 작성했다
* [ ] `GET /api/boards`에서 목록, 마지막 페이지 여부, 전체 페이지 수를 응답했다
* [ ] `getBoardDetail()`에서 `findById()`와 `orElseThrow()`를 사용했다
* [ ] `BoardNotFoundException`을 작성했다
* [ ] 없는 글 조회 시 404 응답을 반환하도록 예외 핸들러를 추가했다
* [ ] `BoardDetailResponseDto`를 작성했다
* [ ] `GET /api/boards/{id}`에서 상세 데이터를 응답했다
* [ ] 화면에서 목록 페이징, 상세 조회, 없는 글 404 응답을 확인했다
