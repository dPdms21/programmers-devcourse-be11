import java.util.*;

class Solution {
    public int solution(int[][] maps) {
        Deque<int[]> q = new ArrayDeque<>();

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        q.offer(new int[]{0,0});

        while (!q.isEmpty()) {
            int[] current = q.poll();

            int x = current[0];
            int y = current[1];

            for (int i = 0; i < 4; i++) {
                int nextX = x + dx[i];
                int nextY = y + dy[i];

                if (nextX < 0 || nextY < 0 || nextX >= maps.length || nextY >= maps[0].length) {
                    continue;
                }

                if (maps[nextX][nextY] != 1) {
                    continue;
                }

                maps[nextX][nextY] = maps[x][y] + 1;
                q.offer(new int[]{nextX, nextY});
            }
        }

        int answer = maps[maps.length-1][maps[0].length-1];

        return answer == 1 ? -1 : answer;
    }
}