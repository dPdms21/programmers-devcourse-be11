import java.util.*;

class Solution {
    public long solution(int[] a, int[][] edges) {
        int n = a.length;

        ArrayList<ArrayList<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        long[] weight = new long[n];
        long sum = 0;

        for (int i = 0; i < n; i++) {
            weight[i] = a[i];
            sum += a[i];
        }

        if (sum != 0) {
            return -1;
        }

        int[] parent = new int[n];
        int[] order = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = -2;
        }

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        parent[0] = -1;

        int index = 0;

        while (!stack.isEmpty()) {
            int current = stack.pop();
            order[index++] = current;

            for (int next : graph.get(current)) {
                if (next == parent[current]) {
                    continue;
                }

                parent[next] = current;
                stack.push(next);
            }
        }

        long answer = 0;

        for (int i = n - 1; i > 0; i--) {
            int current = order[i];
            int p = parent[current];

            answer += Math.abs(weight[current]);
            weight[p] += weight[current];
        }

        return answer;
    }
}