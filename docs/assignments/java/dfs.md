# DFS 구현

그래프를 인접 리스트로 표현하고, 방문 배열과 재귀 호출을 사용해 DFS를 구현한다.

DFS는 한 방향으로 더 이상 이동할 수 없을 때까지 깊이 탐색한 뒤, 이전 정점으로 돌아와 다른 경로를 탐색하는 깊이 우선 탐색 알고리즘이다.

---

## 1. 구현 내용

BFS 과제에서 사용한 것과 같은 정점 9개의 무방향 그래프를 생성하고, 1번 정점부터 DFS를 수행한다.

```text
1 ---- 2 ---- 6
 \    / \    /
  \  /   4 - 5
   \/   / \
   3 - 7 - 8 - 9
```

실행 결과는 다음과 같다.

```text
정점 1에서 시작하는 DFS
1 2 3 7 4 5 6 8 9
```

인접 리스트에 간선을 추가한 순서에 따라 같은 그래프에서도 방문 순서는 달라질 수 있다.

---

## 2. 학습 목표

| 개념       | 학습 내용                     |
| -------- | ------------------------- |
| 그래프      | 정점과 간선으로 구성된 자료구조 이해      |
| 인접 리스트   | 각 정점과 연결된 정점 목록 저장        |
| 무방향 그래프  | 간선을 양쪽 정점에 모두 추가          |
| DFS      | 한 경로를 끝까지 탐색한 뒤 이전 경로로 복귀 |
| 재귀       | 함수 호출 스택을 이용한 깊이 우선 탐색    |
| 방문 배열    | 같은 정점의 중복 방문과 무한 반복 방지    |
| BFS와 DFS | 너비 우선 탐색과 깊이 우선 탐색 비교     |

---

## 3. 핵심 개념

### 3.1 DFS와 백트래킹

DFS는 현재 정점에서 방문하지 않은 인접 정점으로 계속 이동한다.

더 이상 방문할 수 있는 인접 정점이 없으면 이전 함수 호출 위치로 돌아와 다른 경로를 탐색한다.

이처럼 탐색한 경로에서 이전 위치로 돌아오는 과정을 백트래킹이라고 한다.

재귀 방식에서는 함수 호출 스택이 이전 정점 정보를 저장하므로 별도의 복귀 로직을 직접 구현하지 않아도 된다.

---

### 3.2 재귀를 이용한 DFS

재귀 DFS의 기본 흐름은 다음과 같다.

```text
dfs(현재 정점)
    현재 정점을 방문 처리한다.
    현재 정점을 출력한다.

    현재 정점의 인접 정점을 순회한다.
        방문하지 않은 인접 정점이면 dfs를 다시 호출한다.
```

코드로 표현하면 다음과 같다.

```java
private void dfsRecursive(
        int vertex,
        boolean[] visited
) {
    visited[vertex] = true;
    System.out.print(vertex + " ");

    for (int adjacent : adjacencyList[vertex]) {
        if (!visited[adjacent]) {
            dfsRecursive(adjacent, visited);
        }
    }
}
```

재귀 호출이 깊어질수록 호출 스택에 현재 실행 정보가 쌓인다.

탐색할 정점이 없으면 현재 호출이 종료되고 이전 호출로 돌아간다.

---

### 3.3 방문 배열

그래프에는 여러 경로와 사이클이 존재할 수 있다.

방문 여부를 기록하지 않으면 같은 정점을 반복해서 방문하며 재귀 호출이 끝나지 않을 수 있다.

```java
boolean[] visited =
        new boolean[adjacencyList.length];
```

현재 정점에 도착하면 인접 정점을 탐색하기 전에 방문 처리한다.

```java
visited[vertex] = true;
```

방문하지 않은 인접 정점만 재귀 호출한다.

```java
if (!visited[adjacent]) {
    dfsRecursive(adjacent, visited);
}
```

---

### 3.4 BFS와 DFS 비교

| 구분      | BFS                 | DFS                  |
| ------- | ------------------- | -------------------- |
| 탐색 방식   | 시작 정점에서 가까운 정점부터 탐색 | 한 경로를 끝까지 탐색         |
| 주요 자료구조 | `Queue`             | 재귀 호출 스택 또는 `Stack`  |
| 방문 처리   | 큐에 추가할 때 처리         | 정점에 진입할 때 처리         |
| 주요 활용   | 최단 거리, 단계별 탐색       | 경로 탐색, 연결 요소, 사이클 검사 |

BFS는 큐의 FIFO 구조를 사용한다.

DFS는 재귀 호출 스택 또는 직접 생성한 `Stack`의 LIFO 구조를 사용한다.

---

## 4. 파일 구조

| 파일           | 역할                       |
| ------------ | ------------------------ |
| `Graph.java` | 인접 리스트 생성, 간선 추가, DFS 실행 |
| `Main.java`  | 그래프 생성과 탐색 시작            |

필요한 주요 클래스는 다음과 같다.

```java
java.util.LinkedList
```

---

## 5. 단계별 구현

### Step 1. 인접 리스트 그래프 생성

#### 목표

정점 개수만큼 인접 리스트 배열을 생성하고 무방향 간선을 추가한다.

#### 구현 내용

1. `LinkedList<Integer>[]` 타입의 인접 리스트를 선언한다.
2. 정점 번호를 1번부터 사용하기 위해 배열 크기를 `vertex + 1`로 설정한다.
3. 각 배열 요소를 빈 `LinkedList`로 초기화한다.
4. 두 정점을 양쪽 인접 리스트에 추가한다.

<details>
<summary>힌트 보기</summary>

```java
class Graph {
    private LinkedList<Integer>[] adjacencyList;

    public Graph(int vertex) {
        adjacencyList =
                new LinkedList[vertex + 1];

        for (int i = 0;
             i < adjacencyList.length;
             i++) {

            adjacencyList[i] =
                    new LinkedList<>();
        }
    }

    public void addEdge(
            int vertex1,
            int vertex2
    ) {
        adjacencyList[vertex1]
                .add(vertex2);

        adjacencyList[vertex2]
                .add(vertex1);
    }
}
```

</details>

#### 확인

* 배열 크기가 `vertex + 1`인지 확인한다.
* 각 배열 요소가 `LinkedList` 객체로 초기화되어 있는지 확인한다.
* 무방향 간선이 양쪽 인접 리스트에 모두 추가되는지 확인한다.

---

### Step 2. DFS 시작 메서드 구현

#### 목표

외부에서 시작 정점만 전달하면 방문 배열을 생성하고 DFS를 시작하도록 한다.

#### 구현 내용

1. 시작 정점을 매개변수로 받는다.
2. 인접 리스트 크기에 맞는 방문 배열을 생성한다.
3. 탐색 시작 메시지를 출력한다.
4. 실제 재귀 메서드를 호출한다.
5. 탐색 종료 후 줄바꿈을 출력한다.

<details>
<summary>힌트 보기</summary>

```java
public void dfs(int startVertex) {
    boolean[] visited =
            new boolean[adjacencyList.length];

    System.out.println(
            "정점 " + startVertex
            + "에서 시작하는 DFS"
    );

    dfsRecursive(startVertex, visited);

    System.out.println();
}
```

</details>

#### 확인

* `dfs()`를 호출할 때마다 새로운 방문 배열이 생성되는지 확인한다.
* 시작 정점과 방문 배열이 재귀 메서드에 전달되는지 확인한다.

---

### Step 3. 재귀 DFS 구현

#### 목표

현재 정점을 방문하고, 방문하지 않은 인접 정점으로 재귀 호출한다.

#### 구현 내용

1. 현재 정점을 방문 처리한다.
2. 현재 정점을 출력한다.
3. 현재 정점의 인접 리스트를 순회한다.
4. 방문하지 않은 인접 정점을 찾는다.
5. 해당 정점으로 재귀 호출한다.

<details>
<summary>힌트 보기</summary>

```java
private void dfsRecursive(
        int vertex,
        boolean[] visited
) {
    visited[vertex] = true;

    System.out.print(vertex + " ");

    for (int adjacent
            : adjacencyList[vertex]) {

        if (!visited[adjacent]) {
            dfsRecursive(
                    adjacent,
                    visited
            );
        }
    }
}
```

</details>

#### 확인

* 정점 방문 전에 `visited`를 `true`로 변경하는지 확인한다.
* 방문하지 않은 인접 정점에만 재귀 호출하는지 확인한다.
* 더 이상 방문할 정점이 없으면 이전 호출로 돌아가는지 확인한다.
* 사이클이 있어도 무한 반복하지 않는지 확인한다.

---

### Step 4. 그래프 생성과 DFS 실행

#### 목표

정점 9개의 그래프를 만들고 1번 정점부터 DFS를 실행한다.

#### 구현 내용

1. 정점이 9개인 `Graph` 객체를 생성한다.
2. 그래프 구조에 맞게 간선을 추가한다.
3. `dfs(1)`을 호출한다.

<details>
<summary>힌트 보기</summary>

```java
public class Main {

    public static void main(String[] args) {
        Graph graph = new Graph(9);

        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(2, 6);
        graph.addEdge(3, 7);
        graph.addEdge(4, 5);
        graph.addEdge(4, 7);
        graph.addEdge(4, 8);
        graph.addEdge(5, 6);
        graph.addEdge(7, 8);
        graph.addEdge(8, 9);

        graph.dfs(1);
    }
}
```

</details>

#### 확인

다음 방문 순서가 출력되는지 확인한다.

```text
1 2 3 7 4 5 6 8 9
```

간선을 추가한 순서가 다르면 일부 방문 순서는 달라질 수 있다.

---

## 6. 최종 점검

* [ ] 그래프를 인접 리스트로 표현했다.
* [ ] 무방향 간선을 양쪽 정점에 추가했다.
* [ ] DFS 실행 시 새로운 방문 배열을 생성했다.
* [ ] 현재 정점을 방문 처리한 뒤 출력했다.
* [ ] 방문하지 않은 인접 정점만 재귀 호출했다.
* [ ] 한 경로를 끝까지 탐색한 뒤 이전 호출로 돌아왔다.
* [ ] 사이클이 있어도 무한 반복 없이 종료된다.
* [ ] 모든 정점을 한 번씩 방문한다.
* [ ] BFS 결과와 DFS 결과의 차이를 확인했다.

---

## 7. 학습 체크

* [ ] DFS가 한 경로를 끝까지 탐색하는 방식임을 이해했다.
* [ ] 재귀 호출 스택이 백트래킹을 처리하는 과정을 이해했다.
* [ ] 방문 배열이 필요한 이유를 설명할 수 있다.
* [ ] 그래프의 사이클이 무한 반복을 발생시킬 수 있음을 이해했다.
* [ ] BFS는 `Queue`, DFS는 재귀 또는 `Stack`을 사용한다는 차이를 이해했다.
* [ ] 인접 리스트의 저장 순서에 따라 탐색 순서가 달라질 수 있음을 이해했다.

---

## 8. 선택 도전 과제

1. **스택 DFS 구현**: 재귀 호출 대신 `Stack`을 사용해 반복문 방식의 DFS를 구현한다.
2. **시작 정점 변경**: 시작 정점을 3번이나 5번으로 변경해 방문 순서를 비교한다.
3. **경로 탐색**: 특정 정점까지 도달할 수 있는지 확인하고 이동 경로를 출력한다.
4. **사이클 탐지**: 이미 방문한 정점과 부모 정점을 비교해 무방향 그래프의 사이클을 확인한다.
5. **연결 요소 개수 계산**: 모든 정점을 순회하며 연결된 그래프 묶음의 개수를 계산한다.
6. **BFS와 비교**: 같은 그래프에서 BFS와 DFS를 모두 실행해 방문 순서를 비교한다.
