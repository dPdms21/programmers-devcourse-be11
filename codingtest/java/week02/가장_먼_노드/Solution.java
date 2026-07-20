import java.util.*;

class Solution {
    public int solution(int n, int[][] edge) {
        List<List<Integer>> graph = new ArrayList<>();

        for (int i=0; i<=n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i=0; i<edge.length; i++) {
            int a = edge[i][0];
            int b = edge[i][1];

            graph.get(a).add(b);
            graph.get(b).add(a);
        }

        Queue<Integer> q = new LinkedList<>();

        int[] dist = new int[n+1];
        Arrays.fill(dist, -1);

        dist[1] = 0;
        q.offer(1);

        while (!q.isEmpty()) {
            int cur = q.poll();

            for (int next : graph.get(cur)) {
                if (dist[next] == -1) {
                    dist[next] = dist[cur] + 1;
                    q.offer(next);
                }
            }
        }

        int max = 0;

        for (int i=1; i<=n; i++) {
            max = Math.max(max, dist[i]);
        }

        int answer = 0;

        for (int i=1; i<=n; i++) {
            if (dist[i] == max) {
                answer++;
            }
        }

        return answer;
    }
}