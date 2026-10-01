class Solution {
    public long solution(int[] sequence) {
        long answer = 0;
        long s1 = 0;
        long s2 = 0;

        for (int i=0; i<sequence.length; i++) {
            long temp = 0;

            if (i % 2 != 0) {
                temp = sequence[i] * 1;
            }
            else {
                temp = sequence[i] * -1;
            }

            s1 = Math.max(s1 + temp, temp);
            s2 = Math.max(s2 - temp, -temp);

            answer = Math.max(answer, Math.max(s1, s2));
        }

        return answer;
    }
}