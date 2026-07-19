class Solution {
    public int solution(int[] numbers, int target) {
        int answer = dfs(0, 0, numbers, target);

        return answer;
    }

    private int dfs(int i, int sum, int[] numbers, int target) {
        if (i == numbers.length) {
            if (sum == target) {
                return 1;
            } else {
                return 0;
            }
        }

        return dfs(i + 1, sum + numbers[i], numbers, target) + dfs(i + 1, sum - numbers[i], numbers, target);
    }
}