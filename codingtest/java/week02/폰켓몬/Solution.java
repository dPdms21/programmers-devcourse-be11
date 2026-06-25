import java.util.*;

class Solution {
    public int solution(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i=0; i<nums.length; i++) {
            if (!map.containsKey(nums[i])) {
                map.put(nums[i], 1);
            }
            else {
                int n = map.get(nums[i]);
                n++;
                map.put(nums[i], n);
            }
        }

        int answer = 0;
        int N = nums.length;

        if (map.size() > N/2) {
            answer = N/2;
        }
        else {
            answer = map.size();
        }

        return answer;
    }
}