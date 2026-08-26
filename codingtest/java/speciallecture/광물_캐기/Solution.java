import java.util.*;

class Solution {
    public int solution(int[] picks, String[] minerals) {
        int answer = 0;

        int sum = picks[0] + picks[1] + picks[2];
        int num = Math.min(minerals.length, sum * 5);

        ArrayList<int[]> groups = new ArrayList<>();

        for (int i=0; i< num; i+=5) {
            int end = Math.min(i+5, num);

            int d = 0;
            int ir = 0;
            int s = 0;

            for (int j=i; j<end; j++) {
                if (minerals[j].equals("diamond")) {
                    d++;
                }
                else if (minerals[j].equals("iron")) {
                    ir++;
                }
                else {
                    s++;
                }
            }

            int level = d * 25 + ir * 5 + s;

            groups.add(new int[]{d, ir, s, level});
        }

        Collections.sort(groups, (a, b) -> b[3] - a[3]);

        int idx = 0;

        for (int i=0; i<3; i++) {
            while (picks[i]>0 && idx<groups.size()) {
                int[] group = groups.get(idx);

                int d = group[0];
                int ir = group[1];
                int s = group[2];

                if (i == 0) {
                    answer += d + ir + s;
                }
                else if (i == 1) {
                    answer += d * 5 + ir + s;
                }
                else {
                    answer += d * 25 + ir * 5 + s;
                }

                picks[i]--;
                idx++;
            }
        }

        return answer;
    }
}