# JPA 게시판 5 - 게시글 수정·삭제 만들기

> JPA 기반 게시판 과제에서 게시글 수정과 삭제 기능을 구현한다.
> 수정에서는 JPA 변경 감지를 활용해 별도의 `save()` 호출 없이 엔티티 필드 변경만으로 `UPDATE`가 실행되는 흐름을 확인한다.
> 삭제에서는 DB 데이터와 첨부파일을 함께 다루며, 파일 로직을 `FileService`로 분리해 게시글 로직과 파일 I/O 책임을 나눈다.

---

## 0. 먼저 알아둘 점

이 과제는 1편부터 4편까지의 회원가입, 로그인, 게시글 목록 조회, 게시글 상세 조회, 글쓰기, 파일 업로드와 다운로드가 완료되었다는 전제로 진행한다.

이미 작성되어 있어야 하는 요소는 다음과 같다.

* `Board` 엔티티
* `BoardRepository`
* `BoardService`
* `BoardApiController`
* `BoardNotFoundException`
* `GlobalExceptionHandler`
* 파일 업로드 설정
* 파일 저장 로직
* 글쓰기와 다운로드 API
* 상세 화면인 `board-detail.html`
* 상세 화면 요청을 처리하는 `boardDetail.js`
* 수정 화면인 `board-update.html`
* 수정 요청을 보내는 `boardUpdate.js`
* 수정 화면을 보여주는 뷰 컨트롤러인 `GET /update/{id}`

이번 단계에서 작성할 백엔드 요소는 다음과 같다.

* 게시글 삭제 요청 DTO
* 게시글 삭제 서비스와 컨트롤러
* `Board.update()` 엔티티 메서드
* 게시글 수정 요청 DTO
* 게시글 수정 서비스와 컨트롤러
* 파일 로직을 담당하는 `FileService`
* `BoardService`의 트랜잭션 정리

프론트엔드의 요청 형식은 다음과 같다.

| 기능       | 요청                                     |
| -------- | -------------------------------------- |
| 게시글 삭제   | `DELETE /api/boards/{id}`              |
| 삭제 요청 형식 | JSON                                   |
| 삭제 요청 본문 | `{ "filePath": "..." }`                |
| 게시글 수정   | `PUT /api/boards/{id}`                 |
| 수정 요청 형식 | `multipart/form-data`                  |
| 수정 폼 필드  | `title`, `content`, `fileFlag`, `file` |

`fileFlag`는 사용자가 첨부파일을 건드렸는지를 나타내는 값이다. 파일을 유지할지, 교체할지, 제거할지를 구분하기 위해 사용한다.

---

## 1. 무엇을 만드는가?

기존 게시판에 게시글 수정과 삭제 기능을 추가한다. 수정과 삭제는 첨부파일까지 함께 고려해야 한다.

게시글 수정에서는 제목과 내용을 바꾸고, 파일은 유지, 교체, 제거 중 하나로 처리한다. 게시글 삭제에서는 DB의 게시글 행을 삭제하고, 첨부파일이 있다면 디스크에 저장된 파일도 삭제한다.

구현할 API는 다음과 같다.

| 주소와 메서드                   | 역할                     | 계층       |
| ------------------------- | ---------------------- | -------- |
| `GET /update/{id}`        | 게시글 수정 화면 조회           | 뷰 컨트롤러   |
| `PUT /api/boards/{id}`    | 게시글 수정, 파일 유지·교체·제거 처리 | API 컨트롤러 |
| `DELETE /api/boards/{id}` | 게시글 삭제, 첨부파일 삭제 처리     | API 컨트롤러 |

동작 흐름은 다음과 같다.

```text
[수정]
상세 화면
→ 수정 버튼 클릭
→ /update/{id} 이동
→ 기존 제목과 내용이 채워진 수정 폼 표시
→ 제목, 내용 수정
→ 파일 유지, 교체, 제거 중 하나 선택
→ PUT /api/boards/{id} 요청
→ DB 게시글 수정
→ 필요 시 파일 교체 또는 제거
→ 목록으로 이동

[삭제]
상세 화면
→ 삭제 버튼 클릭
→ DELETE /api/boards/{id} 요청
→ DB 게시글 삭제
→ 첨부파일 삭제
→ 목록으로 이동
```

---

## 2. 학습 목표

| 개념                                           | 학습 위치  |
| -------------------------------------------- | ------ |
| `deleteById()`로 게시글 삭제                       | Step 1 |
| JSON 요청 DTO와 setter 필요성                      | Step 1 |
| JPA 변경 감지로 게시글 수정                            | Step 2 |
| setter 대신 의미 있는 엔티티 메서드 사용                   | Step 2 |
| `fileFlag`로 파일 유지·교체·제거 구분                   | Step 3 |
| `PUT`과 `@ModelAttribute`로 multipart 수정 요청 처리 | Step 4 |
| 파일 로직을 `FileService`로 분리                     | Step 5 |
| 클래스 레벨 `@Transactional(readOnly = true)` 적용  | Step 6 |
| 쓰기 메서드에서 트랜잭션 설정 덮어쓰기                        | Step 6 |

---

## 3. 핵심 개념

### (1) JPA 변경 감지

JPA에서는 트랜잭션 안에서 조회한 엔티티가 영속 상태로 관리된다. 영속 상태의 엔티티 필드를 변경하면, 트랜잭션이 끝날 때 JPA가 변경된 값을 감지해 `UPDATE` SQL을 실행한다.

```java
@Transactional
public void updateArticle(Long id, BoardUpdateRequestDto request) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다. id=" + id));

    board.update(request.getTitle(), request.getContent(), filePath);
}
```

위 코드에서는 `boardRepository.save(board)`를 호출하지 않는다. `findById()`로 조회한 `Board`는 영속 상태이고, `board.update()`로 필드가 변경되면 트랜잭션 커밋 시점에 변경 감지가 동작한다.

신규 게시글 작성은 아직 영속 상태가 아닌 새 엔티티를 저장하는 작업이므로 `save()`가 필요하다. 반면 기존 게시글 수정은 영속 상태의 엔티티를 변경하는 작업이므로 변경 감지를 활용할 수 있다.

### (2) setter 대신 의미 있는 메서드 사용

엔티티에 모든 setter를 열어두면 어느 계층에서 어떤 필드가 바뀌었는지 추적하기 어렵다. 또한 `id`, `created`, `userId`처럼 수정 대상이 아닌 필드까지 변경될 위험이 있다.

게시글 수정에서는 `update()`처럼 의미 있는 메서드를 엔티티에 두는 방식이 적절하다.

```java
public void update(String title, String content, String filePath) {
    this.title = title;
    this.content = content;
    this.filePath = filePath;
}
```

이 방식은 게시글 수정 시 변경 가능한 필드를 제목, 내용, 파일 경로로 제한한다. 작성자와 작성일은 수정 대상이 아니므로 변경하지 않는다.

### (3) `fileFlag`

게시글 수정에서 첨부파일은 세 가지 경우로 나뉜다.

| 경우 | 사용자 행동        | `fileFlag` | `file` | 서버 처리                             |
| -- | ------------- | ---------- | ------ | --------------------------------- |
| 유지 | 첨부파일을 건드리지 않음 | `false`    | 없음     | 기존 파일 경로 유지                       |
| 교체 | 새 파일을 선택함     | `true`     | 있음     | 기존 파일 삭제 후 새 파일 저장                |
| 제거 | 기존 첨부파일을 제거함  | `true`     | 없음     | 기존 파일 삭제 후 `filePath`를 `null`로 변경 |

`file == null`만으로는 파일 유지와 파일 제거를 구분할 수 없다. 둘 다 새 파일이 없는 상태이기 때문이다. 따라서 프론트엔드에서 파일 입력을 건드렸는지 여부를 `fileFlag`로 함께 전달한다.

서버는 `fileFlag`가 `false`이면 기존 파일 경로를 유지한다. `fileFlag`가 `true`이면 기존 파일을 삭제하고, 새 파일이 있으면 새 파일을 저장한다. 새 파일이 없으면 첨부파일을 제거한 것으로 보고 `filePath`를 `null`로 둔다.

### (4) 삭제 순서

게시글 삭제에서는 DB 데이터와 디스크 파일을 함께 삭제해야 한다. 이때 파일 삭제는 트랜잭션 롤백 대상이 아니다. DB 작업은 트랜잭션으로 되돌릴 수 있지만, 디스크에서 삭제된 파일은 자동으로 복구되지 않는다.

따라서 삭제는 다음 순서로 처리한다.

```text
DB 게시글 삭제
→ 첨부파일 삭제
```

DB를 먼저 삭제하고 파일을 나중에 삭제하면, 파일 삭제 중 예외가 발생했을 때 트랜잭션이 롤백되어 DB 삭제도 취소될 수 있다. 이 방식은 글은 삭제되었는데 파일 삭제만 실패한 상태를 줄이는 데 도움이 된다.

### (5) FileService 분리

게시글 서비스에 파일 저장, 삭제, 다운로드 로직이 함께 있으면 `BoardService`가 여러 책임을 가지게 된다.

```text
분리 전
BoardService
→ 게시글 저장, 조회, 수정, 삭제
→ 파일 저장, 삭제, 다운로드

분리 후
BoardService
→ 게시글 저장, 조회, 수정, 삭제

FileService
→ 파일 저장, 삭제, 다운로드
```

게시글 로직과 파일 I/O는 변경되는 이유가 다르다. 따라서 파일 관련 로직을 `FileService`로 분리하면 `BoardService`는 게시글 도메인 로직에 집중할 수 있고, 파일 처리 로직은 다른 기능에서도 재사용하기 쉬워진다.

---

## Step 1. 게시글 삭제 만들기

게시글 삭제 요청은 JSON 본문으로 삭제할 파일 경로를 함께 전달한다.

작성할 내용은 다음과 같다.

* `BoardDeleteRequestDto` 생성
* `filePath` 필드 작성
* JSON 바인딩을 위해 기본 생성자와 setter 작성
* `BoardService.deleteArticle()` 작성
* 게시글 존재 여부 확인
* 게시글 삭제 후 첨부파일 삭제
* `BoardApiController`에 `DELETE /api/boards/{id}` 추가

삭제 요청 DTO는 다음과 같다.

```java
@Getter
@Setter
@NoArgsConstructor
public class BoardDeleteRequestDto {

    private String filePath;
}
```

`@RequestBody`는 Jackson을 통해 JSON을 DTO로 변환한다. 이때 setter가 없으면 `filePath` 값이 제대로 들어오지 않을 수 있다.

삭제 서비스는 다음과 같이 작성한다.

```java
@Transactional
public void deleteArticle(Long id, BoardDeleteRequestDto request) {
    if (!boardRepository.existsById(id)) {
        throw new BoardNotFoundException("게시글을 찾을 수 없습니다. id=" + id);
    }

    boardRepository.deleteById(id);
    deleteFile(request.getFilePath());
}
```

첨부파일 삭제 로직은 다음과 같다. 이후 Step 5에서 `FileService`로 분리한다.

```java
private void deleteFile(String filePath) {
    if (filePath == null || filePath.isBlank()) {
        return;
    }

    File file = new File(filePath);

    if (file.exists()) {
        file.delete();
    }
}
```

컨트롤러는 다음과 같다.

```java
@DeleteMapping("/{id}")
public void deleteArticle(@PathVariable long id, @RequestBody BoardDeleteRequestDto request) {
    boardService.deleteArticle(id, request);
}
```

게시글이 존재하지 않으면 기존 `BoardNotFoundException`을 사용해 404 응답으로 처리한다.

---

## Step 2. `Board.update()` 메서드 만들기

게시글 수정에서는 엔티티의 값을 변경해야 한다. 이때 setter를 열기보다 게시글 수정이라는 의미를 가진 `update()` 메서드를 엔티티에 작성한다.

`Board` 엔티티에 다음 메서드를 추가한다.

```java
public void update(String title, String content, String filePath) {
    this.title = title;
    this.content = content;
    this.filePath = filePath;
}
```

작성자 `userId`와 작성일 `created`는 수정 대상이 아니므로 변경하지 않는다.

이 메서드는 트랜잭션 안에서 조회된 영속 상태 엔티티에 대해 호출된다. 필드가 변경되면 트랜잭션 커밋 시점에 JPA 변경 감지가 동작해 `UPDATE` SQL이 실행된다.

---

## Step 3. 게시글 수정 서비스 만들기

게시글 수정 요청은 제목, 내용, 파일 변경 여부, 새 파일을 전달한다. 파일이 포함될 수 있으므로 `multipart/form-data` 요청이다.

작성할 내용은 다음과 같다.

* `BoardUpdateRequestDto` 생성
* `title`, `content`, `fileFlag`, `file` 필드 작성
* `@ModelAttribute` 바인딩을 위해 기본 생성자와 setter 작성
* `BoardService.updateArticle()` 작성
* 게시글 조회
* 파일 유지, 교체, 제거 처리
* `board.update()` 호출

수정 요청 DTO는 다음과 같다.

```java
@Getter
@Setter
@NoArgsConstructor
public class BoardUpdateRequestDto {

    private String title;
    private String content;
    private boolean fileFlag;
    private MultipartFile file;
}
```

수정 서비스는 다음과 같다.

```java
@Transactional
public void updateArticle(Long id, BoardUpdateRequestDto request) {
    Board board = boardRepository.findById(id)
            .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다. id=" + id));

    String filePath = board.getFilePath();

    if (request.isFileFlag()) {
        deleteFile(board.getFilePath());
        filePath = storeFile(request.getFile());
    }

    board.update(request.getTitle(), request.getContent(), filePath);
}
```

처리 흐름은 다음과 같다.

```text
게시글 조회
→ 기존 filePath를 기본값으로 둠
→ fileFlag가 false이면 기존 파일 유지
→ fileFlag가 true이면 기존 파일 삭제
→ 새 파일이 있으면 새 파일 저장
→ 새 파일이 없으면 filePath를 null로 둠
→ board.update() 호출
→ 변경 감지로 UPDATE 실행
```

파일 경로는 클라이언트가 보낸 값을 사용하지 않고, DB에서 조회한 `board.getFilePath()`를 기준으로 처리한다. 클라이언트가 파일 경로를 조작해 보내는 상황을 막기 위해서다.

---

## Step 4. 수정 컨트롤러 만들기

프론트엔드가 호출하는 수정 API는 `PUT /api/boards/{id}`이다. 수정 대상 게시글 id는 요청 본문이 아니라 URL 경로에서 받는다.

파일이 포함될 수 있는 요청이므로 `@ModelAttribute`로 DTO를 받는다.

```java
@PutMapping("/{id}")
public void updateArticle(@PathVariable long id, @ModelAttribute BoardUpdateRequestDto request) {
    boardService.updateArticle(id, request);
}
```

`PUT`은 기존 리소스를 수정할 때 사용하는 HTTP 메서드다. 어떤 게시글을 수정할지는 경로의 `{id}`로 식별한다.

---

## Step 5. FileService로 파일 로직 분리하기

`BoardService`에 파일 저장, 삭제, 다운로드 로직이 함께 있으면 게시글 로직과 파일 I/O 로직이 섞인다.

파일 관련 로직을 `FileService`로 분리한다.

작성할 내용은 다음과 같다.

* `FileService` 생성
* `@Value("${file.upload-dir}")`로 업로드 경로 주입
* 기존 `storeFile()` 이동
* 기존 `deleteFile()` 이동
* 기존 `downloadFile()` 이동
* `BoardService`에서 `FileService` 주입
* 게시글 저장, 수정, 삭제에서 `FileService` 메서드 호출
* 다운로드 컨트롤러에서도 `FileService.downloadFile()` 호출

`FileService` 구조는 다음과 같다.

```java
@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        // 기존 storeFile 로직 이동
    }

    public void deleteFile(String filePath) {
        // 기존 deleteFile 로직 이동
    }

    public Resource downloadFile(String fileName) {
        // 기존 downloadFile 로직 이동
    }
}
```

`BoardService`는 파일 처리 로직을 직접 수행하지 않고 `FileService`에 위임한다.

```java
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileService fileService;

    @Transactional
    public void saveArticle(String userId, String title, String content, MultipartFile file) {
        String filePath = fileService.storeFile(file);

        Board board = Board.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .filePath(filePath)
                .created(LocalDateTime.now())
                .build();

        boardRepository.save(board);
    }

    @Transactional
    public void updateArticle(Long id, BoardUpdateRequestDto request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다. id=" + id));

        String filePath = board.getFilePath();

        if (request.isFileFlag()) {
            fileService.deleteFile(board.getFilePath());
            filePath = fileService.storeFile(request.getFile());
        }

        board.update(request.getTitle(), request.getContent(), filePath);
    }

    @Transactional
    public void deleteArticle(Long id, BoardDeleteRequestDto request) {
        if (!boardRepository.existsById(id)) {
            throw new BoardNotFoundException("게시글을 찾을 수 없습니다. id=" + id);
        }

        boardRepository.deleteById(id);
        fileService.deleteFile(request.getFilePath());
    }
}
```

`BoardService`에서는 `File`, `UUID`, `Resource`, `UrlResource`처럼 파일 처리에만 필요한 import를 제거할 수 있다.

---

## Step 6. 트랜잭션 정리와 통합 실행 확인하기

`BoardService`의 트랜잭션 설정을 정리한다.

조회 메서드가 많고 쓰기 메서드는 일부이므로, 클래스 레벨에는 읽기 전용 트랜잭션을 적용한다.

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    public List<Board> getBoardList(int page, int size) {
        // 조회 로직
    }

    public Board getBoardDetail(Long id) {
        // 조회 로직
    }

    @Transactional
    public void saveArticle(String userId, String title, String content, MultipartFile file) {
        // 쓰기 로직
    }

    @Transactional
    public void updateArticle(Long id, BoardUpdateRequestDto request) {
        // 쓰기 로직
    }

    @Transactional
    public void deleteArticle(Long id, BoardDeleteRequestDto request) {
        // 쓰기 로직
    }
}
```

`@Transactional(readOnly = true)`는 조회 작업에 적합하다. 쓰기 메서드에는 별도로 `@Transactional`을 붙여 클래스 레벨 설정을 덮어쓴다.

메서드 레벨 트랜잭션 설정이 클래스 레벨 트랜잭션 설정보다 우선 적용된다.

통합 실행에서 확인할 내용은 다음과 같다.

1. 상세 화면에서 수정 버튼을 눌러 `/update/{id}`로 이동한다.
2. 제목과 내용을 수정하고 저장한다.
3. 목록 또는 상세 화면에서 수정된 내용이 반영되었는지 확인한다.
4. 파일을 교체하고 저장한다.
5. 기존 파일이 삭제되고 새 파일이 저장되었는지 확인한다.
6. 파일을 제거하고 저장한다.
7. DB의 `file_path`가 `null`로 변경되었는지 확인한다.
8. 상세 화면에서 삭제 버튼을 누른다.
9. 게시글이 목록에서 사라졌는지 확인한다.
10. 첨부파일이 `uploads` 폴더에서 삭제되었는지 확인한다.

---

## 완성 체크리스트

* [ ] `BoardDeleteRequestDto`를 작성했다
* [ ] 삭제 요청 DTO에 `filePath` 필드를 작성했다
* [ ] JSON 바인딩을 위해 setter와 기본 생성자를 작성했다
* [ ] `deleteArticle()`에서 게시글 존재 여부를 확인했다
* [ ] 게시글 삭제 후 첨부파일을 삭제했다
* [ ] `DELETE /api/boards/{id}` API를 작성했다
* [ ] `Board.update()` 메서드를 작성했다
* [ ] `BoardUpdateRequestDto`를 작성했다
* [ ] 수정 요청 DTO에 `fileFlag`와 `MultipartFile file`을 작성했다
* [ ] `updateArticle()`에서 파일 유지, 교체, 제거를 구분했다
* [ ] 파일 경로는 DB에서 조회한 값을 기준으로 처리했다
* [ ] `PUT /api/boards/{id}` API를 작성했다
* [ ] 파일 로직을 `FileService`로 분리했다
* [ ] `BoardService`가 `FileService`에 파일 처리를 위임하도록 수정했다
* [ ] `BoardService`에 클래스 레벨 `@Transactional(readOnly = true)`를 적용했다
* [ ] 쓰기 메서드에 별도 `@Transactional`을 적용했다
* [ ] 화면에서 게시글 수정, 파일 교체, 파일 제거, 게시글 삭제를 확인했다
