# 가계부 만들기 2 (File I/O 활용)

이번 과제에서는 데이터를 메모리 컬렉션이 아니라 날짜별 텍스트 파일(`.txt`)에 저장한다.

프로그램을 종료한 뒤 다시 실행해도 기록이 유지된다는 점이 기존 가계부 과제와의 가장 큰 차이이다.

---

## 1. 무엇을 만드나요?

콘솔에서 동작하는 파일 기반 가계부 프로그램을 구현한다.

모든 내역은 날짜별 파일로 저장한다.

* 하루치 기록은 파일 하나로 저장한다. 예: `2024-09-04.txt`
* 내역을 추가하면 해당 날짜의 파일 뒤에 내용이 이어서 저장된다.
* 내역을 조회하면 파일 내용을 읽어 출력한다.
* 내역을 삭제하면 해당 날짜의 파일을 삭제한다.

실행 메뉴는 다음과 같다.

```text
===== 가계부 (File) =====
1. 내역 추가
2. 내역 조회
3. 삭제
4. 종료
번호 입력 >
```

---

## 2. 요구사항 정리

| 번호 | 기능    | 설명                                            |
| -: | ----- | --------------------------------------------- |
|  1 | 내역 추가 | 오늘 날짜의 파일이 없으면 새로 만들고, 있으면 기존 파일 뒤에 내용을 추가한다. |
|  2 | 내역 조회 | 저장된 날짜 목록을 출력하고, 입력한 날짜의 파일 내용을 조회한다.         |
|  3 | 삭제    | 입력한 날짜의 파일을 삭제한다.                             |
|  4 | 종료    | 프로그램을 종료한다.                                   |

### 파일 저장 형식

```text
공책 : 1000원
연필 : 300원
합계 : 1300원
```

### 내역 추가 예시

```text
항목 이름 > 공책
금액 > 1000
더 추가할까요? (y/n) > y
항목 이름 > 연필
금액 > 300
더 추가할까요? (y/n) > n

2024-09-04.txt에 저장 완료
공책 : 1000원
연필 : 300원
합계 : 1300원
```

### 내역 조회 예시

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

## 3. 핵심 개념

기존 가계부에서는 데이터를 `Map`에 저장했지만, 이번 과제에서는 파일이 데이터 저장소 역할을 한다.

| 작업            | 사용하는 기능                             |
| ------------- | ----------------------------------- |
| 오늘 날짜 확인      | `LocalDate.now()`                   |
| 파일 존재 여부 확인   | `File.exists()`                     |
| 파일에 이어서 쓰기    | `new FileWriter(file, true)`        |
| 파일 읽기         | `BufferedReader.readLine()`         |
| 폴더 안 파일 목록 확인 | `File.list()` 또는 `File.listFiles()` |
| 파일 삭제         | `File.delete()`                     |

### 파일 이어쓰기

`FileWriter`의 두 번째 인자로 `true`를 전달하면 기존 파일 뒤에 내용을 추가한다.

```java
new FileWriter(file, true)
```

`true`를 전달하지 않으면 기존 파일 내용을 덮어쓰므로 주의해야 한다.

### 파일 입출력 예외 처리

파일 입출력 과정에서는 다음과 같은 문제가 발생할 수 있다.

* 파일이 존재하지 않는 경우
* 파일 접근 권한이 없는 경우
* 파일 읽기 또는 쓰기 중 오류가 발생한 경우

따라서 파일을 다루는 코드는 `try-catch` 또는 `throws IOException`을 통해 예외를 처리해야 한다.

### 저장 위치

가계부 파일이 여러 위치에 생성되지 않도록 프로젝트 안의 `accountbook` 폴더에 저장한다.

```text
프로젝트/
└─ accountbook/
   ├─ 2024-09-04.txt
   ├─ 2024-09-03.txt
   └─ ...
```

---

## 4. 파일 구조

| 파일                     | 역할                                  |
| ---------------------- | ----------------------------------- |
| `AccountBook.java`     | 추가, 조회, 삭제 기능을 선언하는 인터페이스           |
| `AccountBookImpl.java` | 파일 입출력을 이용해 가계부 기능을 구현하는 클래스        |
| `Start.java`           | 메뉴를 출력하고 사용자 입력에 따라 기능을 호출하는 실행 클래스 |

기존 가계부와 달리 `Item`과 같은 데이터 클래스는 필수가 아니다.

입력한 항목을 객체로 보관하지 않고 문자열 형태로 파일에 저장하기 때문이다.

다만 내역을 입력받는 동안 합계를 계산하고 저장할 문자열을 만들기 위한 임시 변수는 필요하다.

---

## 5. Step by Step

각 Step을 구현한 뒤 실행 결과를 확인하고 다음 단계로 진행한다.

---

### Step 1. 메뉴 구현 (`Start.java`)

**목표**: 메뉴를 반복해서 출력하고 `4`를 입력하면 프로그램을 종료한다.

**할 일**

* `Scanner`로 메뉴 번호를 입력받는다.
* `while` 반복문으로 메뉴를 계속 출력한다.
* `switch`문으로 기능을 구분한다.
* 숫자가 아닌 입력도 처리한다.
* `4`를 입력하면 프로그램을 종료한다.

**힌트**

```java
Scanner scanner = new Scanner(System.in);

while (true) {
    System.out.println("===== 가계부 (File) =====");
    System.out.println("1. 내역 추가");
    System.out.println("2. 내역 조회");
    System.out.println("3. 삭제");
    System.out.println("4. 종료");
    System.out.print("번호 입력 > ");

    int menu;

    try {
        menu = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("숫자를 입력하세요.");
        continue;
    }

    switch (menu) {
        case 1:
            break;
        case 2:
            break;
        case 3:
            break;
        case 4:
            System.out.println("종료합니다.");
            return;
        default:
            System.out.println("잘못된 번호입니다.");
    }
}
```

**확인**

* 메뉴가 반복해서 출력되는지 확인한다.
* 문자 입력 시 프로그램이 종료되지 않는지 확인한다.
* `4`를 입력하면 프로그램이 종료되는지 확인한다.

---

### Step 2. 인터페이스 작성 (`AccountBook.java`)

**목표**: 가계부 기능을 인터페이스에 선언한다.

**힌트**

```java
public interface AccountBook {

    void addAccount();

    void showAccount();

    void deleteAccount();
}
```

**확인**

인터페이스에 추가, 조회, 삭제 기능이 선언되어 있는지 확인한다.

---

### Step 3. 구현 클래스와 저장 폴더 준비 (`AccountBookImpl.java`)

**목표**: 인터페이스를 구현하고 파일을 저장할 폴더를 준비한다.

**할 일**

* `AccountBookImpl`이 `AccountBook`을 구현하도록 한다.
* 저장 폴더 경로를 상수로 선언한다.
* 저장 폴더가 없으면 생성한다.
* `Start`에서 만든 `Scanner`를 생성자로 전달받는다.
* 인터페이스의 메서드를 재정의한다.

**힌트**

```java
public class AccountBookImpl implements AccountBook {

    private static final String DIR = "accountbook";

    private final Scanner scanner;

    public AccountBookImpl(Scanner scanner) {
        this.scanner = scanner;

        File folder = new File(DIR);

        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    @Override
    public void addAccount() {
        // TODO Step 4
    }

    @Override
    public void showAccount() {
        // TODO Step 5
    }

    @Override
    public void deleteAccount() {
        // TODO Step 6
    }
}
```

`Start.java`에서는 하나의 `Scanner`를 생성해 구현 객체에 전달한다.

```java
Scanner scanner = new Scanner(System.in);

AccountBook accountBook =
        new AccountBookImpl(scanner);
```

메뉴와 기능을 연결한다.

```java
case 1:
    accountBook.addAccount();
    break;
case 2:
    accountBook.showAccount();
    break;
case 3:
    accountBook.deleteAccount();
    break;
```

**확인**

* 프로그램 실행 시 `accountbook` 폴더가 생성되는지 확인한다.
* `Start`와 `AccountBookImpl`이 하나의 `Scanner`를 공유하는지 확인한다.
* 인터페이스 타입으로 구현 객체를 참조하는지 확인한다.

---

### Step 4. 내역 추가 (`addAccount()`)

**목표**: 오늘 날짜의 파일에 항목과 금액을 이어서 저장한다.

**할 일**

1. 오늘 날짜로 파일 경로를 만든다.
2. 항목 이름과 금액을 반복해서 입력받는다.
3. 입력받은 내용을 `StringBuilder`에 저장한다.
4. 금액의 합계를 계산한다.
5. append 모드로 파일에 내용을 저장한다.
6. 저장한 내용을 화면에도 출력한다.
7. 금액에 문자가 입력된 경우 예외를 처리한다.

**힌트**

```java
String today = LocalDate.now().toString();
File file = new File(DIR, today + ".txt");

int total = 0;
StringBuilder content = new StringBuilder();

while (true) {
    System.out.print("항목 이름 > ");
    String name = scanner.nextLine().trim();

    System.out.print("금액 > ");

    int price;

    try {
        price = Integer.parseInt(
                scanner.nextLine().trim()
        );
    } catch (NumberFormatException e) {
        System.out.println("금액은 숫자로 입력하세요.");
        continue;
    }

    content.append(name)
            .append(" : ")
            .append(price)
            .append("원")
            .append(System.lineSeparator());

    total += price;

    System.out.print("더 추가할까요? (y/n) > ");
    String answer = scanner.nextLine().trim();

    if (!answer.equalsIgnoreCase("y")) {
        break;
    }
}

content.append("합계 : ")
        .append(total)
        .append("원")
        .append(System.lineSeparator());

try (FileWriter writer =
             new FileWriter(file, true)) {

    writer.write(content.toString());

    System.out.println(
            file.getName() + "에 저장 완료"
    );

    System.out.print(content);

} catch (IOException e) {
    System.out.println(
            "저장 중 오류: " + e.getMessage()
    );
}
```

파일이 없으면 `FileWriter`가 새 파일을 생성하므로 `createNewFile()`을 별도로 호출하지 않아도 된다.

**확인**

* 오늘 날짜의 `.txt` 파일이 생성되는지 확인한다.
* 항목과 금액이 형식에 맞게 저장되는지 확인한다.
* 같은 날 다시 추가하면 기존 내용 뒤에 이어서 저장되는지 확인한다.
* 합계가 입력한 금액의 합과 일치하는지 확인한다.
* 금액에 문자를 입력해도 프로그램이 종료되지 않는지 확인한다.

### 설계 결정

하루에 여러 번 내역을 추가하면 각 입력 묶음마다 합계가 저장된다.

전체 합계를 하나만 유지하려면 기존 파일을 읽어 합계를 다시 계산하고 파일을 다시 작성해야 한다.

---

### Step 5. 내역 조회 (`showAccount()`)

**목표**: 저장된 날짜 목록을 출력하고 선택한 날짜의 파일 내용을 읽는다.

**할 일**

1. 저장 폴더의 파일 목록을 가져온다.
2. `.txt` 파일만 출력한다.
3. 기록이 없으면 안내 메시지를 출력한다.
4. 파일명에서 `.txt`를 제거해 날짜만 출력한다.
5. 조회할 날짜를 입력받는다.
6. 파일이 존재하면 내용을 한 줄씩 읽어 출력한다.
7. 파일이 없으면 안내 메시지를 출력한다.

**힌트**

```java
File folder = new File(DIR);
String[] files = folder.list();

if (files == null || files.length == 0) {
    System.out.println("기록이 없습니다.");
    return;
}

System.out.println("== 기록된 날짜 ==");

for (String name : files) {
    if (name.endsWith(".txt")) {
        System.out.println(
                name.substring(0, name.length() - 4)
        );
    }
}

System.out.print("조회할 날짜 입력 > ");
String inputDate = scanner.nextLine().trim();

File file = new File(
        DIR,
        inputDate + ".txt"
);

if (!file.exists()) {
    System.out.println(
            "해당 날짜의 기록이 없습니다."
    );
    return;
}

System.out.println("[" + inputDate + "]");

try (BufferedReader reader =
             new BufferedReader(
                     new FileReader(file)
             )) {

    String line;

    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }

} catch (IOException e) {
    System.out.println(
            "조회 중 오류: " + e.getMessage()
    );
}
```

**확인**

* 저장된 날짜 목록이 출력되는지 확인한다.
* 파일명에서 `.txt`가 제거되는지 확인한다.
* 날짜를 입력하면 해당 파일 내용이 출력되는지 확인한다.
* 존재하지 않는 날짜를 입력해도 프로그램이 종료되지 않는지 확인한다.

---

### Step 6. 내역 삭제 (`deleteAccount()`)

**목표**: 입력한 날짜의 파일을 삭제한다.

**할 일**

1. 저장된 날짜 목록을 출력한다.
2. 삭제할 날짜를 입력받는다.
3. 해당 파일의 존재 여부를 확인한다.
4. 파일이 있으면 `delete()`를 호출한다.
5. 삭제 성공, 실패, 파일 없음 상황을 구분해 출력한다.

**힌트**

```java
File folder = new File(DIR);
String[] files = folder.list();

if (files == null || files.length == 0) {
    System.out.println("삭제할 기록이 없습니다.");
    return;
}

System.out.println("== 기록된 날짜 ==");

for (String name : files) {
    if (name.endsWith(".txt")) {
        System.out.println(
                name.substring(0, name.length() - 4)
        );
    }
}

System.out.print("삭제할 날짜 입력 > ");
String inputDate = scanner.nextLine().trim();

File file = new File(
        DIR,
        inputDate + ".txt"
);

if (!file.exists()) {
    System.out.println(
            "해당 날짜의 기록이 없습니다."
    );
    return;
}

if (file.delete()) {
    System.out.println("삭제되었습니다.");
} else {
    System.out.println("삭제에 실패했습니다.");
}
```

**확인**

* 기록이 없을 때 안내 메시지가 출력되는지 확인한다.
* 존재하는 날짜의 파일이 삭제되는지 확인한다.
* 삭제 후 조회 목록에서 해당 날짜가 사라지는지 확인한다.
* 존재하지 않는 날짜를 입력해도 오류가 발생하지 않는지 확인한다.

---

### Step 7. 메뉴와 기능 연결

**목표**: 메뉴 선택에 따라 가계부 기능을 실행한다.

**힌트**

```java
public class Start {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AccountBook accountBook =
                new AccountBookImpl(scanner);

        while (true) {
            System.out.println(
                    "===== 가계부 (File) ====="
            );
            System.out.println("1. 내역 추가");
            System.out.println("2. 내역 조회");
            System.out.println("3. 삭제");
            System.out.println("4. 종료");
            System.out.print("번호 입력 > ");

            int menu;

            try {
                menu = Integer.parseInt(
                        scanner.nextLine().trim()
                );
            } catch (NumberFormatException e) {
                System.out.println(
                        "숫자를 입력하세요."
                );
                continue;
            }

            switch (menu) {
                case 1:
                    accountBook.addAccount();
                    break;
                case 2:
                    accountBook.showAccount();
                    break;
                case 3:
                    accountBook.deleteAccount();
                    break;
                case 4:
                    System.out.println("종료합니다.");
                    return;
                default:
                    System.out.println(
                            "1부터 4까지 입력하세요."
                    );
            }
        }
    }
}
```

**확인**

* 메뉴에 따라 올바른 기능이 호출되는지 확인한다.
* 추가, 조회, 삭제를 연속해서 실행할 수 있는지 확인한다.
* 종료 메뉴를 선택하면 프로그램이 끝나는지 확인한다.

---

## 6. 최종 완성 체크리스트

* [ ] `AccountBook.java`에 추가, 조회, 삭제 기능을 선언했다.
* [ ] `AccountBookImpl.java`가 인터페이스를 구현한다.
* [ ] `Start.java`와 구현 클래스가 하나의 `Scanner`를 공유한다.
* [ ] 저장 폴더 경로를 상수로 관리한다.
* [ ] 저장 폴더가 없으면 자동으로 생성한다.
* [ ] 오늘 날짜를 파일명으로 사용한다.
* [ ] 항목과 금액을 날짜별 파일에 저장한다.
* [ ] 기존 파일에 내용을 이어서 저장한다.
* [ ] 입력한 금액의 합계를 계산한다.
* [ ] 저장된 날짜 목록을 출력한다.
* [ ] 날짜를 입력해 파일 내용을 조회한다.
* [ ] 날짜를 입력해 파일을 삭제한다.
* [ ] 파일 입출력에 try-with-resources를 사용한다.
* [ ] 파일 입출력 예외를 처리한다.
* [ ] 숫자가 아닌 메뉴와 금액 입력을 처리한다.

---

## 7. 선택 도전 과제

1. **전체 합계 갱신**: 같은 날짜에 여러 번 추가해도 합계 줄이 하나만 유지되도록 기존 파일을 읽고 다시 저장한다.
2. **날짜 최신순 정렬**: 조회 시 날짜 목록을 최신 날짜부터 출력한다.
3. **항목 검색**: 특정 항목이 포함된 날짜와 내역을 검색한다.
4. **삭제 전 백업**: 삭제할 파일을 `backup` 폴더로 이동한 뒤 원본을 삭제한다.
5. **월별 합계 계산**: 같은 연도와 월에 해당하는 모든 파일의 금액을 합산한다.
6. **날짜 형식 검증**: `LocalDate.parse()`를 사용해 입력한 날짜 형식을 검사한다.
