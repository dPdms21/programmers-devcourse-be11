class Solution {
    public int[] solution(int brown, int yellow) {
        int[] answer = {};

        int t = brown + yellow;

        for (int w=3; w<t; w++) {
            if (t % w != 0) {
                continue;
            }

            int h = t / w;

            if (w < h) {
                continue;
            }

            if ((w-2) * (h-2) == yellow) {
                answer = new int[]{w, h};
                break;
            }
        }

        return answer;
    }
}