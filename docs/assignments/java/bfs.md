# BFS 구현

그래프를 인접 리스트로 표현하고, `Queue`와 방문 배열을 사용해 BFS를 구현한다.

BFS는 시작 정점에서 가까운 정점부터 차례로 방문하는 너비 우선 탐색 알고리즘이다.

---

## 1. 구현 내용

정점 9개로 구성된 무방향 그래프를 생성하고, 1번 정점부터 BFS를 수행한다.

```text
1 ---- 2 ---- 6
 \    / \    /
  \  /   4 - 5
   \/   / \
   3 - 7 - 8 - 9
```

실행 결과는 다음과 같다.

```text
정점 1에서 시작하는 BFS
1 2 3 4 6 7 5 8 9
```

인접 리스트에 저장된 정점 순서에 따라 같은 그래프에서도 방문 순서는 달라질 수 있다.

---

## 2. 학습 목표

| 개념      | 학습 내용                |
| ------- | -------------------- |
| 그래프     | 정점과 간선으로 구성된 자료구조 이해 |
| 인접 리스트  | 각 정점과 연결된 정점 목록 저장   |
| 무방향 그래프 | 간선을 양쪽 정점에 모두 추가     |
| `Queue` | 먼저 추가된 정점부터 처리       |
| 방문 배열   | 같은 정점의 중복 방문 방지      |
| BFS     | 시작 정점과 가까운 정점부터 탐색   |

---

## 3. 핵심 개념

### 3.1 그래프와 인접 리스트

그래프는 정점과 정점을 연결하는 간선으로 구성된다.

인접 리스트는 각 정점이 자신과 연결된 정점 목록을 갖는 방식이다.

```text
adjacencyList[2] = [1, 3, 4, 6]
```

위 구조는 2번 정점이 1번, 3번, 4번, 6번 정점과 연결되어 있다는 의미다.

이번 과제에서는 다음 배열을 사용한다.

```java
LinkedList<Integer>[] adjacencyList;
```

배열의 각 요소에는 해당 정점과 연결된 정점 목록을 저장한다.

---

### 3.2 무방향 그래프의 간선

무방향 그래프에서는 두 정점 사이를 양방향으로 이동할 수 있다.

1번 정점과 2번 정점이 연결되어 있다면 두 인접 리스트에 모두 추가해야 한다.

```java
adjacencyList[1].add(2);
adjacencyList[2].add(1);
```

한쪽에만 추가하면 방향 그래프로 동작한다.

---

### 3.3 BFS와 `Queue`

BFS는 시작 정점에서 가까운 정점부터 방문한다.

먼저 발견한 정점을 먼저 처리해야 하므로 FIFO 구조인 `Queue`를 사용한다.

BFS의 기본 흐름은 다음과 같다.

1. 시작 정점을 방문 처리한다.
2. 시작 정점을 큐에 추가한다.
3. 큐에서 정점을 하나 꺼낸다.
4. 꺼낸 정점과 연결된 정점을 확인한다.
5. 방문하지 않은 정점을 방문 처리하고 큐에 추가한다.
6. 큐가 빌 때까지 반복한다.

---

### 3.4 방문 배열

그래프에는 여러 경로와 사이클이 존재할 수 있다.

방문 여부를 기록하지 않으면 같은 정점이 반복해서 큐에 들어갈 수 있으므로 `visited` 배열을 사용한다.

```java
boolean[] visited = new boolean[vertexCount + 1];
```

정점 번호를 1번부터 사용하므로 배열 크기를 정점 개수보다 1 크게 만든다.

방문 표시는 큐에서 꺼낼 때가 아니라 큐에 넣을 때 처리한다.

```java
if (!visited[adjacent]) {
    visited[adjacent] = true;
    queue.add(adjacent);
}
```

큐에서 꺼낼 때 방문 처리하면 같은 정점이 여러 경로를 통해 큐에 중복으로 들어갈 수 있다.

---

## 4. 파일 구조

| 파일           | 역할                       |
| ------------ | ------------------------ |
| `Graph.java` | 인접 리스트 생성, 간선 추가, 그래프 출력 |
| `Main.java`  | 그래프 생성과 BFS 실행           |

필요한 주요 클래스는 다음과 같다.

```java
java.util.LinkedList
java.util.Queue
```

---

## 5. 단계별 구현

### Step 1. 인접 리스트 생성

#### 목표

정점 개수만큼 인접 리스트 배열을 생성하고 각 요소를 빈 리스트로 초기화한다.

#### 구현 내용

1. `LinkedList<Integer>[]` 타입의 인접 리스트를 선언한다.
2. 정점 번호를 1번부터 사용하기 위해 배열 크기를 `vertex + 1`로 설정한다.
3. 각 배열 요소를 `LinkedList` 객체로 초기화한다.
4. 인접 리스트를 반환하는 getter를 작성한다.

<details>
<summary>힌트 보기</summary>

```java
class Graph {
    private LinkedList<Integer>[] adjacencyList;

    public Graph(int vertex) {
        adjacencyList = new LinkedList[vertex + 1];

        for (int i = 0; i < adjacencyList.length; i++) {
            adjacencyList[i] = new LinkedList<>();
        }
    }

    public LinkedList<Integer>[] getAdjacencyList() {
        return adjacencyList;
    }
}
```

</details>

#### 확인

* 배열 크기가 `vertex + 1`인지 확인한다.
* 배열의 각 요소가 `null`이 아닌 `LinkedList` 객체인지 확인한다.
* 그래프 객체 생성 시 예외가 발생하지 않는지 확인한다.

---

### Step 2. 간선 추가

#### 목표

두 정점을 연결하는 무방향 간선을 추가한다.

#### 구현 내용

1. 두 정점 번호를 매개변수로 받는다.
2. 첫 번째 정점의 인접 리스트에 두 번째 정점을 추가한다.
3. 두 번째 정점의 인접 리스트에 첫 번째 정점을 추가한다.

<details>
<summary>힌트 보기</summary>

```java
public void addEdge(int vertex1, int vertex2) {
    adjacencyList[vertex1].add(vertex2);
    adjacencyList[vertex2].add(vertex1);
}
```

</details>

#### 확인

1번과 2번을 연결했을 때 다음 두 관계가 모두 생성되는지 확인한다.

```text
1 → 2
2 → 1
```

---

### Step 3. 그래프 출력

#### 목표

각 정점의 인접 리스트를 출력해 그래프 연결 상태를 확인한다.

#### 구현 내용

1. 1번 정점부터 마지막 정점까지 반복한다.
2. 현재 정점 번호를 출력한다.
3. 해당 정점의 인접 리스트를 순회하며 연결된 정점을 출력한다.

<details>
<summary>힌트 보기</summary>

```java
public void printGraph() {
    for (int i = 1; i < adjacencyList.length; i++) {
        System.out.print("Vertex " + i + " : ");

        for (Integer vertex : adjacencyList[i]) {
            System.out.print(vertex + " ");
        }

        System.out.println();
    }
}
```

</details>

#### 확인

2번 정점이 1번, 3번, 4번, 6번과 연결되어 있다면 다음과 같이 출력되는지 확인한다.

```text
Vertex 2 : 1 3 4 6
```

---

### Step 4. BFS 준비

#### 목표

그래프를 생성하고 BFS에 필요한 방문 배열과 큐를 준비한다.

#### 구현 내용

1. 정점이 9개인 그래프를 생성한다.
2. `addEdge()`로 간선을 추가한다.
3. 방문 여부를 기록할 배열을 생성한다.
4. 탐색할 정점을 저장할 큐를 생성한다.
5. 시작 정점을 방문 처리한다.
6. 시작 정점을 큐에 추가한다.

<details>
<summary>힌트 보기</summary>

```java
boolean[] visited = new boolean[9 + 1];

Graph graph = new Graph(9);

graph.addEdge(1, 2);
graph.addEdge(1, 3);
// 나머지 간선 추가

int startVertex = 1;

Queue<Integer> queue = new LinkedList<>();

visited[startVertex] = true;
queue.add(startVertex);

System.out.println(
        "정점 " + startVertex + "에서 시작하는 BFS"
);
```

</details>

#### 확인

* 시작 정점이 방문 처리되어 있는지 확인한다.
* 큐에 시작 정점이 들어 있는지 확인한다.
* 방문 배열 크기가 정점 번호 범위를 포함하는지 확인한다.

---

### Step 5. BFS 실행

#### 목표

큐가 빌 때까지 정점을 꺼내고 인접 정점을 추가하며 그래프를 탐색한다.

#### 구현 내용

1. 큐가 비어 있지 않은 동안 반복한다.
2. 큐의 앞에서 정점을 꺼낸다.
3. 꺼낸 정점을 출력한다.
4. 현재 정점과 연결된 모든 정점을 확인한다.
5. 방문하지 않은 정점을 방문 처리한다.
6. 방문 처리한 정점을 큐에 추가한다.

<details>
<summary>힌트 보기</summary>

```java
while (!queue.isEmpty()) {
    int vertex = queue.poll();

    System.out.print(vertex + " ");

    for (int adjacent
            : graph.getAdjacencyList()[vertex]) {

        if (!visited[adjacent]) {
            visited[adjacent] = true;
            queue.add(adjacent);
        }
    }
}
```

</details>

#### 확인

1번 정점에서 시작했을 때 다음 순서로 출력되는지 확인한다.

```text
1 2 3 4 6 7 5 8 9
```

인접 리스트에 간선을 추가한 순서가 다르면 방문 순서 일부가 달라질 수 있다.

---

## 6. 최종 점검

* [ ] 그래프를 인접 리스트로 표현했다.
* [ ] 각 인접 리스트 요소를 초기화했다.
* [ ] 무방향 간선을 양쪽 정점에 추가했다.
* [ ] `Queue`를 사용해 탐색할 정점을 관리했다.
* [ ] 시작 정점을 방문 처리한 뒤 큐에 추가했다.
* [ ] 큐에서 정점을 꺼내 방문했다.
* [ ] 방문하지 않은 인접 정점만 큐에 추가했다.
* [ ] 정점을 큐에 추가할 때 방문 처리했다.
* [ ] 사이클이 있어도 탐색이 종료된다.
* [ ] 모든 정점을 한 번씩 방문한다.

---

## 7. 학습 체크

* [ ] 그래프의 정점과 간선을 설명할 수 있다.
* [ ] 인접 리스트의 구조를 설명할 수 있다.
* [ ] 무방향 그래프에서 간선을 양쪽에 추가하는 이유를 이해했다.
* [ ] `Queue`의 FIFO 구조가 BFS에 사용되는 이유를 이해했다.
* [ ] 방문 배열이 필요한 이유를 설명할 수 있다.
* [ ] 방문 처리를 큐에 추가할 때 수행하는 이유를 이해했다.
* [ ] BFS가 가까운 정점부터 탐색하는 과정을 설명할 수 있다.

---

## 8. 선택 도전 과제

1. **시작 정점 변경**: 시작 정점을 3번이나 5번으로 변경해 방문 순서를 비교한다.
2. **DFS 구현**: 같은 그래프를 스택 또는 재귀로 탐색해 BFS 결과와 비교한다.
3. **최단 거리 계산**: `dist[]` 배열을 사용해 시작 정점에서 각 정점까지 필요한 최소 간선 수를 기록한다.
4. **경로 복원**: `parent[]` 배열에 각 정점의 이전 정점을 기록하고 특정 정점까지의 경로를 출력한다.
5. **연결 요소 탐색**: 모든 정점을 순회하며 연결되지 않은 그래프의 각 연결 요소를 탐색한다.
6. **인접 행렬 구현**: 인접 리스트 대신 `boolean[][]` 배열로 그래프를 표현한다.
