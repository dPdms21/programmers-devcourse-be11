import java.util.*;

class Solution {
    public int solution(int[] scoville, int K) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        for (int i=0; i< scoville.length; i++) {
            pq.offer(scoville[i]);
        }

        int answer = 0;

        while (pq.peek() < K) {
            if (pq.size() < 2) {
                return -1;
            }

            int one = pq.poll();
            int two = pq.poll();

            int mixed = one + (two * 2);

            pq.offer(mixed);

            answer++;
        }

        return answer;
    }
}