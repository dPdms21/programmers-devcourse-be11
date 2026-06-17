package bfs;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        boolean[] visited = new boolean[9 + 1];
        Graph graph = new Graph(9);

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

        int startVertex = 1;
        Queue<Integer> queue = new LinkedList<>();

        visited[startVertex] = true;
        queue.add(startVertex);
        System.out.println("정점 " + startVertex + "에서 시작하는 BFS");

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            System.out.print(vertex + " ");

            for (int adj : graph.getAdjacencyList()[vertex]) {
                if (!visited[adj]) {
                    visited[adj] = true;
                    queue.add(adj);
                }
            }
        }
    }
}
