import java.util.*;

class Solution {
    public int[] solution(int[] progresses, int[] speeds) {
        Queue<Integer> days = new LinkedList<>();

        for (int i = 0; i < progresses.length; i++) {
            int day = 0;

            while (progresses[i] < 100) {
                day++;
                progresses[i] += speeds[i];
            }

            days.offer(day);
        }

        List<Integer> result = new ArrayList<>();

        while (!days.isEmpty()) {
            int day = days.poll();
            int cnt = 1;

            while (!days.isEmpty() && day >= days.peek()) {
                cnt++;
                days.poll();
            }

            result.add(cnt);
        }

        int[] answer = new int[result.size()];

        for (int i = 0; i < result.size(); i++) {
            answer[i] = result.get(i);
        }

        return answer;
    }
}