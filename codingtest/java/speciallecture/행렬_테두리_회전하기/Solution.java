class Solution {
    public int[] solution(int rows, int columns, int[][] queries) {
        int[] answer = new int[queries.length];

        int[][] arr = new int[rows][columns];

        int n = 1;

        for (int i=0; i<rows; i++) {
            for (int j=0; j<columns; j++) {
                arr[i][j] = n;
                n++;
            }
        }

        for (int i=0; i<queries.length; i++) {
            int v1 = queries[i][0]-1;
            int v2 = queries[i][1]-1;
            int v3 = queries[i][2]-1;
            int v4 = queries[i][3]-1;

            int temp = arr[v1][v2];
            int min = temp;

            for (int j=v1; j<v3; j++) {
                arr[j][v2] = arr[j+1][v2];
                min = Math.min(min, arr[j][v2]);
            }

            for (int j=v2; j<v4; j++) {
                arr[v3][j] = arr[v3][j+1];
                min = Math.min(min, arr[v3][j]);
            }

            for (int j=v3; j>v1; j--) {
                arr[j][v4] = arr[j-1][v4];
                min = Math.min(min, arr[j][v4]);
            }

            for (int j=v4; j>v2; j--) {
                arr[v1][j] = arr[v1][j-1];
                min = Math.min(min, arr[v1][j]);
            }

            arr[v1][v2+1] = temp;
            answer[i] = min;
        }

        return answer;
    }
}