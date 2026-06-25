class Solution {
    public String[] solution(String[] picture, int k) {
        String[] answer = new String[picture.length * k];

        int idx = 0;

        for (int i=0; i<picture.length; i++) {
            StringBuilder sb = new StringBuilder();

            for (int j=0; j<picture[i].length(); j++) {
                char c = picture[i].charAt(j);

                for (int l=0; l<k; l++) {
                    sb.append(c);
                }
            }

            for (int m=0; m<k; m++) {
                answer[idx] = sb.toString();
                idx++;
            }
        }

        return answer;
    }
}
