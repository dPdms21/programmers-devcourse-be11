import java.util.*;

class Solution {
    public int[] solution(int[] progresses, int[] speeds) {
        List<Integer> list = new ArrayList<>();

        int i = 0;

        while (i < progresses.length) {
            int cnt = 0;

            while (progresses[i] < 100) {
                for (int j=i; j<progresses.length; j++) {
                    progresses[j] += speeds[j];
                }
            }

            for (int j=i; j<progresses.length; j++) {
                if (progresses[j] >= 100) {
                    cnt++;
                }
                else {
                    break;
                }
            }

            list.add(cnt);
            i += cnt;
        }

        int[] answer = new int[list.size()];

        for (int j=0; j<list.size(); j++) {
            answer[j] = list.get(j);
        }

        return answer;
    }
}