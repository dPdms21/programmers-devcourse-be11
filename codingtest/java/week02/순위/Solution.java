import java.util.*;

class Solution {
    public int solution(int n, int[][] results) {
        List<List<Integer>> win = new ArrayList<>();
        List<List<Integer>> lose = new ArrayList<>();

        for (int i=0; i<=n; i++) {
            win.add(new ArrayList<>());
            lose.add(new ArrayList<>());
        }

        for (int i=0; i<results.length; i++) {
            int w = results[i][0];
            int l = results[i][1];

            win.get(w).add(l);
            lose.get(l).add(w);
        }

        int answer = 0;

        for (int player=1; player<=n; player++) {
            boolean[] wVisited = new boolean[n+1];
            boolean[] lVisited = new boolean[n+1];

            dfs(player, win, wVisited);
            dfs(player, lose, lVisited);

            int cnt = 0;

            for (int other=1; other<=n; other++) {
                if (other == player) {
                    continue;
                }

                if (wVisited[other] || lVisited[other]) {
                    cnt++;
                }
            }

            if (cnt == n-1) {
                answer++;
            }
        }

        return answer;
    }

    private void dfs(int cur, List<List<Integer>> g, boolean[] visited) {
        visited[cur] = true;

        for (int next : g.get(cur)) {
            if (!visited[next]) {
                dfs(next, g, visited);
            }
        }
    }
}