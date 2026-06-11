# 가계부 만들기 2 (File I/O 활용)

> 이번 과제에서는 데이터를 메모리 컬렉션이 아니라 날짜별 텍스트 파일(`.txt`)에 저장한다. 프로그램을 종료한 뒤 다시 실행해도 기록이 남는다는 점이 1편과의 가장 큰 차이이다.

---

## 1. 무엇을 만드나요?

콘솔에서 동작하는 파일 기반 가계부 프로그램을 구현한다.

모든 내역은 파일로 저장한다.

- 하루치 기록은 파일 1개로 저장한다. 예: `2024-09-04.txt`
- 내역을 추가하면 해당 날짜 파일에 내용이 이어서 저장된다.
- 내역을 조회하면 파일을 읽어서 출력한다.
- 내역을 삭제하면 해당 날짜의 파일을 삭제한다.

실행 화면 메뉴는 다음과 같다.

```text
===== 가계부 (File) =====
1. 내역 추가
2. 내역 조회
3. 삭제
4. 종료
번호 입력 >
```

---

## 2. 요구사항 정리 (기능 명세)

| 번호 | 기능 | 설명 |
|---|---|---|
| 1 | 내역 추가 | 오늘 날짜의 파일이 없으면 새로 만들고, 있으면 기존 파일 뒤에 이어서 내용을 추가한다. |
| 2 | 내역 조회 | 저장된 파일들의 날짜 목록을 보여준다. 날짜를 입력하면 해당 파일의 내용을 읽어 출력한다. |
| 3 | 삭제 | 날짜를 입력받아 해당 날짜의 파일을 삭제한다. |
| 4 | 종료 | 프로그램을 종료한다. |

### 파일에 저장되는 형식

```text
공책 : 1000원
연필 : 300원
합계 : 1300원
```

### 기능 1 동작 예시

**내역 추가**

```text
항목 이름 > 공책
금액 > 1000
더 추가할까요? (y/n) > y
항목 이름 > 연필
금액 > 300
더 추가할까요? (y/n) > n

2024-09-04.txt 에 저장 완료
공책 : 1000원
연필 : 300원
합계 : 1300원
```

### 기능 2 동작 예시

**내역 조회**

```text
== 기록된 날짜 ==
2024-09-04
2024-09-03
2024-09-02
조회할 날짜 입력 > 2024-09-04

[2024-09-04]
공책 : 1000원
연필 : 300원
합계 : 1300원
```

---

## 3. 핵심 개념: 파일을 어떻게 다룰까?

1편은 데이터를 `Map`에 저장했지만, 이번 과제에서는 파일이 곧 데이터이다.

| 하고 싶은 것 | 사용하는 도구 |
|---|---|
| 오늘 날짜 구하기 | `LocalDate.now()` → 문자열 `"2024-09-04"` |
| 파일이 있는지 확인 | `File.exists()` |
| 파일에 이어서 쓰기 | `new FileWriter(파일, true)` |
| 파일 읽기 | `BufferedReader.readLine()` 반복 |
| 폴더 안 파일 목록 확인 | `폴더.list()` 또는 `폴더.listFiles()` |
| 파일 삭제 | `File.delete()` |

파일에 이어서 쓰기 위해서는 `FileWriter`의 두 번째 인자로 `true`를 전달한다.

```java
new FileWriter(file, true)
```

파일을 다루는 코드는 반드시 예외 처리가 필요하다.

파일이 존재하지 않거나, 접근 권한 문제가 발생하거나, 입출력 과정에서 오류가 발생할 수 있기 때문이다.

따라서 `try-catch`를 사용하거나 `throws IOException`을 통해 예외를 처리해야 한다.

### 저장 위치 정하기

파일이 여러 위치에 흩어지지 않도록, 프로젝트 안에 `accountbook` 폴더를 만들고 그 안에 `.txt` 파일을 저장한다.

```text
프로젝트/
 └─ accountbook/
     ├─ 2024-09-04.txt
     ├─ 2024-09-03.txt
     └─ ...
```

---

## 4. 파일 구조 (각 파일의 역할)

| 파일 | 역할 |
|---|---|
| `AccountBook.java` *(인터페이스)* | 추가, 조회, 삭제 기능을 선언한다. |
| `AccountBookImpl.java` | `AccountBook`을 구현한다. 실제 File I/O 로직을 가진다. |
| `Start.java` | `main` 메서드를 가진다. 메뉴를 출력하고 사용자 입력에 따라 기능을 호출한다. |

1편과 달리 `Item` 같은 데이터 클래스는 반드시 필요하지 않다.

항목들을 객체로 저장하지 않고 파일에 문자열로 저장하기 때문이다.

다만 내역을 추가할 때 합계를 계산해야 하므로, 입력받은 값을 잠깐 모아두는 처리는 필요하다.

---

## 5. Step by Step

각 Step에는 목표, 할 일, 힌트, 확인 방법이 있다.

한 Step씩 구현한 뒤 실행 결과를 확인하면서 다음 Step으로 넘어간다.

---

### Step 1. 메뉴부터 띄우기 (`Start.java`)

**목표**: 기능이 없어도 메뉴가 반복 출력되고, `4`를 누르면 종료되도록 만든다.

**할 일**

- `Scanner`로 번호를 입력받는다.
- `while` 반복문과 `switch`문으로 메뉴를 분기한다.
- `4`를 입력하면 프로그램을 종료한다.

**힌트**

```java
Scanner sc = new Scanner(System.in);

while (true) {
    System.out.println("===== 가계부 (File) =====");
    // 1~4 메뉴 출력

    int menu = Integer.parseInt(sc.nextLine());

    switch (menu) {
        case 1:
            break;
        case 2:
            break;
        case 3:
            break;
        case 4:
            System.out.println("종료합니다");
            return;
        default:
            System.out.println("잘못된 번호입니다");
    }
}
```

**확인**: `4`를 입력했을 때 프로그램이 종료되면 성공이다.

---

### Step 2. 인터페이스 작성 (`AccountBook.java`)

**목표**: 가계부 기능 3개를 메서드로 선언한다. 구현은 하지 않는다.

**힌트**

```java
public interface AccountBook {
    void addAccount();    // 1. 내역 추가
    void showAccount();   // 2. 내역 조회
    void deleteAccount(); // 3. 삭제
}
```

**확인**: 인터페이스에는 본문 없이 메서드 선언만 있으면 된다.

---

### Step 3. 구현 뼈대와 저장 폴더 준비 (`AccountBookImpl.java`)

**목표**: 인터페이스를 구현하는 클래스를 만들고, 저장 폴더를 준비한 뒤 `Start`와 연결한다.

**할 일**

- `implements AccountBook`을 작성한다.
- 저장 폴더 경로를 상수로 정한다.
- 저장 폴더가 없으면 생성한다.
- 3개 메서드를 빈 몸통으로 만든다.
- `Start.java`에서 `AccountBook book = new AccountBookImpl();`로 연결한다.

**힌트**

```java
public class AccountBookImpl implements AccountBook {
    private final String DIR = "accountbook";
    private Scanner sc = new Scanner(System.in);

    public AccountBookImpl() {
        File folder = new File(DIR);

        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public void addAccount() {
        // TODO Step 4
    }

    public void showAccount() {
        // TODO Step 5
    }

    public void deleteAccount() {
        // TODO Step 6
    }
}
```

`Start.java`의 `switch`문과 연결한다.

```java
case 1:
    book.addAccount();
    break;
case 2:
    book.showAccount();
    break;
case 3:
    book.deleteAccount();
    break;
```

**확인**: 실행했을 때 프로젝트에 `accountbook` 폴더가 생성되면 성공이다.

---

### Step 4. 기능 1 — 내역 추가 (`addAccount`)

**목표**: 오늘 날짜 파일에 항목들을 이어서 저장하고, 합계를 함께 기록한다.

**할 일**

1. 오늘 날짜로 파일 경로를 만든다. 예: `accountbook/2024-09-04.txt`
2. 항목 이름과 금액을 반복 입력받는다.
3. 입력받은 항목을 임시로 모으고 합계를 계산한다.
4. append 모드로 파일에 내용을 저장한다.
5. 저장한 내용을 화면에도 출력한다.

파일이 없으면 `FileWriter`가 새 파일을 만들어 준다.

따라서 별도로 `createNewFile()`을 호출하지 않아도 된다.

**힌트**

```java
String today = LocalDate.now().toString();
File file = new File(DIR, today + ".txt");

int total = 0;
StringBuilder sb = new StringBuilder();

// 반복:
// 이름 입력
// 금액 입력
// sb에 "이름 : 금액원\n" 추가
// total += 금액

sb.append("합계 : ").append(total).append("원\n");

try (FileWriter fw = new FileWriter(file, true)) {
    fw.write(sb.toString());
} catch (IOException e) {
    System.out.println("저장 중 오류: " + e.getMessage());
}
```

**확인**: `accountbook` 폴더에 오늘 날짜의 `.txt` 파일이 생성되면 성공이다.

파일을 열었을 때 입력한 항목과 합계가 형식대로 저장되어 있어야 한다.

한 번 더 추가했을 때 같은 파일 뒤에 내용이 이어서 저장되면 성공이다.

### 설계 결정 포인트

하루에 여러 번 내역을 추가하면 `합계` 줄도 여러 번 생긴다.

이 방식은 각 입력 묶음별 합계를 기록하는 구조이다.

더 깔끔하게 관리하려면 기존 파일을 읽고 전체 합계를 다시 계산한 뒤, 마지막 합계 줄을 갱신하는 방식으로 발전시킬 수 있다.

---

### Step 5. 기능 2 — 내역 조회 (`showAccount`)

**목표**: 저장된 날짜 목록을 보여주고, 선택한 날짜의 파일 내용을 읽어 출력한다.

**할 일**

1. 저장 폴더의 `.txt` 파일 목록을 가져온다.
2. 파일이 하나도 없으면 "기록이 없습니다"를 출력하고 종료한다.
3. 파일명에서 `.txt`를 제거하고 날짜만 출력한다.
4. 조회할 날짜를 입력받는다.
5. 해당 파일이 있는지 확인한다.
6. 파일이 없으면 "그런 날짜가 없습니다"를 출력한다.
7. 파일이 있으면 `BufferedReader`로 한 줄씩 읽어 출력한다.

**힌트**

```java
File folder = new File(DIR);
String[] files = folder.list();

if (files == null || files.length == 0) {
    System.out.println("기록이 없습니다");
    return;
}

for (String name : files) {
    if (name.endsWith(".txt")) {
        System.out.println(name.replace(".txt", ""));
    }
}

File file = new File(DIR, inputDate + ".txt");

if (file.exists()) {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;

        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    } catch (IOException e) {
        System.out.println("조회 중 오류: " + e.getMessage());
    }
}
```

**확인**: 날짜 목록이 출력되고, 날짜를 입력했을 때 해당 파일 내용이 그대로 출력되면 성공이다.

---

### Step 6. 기능 3 — 삭제 (`deleteAccount`)

**목표**: 입력한 날짜의 파일을 삭제한다.

**할 일**

1. 현재 저장된 날짜 목록을 먼저 보여준다.
2. 삭제할 날짜를 입력받는다.
3. 해당 파일이 있는지 확인한다.
4. 파일이 있으면 `delete()`를 호출한다.
5. 삭제 성공, 삭제 실패, 파일 없음 상황을 각각 안내한다.

**힌트**

```java
File file = new File(DIR, inputDate + ".txt");

if (file.exists()) {
    if (file.delete()) {
        System.out.println("삭제되었습니다");
    } else {
        System.out.println("삭제 실패");
    }
} else {
    System.out.println("그런 날짜가 없습니다");
}
```

**확인**: 삭제 후 내역 조회를 했을 때 해당 날짜가 목록에서 사라져 있으면 성공이다.

---

### Step 7. 마무리 다듬기

**목표**: 예외 상황을 점검하고 프로그램을 완성한다.

**점검 항목**

- [ ] 같은 날 추가를 두 번 하면 파일에 이어서 저장되는가?
- [ ] 기존 파일을 덮어쓰지 않는가?
- [ ] 합계가 입력한 금액들의 합과 맞는가?
- [ ] 기록이 없을 때 조회나 삭제를 하면 안내 메시지가 출력되는가?
- [ ] 없는 날짜를 입력해도 프로그램이 멈추지 않는가?
- [ ] 모든 파일 작업에 예외 처리가 되어 있는가?
- [ ] 금액 입력란에 글자를 넣어도 프로그램이 종료되지 않는가?

여기까지 통과하면 File I/O 가계부 프로그램이 완성된다.

---

## 6. 최종 완성 체크리스트

- [ ] `AccountBook.java` — 인터페이스에 기능 3개 선언
- [ ] `AccountBookImpl.java` — File I/O로 추가, 조회, 삭제 구현
- [ ] `Start.java` — 메뉴 반복, 기능 호출, 종료 구현
- [ ] 날짜별 `.txt` 파일이 폴더에 실제로 생성, 수정, 삭제됨
- [ ] `FileWriter` append 모드, `BufferedReader`, `File.delete()`를 사용함
- [ ] 추가 → 조회 → 삭제가 순서대로 동작함

---

## 7. 선택 도전 과제

1. **전체 합계 갱신**: 같은 날 여러 번 추가해도 합계 줄이 하나로 유지되도록 읽기, 재계산, 다시 쓰기 구조로 개선
2. **정렬**: 조회 시 날짜 목록을 최신순으로 정렬해서 출력
3. **검색**: 특정 항목 이름이 들어간 날짜를 모두 찾아 출력 (`공책` 등)
4. **백업**: 삭제 전에 파일을 `backup` 폴더로 옮긴 뒤 삭제
5. **월별 합계**: 같은 달의 모든 파일 금액을 합쳐 출력 (`2024-09` 등)
