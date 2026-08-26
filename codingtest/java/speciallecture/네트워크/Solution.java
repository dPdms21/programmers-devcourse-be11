import java.util.*;

class Solution {
    ArrayList<Integer>[] graph;
    boolean[] visited;

    public int solution(int n, int[][] computers) {
        graph = new ArrayList[n];
        visited = new boolean[n];

        int answer = 0;

        for (int i=0; i<n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int i=0; i<computers.length; i++) {
            for (int j=0; j<computers[i].length; j++) {
                if (computers[i][j] == 1) {
                    graph[i].add(j);
                }
            }
        }

        for (int node=0; node<n; node++) {
            if (!visited[node]) {
                dfs(node);
                answer++;
            }
        }

        return answer;
    }

    void dfs(int v) {
        visited[v] = true;

        for (int next : graph[v]) {
            if (visited[next]) {
                continue;
            }

            dfs(next);
        }
    }
}