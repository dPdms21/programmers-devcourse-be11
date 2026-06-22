package bfs;

import java.util.*;

public class Graph {
    private LinkedList<Integer>[] adjacencyList;

    public Graph(int vertex) {
        adjacencyList = new LinkedList[vertex + 1];

        for (int i=0; i<adjacencyList.length; i++) {
            adjacencyList[i] = new LinkedList<>();
        }
    }

    public LinkedList<Integer>[] getAdjacencyList() {
        return adjacencyList;
    }

    public void addEdge(int v, int w) {
        adjacencyList[v].add(w);
        adjacencyList[w].add(v);
    }

    public void printGraph() {
        for (int i=1; i<adjacencyList.length; i++) {
            System.out.print("Vertex " + i + " : ");

            for (Integer v : adjacencyList[i]) {
                System.out.print(v + " ");
            }
            System.out.println();
        }
    }
}
