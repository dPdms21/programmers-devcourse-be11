import java.util.*;

class Solution {
    public int solution(int[] priorities, int location) {
        Queue<int[]> q = new LinkedList<>();
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());

        int answer = 0;

        for (int i=0; i<priorities.length; i++) {
            q.add(new int[] {i, priorities[i]});
        }

        for (int i=0; i<priorities.length; i++) {
            pq.offer(priorities[i]);
        }

        while (!q.isEmpty()) {
            int[] v = q.poll();

            if (v[1] == pq.peek()) {
                pq.poll();
                answer++;

                if (v[0] == location) {
                    return answer;
                }
            }
            else {
                q.offer(v);
            }
        }

        return answer;
    }
}