package dfs;

import java.util.*;

public class Graph {
    private LinkedList<Integer>[] adjList;

    public Graph(int v){
        adjList = new LinkedList[v + 1];

        for(int i=0; i<adjList.length; i++){
            adjList[i] = new LinkedList<>();
        }
    }

    public void addEdge(int v, int w){
        adjList[v].add(w);
        adjList[w].add(v);
    }

    public void dfs(int start) {
        boolean[] visited = new boolean[adjList.length];
        System.out.println("정점 " + start + "에서 시작하는 DFS");
        dfsRecur(start, visited);
        System.out.println();
    }

    private void dfsRecur(int v, boolean[] visited) {
        visited[v] = true;
        System.out.print(v + " ");

        for (int i: adjList[v]){
            if (!visited[i]){
                dfsRecur(i, visited);
            }
        }
    }
}
