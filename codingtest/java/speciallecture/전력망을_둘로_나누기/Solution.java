import java.util.*;

class Solution {
    ArrayList<Integer>[] arr;
    boolean[] visited;

    public int solution(int n, int[][] wires) {
        int answer = n;

        for (int i=0; i<wires.length; i++) {
            arr = new ArrayList[n+1];
            visited = new boolean[n+1];

            for (int j=0; j<=n; j++) {
                arr[j] = new ArrayList<>();
            }

            for (int j=0; j<wires.length; j++) {
                if (j == i) {
                    continue;
                }

                arr[wires[j][0]].add(wires[j][1]);
                arr[wires[j][1]].add(wires[j][0]);
            }

            int cnt = dfs(1);
            int diff = Math.abs(cnt - (n - cnt));

            answer = Math.min(answer, diff);
        }

        return answer;
    }

    int dfs(int v) {
        visited[v] = true;

        int cnt = 1;

        for (int next : arr[v]) {
            if (visited[next]) {
                continue;
            }

            cnt += dfs(next);
        }

        return cnt;
    }
}