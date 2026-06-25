import java.util.*;

class Solution {
    public int[] solution(int[] progresses, int[] speeds) {
        Deque<Integer> q = new ArrayDeque<>();

        for (int i=0; i<progresses.length; i++) {
            int day = 0;

            while (progresses[i] < 100) {
                day++;
                progresses[i] += speeds[i];
            }

            q.offer(day);
        }

        int t = q.poll();
        int cnt = 1;

        List<Integer> list = new ArrayList<>();

        while (!q.isEmpty()) {
            if (t >= q.peek()) {
                q.poll();
                cnt++;
            } else {
                list.add(cnt);
                t = q.poll();
                cnt = 1;
            }
        }

        list.add(cnt);

        int[] answer = new int[list.size()];

        for (int i=0; i<list.size(); i++) {
            answer[i] = list.get(i);
        }

        return answer;
    }
}