package 자연수_뒤집어_배열로_만들기;

import java.util.*;

class Solution {
    public int[] solution(long n) {
        List<Integer> list = new LinkedList<>();

        while (n > 0) {
            list.add((int) (n % 10));
            n /= 10;
        }

        return list.stream().mapToInt(Integer::intValue).toArray();
    }
}