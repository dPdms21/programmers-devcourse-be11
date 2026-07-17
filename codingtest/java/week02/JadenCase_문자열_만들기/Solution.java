import java.util.*;

class Solution {
    public String solution(String s) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') {
                sb.append(c);
                first = true;
            }
            else {
                if (first) {
                    sb.append(Character.toUpperCase(c));
                    first = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }

        String answer = "" + sb;

        return answer;
    }
}