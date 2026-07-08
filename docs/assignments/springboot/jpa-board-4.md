# JPA 게시판 4 - 글쓰기 만들기

> JPA 기반 게시판 과제에서 게시글 작성과 파일 업로드, 파일 다운로드 기능을 구현한다.
> 지금까지는 JSON 요청을 중심으로 데이터를 주고받았지만, 파일이 포함된 요청은 `multipart/form-data` 형식으로 처리해야 한다.
> 업로드된 파일은 서버의 디스크에 저장하고, DB에는 게시글 정보와 함께 파일 경로를 저장한다. 이후 상세 화면에서 저장된 파일을 다시 다운로드할 수 있도록 API를 구성한다.

---

## 0. 먼저 알아둘 점

이 과제는 1편부터 3편까지의 회원가입, 로그인, 게시글 목록 조회, 게시글 상세 조회가 완료되었다는 전제로 진행한다.

이미 작성되어 있어야 하는 요소는 다음과 같다.

* `Board` 엔티티
* `BoardRepository`
* `BoardService`
* `BoardApiController`
* DB 접속 정보와 JPA 설정이 포함된 `application.yaml`
* 글쓰기 화면인 `board-write.html`
* 글쓰기 요청을 보내는 `boardWrite.js`
* 상세 화면인 `board-detail.html`
* 상세 화면 요청을 처리하는 `boardDetail.js`
* 글쓰기 화면을 보여주는 뷰 컨트롤러인 `GET /write`

이번 단계에서 작성할 백엔드 요소는 다음과 같다.

* 파일 업로드 설정
* 글쓰기 요청 DTO
* 업로드 파일 저장 로직
* 게시글 저장 API
* 파일 다운로드 API

파일은 프로젝트 루트의 `./uploads` 폴더에 저장한다. 저장 폴더가 없으면 코드에서 생성한다.

프론트엔드의 요청 형식은 다음과 같다.

| 기능    | 요청                                         |
| ----- | ------------------------------------------ |
| 글쓰기   | `POST /api/boards`                         |
| 요청 형식 | `multipart/form-data`                      |
| 폼 필드  | `title`, `content`, `userId`, `file`       |
| 성공 응답 | `200 OK`, 본문 없음                            |
| 다운로드  | `GET /api/boards/file/download/{fileName}` |

글쓰기 요청은 파일이 포함될 수 있으므로 JSON이 아니라 `FormData` 기반의 `multipart/form-data`로 전송된다. 따라서 컨트롤러에서는 `@RequestBody`가 아니라 `@ModelAttribute`를 사용한다.

---

## 1. 무엇을 만드는가?

글쓰기 폼에 제목, 내용, 첨부파일을 입력하고 작성 버튼을 누르면 게시글과 파일이 함께 저장된다.

처리 흐름은 다음과 같다.

```text
[글쓰기]
제목, 내용, 파일 선택
→ multipart/form-data 요청 전송
→ 서버가 파일을 uploads 폴더에 저장
→ 파일 저장 경로를 board 테이블의 file_path에 저장
→ 게시글 저장 완료
→ 목록 페이지로 이동

[다운로드]
상세 화면에서 첨부파일 링크 클릭
→ /api/boards/file/download/{fileName} 요청
→ 서버가 uploads 폴더에서 파일 조회
→ 브라우저가 파일 다운로드
```

구현할 API는 다음과 같다.

| 주소와 메서드                                    | 역할              | 계층       |
| ------------------------------------------ | --------------- | -------- |
| `GET /write`                               | 글쓰기 화면 조회       | 뷰 컨트롤러   |
| `POST /api/boards`                         | 게시글 저장과 첨부파일 저장 | API 컨트롤러 |
| `GET /api/boards/file/download/{fileName}` | 첨부파일 다운로드       | API 컨트롤러 |

---

## 2. 학습 목표

| 개념                                    | 학습 위치          |
| ------------------------------------- | -------------- |
| 파일 업로드 경로와 최대 크기 설정                   | Step 1         |
| `multipart/form-data` 요청 처리           | Step 2         |
| `@ModelAttribute`와 `MultipartFile` 사용 | Step 2         |
| 업로드 파일을 디스크에 저장                       | Step 3         |
| UUID를 활용한 저장 파일명 충돌 방지                | Step 3         |
| 반환 본문이 없는 API 처리                      | Step 4         |
| `Resource`로 파일 다운로드 처리                | Step 5         |
| `Content-Disposition` 헤더로 다운로드 지시     | Step 5         |
| `ResponseEntity`가 필요한 상황 구분           | Step 4, Step 5 |

---

## 3. 핵심 개념

### (1) `@RequestBody`와 `@ModelAttribute`

JSON 요청은 `@RequestBody`로 처리한다. `@RequestBody`는 요청 본문의 JSON을 Jackson이 DTO로 변환하는 방식이다.

반면 파일이 포함된 폼 요청은 `multipart/form-data` 형식으로 전송된다. 이 요청은 JSON이 아니므로 `@RequestBody`로 받을 수 없다. 제목, 내용, 작성자, 파일을 폼 필드로 함께 전달하므로 `@ModelAttribute`를 사용한다.

| 구분      | `@RequestBody` | `@ModelAttribute`     |
| ------- | -------------- | --------------------- |
| 요청 형식   | JSON           | 폼 데이터, multipart      |
| 파일 포함   | 불가             | 가능                    |
| 파일 타입   | 해당 없음          | `MultipartFile`       |
| 값 주입 방식 | JSON 역직렬화      | 기본 생성자와 setter 기반 바인딩 |

파일도 DTO의 필드로 선언할 수 있다. 이때 폼의 `name` 값과 DTO 필드명이 같아야 자동으로 바인딩된다.

### (2) `MultipartFile`과 `transferTo()`

브라우저가 파일을 업로드하면 서블릿 컨테이너가 먼저 임시 위치에 파일을 저장한다. 이 임시 파일이 컨트롤러에서는 `MultipartFile`로 전달된다.

임시 파일은 요청 처리가 끝난 뒤 사라질 수 있으므로, 서비스 로직에서 최종 저장 위치로 옮겨야 한다. 이때 사용하는 메서드가 `transferTo()`이다.

```java
file.transferTo(dest);
```

`transferTo()`는 업로드된 임시 파일을 지정한 최종 경로로 이동하거나 복사한다. 파일 전체를 직접 바이트 배열로 읽어 메모리에 올리는 방식보다 큰 파일 처리에 유리하다.

### (3) 파일 저장명

원본 파일명을 그대로 저장하면 같은 이름의 파일이 업로드될 때 기존 파일이 덮어써질 수 있다. 이를 방지하기 위해 UUID를 붙인 저장 파일명을 사용한다.

```text
원본 파일명: resume.pdf
저장 파일명: 550e8400-e29b-41d4-a716-446655440000_resume.pdf
```

DB에는 저장된 파일의 경로나 파일명을 기록한다. 상세 화면에서는 이 값을 기준으로 다운로드 링크를 구성한다.

### (4) 파일 다운로드 응답

파일 다운로드는 일반 JSON 응답과 다르다. 서버는 파일 데이터를 응답 본문으로 내려주고, 브라우저가 파일을 저장하도록 헤더를 설정해야 한다.

주요 헤더는 다음과 같다.

| 헤더                                       | 역할                         |
| ---------------------------------------- | -------------------------- |
| `Content-Type: application/octet-stream` | 응답 본문이 일반 바이너리 데이터임을 알림    |
| `Content-Disposition: attachment`        | 브라우저가 파일을 열지 않고 다운로드하도록 지시 |
| `filename*`                              | 한글과 공백을 포함한 파일명을 UTF-8로 전달 |

다운로드 응답은 상태 코드, 헤더, 본문을 직접 구성해야 하므로 `ResponseEntity<Resource>`를 사용한다.

---

## Step 1. 파일 업로드 설정하기

파일 저장 경로와 업로드 가능한 최대 크기를 `application.yaml`에 설정한다.

작성할 내용은 다음과 같다.

* `file.upload-dir`: 첨부파일 저장 폴더
* `spring.servlet.multipart.max-file-size`: 파일 1개 최대 크기
* `spring.servlet.multipart.max-request-size`: 요청 전체 최대 크기

설정 예시는 다음과 같다.

```yaml
file:
  upload-dir: ./uploads

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

`file.upload-dir` 값은 서비스에서 `@Value("${file.upload-dir}")`로 주입받아 사용한다.

Spring Boot의 기본 multipart 파일 크기 제한은 작을 수 있으므로, 이미지나 문서 파일을 업로드하려면 제한을 명시적으로 늘려야 한다.

---

## Step 2. 글쓰기 요청 DTO 만들기

파일이 포함된 글쓰기 요청을 받을 DTO를 만든다.

작성할 내용은 다음과 같다.

* `dto/BoardWriteRequestDto` 생성
* `title`, `content`, `userId` 필드 작성
* `MultipartFile file` 필드 작성
* `@ModelAttribute` 바인딩을 위해 기본 생성자와 setter 작성

힌트는 다음과 같다.

```java
@Getter
@Setter
@NoArgsConstructor
public class BoardWriteRequestDto {

    private String title;
    private String content;
    private String userId;
    private MultipartFile file;
}
```

프론트엔드의 FormData 필드명과 DTO 필드명은 같아야 한다.

| FormData 필드명 | DTO 필드명   |
| ------------ | --------- |
| `title`      | `title`   |
| `content`    | `content` |
| `userId`     | `userId`  |
| `file`       | `file`    |

필드명이 다르면 해당 값은 DTO에 바인딩되지 않고 `null`이 될 수 있다.

---

## Step 3. 파일 저장 로직 만들기

업로드된 파일을 `./uploads` 폴더에 저장하고, 저장 경로를 게시글과 함께 저장한다.

`BoardService`에 파일 업로드 경로를 주입한다.

```java
@Value("${file.upload-dir}")
private String uploadDir;
```

게시글 저장 메서드는 파일을 먼저 저장한 뒤, 반환된 파일 경로를 `Board` 엔티티의 `filePath`에 넣어 저장한다.

```java
@Transactional
public void saveArticle(String userId, String title, String content, MultipartFile file) {
    String filePath = storeFile(file);

    Board board = Board.builder()
            .userId(userId)
            .title(title)
            .content(content)
            .filePath(filePath)
            .created(LocalDateTime.now())
            .build();

    boardRepository.save(board);
}
```

파일 저장 메서드는 다음과 같이 작성한다.

```java
private String storeFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
        return null;
    }

    try {
        File dir = new File(uploadDir).getAbsoluteFile();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(dir, storedName);

        file.transferTo(dest);

        return dest.getPath();
    } catch (IOException e) {
        throw new IllegalStateException("파일 저장에 실패했습니다.", e);
    }
}
```

처리 흐름은 다음과 같다.

```text
파일이 없거나 비어 있음
→ null 반환
→ 게시글만 저장

파일이 있음
→ uploads 폴더 확인
→ 폴더가 없으면 생성
→ UUID_원본파일명으로 저장 파일명 생성
→ transferTo()로 최종 위치에 저장
→ 저장 경로 반환
→ 게시글 filePath에 저장
```

첨부파일이 선택되지 않은 경우 `filePath`는 `null`로 저장된다.

---

## Step 4. 글쓰기 컨트롤러 만들기

프론트엔드가 호출하는 `POST /api/boards` API를 작성한다.

파일이 포함된 요청이므로 `@ModelAttribute`로 DTO를 받는다.

```java
@PostMapping
public void saveArticle(@ModelAttribute BoardWriteRequestDto request) {
    boardService.saveArticle(
            request.getUserId(),
            request.getTitle(),
            request.getContent(),
            request.getFile()
    );
}
```

이 API는 성공 시 별도의 응답 본문을 내려줄 필요가 없다. 반환 타입을 `void`로 두면 Spring이 기본적으로 `200 OK`를 응답한다.

단순히 성공 여부만 필요한 요청이라면 `ResponseEntity`로 감쌀 필요가 없다. 반면 파일 다운로드처럼 응답 헤더를 직접 설정해야 하는 경우에는 `ResponseEntity`를 사용한다.

---

## Step 5. 다운로드 API 만들기

저장된 파일을 다운로드할 수 있도록 `GET /api/boards/file/download/{fileName}` API를 작성한다.

서비스에서는 파일명을 받아 `uploads` 폴더에서 파일을 찾고, `Resource`로 반환한다.

```java
public Resource downloadFile(String fileName) {
    try {
        File file = new File(new File(uploadDir).getAbsoluteFile(), fileName);
        Resource resource = new UrlResource(file.toURI());

        if (!resource.exists() || !resource.isReadable()) {
            throw new BoardNotFoundException("파일을 찾을 수 없습니다. fileName=" + fileName);
        }

        return resource;
    } catch (MalformedURLException e) {
        throw new IllegalStateException("파일 경로가 잘못되었습니다.", e);
    }
}
```

컨트롤러에서는 `Resource`를 응답 본문으로 내려주고, 다운로드에 필요한 헤더를 설정한다.

```java
@GetMapping("/file/download/{fileName}")
public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
    Resource resource = boardService.downloadFile(fileName);

    String encoded = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8)
            .replaceAll("\\+", "%20");

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + encoded)
            .body(resource);
}
```

`URLEncoder`는 한글, 공백 같은 문자를 HTTP 헤더에 안전하게 담기 위해 사용한다. 공백은 `+`로 바뀔 수 있으므로 `%20`으로 다시 치환한다.

파일이 존재하지 않거나 읽을 수 없으면 `BoardNotFoundException`을 던지고, 기존 예외 처리 흐름을 통해 404 응답으로 변환한다.

---

## Step 6. 화면에서 통합 실행 확인하기

모든 계층을 구현한 뒤 실제 화면에서 글쓰기와 파일 다운로드가 정상 동작하는지 확인한다.

확인할 내용은 다음과 같다.

1. 애플리케이션을 실행한다.
2. 브라우저에서 `/write`로 접속한다.
3. 제목과 내용을 입력한다.
4. 파일을 선택한다.
5. 작성 버튼을 누른다.
6. 목록 페이지로 이동하는지 확인한다.
7. 프로젝트 루트의 `./uploads` 폴더에 `UUID_원본파일명` 형식의 파일이 생성되었는지 확인한다.
8. DB의 `board` 테이블에 `file_path` 값이 저장되었는지 확인한다.
9. 상세 화면에서 첨부파일 링크를 클릭한다.
10. 파일이 다운로드되는지 확인한다.

잘 안 될 때 확인할 내용은 다음과 같다.

| 증상              | 확인할 내용                                               |
| --------------- | ---------------------------------------------------- |
| 400 또는 저장 실패 발생 | FormData 필드명과 DTO 필드명이 일치하는지 확인한다                    |
| 파일 값이 `null`임   | DTO에 `MultipartFile file` 필드와 setter가 있는지 확인한다       |
| 파일이 저장되지 않음     | `file.upload-dir` 설정과 `@Value` 주입을 확인한다              |
| 413 오류 발생       | `max-file-size`, `max-request-size` 설정을 확인한다         |
| 다운로드가 브라우저에서 열림 | `Content-Disposition: attachment` 헤더를 설정했는지 확인한다     |
| 한글 파일명이 깨짐      | 파일명을 UTF-8로 인코딩하고 `filename*=UTF-8''` 형식으로 넣었는지 확인한다 |
| 다운로드 시 404 발생   | 다운로드 경로와 실제 저장 파일명이 일치하는지 확인한다                       |

---

## 완성 체크리스트

* [ ] `application.yaml`에 `file.upload-dir`를 설정했다
* [ ] multipart 최대 파일 크기와 요청 크기를 설정했다
* [ ] `BoardWriteRequestDto`를 작성했다
* [ ] DTO에 `MultipartFile file` 필드를 작성했다
* [ ] `@ModelAttribute`로 글쓰기 요청을 받았다
* [ ] `BoardService.saveArticle()`을 작성했다
* [ ] `storeFile()`에서 파일이 없으면 `null`을 반환하도록 처리했다
* [ ] `storeFile()`에서 업로드 폴더가 없으면 생성하도록 처리했다
* [ ] UUID를 붙여 저장 파일명을 만들었다
* [ ] `transferTo()`로 업로드 파일을 저장했다
* [ ] 게시글 저장 시 `filePath`를 함께 저장했다
* [ ] `BoardService.downloadFile()`을 작성했다
* [ ] 다운로드 API에서 `Resource`를 응답 본문으로 반환했다
* [ ] `Content-Type`과 `Content-Disposition` 헤더를 설정했다
* [ ] 화면에서 파일 첨부 저장과 다운로드를 확인했다
