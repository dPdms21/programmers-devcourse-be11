import java.util.*;

class Solution {
    public int[] solution(int k, int[] score) {
        int[] answer = new int[score.length];
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        for (int i=0; i<score.length; i++) {
            if (i < k) {
                pq.offer(score[i]);
            }
            else {
                if (score[i] > pq.peek()) {
                    pq.poll();
                    pq.offer(score[i]);
                }
            }

            answer[i] = pq.peek();
        }

        return answer;
    }
}