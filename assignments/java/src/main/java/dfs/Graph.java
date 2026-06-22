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
        System.out.println("정점 " + start + "에서 시작하는 재귀 기반 DFS");
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

    public void dfs2(int start) {
        Stack<Integer> stack = new Stack<>();
        boolean[] visited = new boolean[10];
        System.out.println("\n정점 " + start + "에서 시작하는 stack 기반 DFS");

        stack.push(start);

        while(!stack.isEmpty()){
            int v = stack.pop();

            if (visited[v]){
                continue;
            }

            visited[v] = true;
            System.out.print(v + " ");

            for (int i: adjList[v]){
                if (!visited[i]){
                    stack.push(i);
                }
            }
        }
    }

    public boolean hasPath(int start, int target) {
        boolean[] visited = new boolean[adjList.length];

        return hasPathRecur(start, target, visited);
    }

    private boolean hasPathRecur(int start, int target, boolean[] visited) {
        visited[start] = true;

        if (start == target) {
            return true;
        }

        for (int i: adjList[start]) {
            if (!visited[i] && hasPathRecur(i, target, visited)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasCycle() {
        boolean[] visited = new boolean[adjList.length];

        for (int i=1; i<adjList.length; i++) {
            if (!visited[i] && hasCycleRecur(i, -1, visited)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasCycleRecur(int start, int parent, boolean[] visited) {
        visited[start] = true;

        for (int i: adjList[start]) {
            if (!visited[i]) {
                if (hasCycleRecur(i, parent, visited)) {
                    return true;
                }
            }
            else if (i != parent) {
                return true;
            }
        }

        return false;
    }

    public int cnt() {
        boolean[] visited = new boolean[adjList.length];
        int cnt = 0;

        for (int i=1; i<adjList.length; i++) {
            if (!visited[i]) {
                dfs3(i, visited);
                cnt++;
            }
        }

        return cnt;
    }

    private void dfs3(int v, boolean[] visited) {
        visited[v] = true;

        for (int i : adjList[v]) {
            if (!visited[v]) {
                dfs3(i, visited);
            }
        }
    }
}
