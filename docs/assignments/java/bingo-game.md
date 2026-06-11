# 빙고 게임 만들기 (2차원 배열 활용)

> 콘솔에서 동작하는 빙고 게임을 구현한다. `2차원 배열`, 반복문, 메서드, 난수, 조건문을 종합적으로 연습한다. 컴퓨터와 번갈아 숫자를 부르고, 먼저 정해진 줄 수만큼 빙고를 완성하면 승리한다.

---

## 1. 무엇을 만드나요?

콘솔에서 컴퓨터와 1:1로 겨루는 5×5 빙고 게임을 구현한다.

* 나와 컴퓨터가 각각 1~25 숫자가 무작위로 섞인 빙고판을 가진다.
* 내 차례에 숫자 하나를 부르면 나와 컴퓨터 두 판 모두에서 그 숫자가 지워진다.
* 이어서 컴퓨터가 무작위로 숫자 하나를 부른다.
* 컴퓨터가 부른 숫자도 나와 컴퓨터 두 판 모두에서 지워진다.
* 가로, 세로, 대각선 한 줄이 모두 지워지면 빙고 1줄로 센다.
* 먼저 목표 줄 수를 완성하는 쪽이 승리한다.

실행 화면 예시는 다음과 같다.

```text
===== 내 빙고판 =====
[ 7] [13] [ 1] [22] [ 9]
[ 4] [ ★] [18] [ ★] [ 2]
[25] [11] [ ★] [ 6] [16]
[ 3] [20] [ ★] [14] [ ★]
[ 8] [17] [21] [10] [ 5]

부를 숫자 입력 (1~25) > 7

▶ 내가 부른 숫자: 7
▶ 컴퓨터가 부른 숫자: 18

현재 빙고 줄  → 나: 1줄,  컴퓨터: 0줄
```

`★`는 이미 불려서 마킹된 칸을 의미한다.

---

## 2. 요구사항 정리 (기능 명세)

| 번호 | 기능      | 설명                                                           |
| -- | ------- | ------------------------------------------------------------ |
| 1  | 빙고판 만들기 | 1~25 숫자를 무작위로 섞어 5×5 판에 채운다. 나와 컴퓨터가 각각 빙고판을 가진다.            |
| 2  | 빙고판 출력  | 판을 보기 좋게 출력한다. 지워진 칸은 `★`로 표시한다.                             |
| 3  | 숫자 부르기  | 내 차례에는 직접 입력하고, 컴퓨터 차례에는 무작위로 숫자를 고른다. 이미 부른 숫자는 다시 부를 수 없다. |
| 4  | 마킹      | 부른 숫자를 나와 컴퓨터 두 판 모두에서 찾아 지운다.                               |
| 5  | 빙고 줄 세기 | 가로 5줄, 세로 5줄, 대각선 2줄 중 완성된 줄 수를 센다.                          |
| 6  | 승패 판정   | 먼저 목표 줄 수에 도달한 쪽이 승리한다. 동시에 도달하면 무승부로 처리한다.                  |

---

## 3. 핵심 개념: 데이터를 어떻게 저장할까?

빙고판은 5행 5열의 격자이다.

이를 `2차원 배열`로 표현한다.

```java
int[][] board = new int[5][5];
boolean[][] marked = new boolean[5][5];
```

* `board[r][c]`는 r행 c열에 적힌 숫자를 의미한다.
* `marked[r][c]`는 해당 칸이 지워졌는지 여부를 의미한다.
* 이미 부른 숫자를 다시 부르지 못하게 하려면 `boolean[] called = new boolean[26]`으로 기록한다.

이 과제의 핵심은 2차원 배열을 이중 `for`문으로 순회하는 것이다.

빙고판 만들기, 출력, 마킹, 줄 세기 모두 이중 `for`문을 활용한다.

### 빙고 줄을 세는 법

```text
가로 한 줄: 같은 r에서 c = 0~4가 모두 marked인지 확인
세로 한 줄: 같은 c에서 r = 0~4가 모두 marked인지 확인
대각선 ＼ : marked[0][0], [1][1], [2][2], [3][3], [4][4]가 모두 true인지 확인
대각선 ／ : marked[0][4], [1][3], [2][2], [3][1], [4][0]가 모두 true인지 확인
```

빙고 줄은 최대 12줄까지 나올 수 있다.

가로 5줄, 세로 5줄, 대각선 2줄을 합친 수이다.

---

## 4. 파일 구조 (각 파일의 역할)

| 파일               | 역할                                                    |
| ---------------- | ----------------------------------------------------- |
| `BingoGame.java` | 게임의 모든 로직을 담는 클래스이다. 판 생성, 출력, 마킹, 줄 세기, 게임 진행을 담당한다. |
| `Start.java`     | `main` 메서드를 가진다. `BingoGame` 객체를 생성하고 게임을 시작한다.       |

기능을 메서드 단위로 나누는 것도 이 과제의 목표이다.

`main`에 모든 로직을 작성하지 않고, 판 만들기, 출력하기, 줄 세기처럼 역할별 메서드로 나누어 구현한다.

---

## 5. Step by Step

각 Step에는 목표, 할 일, 힌트, 확인 방법이 있다.

한 Step씩 구현한 뒤 실행 결과를 확인하면서 다음 Step으로 넘어간다.

---

### Step 1. 시작 화면 띄우기 (`Start.java`, `BingoGame.java`)

**목표**: `BingoGame` 객체를 만들고 `play()`를 호출하면 시작 인사말이 출력되도록 한다.

**할 일**

* `BingoGame` 클래스에 `play()` 메서드를 만든다.
* `play()` 메서드에서 시작 인사말을 출력한다.
* `Start.java`의 `main`에서 `BingoGame` 객체를 생성한다.
* 생성한 객체의 `play()`를 호출한다.

**힌트**

```java
public class BingoGame {
    public void play() {
        System.out.println("===== 빙고 게임 =====");
        System.out.println("컴퓨터와 번갈아 숫자를 불러 빙고를 완성하세요!");
    }
}
```

```java
public class Start {
    public static void main(String[] args) {
        BingoGame game = new BingoGame();
        game.play();
    }
}
```

**확인**: 실행했을 때 시작 인사말이 출력되면 성공이다.

---

### Step 2. 빙고판 만들기 (`makeBoard`)

**목표**: 1~25 숫자를 무작위로 섞어 5×5 배열에 채운다.

**할 일**

1. 판 크기와 최대 숫자를 상수로 선언한다.
2. 1~25 숫자를 리스트에 담는다.
3. `Collections.shuffle()`로 숫자 리스트를 섞는다.
4. 섞은 숫자를 이중 `for`문으로 `board[r][c]`에 하나씩 채운다.

**힌트**

```java
static final int SIZE = 5;
static final int MAX = 25;

void makeBoard(int[][] board) {
    List<Integer> nums = new ArrayList<>();

    for (int i = 1; i <= MAX; i++) {
        nums.add(i);
    }

    Collections.shuffle(nums);

    int idx = 0;

    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            board[r][c] = nums.get(idx++);
        }
    }
}
```

**확인**: 다음 Step에서 출력했을 때 1~25가 겹치지 않고 무작위로 들어가 있으면 성공이다.

---

### Step 3. 빙고판 출력하기 (`printBoard`)

**목표**: 빙고판을 격자 모양으로 출력한다. 지워진 칸은 `★`로 표시한다.

**할 일**

* 이중 `for`문으로 한 칸씩 출력한다.
* `marked[r][c]`가 `true`이면 `★`를 출력한다.
* `marked[r][c]`가 `false`이면 `board[r][c]`의 숫자를 출력한다.
* 두 자리 형식으로 칸을 맞춘다.

**힌트**

```java
void printBoard(int[][] board, boolean[][] marked) {
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            if (marked[r][c]) {
                System.out.print("[ ★] ");
            } else {
                System.out.printf("[%2d] ", board[r][c]);
            }
        }

        System.out.println();
    }
}
```

**확인**: 5×5 격자가 가지런히 출력되면 성공이다.

---

### Step 4. 숫자 마킹하기 (`mark`)

**목표**: 부른 숫자를 판에서 찾아 `marked` 값을 `true`로 변경한다.

**할 일**

* 이중 `for`문으로 판 전체를 순회한다.
* `board[r][c]`가 부른 숫자와 같으면 해당 위치의 `marked[r][c]`를 `true`로 변경한다.

**힌트**

```java
void mark(int[][] board, boolean[][] marked, int num) {
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            if (board[r][c] == num) {
                marked[r][c] = true;
            }
        }
    }
}
```

**확인**: 마킹 후 빙고판을 출력했을 때 해당 숫자 칸이 `★`로 바뀌면 성공이다.

---

### Step 5. 빙고 줄 세기 (`countBingo`)

**목표**: 완성된 줄의 개수를 센다. 가로, 세로, 대각선을 모두 확인한다.

**할 일**

1. 가로 5줄을 검사한다.
2. 각 행이 전부 마킹되어 있으면 빙고 줄 수를 1 증가시킨다.
3. 세로 5줄을 검사한다.
4. 각 열이 전부 마킹되어 있으면 빙고 줄 수를 1 증가시킨다.
5. 왼쪽 위에서 오른쪽 아래로 내려가는 대각선을 검사한다.
6. 오른쪽 위에서 왼쪽 아래로 내려가는 대각선을 검사한다.
7. 완성된 줄 수를 반환한다.

**힌트**

```java
int countBingo(boolean[][] marked) {
    int count = 0;

    for (int r = 0; r < SIZE; r++) {
        boolean all = true;

        for (int c = 0; c < SIZE; c++) {
            if (!marked[r][c]) {
                all = false;
            }
        }

        if (all) {
            count++;
        }
    }

    for (int c = 0; c < SIZE; c++) {
        boolean all = true;

        for (int r = 0; r < SIZE; r++) {
            if (!marked[r][c]) {
                all = false;
            }
        }

        if (all) {
            count++;
        }
    }

    boolean d1 = true;

    for (int i = 0; i < SIZE; i++) {
        if (!marked[i][i]) {
            d1 = false;
        }
    }

    if (d1) {
        count++;
    }

    boolean d2 = true;

    for (int i = 0; i < SIZE; i++) {
        if (!marked[i][SIZE - 1 - i]) {
            d2 = false;
        }
    }

    if (d2) {
        count++;
    }

    return count;
}
```

**확인**: 한 줄을 일부러 모두 마킹했을 때 `countBingo()`가 1을 반환하면 성공이다.

---

### Step 6. 숫자 부르기 (`playerPick`, `computerPick`)

**목표**: 내 차례에는 입력으로 숫자를 고르고, 컴퓨터 차례에는 난수로 숫자를 고른다. 이미 부른 숫자는 다시 고르지 못하게 한다.

**할 일**

1. `boolean[] called = new boolean[MAX + 1]`로 이미 부른 숫자를 기록한다.
2. `playerPick()`에서 1~25 범위인지 확인한다.
3. 이미 부른 숫자인지 확인한다.
4. 잘못된 입력이면 다시 입력받는다.
5. `computerPick()`에서 아직 부르지 않은 숫자가 나올 때까지 난수를 뽑는다.

**힌트**

```java
boolean[] called = new boolean[MAX + 1];
Scanner sc = new Scanner(System.in);
Random rand = new Random();

int playerPick() {
    while (true) {
        System.out.print("부를 숫자 입력 (1~25) > ");

        int num;

        try {
            num = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력하세요.");
            continue;
        }

        if (num < 1 || num > MAX) {
            System.out.println("1~25 사이로 입력하세요.");
        } else if (called[num]) {
            System.out.println("이미 부른 숫자입니다.");
        } else {
            return num;
        }
    }
}

int computerPick() {
    int num;

    do {
        num = rand.nextInt(MAX) + 1;
    } while (called[num]);

    return num;
}
```

**확인**: 범위를 벗어난 값이나 이미 부른 숫자를 입력하면 다시 묻고, 올바른 숫자만 통과하면 성공이다.

---

### Step 7. 게임 진행과 승패 판정 (`play`)

**목표**: 두 판을 만들고, 번갈아 숫자를 부르며, 누군가 목표 줄 수에 도달하면 게임을 종료한다.

**할 일**

1. `play()`에서 나와 컴퓨터의 빙고판을 `makeBoard()`로 만든다.
2. `while(true)` 안에서 내 빙고판을 출력한다.
3. 내가 숫자를 부른다.
4. 부른 숫자를 나와 컴퓨터 두 판 모두에 마킹한다.
5. 승패를 확인한다.
6. 컴퓨터가 숫자를 부른다.
7. 컴퓨터가 부른 숫자를 나와 컴퓨터 두 판 모두에 마킹한다.
8. 승패를 확인한다.
9. 현재 나와 컴퓨터의 빙고 줄 수를 출력한다.

**힌트**

```java
static final int TARGET = 3;

public void play() {
    makeBoard(playerBoard);
    makeBoard(computerBoard);

    System.out.println("먼저 " + TARGET + "줄을 완성하면 승리!");

    while (true) {
        System.out.println("\n===== 내 빙고판 =====");
        printBoard(playerBoard, playerMarked);

        int num = playerPick();
        callNumber(num, "내가");

        if (checkWin()) {
            break;
        }

        int cNum = computerPick();
        callNumber(cNum, "컴퓨터가");

        if (checkWin()) {
            break;
        }

        System.out.println("\n현재 빙고 줄  → 나: " + countBingo(playerMarked)
                + "줄,  컴퓨터: " + countBingo(computerMarked) + "줄");
    }
}
```

`callNumber()`와 `checkWin()`은 중복을 줄이기 위한 도우미 메서드로 분리할 수 있다.

**확인**: 게임이 끝까지 진행되고, 한쪽이 3줄을 만들면 승패가 출력되며 종료되면 성공이다.

---

### Step 8. 마무리 다듬기

**목표**: 예외 상황과 승패 처리를 점검하고 게임을 완성한다.

**점검 항목**

* [ ] 1~25가 겹치지 않고 무작위로 배치되는지 확인한다.
* [ ] 같은 숫자를 두 번 부를 수 없는지 확인한다.
* [ ] 숫자 입력란에 글자를 넣어도 프로그램이 종료되지 않는지 확인한다.
* [ ] 가로, 세로, 대각선 줄이 정확히 세어지는지 확인한다.
* [ ] 양쪽이 동시에 목표 줄에 도달하면 무승부로 처리되는지 확인한다.
* [ ] 게임이 끝나면 양쪽 판을 모두 보여주는지 확인한다.

여기까지 통과하면 빙고 게임이 완성된다.

---

## 6. 최종 완성 체크리스트

* [ ] `BingoGame.java`에서 판 생성, 출력, 마킹, 줄 세기, 게임 진행 메서드를 구현한다.
* [ ] `Start.java`에서 `BingoGame` 객체를 생성하고 `play()`를 호출한다.
* [ ] 2차원 배열 `int[][]`, `boolean[][]`을 사용한다.
* [ ] `Collections.shuffle()` 또는 `Random`으로 숫자를 무작위 배치한다.
* [ ] 만들기, 출력, 부르기, 마킹, 줄 세기, 승패 판정이 순서대로 동작한다.

---

## 7. 선택 도전 과제

1. **빙고 외치기**: 줄이 완성될 때마다 "빙고!"를 한 번씩만 출력
2. **줄 수 선택**: 시작할 때 목표 줄 수를 입력받아 설정 (`3`, `4`, `5` 등)
3. **판 크기 변경**: `SIZE`만 바꿔도 3×3, 7×7로 동작하도록 개선
4. **2인용 모드**: 컴퓨터 대신 사람 2명이 번갈아 입력
5. **숫자 직접 채우기**: 무작위 대신 사용자가 원하는 위치에 숫자를 직접 배치
6. **기록 저장**: 승패 결과를 파일에 누적 기록 (`3승 1패` 등)
