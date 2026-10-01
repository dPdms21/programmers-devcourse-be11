class Solution {
    public int solution(int n) {
        if (n == 0) {
            return 0;
        }

        int f1 = 0;
        int f2 = 1;

        for (int i=2; i<=n; i++) {
            int f3 = (f1 + f2) % 1234567;
            f1 = f2;
            f2 = f3;
        }

        return f2;
    }
}