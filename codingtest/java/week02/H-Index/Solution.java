class Solution {
    public int solution(int[] citations) {
        int h = 0;

        for (int i=1; i<=citations.length; i++) {
            int cnt = 0;

            for (int j=0; j<citations.length; j++) {
                if (i <= citations[j]) {
                    cnt++;
                }
            }

            if (i <= cnt) {
                h = i;
            }
        }

        return h;
    }
}