class Solution {
    public int solution(int n, int[][] computers) {
        int answer = 0;
        boolean[] visited = new boolean[n];

        for (int i=0; i<computers.length; i++) {
            if (!visited[i]) {
                answer++;

                dfs(i, computers, visited);
            }
        }

        return answer;
    }

    private void dfs(int num, int[][] computers, boolean[] visited) {
        visited[num] = true;

        for (int j=0; j<computers[num].length; j++) {
            if (computers[num][j] == 1 && !visited[j]) {
                dfs(j, computers, visited);
            }
        }
    }
}