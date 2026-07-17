import java.util.*;

class Solution {
    public int[] solution(int[] arr) {
        if (arr.length == 1) {
            return new int[]{-1};
        }

        List<Integer> list = new ArrayList<>();

        for (int i=0; i<arr.length; i++) {
            list.add(arr[i]);
        }

        int min = arr[0];
        int idx = 0;

        for (int i=1; i<arr.length; i++) {
            if (min > arr[i]) {
                min = arr[i];
                idx = i;
            }
        }

        list.remove(idx);

        int[] answer = new int[list.size()];

        for (int i=0; i<list.size(); i++) {
            answer[i] = list.get(i);
        }

        return answer;
    }
}