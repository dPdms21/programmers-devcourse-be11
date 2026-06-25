class Solution {
    public int[] solution(String[] park, String[] routes) {
        int h = 0;
        int w = 0;

        for (int i=0; i<park.length; i++) {
            for (int j=0; j<park[i].length(); j++) {
                char c = park[i].charAt(j);

                if (c == 'S') {
                    h = i;
                    w = j;
                    break;
                }
            }
        }

        for (int i=0; i<routes.length; i++) {
            boolean valid = true;
            int th = h;
            int tw = w;

            char op = routes[i].charAt(0);
            int n = routes[i].charAt(2) - '0';

            if (op == 'N') {
                for (int j=0; j<n; j++) {
                    th--;

                    if (th < 0 || th > park.length - 1) {
                        valid = false;
                        break;
                    }
                    else {
                        if (park[th].charAt(tw) == 'X') {
                            valid = false;
                            break;
                        }
                    }
                }
            }
            else if (op == 'S') {
                for (int j=0; j<n; j++) {
                    th++;

                    if (th < 0 || th > park.length - 1) {
                        valid = false;
                        break;
                    }
                    else {
                        if (park[th].charAt(tw) == 'X') {
                            valid = false;
                            break;
                        }
                    }
                }
            }
            else if (op == 'W') {
                for (int j=0; j<n; j++) {
                    tw--;

                    if (tw < 0 || tw > park[0].length() - 1) {
                        valid = false;
                        break;
                    }
                    else {
                        if (park[th].charAt(tw) == 'X') {
                            valid = false;
                            break;
                        }
                    }
                }
            }
            else {
                for (int j=0; j<n; j++) {
                    tw++;

                    if (tw < 0 || tw > park[0].length() - 1) {
                        valid = false;
                        break;
                    }
                    else {
                        if (park[th].charAt(tw) == 'X') {
                            valid = false;
                            break;
                        }
                    }
                }
            }

            if (valid) {
                h = th;
                w = tw;
            }
        }

        return new int[]{h,w};
    }
}
