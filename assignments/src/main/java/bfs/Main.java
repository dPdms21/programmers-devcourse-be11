package bfs;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        int vertexCount = 9;

        boolean[] visited = new boolean[vertexCount + 1];
        int[] dist = new int[vertexCount + 1];
        int[] parent = new int[vertexCount + 1];
        Graph graph = new Graph(vertexCount);

        graph.addEdge(1, 2);
        graph.addEdge(1, 3);

        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(2, 6);

        graph.addEdge(3, 7);

        graph.addEdge(4, 5);
        graph.addEdge(4, 7);

        graph.addEdge(5, 6);

        graph.addEdge(7, 8);
        graph.addEdge(8, 9);

        graph.printGraph();
        System.out.println();

        int startVertex = 1; // 3,5
        Queue<Integer> queue = new LinkedList<>();

        visited[startVertex] = true;
        dist[startVertex] = 0;
        queue.add(startVertex);
        System.out.println("정점 " + startVertex + "에서 시작하는 BFS");

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            System.out.print(vertex + " ");

            for (int adj : graph.getAdjacencyList()[vertex]) {
                if (!visited[adj]) {
                    visited[adj] = true;
                    dist[adj] = dist[vertex] + 1;
                    parent[adj] = vertex;
                    queue.add(adj);
                }
            }
        }

        System.out.println();
        System.out.println();

        int targetVertex = 9;
        List<Integer> path = new ArrayList<>();

        for (int vertex=targetVertex; vertex!=0; vertex=parent[vertex]) {
            path.add(vertex);

            if (vertex == startVertex) {
                break;
            }
        }

        Collections.reverse(path);
        System.out.println("최단 경로: " + path);
        System.out.println();

        for (int i = 1; i < dist.length; i++) {
            System.out.println(
                    startVertex + " → " + i + " 최단 거리: " + dist[i]
            );
        }
    }
}
