# 그래프

## 그래프의 구성

그래프는 여러 정점과 정점을 연결하는 간선으로 구성된다.

* `Node`: 데이터를 저장하는 정점
* `Connection`: 정점 사이의 연결
* `weight`: 연결에 부여된 가중치

방향 그래프에서는 연결 방향에 따라 이동 가능 여부가 달라진다.

```text
A → D
```

위 연결은 `A`에서 `D`로 이동할 수 있다는 의미이며, 반대 방향 이동은 별도의 연결이 있어야 가능하다.

## 가중치가 있는 연결

가중치는 정점이 아니라 정점 사이의 연결에 저장한다.

```java
class Connection<T> {

    private Node<T> node;
    private int weight;
}
```

정점은 연결된 다른 정점과 가중치를 함께 관리한다.

```java
class Node<T> {

    private T data;
    private List<Connection<T>> connections;
    private boolean visited;
}
```

## 그래프 연결

방향과 가중치를 지정해 정점을 연결한다.

```java
a.link(d, 2);
b.link(a, 5);
b.link(e, 4);
```

`a.link(d, 2)`는 `A`에서 `D`로 가중치 `2`의 간선이 존재한다는 의미다.

방향 그래프에서는 반대 방향도 필요하다면 별도로 연결해야 한다.

## BFS

BFS는 가까운 정점부터 너비 방향으로 탐색한다.

Queue를 사용하며, 먼저 들어온 정점을 먼저 확인한다.

```text
시작 정점 추가
→ Queue에서 정점 꺼내기
→ 방문 처리
→ 연결된 미방문 정점 추가
→ 목표 정점을 찾을 때까지 반복
```

```java
Queue<Node<String>> queue = new ArrayDeque<>();
queue.offer(start);

while (!queue.isEmpty()) {
    Node<String> current = queue.poll();

    if (current.equals(target)) {
        break;
    }

    for (Connection<String> connection : current.connections()) {
        Node<String> next = connection.getNode();

        if (!next.isVisited()) {
            queue.offer(next);
        }
    }
}
```

방문 여부를 관리하지 않으면 순환 구조에서 같은 정점을 반복해서 탐색할 수 있다.

## DFS

DFS는 한 경로를 끝까지 탐색한 뒤 이전 지점으로 돌아와 다른 경로를 확인한다.

Stack을 사용하면 반복문으로 구현할 수 있다.

```text
시작 정점 추가
→ Stack에서 정점 꺼내기
→ 방문 처리
→ 연결된 미방문 정점 추가
→ 목표 정점을 찾을 때까지 반복
```

BFS의 Queue를 Stack으로 변경하면 기본적인 DFS 구조가 된다.

## BFS와 DFS 비교

| 구분      | BFS         | DFS         |
| ------- | ----------- | ----------- |
| 사용 자료구조 | Queue       | Stack 또는 재귀 |
| 탐색 방식   | 가까운 정점부터 탐색 | 한 경로를 깊게 탐색 |
| 주요 활용   | 최소 간선 수 탐색  | 경로 탐색, 순회   |
| 시간복잡도   | `O(V + E)`  | `O(V + E)`  |

`V`는 정점 수, `E`는 간선 수다.

가중치가 없는 그래프에서 BFS는 시작점에서 목표까지의 최소 간선 수를 찾는 데 사용할 수 있다.

가중치가 있는 그래프에서는 단순 BFS만으로 최소 가중치 경로를 보장하지 않는다. 최소 가중치 경로가 필요하면 가중치 조건에 맞는 별도 최단 경로 알고리즘을 사용해야 한다.

## 가중치 누적

탐색 과정에서 현재까지 지나온 간선의 가중치를 누적할 수 있다.

```java
int nextWeight = currentWeight + connection.getWeight();
```

다만 이 값은 해당 탐색 경로를 따라 누적된 값이다.

먼저 목표 정점에 도착했다고 해서 항상 전체 경로 중 최소 가중치라는 의미는 아니다.

## 인접 행렬

그래프의 연결 상태를 2차원 배열로 표현할 수 있다.

```java
int[][] matrix = {
        {0, 0, 0, 2, 0},
        {5, 0, 6, 0, 4},
        {0, 6, 0, 0, 0},
        {0, 0, 2, 0, 0},
        {0, 0, 0, 3, 0}
};
```

행은 출발 정점, 열은 도착 정점을 의미한다.

```text
matrix[i][j]
= i번 정점에서 j번 정점으로 가는 간선의 가중치
```

자료에서는 `0`을 연결이 없는 상태로 사용한다.

따라서 실제 가중치로 `0`을 허용해야 하는 그래프에서는 다른 표현 방법이 필요하다.

## 인접 행렬로 그래프 생성

인접 행렬을 순회하면서 값이 존재하는 위치에 간선을 생성할 수 있다.

```java
void generate(List<Node<String>> nodes, int[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix[i].length; j++) {
            int weight = matrix[i][j];

            if (weight == 0) {
                continue;
            }

            nodes.get(i).link(nodes.get(j), weight);
        }
    }
}
```

행렬을 사용하면 정점 사이의 연결과 가중치를 데이터 형태로 관리할 수 있다.

## 인접 행렬과 인접 리스트

| 구분       | 인접 행렬      | 인접 리스트      |
| -------- | ---------- | ----------- |
| 저장 방식    | 2차원 배열     | 연결된 정점 목록   |
| 공간복잡도    | `O(V²)`    | `O(V + E)`  |
| 간선 존재 확인 | 빠름         | 연결 목록 탐색 필요 |
| 적합한 그래프  | 간선이 많은 그래프 | 간선이 적은 그래프  |

`Node` 내부에 `List<Connection<T>>`를 두는 방식은 인접 리스트 방식에 해당한다.

## 제네릭 그래프

정점 데이터를 특정 타입으로 제한하지 않으려면 제네릭을 사용한다.

```java
Graph<String> graph = new Graph<>();
Graph<Integer> graph = new Graph<>();
```

`Node<T>`, `Connection<T>`, `Graph<T>`가 동일한 타입 매개변수를 사용하면 다양한 타입의 데이터를 저장하는 그래프를 만들 수 있다.

## 방문 상태 초기화

같은 그래프에서 BFS와 DFS를 반복 실행하려면 이전 탐색의 방문 상태를 초기화해야 한다.

```java
void reset() {
    nodes.forEach(Node::resetVisit);
}
```

초기화하지 않으면 이전 탐색에서 방문한 정점이 다음 탐색에서 제외될 수 있다.

## 기억할 판단 기준

* 그래프는 정점과 간선으로 구성된다.
* 방향 그래프에서는 간선의 방향을 구분해야 한다.
* 가중치는 정점 사이의 연결에 저장한다.
* BFS는 Queue, DFS는 Stack 또는 재귀를 사용한다.
* 순환 그래프에서는 방문 여부를 반드시 관리한다.
* 인접 행렬은 간선 확인이 빠르지만 `O(V²)` 공간이 필요하다.
* 인접 리스트는 실제로 존재하는 간선 중심으로 저장한다.
* 단순 BFS와 DFS의 누적 가중치는 최단 가중치 경로를 보장하지 않는다.
* 그래프를 재사용해 탐색하려면 방문 상태를 초기화해야 한다.
