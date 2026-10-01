class Solution {
    public int solution(int[][] info, int n, int m) {
        boolean[][] dp = new boolean[n][m];
        dp[0][0] = true;

        for (int[] item : info) {
            int aTrace = item[0];
            int bTrace = item[1];

            boolean[][] next = new boolean[n][m];

            for (int a = 0; a < n; a++) {
                for (int b = 0; b < m; b++) {
                    if (!dp[a][b]) {
                        continue;
                    }

                    // A가 훔치는 경우
                    if (a + aTrace < n) {
                        next[a + aTrace][b] = true;
                    }

                    // B가 훔치는 경우
                    if (b + bTrace < m) {
                        next[a][b + bTrace] = true;
                    }
                }
            }

            dp = next;
        }

        for (int a = 0; a < n; a++) {
            for (int b = 0; b < m; b++) {
                if (dp[a][b]) {
                    return a;
                }
            }
        }

        return -1;
    }
}