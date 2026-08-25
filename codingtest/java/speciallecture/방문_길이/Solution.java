import java.util.*;

class Solution {
    public int solution(String dirs) {
        HashSet<String> set = new HashSet<>();

        int x = 0;
        int y = 0;
        int cnt = 0;

        for (int i=0; i<dirs.length(); i++) {
            int dx = x;
            int dy = y;
            char c = dirs.charAt(i);

            if (c == 'U') {
                dy++;
            }
            if (c == 'D') {
                dy--;
            }
            if (c == 'R') {
                dx++;
            }
            if (c == 'L') {
                dx--;
            }

            if (dy > 5 || dy < -5) {
                continue;
            }
            if (dx > 5 || dx < -5) {
                continue;
            }

            String path = x + "," + y + "->" + dx + "," + dy;
            String reverse = dx + "," + dy + "->" + x + "," + y;

            x = dx;
            y = dy;

            if (!set.contains(path)) {
                cnt++;
                set.add(path);
                set.add(reverse);
            }
        }

        return cnt;
    }
}