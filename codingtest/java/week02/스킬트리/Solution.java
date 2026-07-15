import java.util.*;

class Solution {
    public int solution(String skill, String[] skill_trees) {
        int answer = 0;

        for (int i=0; i<skill_trees.length; i++) {
            String temp = "";

            for (int j=0; j<skill_trees[i].length(); j++) {
                char c = skill_trees[i].charAt(j);

                if (skill.indexOf(c) != -1) {
                    temp += c;
                }
            }

            if (skill.startsWith(temp)) {
                answer++;
            }
        }

        return answer;
    }
}