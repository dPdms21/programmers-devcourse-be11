import java.util.*;

class Solution {
    public int solution(String begin, String target, String[] words) {
        Deque<String> q = new ArrayDeque<>();
        boolean[] visited = new boolean[words.length];

        int answer = 0;

        q.offer(begin);

        while (!q.isEmpty()) {
            int size = q.size();

            for (int i=0; i<size; i++) {
                String word = q.poll();

                if (word.equals(target)) {
                    return answer;
                }

                for (int j=0; j<words.length; j++) {
                    if (!visited[j]) {
                        int diff = 0;

                        for (int k=0; k<word.length(); k++) {
                            if (word.charAt(k) != words[j].charAt(k)) {
                                diff++;

                                if (diff > 1) {
                                    break;
                                }
                            }
                        }

                        if (diff == 1) {
                            visited[j] = true;
                            q.offer(words[j]);
                        }
                    }
                }
            }

            answer++;
        }

        return 0;
    }
}