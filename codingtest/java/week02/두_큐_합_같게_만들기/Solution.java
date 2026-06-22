import java.util.*;

class Solution {
    public int solution(int[] queue1, int[] queue2) {
        int answer = 0;

        Queue<Integer> q1 = new ArrayDeque<>();
        Queue<Integer> q2 = new ArrayDeque<>();

        long sum1 = 0;
        long sum2 = 0;

        for (int i : queue1) {
            q1.add(i);
            sum1 += i;
        }

        for (int i : queue2) {
            q2.add(i);
            sum2 += i;
        }

        if ((sum1 + sum2) % 2 != 0) {
            return -1;
        }

        long target = (sum1 + sum2) / 2;
        int limit = (queue1.length + queue2.length) * 2;

        while (sum1 != target && answer < limit) {
            if (sum1 > target) {
                int temp = q1.poll();
                q2.add(temp);
                sum1 -= temp;
                sum2 += temp;
            }
            else {
                int temp = q2.poll();
                q1.add(temp);
                sum2 -= temp;
                sum1 += temp;
            }

            answer++;
        }

        return sum1 == target ? answer : -1;
    }
}