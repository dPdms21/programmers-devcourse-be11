import java.util.*;

class Solution {
    public int[] solution(int n, String[] words) {
        int[] answer = new int[2];
        Set<String> set = new HashSet<>();

        set.add(words[0]);

        char c1 = words[0].charAt(words[0].length()-1);

        for (int i=1; i<words.length; i++) {
            char c2 = words[i].charAt(0);

            if (c1 != c2 || set.contains(words[i])) {
                answer[0] = i % n + 1;
                answer[1] = i / n +1;

                return answer;
            }

            set.add(words[i]);
            c1 = words[i].charAt(words[i].length()-1);
        }

        return answer;
    }
}